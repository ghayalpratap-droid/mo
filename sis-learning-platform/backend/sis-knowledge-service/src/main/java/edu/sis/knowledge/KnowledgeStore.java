package edu.sis.knowledge;

import edu.sis.shared.Identity;
import java.time.*;
import java.util.*;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.server.ResponseStatusException;

@Service
public class KnowledgeStore {
  final JdbcTemplate db;
  final ModelGateway models;
  final TransactionTemplate tx;
  final int chunkTokens;
  final int overlapTokens;

  public KnowledgeStore(JdbcTemplate d, ModelGateway m, PlatformTransactionManager manager, @org.springframework.beans.factory.annotation.Value("${sis.ai.chunk-tokens:600}") int chunkTokens, @org.springframework.beans.factory.annotation.Value("${sis.ai.chunk-overlap:80}") int overlapTokens) {
    db = d;
    models = m;
    tx = new TransactionTemplate(manager);
    this.chunkTokens=chunkTokens;this.overlapTokens=overlapTokens;
  }

  static ResponseStatusException fail(int code, String message) {
    return new ResponseStatusException(HttpStatus.valueOf(code), message);
  }

  public record Passage(
      UUID id,
      String source,
      String version,
      String heading,
      int startLine,
      int endLine,
      String content,
      double score) {}

  public record Chunk(String heading, int start, int end, String content) {}

  public static List<Chunk> chunks(String text) {
    return TokenChunks.split(text, 600, 80);
  }

  public Object ingest(Identity who, String name, String text, String provider, String audience) {
    who.administrator();
    if (!name.matches("[A-Za-z0-9_.-]{1,150}\\.(md|java|sql|xml|yaml|yml|properties|json)")
        || text.length() > 500000
        || text.isBlank()
        || !Set.of("ALL", "A", "B").contains(provider)
        || !Set.of("PUBLIC", "INTERNAL").contains(audience))
      throw fail(400, "Invalid source, scope or audience");
    SourceValidation.validate(name, text);
    String masked = ModelGateway.scrub(text);
    UUID job = UUID.randomUUID();
    db.update(
        "insert into ingestion_jobs(id,source_name,status,detail) values(?,?,?,?)",
        job,
        name,
        "RUNNING",
        "Preparing sanitized chunks");
    try {
      var pieces = TokenChunks.split(masked,chunkTokens,overlapTokens);
      List<float[]> vectors = models.embedBatch(pieces.stream().map(Chunk::content).toList());
      Object value =
          tx.execute(
              status -> {
                // Serialise publication per logical name; active version changes only after all
                // inserts succeed.
                db.queryForList("select pg_advisory_xact_lock(hashtext(?))", name);
                var existing =
                    db.queryForList("select * from source_files where name=? for update", name);
                UUID sid =
                    existing.isEmpty() ? UUID.randomUUID() : (UUID) existing.getFirst().get("id");
                String hash = ModelGateway.hash(masked + provider + audience + models.model);
                if (!existing.isEmpty()
                    && !Boolean.TRUE.equals(existing.getFirst().get("deleted"))) {
                  var old =
                      db.queryForList(
                          "select id from documents where id=? and content_hash=?",
                          existing.getFirst().get("active_version"),
                          hash);
                  if (!old.isEmpty()) return Map.of("sourceId", sid, "unchanged", true);
                }
                if (existing.isEmpty())
                  db.update(
                      "insert into source_files(id,name,provider,audience) values(?,?,?,?)",
                      sid,
                      name,
                      provider,
                      audience);
                UUID doc = UUID.randomUUID();
                db.update(
                    "insert into documents(id,source_id,content_hash,model) values(?,?,?,?)",
                    doc,
                    sid,
                    hash,
                    models.model);
                for (int i = 0; i < pieces.size(); i++) {
                  var c = pieces.get(i);
                  UUID id = UUID.randomUUID();
                  db.update(
                      "insert into"
                          + " document_chunks(id,document_id,heading,start_line,end_line,content)"
                          + " values(?,?,?,?,?,?)",
                      id,
                      doc,
                      c.heading(),
                      c.start(),
                      c.end(),
                      c.content());
                  db.update(
                      "insert into embeddings(id,embedding,model) values(?,?::public.vector,?)",
                      id,
                      ModelGateway.vector(vectors.get(i)),
                      models.model);
                }
                db.update(
                    "update source_files set"
                        + " active_version=?,provider=?,audience=?,deleted=false,updated_at=now()"
                        + " where id=?",
                    doc,
                    provider,
                    audience,
                    sid);
                audit(who, "INGESTION", sid);
                return Map.of("sourceId", sid, "version", doc, "chunks", pieces.size());
              });
      db.update(
          "update ingestion_jobs set status='SUCCEEDED',detail='Atomic publication complete' where"
              + " id=?",
          job);
      return Map.of("jobId", job, "result", value);
    } catch (Exception e) {
      db.update(
          "update ingestion_jobs set status='FAILED',detail='Indexing failed; previous version"
              + " retained' where id=?",
          job);
      throw fail(503, "Indexing failed; previous version retained");
    }
  }

  static final String ACL =
      " not s.deleted and d.id=s.active_version and (?='ALL' or s.provider='ALL' or s.provider=?)"
          + " and (s.audience='PUBLIC' or ?) ";

  public List<Passage> search(Identity who, String question) {
    if (question.isBlank() || question.length() > 2000)
      throw fail(400, "Question must contain 1..2000 characters");
    String q = ModelGateway.scrub(question);
    long start = System.nanoTime();
    String vector = ModelGateway.vector(models.embed(q));
    String from =
        " from document_chunks c join documents d on d.id=c.document_id join source_files s on"
            + " s.id=d.source_id join embeddings e on e.id=c.id where "
            + ACL
            + " and e.model=? ";
    String columns =
        "select c.id,s.name,d.content_hash,c.heading,c.start_line,c.end_line,c.content";
    var semantic =
        db.queryForList(
            columns
                + ",1-(e.embedding <=> ?::public.vector) as score"
                + from
                + "order by e.embedding <=> ?::public.vector limit 20",
            vector,
            who.scope(),
            who.provider(),
            who.staff(),
            models.model,
            vector);
    var keyword =
        db.queryForList(
            columns
                + ",1.0 as score"
                + from
                + "and (c.content_tsv @@ plainto_tsquery('simple',?) or position(lower(?) in"
                + " lower(c.content))>0) order by"
                + " ts_rank(c.content_tsv,plainto_tsquery('simple',?)) desc limit 20",
            who.scope(),
            who.provider(),
            who.staff(),
            models.model,
            q,
            q,
            q);
    var scores = new HashMap<UUID, Double>();
    var passages = new HashMap<UUID, Passage>();
    for (var branch : List.of(keyword, semantic)) {
      int rank = 0;
      for (var row : branch) {
        rank++;
        UUID id = (UUID) row.get("id");
        double sim = ((Number) row.get("score")).doubleValue();
        if (branch == semantic && sim < 0.12) continue;
        scores.merge(id, 1.0 / (60 + rank), Double::sum);
        passages.put(
            id,
            new Passage(
                id,
                row.get("name").toString(),
                row.get("content_hash").toString(),
                row.get("heading").toString(),
                ((Number) row.get("start_line")).intValue(),
                ((Number) row.get("end_line")).intValue(),
                row.get("content").toString(),
                sim));
      }
    }
    var result =
        scores.entrySet().stream()
            .sorted(Map.Entry.<UUID, Double>comparingByValue().reversed())
            .limit(6)
            .map(e -> passages.get(e.getKey()))
            .toList();
    db.update(
        "insert into retrieval_logs(id,actor,chunk_ids,latency_ms) values(?,?,?::jsonb,?)",
        UUID.randomUUID(),
        who.user(),
        "["
            + result.stream()
                .map(p -> "\"" + p.id() + "\"")
                .collect(java.util.stream.Collectors.joining(","))
            + "]",
        (System.nanoTime() - start) / 1000000);
    return result;
  }

  public Object citation(Identity who, UUID id) {
    var rows =
        db.queryForList(
            "select s.name,d.content_hash,c.heading,c.start_line,c.end_line,c.content from"
                + " document_chunks c join documents d on d.id=c.document_id join source_files s on"
                + " s.id=d.source_id where c.id=? and "
                + ACL,
            id,
            who.scope(),
            who.provider(),
            who.staff());
    if (rows.isEmpty()) throw fail(404, "Citation unavailable");
    return rows.getFirst();
  }

  public void delete(Identity who, UUID id) {
    who.administrator();
    db.update("update source_files set deleted=true,updated_at=now() where id=?", id);
    audit(who, "SOURCE_DELETED", id);
  }

  boolean visibleMessage(Identity who, UUID message) {
    var rows = db.queryForList("select chunk_id from message_sources where message_id=?", message);
    for (var row : rows) {
      try {
        citation(who, (UUID) row.get("chunk_id"));
      } catch (ResponseStatusException denied) {
        return false;
      }
    }
    return true;
  }

  void audit(Identity who, String event, UUID target) {
    db.update(
        "insert into audit_logs(id,actor,event_type,target_id) values(?,?,?,?)",
        UUID.randomUUID(),
        who.user(),
        event,
        target);
  }

  void own(Identity who, UUID session) {
    if (db.queryForObject(
            "select count(*) from chat_sessions where id=? and owner=? and provider=?",
            Integer.class,
            session,
            who.user(),
            who.provider())
        != 1) throw fail(404, "Session not found");
  }
}

package edu.sis.domain;

import static edu.sis.domain.Contracts.*;

import edu.sis.shared.Identity;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import org.springframework.http.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class BusinessController {
  final BusinessService svc;
  final JdbcTemplate db;
  final Validator validator;

  public BusinessController(BusinessService s, JdbcTemplate d, Validator v) {
    svc = s;
    db = d;
    validator = v;
  }

  Identity who(Jwt j) {
    return Identity.of(j);
  }

  @GetMapping("/providers")
  public Object providers(@AuthenticationPrincipal Jwt j) {
    return db.queryForList(
        "select * from providers where ?='ALL' or id=?", who(j).scope(), who(j).provider());
  }

  @GetMapping("/courses")
  public Object courses(@AuthenticationPrincipal Jwt j) {
    return db.queryForList(
        "select * from courses where ?='ALL' or provider=?", who(j).scope(), who(j).provider());
  }

  @GetMapping("/academic-years")
  public Object years() {
    return db.queryForList("select * from academic_years where active=true order by id");
  }

  @GetMapping("/reference-data")
  public Object refs() {
    return Map.of(
        "sources",
        List.of("UG", "PGL", "PGD", "PGNI", "APPLY_ONCE", "LLE"),
        "currency",
        "GBP",
        "policy",
        "DEMO_ASSUMPTION");
  }

  @GetMapping("/applications")
  public Object list(
      @AuthenticationPrincipal Jwt j,
      @RequestParam(defaultValue = "") String q,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size) {
    if (page < 0 || page > 10000 || size < 1 || size > 50 || q.length() > 100)
      throw BusinessService.fail(400, "Invalid pagination or query");
    var p = who(j);
    return db.queryForList(
        "select"
            + " id,provider,source_ref,student_name,source,course_id,academic_year,fee,currency,status,registration,attendance,version,created_at"
            + " from applications where (?='ALL' or provider=?) and (lower(source_ref) like ? or"
            + " lower(student_name) like ?) order by created_at desc,id limit ? offset ?",
        p.scope(),
        p.provider(),
        "%" + q.toLowerCase() + "%",
        "%" + q.toLowerCase() + "%",
        size,
        page * size);
  }

  @PostMapping("/applications")
  @ResponseStatus(HttpStatus.CREATED)
  public View create(
      @AuthenticationPrincipal Jwt j,
      @Valid @RequestBody Intake r,
      @RequestHeader("Idempotency-Key") String key) {
    return svc.create(who(j), r, key);
  }

  @GetMapping("/applications/{id}")
  public View get(@AuthenticationPrincipal Jwt j, @PathVariable UUID id) {
    return svc.get(who(j), id);
  }

  @GetMapping("/applications/{id}/summary")
  public Object summary(@AuthenticationPrincipal Jwt j, @PathVariable UUID id) {
    var a = svc.get(who(j), id);
    return Map.of(
        "id",
        a.id(),
        "provider",
        a.provider(),
        "registration",
        a.registration(),
        "attendance",
        a.attendance(),
        "status",
        a.status(),
        "asOf",
        Instant.now(),
        "source",
        "LIVE_BUSINESS_API");
  }

  @GetMapping("/applications/{id}/history")
  public Object history(@AuthenticationPrincipal Jwt j, @PathVariable UUID id) {
    svc.get(who(j), id);
    return db.queryForList(
        "select * from histories where application_id=? order by occurred_at desc,id desc limit 50",
        id);
  }

  @PostMapping("/applications/{id}/registration")
  public View registration(
      @AuthenticationPrincipal Jwt j,
      @PathVariable UUID id,
      @Valid @RequestBody Confirmation r,
      @RequestHeader("Idempotency-Key") String key) {
    return svc.confirm(who(j), id, r, "REGISTRATION", key);
  }

  @PostMapping("/applications/{id}/attendance")
  public View attendance(
      @AuthenticationPrincipal Jwt j,
      @PathVariable UUID id,
      @Valid @RequestBody Confirmation r,
      @RequestHeader("Idempotency-Key") String key) {
    return svc.confirm(who(j), id, r, "ATTENDANCE", key);
  }

  @PostMapping("/applications/{id}/cocs/preview")
  public Preview preview(
      @AuthenticationPrincipal Jwt j, @PathVariable UUID id, @Valid @RequestBody Change r) {
    return svc.preview(who(j), id, r);
  }

  @PostMapping("/applications/{id}/cocs")
  public Object coc(
      @AuthenticationPrincipal Jwt j,
      @PathVariable UUID id,
      @Valid @RequestBody Change r,
      @RequestHeader("Idempotency-Key") String key) {
    return svc.change(who(j), id, r, key);
  }

  @GetMapping("/applications/{id}/cocs")
  public Object cocs(@AuthenticationPrincipal Jwt j, @PathVariable UUID id) {
    svc.get(who(j), id);
    return db.queryForList(
        "select * from cocs where application_id=? order by created_at desc limit 50", id);
  }

  @PostMapping("/applications/{id}/payment-schedules")
  public Object pay(
      @AuthenticationPrincipal Jwt j, @PathVariable UUID id, @RequestParam long version) {
    return svc.schedule(who(j), id, version);
  }

  @GetMapping("/applications/{id}/payment-schedules")
  public Object payments(@AuthenticationPrincipal Jwt j, @PathVariable UUID id) {
    svc.get(who(j), id);
    return db.queryForList("select * from instalments where application_id=? order by number", id);
  }

  @GetMapping("/reports")
  public Object reports(@AuthenticationPrincipal Jwt j) {
    var p = who(j);
    if (!p.admin() && !p.roles().contains("REPORTER"))
      throw BusinessService.fail(403, "Reporting role required");
    return db.queryForList(
        "select provider,count(*) as applications,sum(fee) as total_fee from applications where"
            + " (?='ALL' or provider=?) group by provider",
        p.scope(),
        p.provider());
  }

  @GetMapping("/operations")
  public Object ops(@AuthenticationPrincipal Jwt j) {
    var p = who(j);
    if (!p.staff()) throw BusinessService.fail(403, "Support role required");
    return db.queryForList(
        "select event_type,status,count(*) as total from outbox_events where (?='ALL' or"
            + " provider=?) group by event_type,status",
        p.scope(),
        p.provider());
  }

  @PostMapping("/imports")
  @ResponseStatus(HttpStatus.CREATED)
  public Object upload(@AuthenticationPrincipal Jwt j, @Valid @RequestBody CsvUpload r) {
    var p = who(j);
    p.writer();
    java.util.List<org.apache.commons.csv.CSVRecord> rows;
    try (var csv =
        org.apache.commons.csv.CSVFormat.DEFAULT
            .builder()
            .setHeader()
            .setSkipHeaderRecord(true)
            .setIgnoreEmptyLines(false)
            .get()
            .parse(new java.io.StringReader(r.csv()))) {
      if (!csv.getHeaderNames()
          .equals(List.of("sourceRef", "studentName", "source", "courseId", "academicYear", "fee")))
        throw BusinessService.fail(400, "Expected six-column CSV header");
      rows = csv.getRecords();
      if (rows.size() > 500) throw BusinessService.fail(400, "At most 500 CSV rows");
    } catch (java.io.IOException | java.io.UncheckedIOException malformed) {
      throw BusinessService.fail(400, "Malformed CSV");
    }
    UUID job = UUID.randomUUID();
    db.update(
        "insert into import_jobs values(?,?,?,CURRENT_TIMESTAMP)", job, p.provider(), "RUNNING");
    for (var row : rows) {
      String status = "CREATED", detail = "Application imported";
      try {
        if (row.size() != 6) throw new IllegalArgumentException();
        var req =
            new Intake(
                p.provider(),
                row.get(0),
                row.get(1),
                row.get(2),
                row.get(3),
                Integer.parseInt(row.get(4)),
                new BigDecimal(row.get(5)));
        if (!validator.validate(req).isEmpty()) throw new IllegalArgumentException();
        svc.create(p, req, job + "-" + row.getRecordNumber());
      } catch (org.springframework.web.server.ResponseStatusException rejected) {
        if (rejected.getStatusCode().value() == 409) {
          status = "DUPLICATE";
          detail = "Source reference already imported";
        } else if (rejected.getStatusCode().value() == 400
            || rejected.getStatusCode().value() == 422) {
          status = "INVALID";
          detail = "Row failed deterministic validation";
        } else throw rejected;
      } catch (IllegalArgumentException invalid) {
        status = "INVALID";
        detail = "Invalid field count or value";
      }
      db.update(
          "insert into import_rows values(?,?,?,?,?)",
          UUID.randomUUID(),
          job,
          row.getRecordNumber(),
          status,
          detail);
    }
    db.update("update import_jobs set status='COMPLETED' where id=?", job);
    return Map.of("id", job, "status", "COMPLETED");
  }

  @GetMapping("/imports/{id}")
  public Object imported(@AuthenticationPrincipal Jwt j, @PathVariable UUID id) {
    var jobs = db.queryForList("select * from import_jobs where id=?", id);
    if (jobs.isEmpty()) throw BusinessService.fail(404, "Import not found");
    Object provider = jobs.getFirst().getOrDefault("provider", jobs.getFirst().get("PROVIDER"));
    who(j).check(provider.toString());
    return db.queryForList(
        "select row_number,status,detail from import_rows where job_id=? order by row_number", id);
  }

  @GetMapping(value = "/imports/{id}/results", produces = "text/csv")
  public String results(@AuthenticationPrincipal Jwt j, @PathVariable UUID id) {
    @SuppressWarnings("unchecked")
    var rows = (List<Map<String, Object>>) imported(j, id);
    StringBuilder out = new StringBuilder("row,status,detail\n");
    for (var row : rows)
      out.append(
              row.values().stream()
                  .map(Object::toString)
                  .collect(java.util.stream.Collectors.joining(",")))
          .append("\n");
    return out.toString();
  }
}

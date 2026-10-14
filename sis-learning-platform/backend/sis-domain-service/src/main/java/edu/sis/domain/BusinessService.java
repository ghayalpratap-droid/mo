package edu.sis.domain;

import static edu.sis.domain.Contracts.*;

import edu.sis.shared.Identity;
import java.math.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.*;
import java.util.*;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class BusinessService {
  final Applications apps;
  final JdbcTemplate db;

  public BusinessService(Applications a, JdbcTemplate d) {
    apps = a;
    db = d;
  }

  static ResponseStatusException fail(int status, String message) {
    return new ResponseStatusException(HttpStatus.valueOf(status), message);
  }

  public View get(Identity who, UUID id) {
    var a = apps.findById(id).orElseThrow(() -> fail(404, "Application not found"));
    who.check(a.provider);
    return View.of(a);
  }

  StudentApplication locked(Identity who, UUID id, long version) {
    who.writer();
    var a = apps.lock(id).orElseThrow(() -> fail(404, "Application not found"));
    who.check(a.provider);
    if (a.version != version) throw fail(409, "Version conflict; refresh the application");
    return a;
  }

  @Transactional
  public View create(Identity who, Intake r, String key) {
    who.writer();
    who.check(r.provider());
    String fingerprint = hash(r.toString());
    UUID previous = receipt(r.provider(), key, fingerprint);
    if (previous != null) return get(who, previous);
    if (apps.findByProviderAndSourceRef(r.provider(), r.sourceRef()).isPresent())
      throw fail(409, "Source reference already exists");
    course(r.courseId(), r.provider());
    var a = new StudentApplication();
    a.id = UUID.randomUUID();
    a.provider = r.provider();
    a.sourceRef = r.sourceRef();
    a.studentName = r.studentName().strip();
    a.source = r.source();
    a.courseId = r.courseId();
    a.academicYear = r.academicYear();
    a.fee = r.fee().setScale(2, RoundingMode.UNNECESSARY);
    a.credits =
        db.queryForObject("select credits from courses where id=?", Integer.class, a.courseId);
    a.currency = "GBP";
    a.status = "ACTIVE";
    a.registration = "PENDING";
    a.attendance = "PENDING";
    a.createdAt = Instant.now();
    a.updatedAt = a.createdAt;
    apps.saveAndFlush(a);
    UUID student = UUID.randomUUID();
    db.update(
        "insert into students values(?,?,?,CURRENT_TIMESTAMP)", student, a.provider, a.studentName);
    db.update("update applications set student_id=? where id=?", student, a.id);
    UUID period = UUID.randomUUID();
    db.update(
        "insert into tuition_periods values(?,?,?,?,?,?)",
        period,
        a.id,
        a.provider,
        LocalDate.of(a.academicYear, 8, 1),
        LocalDate.of(a.academicYear + 1, 7, 31),
        a.status);
    db.update(
        "insert into tuition_finance_details values(?,?,?,?,?)",
        UUID.randomUUID(),
        period,
        a.fee,
        "GBP",
        a.credits);
    record(a, who, "APPLICATION_CREATED", "Synthetic intake accepted");
    saveReceipt(a.provider, key, fingerprint, a.id);
    return View.of(a);
  }

  void course(String id, String provider) {
    if (db.queryForObject(
            "select count(*) from courses where id=? and provider=?", Integer.class, id, provider)
        != 1) throw fail(400, "Course does not belong to provider");
  }

  @Transactional
  public View confirm(Identity who, UUID id, Confirmation r, String kind, String key) {
    var visible = get(who, id);
    String fp = hash(id + kind + r);
    UUID previous = receipt(visible.provider(), key, fp);
    if (previous != null) {
      who.writer();
      return get(who, id);
    }
    var a = locked(who, id, r.version());
    if (!a.status.equals("ACTIVE")) throw fail(422, "Application must be active");
    if (kind.equals("REGISTRATION")) {
      if (!r.status().equals("CONFIRMED")) throw fail(422, "Registration must be CONFIRMED");
      a.registration = r.status();
    } else {
      if (!a.registration.equals("CONFIRMED"))
        throw fail(422, "Registration required before attendance");
      a.attendance = r.status();
    }
    a.updatedAt = Instant.now();
    apps.flush();
    record(a, who, kind, r.status());
    saveReceipt(a.provider, key, fp, a.id);
    return View.of(a);
  }

  public Preview preview(Identity who, UUID id, Change r) {
    var a = get(who, id);
    var errors = new ArrayList<String>();
    if (r.version() != a.version()) errors.add("Version conflict");
    if (r.effectiveDate().isAfter(LocalDate.now()))
      errors.add("Future effective date is not supported by this demo");
    if (Set.of("WITHDRAWN", "CANCELLED").contains(a.status()))
      errors.add("Closed applications cannot change");
    if (r.type().equals("SUSPENSION")
        && (!a.status().equals("ACTIVE") || !a.attendance().equals("CONFIRMED")))
      errors.add("Suspension requires active confirmed attendance");
    if (r.type().equals("RESUMPTION") && !a.status().equals("SUSPENDED"))
      errors.add("Resumption requires suspension");
    if (Set.of("FEE_CHANGE", "FEE_WAIVER").contains(r.type())) {
      if (r.amount() == null
          || r.amount().signum() < 0
          || r.amount().scale() > 2
          || r.amount().compareTo(new BigDecimal("100000")) > 0)
        errors.add("Amount must be 0..100000 with at most two decimal places");
      else if (r.type().equals("FEE_WAIVER") && r.amount().compareTo(a.fee()) > 0)
        errors.add("Waiver exceeds fee");
      if (db.queryForObject(
              "select count(*) from instalments where application_id=?", Integer.class, id)
          > 0) errors.add("Scheduled payments require a separate finance review");
    }
    if (r.type().equals("CREDITS") && (r.credits() == null || r.credits() < 1 || r.credits() > 180))
      errors.add("Credits must be 1..180");
    if (Set.of("COURSE_TRANSFER", "PROVIDER_TRANSFER").contains(r.type())) {
      if (r.targetCourse() == null) errors.add("Target course required");
      else if (db.queryForObject(
              "select count(*) from courses where id=? and provider=?",
              Integer.class,
              r.targetCourse(),
              a.provider())
          != 1)
        errors.add(
            "Demo transfer requires a course within the same provider; cross-provider transfer"
                + " needs separate authorization");
      if (r.type().equals("PROVIDER_TRANSFER"))
        errors.add("Cross-provider transfer is not implemented");
    }
    return new Preview(
        errors.isEmpty(), errors, "DEMO_ASSUMPTION: synthetic validation, not SLC policy");
  }

  @Transactional
  public Map<String, Object> change(Identity who, UUID id, Change r, String key) {
    var visible = get(who, id);
    String fp = hash(id + r.toString());
    UUID prev = receipt(visible.provider(), key, fp);
    who.writer();
    if (prev != null) return db.queryForMap("select * from cocs where id=?", prev);
    var a = locked(who, id, r.version());
    var result = preview(who, id, r);
    UUID coc = UUID.randomUUID();
    String state = result.valid() ? "ACCEPTED" : "REJECTED";
    String reason =
        result.valid() ? "Synthetic rule checks passed" : String.join("; ", result.messages());
    db.update(
        "insert into cocs values(?,?,?,?,?,?,?,?)",
        coc,
        id,
        a.provider,
        r.type(),
        state,
        reason,
        r.effectiveDate(),
        java.sql.Timestamp.from(Instant.now()));
    if (result.valid()) {
      switch (r.type()) {
        case "WITHDRAWAL" -> a.status = "WITHDRAWN";
        case "CANCELLATION" -> a.status = "CANCELLED";
        case "SUSPENSION" -> a.status = "SUSPENDED";
        case "RESUMPTION" -> a.status = "ACTIVE";
        case "FEE_CHANGE" -> a.fee = r.amount().setScale(2);
        case "FEE_WAIVER" -> a.fee = a.fee.subtract(r.amount());
        case "COURSE_TRANSFER" -> {
          a.courseId = r.targetCourse();
          a.credits =
              db.queryForObject(
                  "select credits from courses where id=?", Integer.class, a.courseId);
        }
        case "CREDITS" -> a.credits = r.credits();
      }
      // The command timestamp advances optimistic version on every CoC decision.
    }
    a.updatedAt = Instant.now();
    apps.flush();
    record(a, who, "COC_" + state, r.type() + ": " + reason);
    saveReceipt(a.provider, key, fp, coc);
    return db.queryForMap("select * from cocs where id=?", coc);
  }

  @Transactional
  public List<Map<String, Object>> schedule(Identity who, UUID id, long version) {
    var a = locked(who, id, version);
    if (!a.registration.equals("CONFIRMED") || !a.status.equals("ACTIVE"))
      throw fail(422, "Active registered application required");
    if (db.queryForObject(
            "select count(*) from instalments where application_id=?", Integer.class, id)
        == 0) {
      var amounts = split(a.fee);
      for (int i = 0; i < 3; i++)
        db.update(
            "insert into instalments values(?,?,?,?,?,?,?)",
            UUID.randomUUID(),
            id,
            a.provider,
            i + 1,
            amounts.get(i),
            "GBP",
            LocalDate.now().plusMonths(i * 3L));
      record(a, who, "PAYMENTS_SCHEDULED", "DEMO 25/25/remainder in GBP");
    }
    return db.queryForList("select * from instalments where application_id=? order by number", id);
  }

  public static List<BigDecimal> split(BigDecimal total) {
    var part = total.multiply(new BigDecimal("0.25")).setScale(2, RoundingMode.HALF_UP);
    return List.of(part, part, total.subtract(part.multiply(new BigDecimal("2"))).setScale(2));
  }

  void record(StudentApplication a, Identity who, String event, String detail) {
    db.update(
        "insert into histories values(?,?,?,?,?,?,?)",
        UUID.randomUUID(),
        a.id,
        a.provider,
        event,
        detail,
        who.user(),
        java.sql.Timestamp.from(Instant.now()));
    db.update("update tuition_periods set status=? where application_id=?", a.status, a.id);
    db.update(
        "update tuition_finance_details set fee=?,credits=? where tuition_period_id in(select id"
            + " from tuition_periods where application_id=?)",
        a.fee,
        a.credits,
        a.id);
    db.update(
        "insert into tuition_period_histories select ?,id,?,?,?,?,CURRENT_TIMESTAMP from"
            + " tuition_periods where application_id=?",
        UUID.randomUUID(),
        a.status,
        a.fee,
        a.credits,
        event,
        a.id);
    String target =
        event.equals("ATTENDANCE")
            ? "BAS"
            : event.equals("REGISTRATION")
                ? "CEM"
                : event.startsWith("COC") ? (a.source.startsWith("PG") ? "BCF" : "LAP") : "MULE";
    db.update(
        "insert into"
            + " outbox_events(id,provider,aggregate_id,event_type,status,attempts,created_at,target)"
            + " values(?,?,?,?,?,?,CURRENT_TIMESTAMP,?)",
        UUID.randomUUID(),
        a.provider,
        a.id,
        event,
        "PENDING",
        0,
        target);
  }

  UUID receipt(String provider, String key, String fingerprint) {
    if (key == null || !key.matches("[A-Za-z0-9-]{1,100}"))
      throw fail(400, "Idempotency-Key required (letters, digits, hyphens)");
    var found =
        db.queryForList(
            "select * from request_receipts where provider=? and request_key=?", provider, key);
    if (found.isEmpty()) return null;
    if (!fingerprint.equals(found.getFirst().get("FINGERPRINT"))
        && !fingerprint.equals(found.getFirst().get("fingerprint")))
      throw fail(409, "Idempotency key reused with different request");
    Object id = found.getFirst().getOrDefault("result_id", found.getFirst().get("RESULT_ID"));
    return UUID.fromString(id.toString());
  }

  void saveReceipt(String provider, String key, String fp, UUID id) {
    db.update("insert into request_receipts values(?,?,?,?)", provider, key, fp, id);
  }

  static String hash(String s) {
    try {
      return HexFormat.of()
          .formatHex(
              MessageDigest.getInstance("SHA-256").digest(s.getBytes(StandardCharsets.UTF_8)));
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }
}

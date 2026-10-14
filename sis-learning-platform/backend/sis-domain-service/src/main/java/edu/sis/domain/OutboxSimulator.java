package edu.sis.domain;

import java.time.*;
import java.util.*;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@EnableScheduling
class SchedulerConfig {}

@Service
public class OutboxSimulator {
  private final JdbcTemplate db;
  private final LocalIntegrationSimulator receiver;

  public OutboxSimulator(JdbcTemplate d, LocalIntegrationSimulator r) {
    db = d;
    receiver = r;
  }

  @Scheduled(fixedDelay = 3000)
  @Transactional
  public void deliver() {
    var rows =
        db.queryForList(
            "select id,provider,aggregate_id,event_type,target,attempts from outbox_events where"
                + " status='PENDING' and next_attempt<=CURRENT_TIMESTAMP order by created_at limit"
                + " 50 for update");
    for (var row : rows) {
      Object id = value(row, "id");
      int attempts = ((Number) value(row, "attempts")).intValue() + 1;
      try {
        var event =
            new ExternalReceiver.Event(
                UUID.fromString(id.toString()),
                value(row, "provider").toString(),
                UUID.fromString(value(row, "aggregate_id").toString()),
                value(row, "event_type").toString(),
                ExternalReceiver.Target.valueOf(value(row, "target").toString()));
        if (db.queryForObject(
                "select count(*) from processed_events where event_id=?", Integer.class, id)
            == 0) {
          var receipt = receiver.receive(event);
          db.update(
              "insert into processed_events values(?,?,CURRENT_TIMESTAMP)", id, receipt.receiver());
        }
        db.update(
            "update outbox_events set status='DELIVERED',attempts=?,last_error=null where id=?",
            attempts,
            id);
      } catch (IllegalStateException failure) {
        db.update(
            "update outbox_events set status=?,attempts=?,next_attempt=?,last_error=? where id=?",
            attempts >= 5 ? "DEAD" : "PENDING",
            attempts,
            java.sql.Timestamp.from(Instant.now().plusSeconds(Math.min(60, 1L << attempts))),
            "Synthetic receiver failure",
            id);
      }
    }
  }

  static Object value(Map<String, Object> row, String key) {
    return row.getOrDefault(key, row.get(key.toUpperCase(java.util.Locale.ROOT)));
  }
}

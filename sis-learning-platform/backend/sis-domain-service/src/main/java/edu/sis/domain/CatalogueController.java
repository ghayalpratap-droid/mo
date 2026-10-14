package edu.sis.domain;

import edu.sis.shared.Identity;
import java.util.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class CatalogueController {
  final JdbcTemplate db;
  final BusinessService business;

  public CatalogueController(JdbcTemplate d, BusinessService b) {
    db = d;
    business = b;
  }

  @GetMapping("/catalogue")
  public Object catalogue(@AuthenticationPrincipal Jwt jwt) {
    var p = Identity.of(jwt);
    return Map.of(
        "campuses",
        db.queryForList(
            "select * from campuses where ?='ALL' or provider=?", p.scope(), p.provider()),
        "modules",
        db.queryForList(
            "select * from modules where ?='ALL' or provider=?", p.scope(), p.provider()),
        "qualifications",
        db.queryForList("select * from qualifications"),
        "years",
        db.queryForList("select * from academic_years"));
  }

  @GetMapping("/modules")
  public Object modules(@AuthenticationPrincipal Jwt jwt) {
    var p = Identity.of(jwt);
    return db.queryForList(
        "select * from modules where ?='ALL' or provider=?", p.scope(), p.provider());
  }

  @GetMapping("/students")
  public Object students(@AuthenticationPrincipal Jwt jwt) {
    var p = Identity.of(jwt);
    return db.queryForList(
        "select id,provider,'Fictitious learner' as masked_label,created_at from students where"
            + " ?='ALL' or provider=? order by created_at desc limit 50",
        p.scope(),
        p.provider());
  }

  @GetMapping("/applications/{id}/tuition-periods")
  public Object tuition(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID id) {
    business.get(Identity.of(jwt), id);
    return db.queryForList(
        "select t.id,t.provider,t.start_date,t.end_date,t.status,f.fee,f.currency,f.credits from"
            + " tuition_periods t join tuition_finance_details f on f.tuition_period_id=t.id where"
            + " t.application_id=?",
        id);
  }

  @GetMapping("/rules")
  public Object rules() {
    return db.queryForList("select * from rule_configs order by id");
  }

  @GetMapping("/operations/toggles")
  public Object toggles(@AuthenticationPrincipal Jwt jwt) {
    if (!Identity.of(jwt).staff()) throw BusinessService.fail(403, "Support role required");
    return db.queryForList("select * from feature_toggles order by name");
  }

  @GetMapping("/operations/exceptions")
  public Object exceptions(@AuthenticationPrincipal Jwt jwt) {
    var p = Identity.of(jwt);
    if (!p.staff()) throw BusinessService.fail(403, "Support role required");
    return db.queryForList(
        "select id,provider,service,error_code,message_masked,created_at from sync_exceptions where"
            + " ?='ALL' or provider=? order by created_at desc limit 50",
        p.scope(),
        p.provider());
  }
}

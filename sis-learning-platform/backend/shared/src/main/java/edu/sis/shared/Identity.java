package edu.sis.shared;

import java.util.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.server.ResponseStatusException;

public record Identity(String user, String provider, List<String> roles) {
  public static Identity of(Jwt jwt) {
    return new Identity(
        jwt.getSubject(), jwt.getClaimAsString("provider"), jwt.getClaimAsStringList("roles"));
  }

  public boolean admin() {
    return roles.contains("ADMIN");
  }

  public boolean staff() {
    return admin() || roles.contains("SUPPORT") || roles.contains("DEVELOPER");
  }

  public void writer() {
    if (!admin() && !roles.contains("ADVISOR"))
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Write role required");
  }

  public void administrator() {
    if (!admin()) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Administrator required");
  }

  public String scope() {
    return admin() ? "ALL" : provider;
  }

  public void check(String owner) {
    if (!admin() && !provider.equals(owner))
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Record not found");
  }
}

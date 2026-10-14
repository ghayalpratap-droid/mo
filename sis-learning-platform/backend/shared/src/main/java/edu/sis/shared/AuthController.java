package edu.sis.shared;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.time.*;
import java.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {
  private final JwtEncoder encoder;
  private final BCryptPasswordEncoder passwords;
  private final String hash;
  private static final Map<String, List<String>> USERS =
      Map.of(
          "admin",
          List.of("ADMIN"),
          "advisor-a",
          List.of("ADVISOR"),
          "advisor-b",
          List.of("ADVISOR"),
          "developer",
          List.of("DEVELOPER"),
          "support",
          List.of("SUPPORT"),
          "reporter",
          List.of("REPORTER"));

  public AuthController(
      JwtEncoder e, BCryptPasswordEncoder p, @Value("${sis.demo-password}") String secret) {
    encoder = e;
    passwords = p;
    hash = p.encode(secret);
  }

  public record Login(
      @NotBlank @Size(max = 40) String username, @NotBlank @Size(max = 100) String password) {}

  @PostMapping("/login")
  public Map<String, Object> login(@Valid @RequestBody Login r) {
    if (!passwords.matches(r.password, hash) || !USERS.containsKey(r.username))
      throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
    var now = Instant.now();
    var provider = r.username.equals("advisor-b") ? "B" : "A";
    var claims =
        JwtClaimsSet.builder()
            .issuer("sis-local")
            .subject(r.username)
            .issuedAt(now)
            .expiresAt(now.plusSeconds(3600))
            .claim("roles", USERS.get(r.username))
            .claim("provider", provider)
            .build();
    return Map.of(
        "token",
        encoder
            .encode(JwtEncoderParameters.from(JwsHeader.with(MacAlgorithm.HS256).build(), claims))
            .getTokenValue(),
        "username",
        r.username,
        "provider",
        provider,
        "roles",
        USERS.get(r.username));
  }

  @GetMapping("/me")
  public Identity me(@AuthenticationPrincipal Jwt jwt) {
    return Identity.of(jwt);
  }
}

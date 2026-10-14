package edu.sis.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "applications")
public class StudentApplication {
  @Id public UUID id;

  @Column(nullable = false, length = 8)
  public String provider;

  @Column(nullable = false, length = 80)
  public String sourceRef;

  @Column(nullable = false, length = 100)
  public String studentName;

  @Column(nullable = false, length = 20)
  public String source;

  @Column(nullable = false, length = 20)
  public String courseId;

  @Column(nullable = false)
  public int academicYear;

  @Column(nullable = false, precision = 12, scale = 2)
  public BigDecimal fee;

  @Column(nullable = false, length = 3)
  public String currency;

  @Column(nullable = false, length = 30)
  public String status;

  @Column(nullable = false, length = 20)
  public String registration;

  @Column(nullable = false, length = 20)
  public String attendance;

  @Column(nullable = false)
  public int credits;

  @Version public long version;

  @Column(nullable = false)
  public Instant updatedAt;

  @Column(nullable = false)
  public Instant createdAt;
}

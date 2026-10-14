package edu.sis.domain;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;

public class Contracts {
  public record Intake(
      @NotBlank @Pattern(regexp = "[AB]") String provider,
      @NotBlank @Pattern(regexp = "[A-Za-z0-9-]{1,80}") String sourceRef,
      @NotBlank @Size(max = 100) String studentName,
      @NotBlank @Pattern(regexp = "UG|PGL|PGD|PGNI|APPLY_ONCE|LLE") String source,
      @NotBlank String courseId,
      @Min(2020) @Max(2040) int academicYear,
      @NotNull @DecimalMin("0.00") @DecimalMax("100000.00") @Digits(integer = 6, fraction = 2)
          BigDecimal fee) {}

  public record Confirmation(
      @NotNull Long version,
      @NotBlank @Pattern(regexp = "CONFIRMED|NOT_ATTENDING") String status) {}

  public record Change(
      @NotNull Long version,
      @NotBlank
          @Pattern(
              regexp =
                  "WITHDRAWAL|SUSPENSION|RESUMPTION|CANCELLATION|FEE_CHANGE|FEE_WAIVER|CREDITS|COURSE_TRANSFER|PROVIDER_TRANSFER")
          String type,
      @NotNull LocalDate effectiveDate,
      BigDecimal amount,
      String targetCourse,
      Integer credits) {}

  public record View(
      UUID id,
      String provider,
      String sourceRef,
      String studentName,
      String source,
      String courseId,
      int academicYear,
      BigDecimal fee,
      String currency,
      String status,
      String registration,
      String attendance,
      long version,
      Instant createdAt,
      int credits) {
    static View of(StudentApplication a) {
      return new View(
          a.id,
          a.provider,
          a.sourceRef,
          a.studentName,
          a.source,
          a.courseId,
          a.academicYear,
          a.fee,
          a.currency,
          a.status,
          a.registration,
          a.attendance,
          a.version,
          a.createdAt,
          a.credits);
    }
  }

  public record Preview(boolean valid, List<String> messages, String policy) {}

  public record CsvUpload(@NotBlank @Size(max = 100000) String csv) {}
}

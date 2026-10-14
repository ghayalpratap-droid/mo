package edu.sis.domain;

import jakarta.persistence.LockModeType;
import java.util.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

public interface Applications extends JpaRepository<StudentApplication, UUID> {
  Optional<StudentApplication> findByProviderAndSourceRef(String provider, String ref);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("select a from StudentApplication a where a.id=:id")
  Optional<StudentApplication> lock(@Param("id") UUID id);
}

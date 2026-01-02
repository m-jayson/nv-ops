package com.mt.common;


import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.PrePersist;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.Instant;


@ApplicationScoped
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuditListener {

  CurrentUser currentUser;


  @PrePersist
  public void setCreatedFields (Object entity) {

    if (entity instanceof Auditable auditable) {
      auditable.setCreatedOn(Instant.now());
      auditable.setCreatedBy(currentUser.get());
    }
  }
}

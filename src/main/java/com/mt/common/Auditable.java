package com.mt.common;


import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;


@Getter
@Setter
@MappedSuperclass
public abstract class Auditable extends PanacheEntityBase {

  protected Instant createdOn;

  protected Instant lastUpdatedOn;

  protected String createdBy;


}

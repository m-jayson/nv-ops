package com.mt.sweep;


import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.Instant;


@ApplicationScoped
public class SweepRepository {

  @WithTransaction
  public Uni<Sweep> saveOrUpdate (String trackingNumber, String type, String status) {

    return Sweep.<Sweep>findById(trackingNumber)
        .flatMap(sweep -> {
          if (sweep == null) {
            var s = new Sweep(trackingNumber, type, status);
            s.setLastUpdatedOn(Instant.now());
            return s.persist();
          } else {
            sweep.setStatus("test");
            sweep.setLastUpdatedOn(Instant.now());
            return Uni.createFrom().item(sweep);
          }
        });
  }
}

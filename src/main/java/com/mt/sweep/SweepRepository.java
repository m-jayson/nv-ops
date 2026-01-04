package com.mt.sweep;


import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;


@ApplicationScoped
public class SweepRepository {

  public Uni<List<Sweep>> findOrders (
      LocalDate start,
      LocalDate end,
      boolean rts, boolean onHold, boolean otherHub,
      Long hubId
  ) {

    var query = new StringBuilder(
        "isRts = ?1 and isOnHold = ?2 and function('DATE', lastUpdatedOn) >= ?3 and function('DATE', lastUpdatedOn) <= ?4"
    );

    Object[] params;

    if (otherHub && hubId != null) {
      query.append(" and responsibleHubId <> ?5");
      params = new Object[]{rts, onHold, start, end, hubId};
    } else {
      params = new Object[]{rts, onHold, start, end};
    }

    return Sweep.<Sweep>find(query.toString(), params).list();
  }


  @WithTransaction
  public Uni<Sweep> saveOrUpdate (
      String trackingNumber,
      boolean isRts,
      boolean isOnHold,
      Long responsibleHubId,
      String status,
      String shipperName,
      Long shipperId,
      String completeAddress
  ) {

    return Sweep.<Sweep>findById(trackingNumber)
        .flatMap(sweep -> {
          if (sweep == null) {
            var s = Sweep.builder()
                .trackingNumber(trackingNumber)
                .shipperId(shipperId)
                .shipperName(shipperName)
                .fullAddress(completeAddress)
                .isRts(isRts)
                .isOnHold(isOnHold)
                .status(status)
                .build();
            s.setLastUpdatedOn(Instant.now());
            return s.persist();
          } else {
            sweep.setStatus(status);
            sweep.setShipperId(shipperId);
            sweep.setShipperName(shipperName);
            sweep.setFullAddress(completeAddress);
            sweep.setLastUpdatedOn(Instant.now());
            return Uni.createFrom().item(sweep);
          }
        });
  }
}

package com.mt.sweep;


import com.mt.order.OrderClientApi.OrderSearchResponse.SearchData.Order;
import com.mt.sweep.SweepClientApi.SweepResponse.ParcelRoutingData;
import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.jbosslog.JBossLog;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;


@JBossLog
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

    log.infof("[Parcel Sweep Query] %s", query.toString());

    return Sweep.<Sweep>find(query.toString(), params).list();
  }


  @WithTransaction
  public Uni<Sweep> updateReSweep (Sweep order, ParcelRoutingData parcelRoutingData, Order sweepOrder, String status) {

    log.infof("[Parcel ReSweep] %s", order.getTrackingNumber());

    var address = String.join(" ", sweepOrder.to_address1(), sweepOrder.to_address2());
    var shipperName = sweepOrder.to_name();
    var shipperId = sweepOrder.shipper_id();

    order.setRts(parcelRoutingData.rtsed());
    order.setResponsibleHubId(parcelRoutingData.responsibleHubId());
    order.setOnHold(parcelRoutingData.onHold());
    order.setStatus(status);
    order.setFullAddress(address);
    order.setShipperId(shipperId);
    order.setShipperName(shipperName);

    return Panache.getSession()
        .flatMap(session -> session.merge(order))
        .replaceWith(order);
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
                .responsibleHubId(responsibleHubId)
                .build();
            s.setLastUpdatedOn(Instant.now());
            return s.persist();
          } else {
            sweep.setStatus(status);
            sweep.setShipperId(shipperId);
            sweep.setShipperName(shipperName);
            sweep.setFullAddress(completeAddress);
            sweep.setResponsibleHubId(responsibleHubId);
            sweep.setLastUpdatedOn(Instant.now());
            return Uni.createFrom().item(sweep);
          }
        });
  }
}

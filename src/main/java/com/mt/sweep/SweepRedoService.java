package com.mt.sweep;


import com.mt.order.OrderService;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.jbosslog.JBossLog;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.time.LocalDate;
import java.util.Map;


@JBossLog
@ApplicationScoped
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SweepRedoService {

  @NonFinal
  @RestClient
  SweepClientApi sweepClientApi;

  SweepConfig sweepConfig;

  OrderService orderService;

  SweepMapper sweepMapper;

  SweepRepository sweepRepository;


  @WithTransaction
  public Uni<Void> reSweep (
      LocalDate start,
      LocalDate end,
      boolean rts,
      boolean otherHub,
      boolean onHold
  ) {

    return this.sweepRepository.findOrders(start, end, rts, onHold, otherHub, sweepConfig.hubId())
        .onItem().transformToUni(orderEntities -> {
          return Multi.createFrom().iterable(orderEntities)
              .onItem().transformToUniAndMerge(orderEntity -> {
                log.infof("Sweep %s", orderEntity.getTrackingNumber());
                var trackingId = orderEntity.getTrackingNumber();
                var request = this.sweepMapper.toSweepRequest(sweepConfig, trackingId);
                return sweepClientApi.sweep(request)
                    .onItem().transformToUni(o -> {
                      var parcelSweepInfo = o.parcelRoutingData().entrySet()
                          .stream()
                          .filter(x -> x.getKey().equalsIgnoreCase(trackingId))
                          .map(Map.Entry::getValue)
                          .findFirst();

                      return Uni.createFrom().optional(parcelSweepInfo)
                          .onItem().transformToUni(parcelRoutingData -> {
                            return orderService.findByTrackingId(trackingId)
                                .onItem().transformToUni(order -> {
                                  return sweepRepository.updateReSweep(
                                      orderEntity, parcelRoutingData, order,
                                      o.granularStatus()
                                  );
                                });
                          });
                    });
              })
              .collect().asList().replaceWithVoid();
        });
  }




}

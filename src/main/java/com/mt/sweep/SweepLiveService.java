package com.mt.sweep;


import com.mt.common.OpsException;
import com.mt.order.OrderService;
import io.quarkus.runtime.annotations.RegisterForReflection;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.jbosslog.JBossLog;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;
import java.util.Map;
import java.util.Objects;


@JBossLog
@ApplicationScoped
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SweepLiveService {

  @NonFinal
  @RestClient
  SweepClientApi sweepClientApi;

  SweepConfig sweepConfig;

  OrderService orderService;

  SweepMapper sweepMapper;

  SweepRepository sweepRepository;


  public Uni<SweepOrder> parcelSweeperLive (String trackingId) {

    var request = this.sweepMapper.toSweepRequest(sweepConfig, trackingId);

    return sweepClientApi.sweep(request)
        .onItem().transformToUni(sweepResponse -> {
          var parcelRoutingData = sweepResponse.parcelRoutingData();

          if (Objects.isNull(parcelRoutingData) || parcelRoutingData.entrySet().isEmpty()) {
            return Uni.createFrom().failure(new OpsException("No Parcel Found", "sweep", 404));
          }

          var entrySet = parcelRoutingData.entrySet();

          var parcelSweepInfo = entrySet
              .stream()
              .filter(x -> x.getKey().equalsIgnoreCase(trackingId))
              .map(Map.Entry::getValue)
              .findFirst();

          return Uni.createFrom().optional(parcelSweepInfo)
              .onItem().transformToUni(sweepData -> {
                var granularStatus = sweepResponse.granularStatus();

                return orderService.findByTrackingId(trackingId)
                    .onItem()
                    .transformToUni(order -> {
                      return Uni.createFrom().item(this.sweepMapper.toSweepOrder(sweepResponse, sweepData, order))
                          .onItem().transformToUni(sweepOrder -> {
                            if (Objects.isNull(sweepData.toAlert())) {
                              log.infof("[Parcel Sweep] [%s Status] %s", granularStatus, trackingId);
                              return persistRecoveryTracking(
                                  trackingId,
                                  granularStatus,
                                  sweepData,
                                  granularStatus,
                                  sweepOrder.order
                              );
                            }
                            var isOnHold = sweepData.onHold();
                            if (null == isOnHold || isOnHold) {
                              log.infof("[Parcel Sweep] [On Hold Status] %s", trackingId);
                              return persistRecoveryTracking(
                                  trackingId, granularStatus, sweepData, "On Hold",
                                  sweepOrder.order
                              );
                            }
                            if (!Objects.equals(sweepData.responsibleHubId(), sweepConfig.hubId())) {
                              log.infof("[Parcel Sweep] [Other Hub Status] %s", trackingId);
                              return persistRecoveryTracking(
                                  trackingId, granularStatus, sweepData, "Other Hub",
                                  sweepOrder.order
                              );
                            }

                            return this.sweepRepository.saveOrUpdate(
                                    trackingId,
                                    sweepData.rtsed(),
                                    false,
                                    sweepData.responsibleHubId(),
                                    granularStatus,
                                    order.to_name(),
                                    order.shipper_id(),
                                    String.join(" ", order.to_address1(), order.to_address2(), order.to_city())
                                )
                                .onItem().transformToUni(o -> Uni.createFrom().item(sweepOrder));
                          });
                    });
              });
        });
  }


  private Uni<SweepOrder> persistRecoveryTracking (
      String trackingId,
      String granularStatus,
      SweepClientApi.SweepResponse.ParcelRoutingData sweepData,
      String exceptionErr,
      SweepOrder.Order order
  ) {

    return this.sweepRepository.saveOrUpdate(
            trackingId,
            sweepData.rtsed(),
            sweepData.onHold() != null && sweepData.onHold(),
            sweepData.responsibleHubId(),
            granularStatus,
            order.getShipperName(),
            order.getShipperId(),
            String.join(" ", order.getAddress1(), order.getAddress2(), order.getCity())
        )
        .onItem().transformToUni(o -> Uni.createFrom().failure(new OpsException(exceptionErr, "sweep", 404)));
  }


  @Data
  @Builder
  @RegisterForReflection
  public static class SweepOrder {

    String status;

    String responsibleHubName;

    String zoneName;

    String rackSector;

    String nextNode;

    Order order;

    List<String> tags;

    Boolean isRts;

    @Data
    @Builder
    @RegisterForReflection
    public static class Order {

      Long shipperId;

      String shipperName;

      String trackingId;

      String address1;

      String address2;

      String city;
    }
  }
}

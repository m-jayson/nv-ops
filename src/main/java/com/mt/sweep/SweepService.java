package com.mt.sweep;


import com.mt.common.OpsException;
import com.mt.order.OrderService;
import com.mt.sweep.SweepService.SweepOrder.Order;
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

import static com.mt.sweep.SweepClientApi.SweepRequest;


@JBossLog
@ApplicationScoped
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SweepService {

  @NonFinal
  @RestClient
  SweepClientApi sweepClientApi;

  SweepConfig sweepConfig;

  OrderService orderService;


  public Uni<SweepOrder> sweep (String trackingId) {

    var hubUser = sweepConfig.hubUser().filter(s -> !s.isBlank()).orElse(null);

    var request = new SweepRequest(
        sweepConfig.hubId(),
        trackingId,
        sweepConfig.taskId(),
        sweepConfig.nodeId(),
        sweepConfig.toReturnDpId(),
        hubUser
    );

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
                if (Objects.isNull(sweepData.toAlert())) {
                  return Uni.createFrom().failure(new OpsException(sweepResponse.granularStatus(), "sweep", 404));
                }
                if (sweepData.onHold()) {
                  return Uni.createFrom().failure(new OpsException("On Hold", "sweep", 404));
                }
                if (!Objects.equals(sweepData.responsibleHubId(), sweepConfig.hubId())) {
                  return Uni.createFrom().failure(new OpsException("Other Hub", "sweep", 404));
                }
                return orderService.findByTrackingId(trackingId)
                    .onItem().transformToUni(order -> {
                      return Uni.createFrom().item(SweepOrder.builder()
                                                       .status(sweepResponse.granularStatus())
                                                       .responsibleHubName(sweepData.responsibleHubName())
                                                       .zoneName(sweepData.zoneName())
                                                       .rackSector(sweepData.rackSector())
                                                       .nextNode(sweepData.nextNode())
                                                       .tags(sweepData.tags())
                                                       .isRts(sweepData.rtsed())
                                                       .order(Order.builder()
                                                                  .shipperId(order.shipper_id())
                                                                  .shipperName(order.to_name())
                                                                  .trackingId(order.tracking_id())
                                                                  .address1(order.to_address1())
                                                                  .address2(order.to_address2())
                                                                  .city(order.to_city())
                                                                  .build())
                                                       .build());
                    });
              });
        });
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

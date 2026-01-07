package com.mt.route;


import com.mt.common.AppConfig;
import com.mt.pickup.Pickup;
import com.mt.route.RouteClientDto.PickupSearchRequest;
import com.mt.route.RouteClientDto.PickupSearchRequest.DateTimeRange;
import com.mt.route.RouteClientDto.PickupSearchRequest.InFilter;
import com.mt.route.RouteClientDto.PickupSearchRequest.Query;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.jbosslog.JBossLog;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;


@JBossLog
@ApplicationScoped
@RequiredArgsConstructor
@Produces(MediaType.APPLICATION_JSON)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class RouteService {

  @NonFinal
  @RestClient
  RouteClient routeClient;

  AppConfig appConfig;

  RoutePickupService routePickupService;


  public Uni<Void> fetchPickUpJob (LocalDate date) {

    var lowerBound = OffsetDateTime.of(date, LocalTime.of(0, 0, 0), ZoneOffset.UTC);
    var upperBound = OffsetDateTime.of(date, LocalTime.of(23, 59, 59), ZoneOffset.UTC);

    var pickUpJobRequest = PickupSearchRequest.builder()
        .limit(500)
        .query(Query.builder()
                   .hubId(InFilter.<Long>builder()
                              .in(List.of(appConfig.hubId()))
                              .build())
                   .pickupAppointmentJobType(InFilter.<String>builder()
                                                 .in(List.of("CORE_PICKUP_APPOINTMENT_JOB"))
                                                 .build())
                   .pickupServiceType(InFilter.<String>builder()
                                          .in(List.of("Scheduled", "Implant-Manifest"))
                                          .build())
                   .pickupLatestDatetime(DateTimeRange.builder()
                                             .lowerBound(lowerBound)
                                             .upperBound(upperBound)
                                             .build())
                   .build())
        .build();

    return this.routeClient.searchPaJob(pickUpJobRequest)
        .map(RouteClientDto.PostPaJob::data)
        .onItem().transformToMulti(Multi.createFrom()::iterable)
        .onItem().transformToUniAndMerge(pickUpJobResponse -> {
          var jobId = pickUpJobResponse.pickupAppointmentJobId();
          return routeClient.getPickUpProof(jobId, "PICKUP_APPOINTMENT")
              .onFailure().recoverWithItem(throwable -> List.of())
              .onItem().transformToUni(proofs -> {
                var proofMap = proofs.stream()
                    .collect(Collectors.groupingBy(
                        RouteClientDto.PickUpJobInfoResponse::getStatus,
                        Collectors.summingInt(p -> {
                          var metadata = p.getMetadata();
                          return Math.toIntExact(
                              "Success".equalsIgnoreCase(p.getStatus())
                                  ? metadata.getParcelPickupQuantity()
                                  : metadata.getParcelQuantity()
                          );
                        })
                    ));
                return Uni.createFrom().item(proofMap);
              })
              .onItem().transformToUni(proofMap -> {
                return Uni.createFrom().item(Pickup.builder()
                                                 .waypointId(pickUpJobResponse.waypointId())
                                                 .routeId(pickUpJobResponse.routeId())
                                                 .jobId(jobId)
                                                 .driverId(pickUpJobResponse.driverId())
                                                 .status(pickUpJobResponse.status())
                                                 .pickUpDate(date)
                                                 .shipperId(pickUpJobResponse.shipperId())
                                                 .pickUpResult(proofMap)
                                                 .build());
              });
        }).collect().asList()
        .onItem().transformToUni(pickups -> this.routePickupService.updatePickUp(pickups, date))
        .replaceWithVoid();
  }
}

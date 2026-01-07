package com.mt.route;


import com.mt.pickup.Pickup;
import com.mt.pickup.PickupRepository;
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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@JBossLog
@ApplicationScoped
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoutePickupService {

  @NonFinal
  @RestClient
  RouteClient routeClient;

  PickupRepository pickupRepository;


//  public Uni<Void> handlePickUp (List<Long> jobIds, LocalDate date) {
//
//    var postPaJobRequest = RouteClientDto.PostPaJobRequest.builder()
//        .query(Map.of(
//            "pickup_appointment_job_type", Map.of("in", List.of("CORE_PICKUP_APPOINTMENT_JOB")),
//            "pickup_appointment_job_id", Map.of("in", jobIds)
//        ))
//        .limit(500)
//        .search_after(0)
//        .build();
//
//    return routeClient.searchPaJob(postPaJobRequest)
//        .onFailure().recoverWithItem(new RouteClientDto.PostPaJob(List.of()))
//        .onItem().transform(RouteClientDto.PostPaJob::data)
//        .onItem().transformToMulti(Multi.createFrom()::iterable)
//        .onItem().transformToMultiAndMerge(postPaJob -> {
//          var pickupAppointmentJobId = postPaJob.pickupAppointmentJobId();
//          return routeClient.getPickUpProof(pickupAppointmentJobId, "PICKUP_APPOINTMENT")
//              .onFailure().recoverWithItem(throwable -> {
//                log.warnf(
//                    "Failed to get pickup proof for job %s route %s waypoint %s: %s",
//                    pickupAppointmentJobId,
//                    postPaJob.routeId(),
//                    postPaJob.waypointId(),
//                    throwable.getMessage()
//                );
//                return List.of();
//              })
//              .onItem().transformToMulti(pickUpJobInfoResponses -> {
//                var proofMap = pickUpJobInfoResponses.stream()
//                    .collect(Collectors.groupingBy(
//                        RouteClientDto.PickUpJobInfoResponse::getStatus,
//                        Collectors.summingInt(p -> {
//                          var metadata = p.getMetadata();
//                          return Math.toIntExact(
//                              "Success".equalsIgnoreCase(p.getStatus())
//                                  ? metadata.getParcelPickupQuantity()
//                                  : metadata.getParcelQuantity()
//                          );
//                        })
//                    ));
//
//                return Multi.createFrom().item(Pickup.builder()
//                                                   .waypointId(postPaJob.waypointId())
//                                                   .routeId(postPaJob.routeId())
//                                                   .jobId(pickupAppointmentJobId)
//                                                   .driverId(postPaJob.driverId())
//                                                   .status(postPaJob.status())
//                                                   .pickUpDate(date)
//                                                   .shipperId(postPaJob.shipperId())
//                                                   .pickUpResult(proofMap)
//                                                   .build());
//              });
//        }).collect().asList()
//        .onItem().transformToUni(pickUps -> pickupRepository.bulkUpdatePickUp(date, pickUps))
//        .replaceWithVoid();
//  }

  public Uni<Void> updatePickUp(List<Pickup> pickups, LocalDate date){
    return pickupRepository.bulkUpdatePickUp(date, pickups);
  }
}

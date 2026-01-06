package com.mt.route;


import com.mt.common.AppConfig;
import com.mt.pickup.Pickup;
import com.mt.route.RouteClientDto.GetRoutes;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.jbosslog.JBossLog;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.resteasy.reactive.ClientWebApplicationException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
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

  RouteRTSService routeRTSService;


  public Uni<Void> fetchRoutes (LocalDate date) {

    var formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssX");

    // Start at 16:00 UTC on the startDate and -1 day
    var lower = OffsetDateTime.of(date.minusDays(1), LocalTime.of(16, 0), ZoneOffset.UTC);

    // End at 15:59:59 UTC on the endDate
    var upper = OffsetDateTime.of(date, LocalTime.of(15, 59, 59), ZoneOffset.UTC);

    var lowerStr = lower.format(formatter);
    var upperStr = upper.format(formatter);

    log.infof("Fetching route %s | %s", lowerStr, upperStr);

    //fetch route
    return this.routeClient.getRoutes(1000, lowerStr, upperStr, appConfig.hubId())
        .onItem().transform(GetRoutes::data)
        .onItem().transformToMulti(Multi.createFrom()::iterable)

        .onItem().transformToMultiAndMerge(routeInfo -> {
          //for each route fetch waypoint
          return this.routeClient.getWaypoints(routeInfo.id(), true, true)
              .onFailure(ClientWebApplicationException.class)
              .recoverWithItem(new RouteClientDto.GetWaypoints(List.of()))
              .map(RouteClientDto.GetWaypoints::data)
              .onItem().transformToMulti(Multi.createFrom()::iterable)

              .onItem()
              .transformToMultiAndMerge(waypoint -> {
                //for each waypoint fetch job
                return this.routeClient.getJob(waypoint.id())
                    .map(RouteClientDto.GetJob::data)
                    .onItem().transformToMulti(Multi.createFrom()::iterable)
                    .onItem().transformToMultiAndMerge(job -> {
                      var jobType = job.job_type().replace(" ", "_").toUpperCase();
                      return Multi.createFrom().item(Wrapper.builder()
                                                         .jobType(jobType)
                                                         .jobId(job.job_id())
                                                         .build());
                    });
              });
        })
        .collect().asList()
        .onItem().transform(wrappers -> wrappers.stream().collect(Collectors.groupingBy(Wrapper::getJobType)))

        .onItem().transformToMulti(groupedWrappers -> Multi.createFrom().iterable(groupedWrappers.entrySet()))
        .onItem().transformToUniAndMerge(groupedWrappers -> {
          if ("PICKUP_APPOINTMENT".equalsIgnoreCase(groupedWrappers.getKey())) {
            return this.routePickupService.handlePickUp(groupedWrappers, date);
          }
          return Uni.createFrom().nullItem();
        }).collect().asList().replaceWithVoid();

  }


  @Getter
  @Builder
  public static class Wrapper {

    String jobType;

    Long jobId;
  }
}

package com.mt.route;


import com.mt.route.RouteClientDto.PickUpRequest.PostPickUpRequest;
import io.quarkus.security.Authenticated;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.jbosslog.JBossLog;


@JBossLog
@Path("/api/v1/route-logs")
@RequiredArgsConstructor
@Produces(MediaType.APPLICATION_JSON)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class RouteApi {

  RouteService routeService;


  @POST
  @Authenticated
  public Uni<Response> fetchDailyRoutes (PostPickUpRequest postPickUpRequest) {

    return this.routeService.fetchRoutes(postPickUpRequest.date())
        .onItem().transform(x -> Response.accepted(x).build());
  }
}

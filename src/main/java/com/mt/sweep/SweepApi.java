package com.mt.sweep;


import io.quarkus.security.Authenticated;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.jbosslog.JBossLog;
import org.eclipse.microprofile.jwt.JsonWebToken;


@JBossLog
@Path("/api/v1/sweep")
@RequiredArgsConstructor
@Produces(MediaType.APPLICATION_JSON)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class SweepApi {

  SweepService sweepService;

  JsonWebToken jsonWebToken;


  @GET
  @Authenticated
  @Path("/{trackingId}")
  public Uni<Response> findOrderAndSweep (@PathParam("trackingId") String trackingId) {

    log.infof("[Parcel Sweep] trackingId=%s | sub=%s", trackingId, jsonWebToken.getClaim("sub"));

    return sweepService.parcelSweeperLive(trackingId)
        .onItem().ifNotNull()
        .transform(order -> Response.ok(order).build());
  }
}

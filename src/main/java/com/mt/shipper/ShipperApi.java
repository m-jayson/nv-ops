package com.mt.shipper;


import io.quarkus.security.Authenticated;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.jbosslog.JBossLog;


@JBossLog
@Path("/api/v1/shipper")
@RequiredArgsConstructor
@Produces(MediaType.APPLICATION_JSON)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ShipperApi {

  ShipperService shipperService;


  @PATCH
  @Authenticated
  public Uni<Response> findOrderAndSweep () {

    return shipperService.fetchShipper()
        .onItem().transform(order -> Response.accepted().build());
  }

}

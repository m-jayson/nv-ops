package com.mt.sweep;


import io.quarkus.security.Authenticated;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.jbosslog.JBossLog;

import java.time.LocalDate;
import java.util.Map;


@JBossLog
@Path("/api/v1/sweep")
@RequiredArgsConstructor
@Produces(MediaType.APPLICATION_JSON)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class SweepApi {

  SweepRedoService sweepRedoService;

  SweepExportService sweepExportService;

  SweepLiveService sweepLiveService;


  @GET
  @Authenticated
  @Path("/{trackingId}")
  public Uni<Response> findOrderAndSweep (@PathParam("trackingId") String trackingId) {

    log.infof("[Parcel Sweep] trackingId=%s", trackingId);

    return sweepLiveService.parcelSweeperLive(trackingId)
        .onItem().ifNotNull()
        .transform(order -> Response.ok(order).build());
  }


  @POST
  @Path("/export")
  @Authenticated
  @Produces("text/csv")
  public Uni<Response> exportCsv (
      @QueryParam("rts") boolean rts,
      @QueryParam("on_hold") boolean onHold,
      @QueryParam("other_hub") boolean otherHub,
      Map<String, LocalDate> parameter
  ) {

    var startDate = parameter.get("startDate");
    var endDate = parameter.get("endDate");

    log.infof(
        "[Parcel Sweep Export] startDate=%s | endDate=%s",
        startDate,
        endDate
    );

    var fileName = String.format(
        "sweeps-%s_%s.csv",
        startDate != null ? startDate.toString() : "start",
        endDate != null ? endDate.toString() : "end"
    );

    return sweepExportService.csvExport(startDate, endDate, rts, otherHub, onHold)
        .onItem().transformToMulti(Multi.createFrom()::iterable)
        .map(Sweep::getTrackingNumber)
        .collect().asList()
        .onItem().transform(trackingNumbers -> {
          var csvContent = String.join("\n", trackingNumbers);

          return Response.ok(csvContent)
              .header("Content-Disposition", "attachment; filename=\"" + fileName + "\"")
              .build();
        });
  }


  @POST
  @Path("/resweep")
  @Authenticated
  public Uni<Response> resweep (
      @QueryParam("rts") boolean rts,
      @QueryParam("on_hold") boolean onHold,
      @QueryParam("other_hub") boolean otherHub,
      Map<String, LocalDate> parameter
  ) {

    var startDate = parameter.get("startDate");
    var endDate = parameter.get("endDate");
    log.infof("[Parcel ReSweep] startDate=%s | endDate=%s | sub=%s", startDate, endDate);
    return sweepRedoService.reSweep(startDate, endDate, rts, onHold, otherHub)
        .onItem().transform(order -> Response.ok(order).build());
  }

}

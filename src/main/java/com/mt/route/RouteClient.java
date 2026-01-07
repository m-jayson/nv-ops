package com.mt.route;


import com.mt.common.AuthorizationBearerFilter;
import com.mt.route.RouteClientDto.*;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;


@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RegisterRestClient(configKey = "nv-client")
@RegisterProvider(AuthorizationBearerFilter.class)
public interface RouteClient {

  @GET
  @Path("/route-v2/routes")
  Uni<GetRoutes> getRoutes (
      @QueryParam("page_size") int pageSize,
      @QueryParam("start_time") String startTime,
      @QueryParam("end_time") String endTime,
      @QueryParam("hub_ids") long hubId
  );


  @GET
  @Path("/route-v2/routes/{routeId}/waypoints")
  Uni<GetWaypoints> getWaypoints (
      @PathParam("routeId") long routeId,
      @QueryParam("masked") boolean masked,
      @QueryParam("sortBySeqNo") boolean sortBySeqNo
  );


  @POST
  @Path("/pa-job-search/search")
  Uni<PickUpJobResponse> search (PickUpJobRequest request);


  @GET
  @Path("/route-v2/waypoints/{waypointId}/jobs")
  Uni<GetJob> getJob (@PathParam("waypointId") long waypointId);

  @POST
  @Path("/pa-job-search/search")
  Uni<PostPaJob> searchPaJob (PickupSearchRequest request);

  @GET
  @Path("/control/proofs")
  Uni<List<PickUpJobInfoResponse>> getPickUpProof (
      @QueryParam("jobId") long jobId,
      @QueryParam("jobType") String jobType
  );
}

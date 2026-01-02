package com.mt.sweep;


import com.mt.common.AuthorizationBearerFilter;
import io.quarkus.runtime.annotations.RegisterForReflection;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;
import java.util.Map;


@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RegisterRestClient(configKey = "nv-client")
@RegisterProvider(AuthorizationBearerFilter.class)
public interface SweepClientApi {

  @POST
  @Path("/sort/2.0/scans/sweeper")
  Uni<SweepResponse> sweep (SweepRequest request);


  @RegisterForReflection
  record SweepRequest(
      long hub_id,
      String scan,
      int task_id,
      long node_id,
      boolean to_return_dp_id,
      String hub_user
  ) {

  }

  @RegisterForReflection
  record SweepResponse(
      String granularStatus,
      Map<String, ParcelRoutingData> parcelRoutingData,
      boolean is_av_display_on,
      boolean is_aved,
      String av_status,
      List<String> order_3pl_tags
  ) {

    @RegisterForReflection
    public record ParcelRoutingData(
        Boolean toAlert,
        Long routeId,
        int priorityLevel,
        boolean onHold,
        long responsibleHubId,
        String zoneName,
        long zoneGlobalId,
        String rackSector,
        String nextNode,
        boolean rtsed,
        String responsibleHubName,
        List<String> tags,
        String avStatus,
        String avMode
    ) {

    }
  }
}

package com.mt.token;


import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.reactive.RestResponse;

import java.util.Map;


@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@RegisterRestClient(configKey = "nv-client")
public interface Opv2TokenClientApi {

  @GET
  @Path("/aaa/1.0/external/userscopes")
  Uni<RestResponse<Map<String, Object>>> testToken (
      @HeaderParam("Authorization") String authorization
  );
}

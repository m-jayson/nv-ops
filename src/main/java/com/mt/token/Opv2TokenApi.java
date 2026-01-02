package com.mt.token;


import io.quarkus.security.Authenticated;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PATCH;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.jbosslog.JBossLog;

import java.util.Map;


@JBossLog
@Path("/api/v1/opv2-token")
@RequiredArgsConstructor
@Produces(MediaType.APPLICATION_JSON)
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class Opv2TokenApi {

  Opv2TokenService opv2TokenService;


  @PATCH
  @Authenticated
  public Uni<Response> appendToken (Opv2TokenRequest tokenRequest) {

    return opv2TokenService.updateToken(tokenRequest.token())
        .onItem().transform(order -> Response.accepted().build());
  }


  @GET
  @Authenticated
  public Uni<Response> fetchToken () {
    //TODO:: test call to ninjavan api
    return opv2TokenService.fetchToken()
        .onItem().transform(token -> Response.ok(Map.of("X-NV-Token", token)).build());
  }


  record Opv2TokenRequest(String token) {

  }
}

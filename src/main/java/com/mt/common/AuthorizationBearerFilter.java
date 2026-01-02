package com.mt.common;


import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.jbosslog.JBossLog;


@JBossLog
@Provider
public class AuthorizationBearerFilter implements ClientRequestFilter {

  @Context
  HttpHeaders incomingHeaders;


  @Override
  public void filter (ClientRequestContext ctx) {

    var token = incomingHeaders.getHeaderString("X-NV-Token");
    if (token != null) {
      ctx.getHeaders().putSingle("Authorization", "Bearer " + token);
    }
  }
}


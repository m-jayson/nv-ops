package com.mt.common;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.resteasy.reactive.ClientWebApplicationException;

import java.util.Map;

@Provider
public class RestClientUnauthorizedMapper implements ExceptionMapper<ClientWebApplicationException> {

  @Override
  public Response toResponse(ClientWebApplicationException exception) {

    // If it's 401 Unauthorized
    if (exception.getResponse() != null && exception.getResponse().getStatus() == 401) {
      // Wrap as OpsException and return
      OpsException opsException = new OpsException(
          "Your request is not authorized. Please check API Ninjavan key / token.",
          "Check API Token",
          401
      );

      return Response.status(Response.Status.NOT_ACCEPTABLE)
          .entity(Map.of(
              "code", "UNAUTHORIZED",
              "message", opsException.getMessage(),
              "action", opsException.getAction(),
              "statusCode", opsException.getStatusCode()
          ))
          .build();
    }

    // Other HTTP errors
    int status = exception.getResponse() != null ? exception.getResponse().getStatus() : 500;
    OpsException opsException = new OpsException(
        exception.getMessage(),
        "HTTP request failed",
        status
    );

    return Response.status(status)
        .entity(Map.of(
            "code", "HTTP_ERROR",
            "message", opsException.getMessage(),
            "action", opsException.getAction(),
            "statusCode", opsException.getStatusCode()
        ))
        .build();
  }
}

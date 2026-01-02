package com.mt.common;


import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import java.util.Map;

@Provider
public class OpsExceptionMapper implements ExceptionMapper<OpsException> {

  @Override
  public Response toResponse(OpsException exception) {
    return Response.status(exception.getStatusCode())
        .entity(Map.of(
            "code", "OPS_ERROR",
            "message", exception.getMessage(),
            "action", exception.getAction(),
            "statusCode", exception.getStatusCode()
        ))
        .build();
  }
}
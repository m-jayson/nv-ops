package com.mt.common;


import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = true)
public class OpsException extends RuntimeException {

  private String action;

  private int statusCode;


  public OpsException (String message, String action, int statusCode) {

    super(message);
    this.action = action;
    this.statusCode = statusCode;
  }
}

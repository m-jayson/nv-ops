package com.mt.common;


import jakarta.enterprise.context.ApplicationScoped;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.eclipse.microprofile.jwt.JsonWebToken;


@ApplicationScoped
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CurrentUser {

  JsonWebToken jsonWebToken;


  public String get () {

    return jsonWebToken.getClaim("sub");
  }
}

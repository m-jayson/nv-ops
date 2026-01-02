package com.mt.token;


import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.Instant;
import java.util.UUID;


@ApplicationScoped
public class Opv2TokenRepository {

  @WithTransaction
  public Uni<Opv2Token> saveOrUpdate (String token) {

    var opv2Token = Opv2Token.builder()
        .bearerToken(token)
        .inUse(true)
        .build();
    opv2Token.setLastUpdatedOn(Instant.now());
    return opv2Token.persist();
  }


  @WithTransaction
  public Uni<Integer> updateOtherTokensFalse (UUID id) {

    return Opv2Token.update("inUse = false where id <> ?1 and inUse = true", id);
  }


  @WithTransaction
  public Uni<Opv2Token> findInUse () {

    return Opv2Token.find("inUse", true).firstResult();
  }
}

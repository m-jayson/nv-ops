package com.mt.route;


import io.smallrye.mutiny.GroupedMulti;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.LocalDate;


@ApplicationScoped
public class RouteRTSService {

  public Uni<Void> handleRts (GroupedMulti<String, RouteClientDto.Wrapper> group, LocalDate date) {

    return null;
  }
}

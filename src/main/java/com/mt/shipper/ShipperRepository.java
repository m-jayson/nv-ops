package com.mt.shipper;


import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.jbosslog.JBossLog;

import java.util.List;


@JBossLog
@ApplicationScoped
public class ShipperRepository implements PanacheRepository<Shipper> {

  @WithTransaction
  public Uni<Void> bulkSave (List<Shipper> shippers) {

    return persist(shippers);
  }

  @WithSession
  public Uni<List<ShipperList>> findAllShippersView () {

    return ShipperList.listAll();
  }
}

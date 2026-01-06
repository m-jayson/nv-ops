package com.mt.pickup;


import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.extern.jbosslog.JBossLog;

import java.time.LocalDate;
import java.util.List;


@JBossLog
@ApplicationScoped
public class PickupRepository implements PanacheRepository<Pickup> {

  @WithTransaction
  public Uni<Void> bulkUpdatePickUp (LocalDate date, List<Pickup> pickUps) {

    return deleteByPickUpDate(date)
        .onItem().transformToUni(v -> persist(pickUps))
        .onItem().invoke(v -> log.infof("Bulk Pickup refreshed for %s", date));
  }


  public Uni<Long> deleteByPickUpDate (LocalDate pickUpDate) {

    return delete("pickUpDate", pickUpDate)
        .onItem().invoke(aLong -> log.infof("Total records deleted %s on %s", aLong, pickUpDate));
  }

}

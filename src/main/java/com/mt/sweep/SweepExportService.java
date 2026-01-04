package com.mt.sweep;


import com.mt.order.OrderService;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.jbosslog.JBossLog;

import java.time.LocalDate;
import java.util.List;


@JBossLog
@ApplicationScoped
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SweepExportService {
  SweepConfig sweepConfig;

  OrderService orderService;

  SweepMapper sweepMapper;

  SweepRepository sweepRepository;

  public Uni<List<Sweep>> csvExport (LocalDate start, LocalDate end, boolean rts, boolean otherHub, boolean onHold) {

    return this.sweepRepository.findOrders(start, end, rts, onHold, otherHub, sweepConfig.hubId());
  }

}

package com.mt.sweep;


import io.smallrye.config.ConfigMapping;

import java.util.Optional;


@ConfigMapping(prefix = "nv.sweep")
public interface SweepConfig {

  long hubId ();


  long nodeId ();


  int taskId ();


  boolean toReturnDpId ();


  Optional<String> hubUser();

  String responsibleHubName();
}

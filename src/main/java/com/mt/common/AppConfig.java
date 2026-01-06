package com.mt.common;


import io.smallrye.config.ConfigMapping;


@ConfigMapping(prefix = "nv.app")
public interface AppConfig {

  long hubId ();

}

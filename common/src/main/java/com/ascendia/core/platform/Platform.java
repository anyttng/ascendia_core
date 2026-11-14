package com.ascendia.core.platform;

public interface Platform {
    boolean isClient();

    String modVersion();

    String loaderName();

    String loaderVersion();
}

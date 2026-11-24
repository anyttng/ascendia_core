package com.ascendiacore.platform;

public interface Platform {
    boolean isClient();

    String getModVersion();

    String getLoaderName();

    String getLoaderVersion();
}

package com.ascendiacore.gui.theme;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.ascendiacore.AscendiaCore;

public final class Themes {
    public static final String VANILLA = AscendiaCore.MODID + ":vanilla";
    public static final String BOOK = AscendiaCore.MODID + ":book";

    private static volatile Map<String, Theme> loaded = Map.of();
    private static final Set<String> REPORTED_MISSING = ConcurrentHashMap.newKeySet();

    private Themes() {
    }

    public static Theme get(String id) {
        Map<String, Theme> themes = loaded;
        Theme theme = themes.get(id);
        if (theme != null) {
            return theme;
        }
        Theme fallback = themes.get(VANILLA);
        if (fallback == null) {
            throw new IllegalStateException("Theme " + id + " was asked for, and no " + VANILLA + " is loaded to stand in");
        }
        if (REPORTED_MISSING.add(id)) {
            AscendiaCore.LOGGER.warn("Theme {} is not loaded, {} stands in for it", id, VANILLA);
        }
        return fallback;
    }

    public static void replaceAll(Map<String, Theme> themes) {
        loaded = Map.copyOf(themes);
        REPORTED_MISSING.clear();
    }
}

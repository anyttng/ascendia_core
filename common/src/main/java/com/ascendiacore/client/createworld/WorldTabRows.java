package com.ascendiacore.client.createworld;

import java.util.List;

public final class WorldTabRows {
    private static final RowRegistry REGISTRY = new RowRegistry();

    public static void register(WorldTabRow row) {
        REGISTRY.add(row);
    }

    static List<WorldTabRow> getRows() {
        return REGISTRY.getRows();
    }

    private WorldTabRows() {
    }
}

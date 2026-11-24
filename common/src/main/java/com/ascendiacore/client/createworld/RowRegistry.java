package com.ascendiacore.client.createworld;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

final class RowRegistry {
    private final List<WorldTabRow> rows = new CopyOnWriteArrayList<>();

    void add(WorldTabRow row) {
        rows.add(row);
    }

    List<WorldTabRow> getRows() {
        return List.copyOf(rows);
    }
}

package com.ascendiacore.gui.layout.model;

public enum Axis {
    HORIZONTAL,
    VERTICAL;

    public int along(int horizontal, int vertical) {
        return this == HORIZONTAL ? horizontal : vertical;
    }

    public int across(int horizontal, int vertical) {
        return this == HORIZONTAL ? vertical : horizontal;
    }

    public int horizontal(int along, int across) {
        return this == HORIZONTAL ? along : across;
    }

    public int vertical(int along, int across) {
        return this == HORIZONTAL ? across : along;
    }
}

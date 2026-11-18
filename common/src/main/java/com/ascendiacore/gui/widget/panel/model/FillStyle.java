package com.ascendiacore.gui.widget.panel.model;

public record FillStyle(int color, int ink, int mutedInk, int errorInk, boolean inkShadow) {
    public static final int NO_FILL = 0;

    public boolean drawn() {
        return color != NO_FILL;
    }
}

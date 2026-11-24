package com.ascendiacore.gui.widget.panel.model;

public record FillStyle(int color, int ink, int mutedInk, int errorInk, boolean hasInkShadow) {
    public static final int NO_FILL = 0;

    public boolean isDrawn() {
        return color != NO_FILL;
    }
}

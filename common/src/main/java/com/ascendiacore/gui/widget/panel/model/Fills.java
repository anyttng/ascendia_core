package com.ascendiacore.gui.widget.panel.model;

public record Fills(FillStyle parchment, FillStyle page, FillStyle dark) {
    public FillStyle getFill(FillKind kind) {
        return switch (kind) {
            case PARCHMENT -> parchment;
            case PAGE -> page;
            case DARK -> dark;
        };
    }
}

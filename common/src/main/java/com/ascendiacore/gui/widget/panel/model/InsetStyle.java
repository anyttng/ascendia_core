package com.ascendiacore.gui.widget.panel.model;

public record InsetStyle(FrameStyle light, FrameStyle dark, FrameStyle rim, Fills fills) {
    public FrameStyle getFrame(InsetFrame frame) {
        return switch (frame) {
            case LIGHT -> light;
            case DARK -> dark;
            case RIM -> rim;
        };
    }
}

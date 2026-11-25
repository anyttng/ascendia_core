package com.ascendiacore.gui.widget.progressbar.model;

public record ProgressBarStyle(String track, String trackLight, String fill) {
    public String getTrack(ProgressBarTrack kind) {
        return switch (kind) {
            case DARK -> track;
            case LIGHT -> trackLight;
        };
    }
}

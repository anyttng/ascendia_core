package com.ascendiacore.gui.widget.link.model;

import com.ascendiacore.gui.widget.panel.model.FillKind;

public record LinkStyle(LinkColors onLight, LinkColors onMid, LinkColors onDark) {
    public LinkColors getColors(FillKind fill) {
        return switch (fill) {
            case PARCHMENT -> onLight;
            case PAGE -> onMid;
            case DARK -> onDark;
        };
    }
}

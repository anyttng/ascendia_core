package com.ascendiacore.gui.widget.link.model;

import com.ascendiacore.gui.widget.panel.model.FillKind;

public record LinkStyle(LinkColors onLight, LinkColors onDark) {
    public LinkColors getColors(FillKind fill) {
        return fill == FillKind.DARK ? onDark : onLight;
    }
}

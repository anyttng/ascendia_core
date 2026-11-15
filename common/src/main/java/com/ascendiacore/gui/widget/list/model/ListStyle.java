package com.ascendiacore.gui.widget.list.model;

import com.ascendiacore.gui.widget.panel.model.FillStyle;

public record ListStyle(
        String row,
        String rowHover,
        String rowSelected,
        FillStyle framedText,
        FillStyle framedSelectedText,
        FillStyle buttonText,
        FillStyle buttonSelectedText) {
    public FillStyle text(RowStyle rowStyle, boolean selected) {
        return switch (rowStyle) {
            case FRAMED -> selected ? framedSelectedText : framedText;
            case BUTTON -> selected ? buttonSelectedText : buttonText;
        };
    }
}

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
    public FillStyle getText(RowStyle rowStyle, boolean isSelected) {
        return switch (rowStyle) {
            case FRAMED -> isSelected ? framedSelectedText : framedText;
            case BUTTON -> isSelected ? buttonSelectedText : buttonText;
        };
    }
}

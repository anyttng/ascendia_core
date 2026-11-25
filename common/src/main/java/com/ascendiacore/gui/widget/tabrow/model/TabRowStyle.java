package com.ascendiacore.gui.widget.tabrow.model;

public record TabRowStyle(
        String normal,
        String hover,
        String selected,
        String selectedHover,
        int labelColor,
        int hoverLabelColor,
        int selectedLabelColor,
        boolean hasLabelShadow) {
}

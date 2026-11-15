package com.ascendiacore.gui.widget.button.model;

public record ButtonStyle(
        String normal,
        String hover,
        String disabled,
        String selected,
        String selectedHover,
        int labelColor,
        int disabledLabelColor,
        int selectedLabelColor,
        boolean labelShadow) {
}

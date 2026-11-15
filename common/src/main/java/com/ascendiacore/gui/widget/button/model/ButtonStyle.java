package com.ascendiacore.gui.widget.button.model;

public record ButtonStyle(
        String normal,
        String hover,
        String disabled,
        int labelColor,
        int disabledLabelColor,
        boolean labelShadow) {
}

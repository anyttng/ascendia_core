package com.ascendiacore.gui.widget.textfield.model;

public record TextFieldStyle(
        String normal,
        String focused,
        String disabled,
        int textColor,
        int disabledTextColor,
        int placeholderColor,
        boolean textShadow) {
}

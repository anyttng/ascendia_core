package com.ascendiacore.gui.base.input.model;

public record Modifiers(boolean isShiftDown, boolean isControlDown, boolean isAltDown) {
    public static final Modifiers NONE = new Modifiers(false, false, false);
}

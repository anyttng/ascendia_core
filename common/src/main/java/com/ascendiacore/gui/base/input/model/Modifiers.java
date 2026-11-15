package com.ascendiacore.gui.base.input.model;

public record Modifiers(boolean shift, boolean control, boolean alt) {
    public static final Modifiers NONE = new Modifiers(false, false, false);
}

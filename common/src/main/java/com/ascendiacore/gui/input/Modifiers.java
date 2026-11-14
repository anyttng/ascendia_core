package com.ascendiacore.gui.input;

public record Modifiers(boolean shift, boolean control, boolean alt) {
    public static final Modifiers NONE = new Modifiers(false, false, false);
}

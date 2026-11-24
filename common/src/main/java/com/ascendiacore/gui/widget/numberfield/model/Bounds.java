package com.ascendiacore.gui.widget.numberfield.model;

public record Bounds(int min, int max) {
    public Bounds {
        if (min > max) {
            throw new IllegalArgumentException("Bounds need min <= max: " + min + ", " + max);
        }
    }

    public boolean isBelow(int value) {
        return value < min;
    }

    public boolean isAbove(int value) {
        return value > max;
    }

    public boolean isContaining(int value) {
        return !isBelow(value) && !isAbove(value);
    }
}

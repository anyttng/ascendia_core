package com.ascendiacore.gui.widget.numberfield.model;

public record Bounds(int min, int max) {
    public Bounds {
        if (min > max) {
            throw new IllegalArgumentException("Bounds need min <= max: " + min + ", " + max);
        }
    }

    public boolean below(int value) {
        return value < min;
    }

    public boolean above(int value) {
        return value > max;
    }

    public boolean contains(int value) {
        return !below(value) && !above(value);
    }
}

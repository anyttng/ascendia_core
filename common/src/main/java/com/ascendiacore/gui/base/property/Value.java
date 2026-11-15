package com.ascendiacore.gui.base.property;

public final class Value<T> implements Property<T> {
    private T value;

    private Value(T initial) {
        this.value = initial;
    }

    public static <T> Value<T> of(T initial) {
        return new Value<>(initial);
    }

    @Override
    public T get() {
        return value;
    }

    public void set(T value) {
        this.value = value;
    }
}

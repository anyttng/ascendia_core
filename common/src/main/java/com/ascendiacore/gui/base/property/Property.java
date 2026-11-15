package com.ascendiacore.gui.base.property;

@FunctionalInterface
public interface Property<T> {
    T get();

    static <T> Property<T> of(T value) {
        return () -> value;
    }
}

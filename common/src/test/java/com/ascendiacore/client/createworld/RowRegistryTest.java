package com.ascendiacore.client.createworld;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

class RowRegistryTest {
    @Test
    void anEmptyRegistryHoldsNoRow() {
        assertEquals(List.of(), new RowRegistry().getRows(), "nothing registered means nothing to insert");
    }

    @Test
    void rowsComeBackInRegistrationOrder() {
        RowRegistry registry = new RowRegistry();
        WorldTabRow first = state -> List.of();
        WorldTabRow second = state -> List.of();
        WorldTabRow third = state -> List.of();
        registry.add(first);
        registry.add(second);
        registry.add(third);

        assertEquals(List.of(first, second, third), registry.getRows(), "rows from several mods keep the order they came in");
    }
}

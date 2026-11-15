package com.ascendiacore.gui.base.property;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class PropertyTest {
    private static final int SIZE_IN_CHUNKS = 128;
    private static final int BLOCKS_PER_CHUNK = 16;

    @Test
    void fixedPropertyKeepsItsValue() {
        Property<String> shape = Property.of("Toroidal");
        assertEquals("Toroidal", shape.get());
    }

    @Test
    void valueReturnsWhatWasSetLast() {
        Value<Integer> size = Value.of(SIZE_IN_CHUNKS);
        assertEquals(SIZE_IN_CHUNKS, size.get());
        size.set(SIZE_IN_CHUNKS * 2);
        assertEquals(SIZE_IN_CHUNKS * 2, size.get());
    }

    @Test
    void ruleIsReadAgainAfterItsInputChanges() {
        Value<Integer> size = Value.of(SIZE_IN_CHUNKS);
        Property<Integer> blocks = () -> size.get() * BLOCKS_PER_CHUNK;
        assertEquals(SIZE_IN_CHUNKS * BLOCKS_PER_CHUNK, blocks.get());
        size.set(SIZE_IN_CHUNKS / 2);
        assertEquals(SIZE_IN_CHUNKS / 2 * BLOCKS_PER_CHUNK, blocks.get());
    }
}

package com.ascendiacore.gui.base.draw;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class MarqueeTest {
    private static final int ROOM_PIXELS = 100;
    private static final int OVERFLOW_PIXELS = 40;
    private static final int TEXT_WIDTH_PIXELS = ROOM_PIXELS + OVERFLOW_PIXELS;
    private static final long PAUSE_MILLIS = 1500;
    private static final long TRAVEL_MILLIS = 2000;
    private static final long CYCLE_MILLIS = 2 * (PAUSE_MILLIS + TRAVEL_MILLIS);

    @Test
    void textThatFitsDoesNotMove() {
        assertEquals(0, Marquee.offsetPixels(ROOM_PIXELS, ROOM_PIXELS, 1234));
        assertEquals(0, Marquee.offsetPixels(ROOM_PIXELS - 10, ROOM_PIXELS, 1234));
    }

    @Test
    void textRestsAtItsStartForThePause() {
        assertEquals(0, Marquee.offsetPixels(TEXT_WIDTH_PIXELS, ROOM_PIXELS, 0));
        assertEquals(0, Marquee.offsetPixels(TEXT_WIDTH_PIXELS, ROOM_PIXELS, PAUSE_MILLIS - 1));
    }

    @Test
    void textTravelsAtOneSpeedBothWays() {
        long quarterMillis = TRAVEL_MILLIS / 4;
        assertEquals(OVERFLOW_PIXELS / 4, Marquee.offsetPixels(TEXT_WIDTH_PIXELS, ROOM_PIXELS, PAUSE_MILLIS + quarterMillis));
        assertEquals(OVERFLOW_PIXELS / 2, Marquee.offsetPixels(TEXT_WIDTH_PIXELS, ROOM_PIXELS, PAUSE_MILLIS + 2 * quarterMillis));
        assertEquals(OVERFLOW_PIXELS * 3 / 4, Marquee.offsetPixels(TEXT_WIDTH_PIXELS, ROOM_PIXELS, PAUSE_MILLIS + 3 * quarterMillis));
        long wayBackMillis = 2 * PAUSE_MILLIS + TRAVEL_MILLIS;
        assertEquals(OVERFLOW_PIXELS * 3 / 4, Marquee.offsetPixels(TEXT_WIDTH_PIXELS, ROOM_PIXELS, wayBackMillis + quarterMillis));
        assertEquals(OVERFLOW_PIXELS / 4, Marquee.offsetPixels(TEXT_WIDTH_PIXELS, ROOM_PIXELS, wayBackMillis + 3 * quarterMillis));
    }

    @Test
    void textRestsAtItsEndForThePause() {
        long arrivedMillis = PAUSE_MILLIS + TRAVEL_MILLIS;
        assertEquals(OVERFLOW_PIXELS, Marquee.offsetPixels(TEXT_WIDTH_PIXELS, ROOM_PIXELS, arrivedMillis));
        assertEquals(OVERFLOW_PIXELS, Marquee.offsetPixels(TEXT_WIDTH_PIXELS, ROOM_PIXELS, arrivedMillis + PAUSE_MILLIS - 1));
    }

    @Test
    void cycleRepeats() {
        assertEquals(Marquee.offsetPixels(TEXT_WIDTH_PIXELS, ROOM_PIXELS, PAUSE_MILLIS + 700),
                Marquee.offsetPixels(TEXT_WIDTH_PIXELS, ROOM_PIXELS, CYCLE_MILLIS + PAUSE_MILLIS + 700));
    }

    @Test
    void offsetNeverLeavesTheOverflow() {
        for (long timeMillis = 0; timeMillis < 2 * CYCLE_MILLIS; timeMillis += 137) {
            int offsetPixels = Marquee.offsetPixels(TEXT_WIDTH_PIXELS, ROOM_PIXELS, timeMillis);
            assertTrue(offsetPixels >= 0 && offsetPixels <= OVERFLOW_PIXELS, "offset " + offsetPixels + " at " + timeMillis + " ms");
        }
    }
}

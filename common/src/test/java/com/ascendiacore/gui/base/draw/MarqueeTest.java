package com.ascendiacore.gui.base.draw;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class MarqueeTest {
    private static final int ROOM = 100;
    private static final int OVERFLOW = 40;
    private static final int TEXT_WIDTH = ROOM + OVERFLOW;
    private static final long PAUSE_MILLIS = 1500;
    private static final long TRAVEL_MILLIS = 2000;
    private static final long CYCLE_MILLIS = 2 * (PAUSE_MILLIS + TRAVEL_MILLIS);

    @Test
    void textThatFitsDoesNotMove() {
        assertEquals(0, Marquee.offset(ROOM, ROOM, 1234));
        assertEquals(0, Marquee.offset(ROOM - 10, ROOM, 1234));
    }

    @Test
    void textRestsAtItsStartForThePause() {
        assertEquals(0, Marquee.offset(TEXT_WIDTH, ROOM, 0));
        assertEquals(0, Marquee.offset(TEXT_WIDTH, ROOM, PAUSE_MILLIS - 1));
    }

    @Test
    void textTravelsAtOneSpeedBothWays() {
        long quarter = TRAVEL_MILLIS / 4;
        assertEquals(OVERFLOW / 4, Marquee.offset(TEXT_WIDTH, ROOM, PAUSE_MILLIS + quarter));
        assertEquals(OVERFLOW / 2, Marquee.offset(TEXT_WIDTH, ROOM, PAUSE_MILLIS + 2 * quarter));
        assertEquals(OVERFLOW * 3 / 4, Marquee.offset(TEXT_WIDTH, ROOM, PAUSE_MILLIS + 3 * quarter));
        long wayBack = 2 * PAUSE_MILLIS + TRAVEL_MILLIS;
        assertEquals(OVERFLOW * 3 / 4, Marquee.offset(TEXT_WIDTH, ROOM, wayBack + quarter));
        assertEquals(OVERFLOW / 4, Marquee.offset(TEXT_WIDTH, ROOM, wayBack + 3 * quarter));
    }

    @Test
    void textRestsAtItsEndForThePause() {
        long arrived = PAUSE_MILLIS + TRAVEL_MILLIS;
        assertEquals(OVERFLOW, Marquee.offset(TEXT_WIDTH, ROOM, arrived));
        assertEquals(OVERFLOW, Marquee.offset(TEXT_WIDTH, ROOM, arrived + PAUSE_MILLIS - 1));
    }

    @Test
    void cycleRepeats() {
        assertEquals(Marquee.offset(TEXT_WIDTH, ROOM, PAUSE_MILLIS + 700),
                Marquee.offset(TEXT_WIDTH, ROOM, CYCLE_MILLIS + PAUSE_MILLIS + 700));
    }

    @Test
    void offsetNeverLeavesTheOverflow() {
        for (long time = 0; time < 2 * CYCLE_MILLIS; time += 137) {
            int offset = Marquee.offset(TEXT_WIDTH, ROOM, time);
            assertTrue(offset >= 0 && offset <= OVERFLOW, "offset " + offset + " at " + time + " ms");
        }
    }
}

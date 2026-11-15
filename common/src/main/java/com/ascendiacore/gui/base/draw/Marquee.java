package com.ascendiacore.gui.base.draw;

public final class Marquee {
    private static final int PIXELS_PER_SECOND = 20;
    private static final long PAUSE_MILLIS = 1500;
    private static final long MILLIS_PER_SECOND = 1000;

    private Marquee() {
    }

    public static int offset(int textWidth, int room, long timeMillis) {
        int overflow = textWidth - room;
        if (overflow <= 0) {
            return 0;
        }
        long travelMillis = overflow * MILLIS_PER_SECOND / PIXELS_PER_SECOND;
        long phase = Math.floorMod(timeMillis, 2 * (PAUSE_MILLIS + travelMillis));
        if (phase < PAUSE_MILLIS) {
            return 0;
        }
        phase -= PAUSE_MILLIS;
        if (phase < travelMillis) {
            return travelled(phase);
        }
        phase -= travelMillis;
        if (phase < PAUSE_MILLIS) {
            return overflow;
        }
        return overflow - travelled(phase - PAUSE_MILLIS);
    }

    private static int travelled(long millis) {
        return (int) (millis * PIXELS_PER_SECOND / MILLIS_PER_SECOND);
    }
}

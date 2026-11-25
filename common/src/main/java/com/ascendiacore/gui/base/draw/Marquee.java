package com.ascendiacore.gui.base.draw;

public final class Marquee {
    private static final int PIXELS_PER_SECOND = 20;
    private static final long PAUSE_MILLIS = 1500;
    private static final long MILLIS_PER_SECOND = 1000;

    private Marquee() {
    }

    public static int offsetPixels(int textWidthPixels, int roomPixels, long timeMillis) {
        int overflowPixels = textWidthPixels - roomPixels;
        if (overflowPixels <= 0) {
            return 0;
        }
        long travelMillis = overflowPixels * MILLIS_PER_SECOND / PIXELS_PER_SECOND;
        long phaseMillis = Math.floorMod(timeMillis, 2 * (PAUSE_MILLIS + travelMillis));
        if (phaseMillis < PAUSE_MILLIS) {
            return 0;
        }
        phaseMillis -= PAUSE_MILLIS;
        if (phaseMillis < travelMillis) {
            return travelledPixels(phaseMillis);
        }
        phaseMillis -= travelMillis;
        if (phaseMillis < PAUSE_MILLIS) {
            return overflowPixels;
        }
        return overflowPixels - travelledPixels(phaseMillis - PAUSE_MILLIS);
    }

    private static int travelledPixels(long elapsedMillis) {
        return (int) (elapsedMillis * PIXELS_PER_SECOND / MILLIS_PER_SECOND);
    }
}

package com.ascendiacore.gui.widget.scrollbar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ScrollTest {
    private static final int STEP_PIXELS = 22;
    private static final int VIEW_PIXELS = 100;
    private static final int LONG_PIXELS = 250;
    private static final int SHORT_PIXELS = 60;

    @Test
    void contentLongerThanTheViewScrollsByTheDifference() {
        Scroll scroll = scroll(LONG_PIXELS);
        assertTrue(scroll.isScrollable());
        assertEquals(LONG_PIXELS - VIEW_PIXELS, scroll.getMaxOffsetPixels());
        assertEquals(0, scroll.getOffsetPixels());
    }

    @Test
    void contentThatFitsDoesNotScroll() {
        Scroll scroll = scroll(SHORT_PIXELS);
        assertFalse(scroll.isScrollable());
        assertEquals(0, scroll.getMaxOffsetPixels());
        scroll.scrollBy(STEP_PIXELS);
        assertEquals(0, scroll.getOffsetPixels());
    }

    @Test
    void offsetStaysWithinItsBounds() {
        Scroll scroll = scroll(LONG_PIXELS);
        scroll.scrollTo(-5);
        assertEquals(0, scroll.getOffsetPixels());
        scroll.scrollTo(LONG_PIXELS);
        assertEquals(LONG_PIXELS - VIEW_PIXELS, scroll.getOffsetPixels());
    }

    @Test
    void aTallerViewPullsTheOffsetBackInsideItsBounds() {
        Scroll scroll = scroll(LONG_PIXELS);
        scroll.scrollTo(LONG_PIXELS - VIEW_PIXELS);
        scroll.setViewPixels(LONG_PIXELS - 10);
        assertEquals(10, scroll.getOffsetPixels());
    }

    @Test
    void wheelDownMovesOneStepFurtherAndWheelUpOneStepBack() {
        Scroll scroll = scroll(LONG_PIXELS);
        scroll.wheel(-1);
        assertEquals(STEP_PIXELS, scroll.getOffsetPixels());
        scroll.wheel(-2);
        assertEquals(3 * STEP_PIXELS, scroll.getOffsetPixels());
        scroll.wheel(1);
        assertEquals(2 * STEP_PIXELS, scroll.getOffsetPixels());
    }

    private static Scroll scroll(int contentPixels) {
        Scroll scroll = new Scroll(STEP_PIXELS);
        scroll.setViewPixels(VIEW_PIXELS);
        scroll.setContentPixels(contentPixels);
        return scroll;
    }
}

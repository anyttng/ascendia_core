package com.ascendiacore.gui.widget.scrollbar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class ScrollTest {
    private static final int STEP = 22;
    private static final int VIEW = 100;
    private static final int LONG = 250;
    private static final int SHORT = 60;

    @Test
    void contentLongerThanTheViewScrollsByTheDifference() {
        Scroll scroll = scroll(LONG);
        assertTrue(scroll.scrollable());
        assertEquals(LONG - VIEW, scroll.maxOffset());
        assertEquals(0, scroll.offset());
    }

    @Test
    void contentThatFitsDoesNotScroll() {
        Scroll scroll = scroll(SHORT);
        assertFalse(scroll.scrollable());
        assertEquals(0, scroll.maxOffset());
        scroll.scrollBy(STEP);
        assertEquals(0, scroll.offset());
    }

    @Test
    void offsetStaysWithinItsBounds() {
        Scroll scroll = scroll(LONG);
        scroll.scrollTo(-5);
        assertEquals(0, scroll.offset());
        scroll.scrollTo(LONG);
        assertEquals(LONG - VIEW, scroll.offset());
    }

    @Test
    void aTallerViewPullsTheOffsetBackInsideItsBounds() {
        Scroll scroll = scroll(LONG);
        scroll.scrollTo(LONG - VIEW);
        scroll.setView(LONG - 10);
        assertEquals(10, scroll.offset());
    }

    @Test
    void wheelDownMovesOneStepFurtherAndWheelUpOneStepBack() {
        Scroll scroll = scroll(LONG);
        scroll.wheel(-1);
        assertEquals(STEP, scroll.offset());
        scroll.wheel(-2);
        assertEquals(3 * STEP, scroll.offset());
        scroll.wheel(1);
        assertEquals(2 * STEP, scroll.offset());
    }

    private static Scroll scroll(int content) {
        Scroll scroll = new Scroll(STEP);
        scroll.setView(VIEW);
        scroll.setContent(content);
        return scroll;
    }
}

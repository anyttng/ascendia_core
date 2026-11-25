package com.ascendiacore.gui.widget.scrollbar;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.ascendiacore.gui.base.draw.RecordingCanvas;
import com.ascendiacore.gui.base.input.PointerInput;
import com.ascendiacore.gui.base.input.WheelInput;
import com.ascendiacore.gui.base.input.model.Modifiers;
import com.ascendiacore.gui.base.input.model.PointerButton;
import com.ascendiacore.gui.theme.TestThemes;
import com.ascendiacore.gui.widget.scrollbar.model.ScrollbarStyle;

class ScrollbarTest {
    private static final ScrollbarStyle STYLE = TestThemes.BOOK.scrollbar();
    private static final int STEP_PIXELS = 22;
    private static final int LEFT = 10;
    private static final int TOP = 20;
    private static final int HEIGHT_PIXELS = 100;
    private static final int TRACK_PIXELS = HEIGHT_PIXELS - 2;
    private static final int VIEW_PIXELS = 100;
    private static final int OUTSIDE = -1;

    private final Scroll scroll = new Scroll(STEP_PIXELS);

    @Test
    void thumbIsTheShareOfTheContentInView() {
        Scrollbar bar = bar(2 * VIEW_PIXELS);
        assertEquals(TRACK_PIXELS / 2, bar.getThumbLengthPixels());
        assertEquals(TOP + 1, bar.getThumbTop());
    }

    @Test
    void thumbFillsTheTrackWhenEverythingFits() {
        Scrollbar bar = bar(VIEW_PIXELS / 2);
        assertEquals(TRACK_PIXELS, bar.getThumbLengthPixels());
        assertEquals(TOP + 1, bar.getThumbTop());
    }

    @Test
    void thumbNeverGetsShorterThanItsSprite() {
        Scrollbar bar = bar(100 * VIEW_PIXELS);
        assertEquals(14, bar.getThumbLengthPixels());
    }

    @Test
    void thumbReachesTheEndOfTheTrackAtTheLastOffset() {
        Scrollbar bar = bar(2 * VIEW_PIXELS);
        scroll.scrollTo(scroll.getMaxOffsetPixels());
        assertEquals(TOP + 1 + TRACK_PIXELS - bar.getThumbLengthPixels(), bar.getThumbTop());
    }

    @Test
    void draggingTheThumbMovesTheContent() {
        Scrollbar bar = bar(2 * VIEW_PIXELS);
        int freePixels = TRACK_PIXELS - bar.getThumbLengthPixels();
        int grabbed = bar.getThumbTop() + 5;
        assertTrue(bar.tryPressPointer(pointer(grabbed)));
        assertTrue(bar.tryDragPointer(pointer(grabbed + freePixels)));
        assertEquals(scroll.getMaxOffsetPixels(), scroll.getOffsetPixels());
        int halfwayPixels = freePixels / 2;
        assertTrue(bar.tryDragPointer(pointer(grabbed + halfwayPixels)));
        assertEquals(Math.round((float) halfwayPixels / freePixels * scroll.getMaxOffsetPixels()), scroll.getOffsetPixels());
    }

    @Test
    void dragPastEitherEndStopsAtThatEnd() {
        Scrollbar bar = bar(2 * VIEW_PIXELS);
        int grabbed = bar.getThumbTop() + 5;
        bar.tryPressPointer(pointer(grabbed));
        bar.tryDragPointer(pointer(grabbed + 10 * HEIGHT_PIXELS));
        assertEquals(scroll.getMaxOffsetPixels(), scroll.getOffsetPixels());
        bar.tryDragPointer(pointer(grabbed - 10 * HEIGHT_PIXELS));
        assertEquals(0, scroll.getOffsetPixels());
    }

    @Test
    void pressOnTheTrackDoesNotJumpButADragFromThereMovesTheContent() {
        Scrollbar bar = bar(2 * VIEW_PIXELS);
        int below = bar.getThumbTop() + bar.getThumbLengthPixels() + 10;
        assertTrue(bar.tryPressPointer(pointer(below)));
        assertEquals(0, scroll.getOffsetPixels());
        bar.tryDragPointer(pointer(below + TRACK_PIXELS - bar.getThumbLengthPixels()));
        assertEquals(scroll.getMaxOffsetPixels(), scroll.getOffsetPixels());
    }

    @Test
    void noDragWithoutAPressAndNoneAfterTheRelease() {
        Scrollbar bar = bar(2 * VIEW_PIXELS);
        assertFalse(bar.tryDragPointer(pointer(TOP + 50)));
        int grabbed = bar.getThumbTop() + 5;
        bar.tryPressPointer(pointer(grabbed));
        assertTrue(bar.tryReleasePointer(pointer(grabbed)));
        assertFalse(bar.tryDragPointer(pointer(grabbed + 20)));
        assertEquals(0, scroll.getOffsetPixels());
    }

    @Test
    void wheelOverTheBarScrolls() {
        Scrollbar bar = bar(2 * VIEW_PIXELS);
        assertTrue(bar.tryScrollWheel(new WheelInput(LEFT + 2, TOP + 50, -1)));
        assertEquals(STEP_PIXELS, scroll.getOffsetPixels());
    }

    @Test
    void thumbLightsUpUnderThePointerAndWhileDragged() {
        Scrollbar bar = bar(2 * VIEW_PIXELS);
        assertEquals(STYLE.thumbHover(), thumbSprite(bar, bar.getThumbTop() + 2));
        assertEquals(STYLE.thumb(), thumbSprite(bar, bar.getThumbTop() + bar.getThumbLengthPixels() + 5));
        assertEquals(STYLE.thumb(), thumbSprite(bar, OUTSIDE));
        bar.tryPressPointer(pointer(bar.getThumbTop() + 2));
        assertEquals(STYLE.thumbHover(), thumbSprite(bar, OUTSIDE));
    }

    @Test
    void trackCoversTheBarAndTheThumbSitsOnePixelInside() {
        Scrollbar bar = bar(2 * VIEW_PIXELS);
        RecordingCanvas canvas = new RecordingCanvas();
        bar.draw(canvas, OUTSIDE, OUTSIDE);
        assertEquals(new RecordingCanvas.Sprite(STYLE.track(), LEFT, TOP, Scrollbar.WIDTH_PIXELS, HEIGHT_PIXELS), canvas.sprites().get(0));
        assertEquals(new RecordingCanvas.Sprite(STYLE.thumb(), LEFT + 1, TOP + 1, Scrollbar.WIDTH_PIXELS - 2, bar.getThumbLengthPixels()),
                canvas.sprites().get(1));
    }

    private Scrollbar bar(int contentPixels) {
        scroll.setViewPixels(VIEW_PIXELS);
        scroll.setContentPixels(contentPixels);
        Scrollbar bar = new Scrollbar(STYLE, scroll);
        bar.setPosition(LEFT, TOP);
        bar.setSize(Scrollbar.WIDTH_PIXELS, HEIGHT_PIXELS);
        return bar;
    }

    private static String thumbSprite(Scrollbar bar, int pointerY) {
        RecordingCanvas canvas = new RecordingCanvas();
        bar.draw(canvas, pointerY == OUTSIDE ? OUTSIDE : LEFT + 2, pointerY);
        return canvas.sprites().get(1).sprite();
    }

    private static PointerInput pointer(double y) {
        return new PointerInput(LEFT + 2, y, PointerButton.LEFT, Modifiers.NONE);
    }
}

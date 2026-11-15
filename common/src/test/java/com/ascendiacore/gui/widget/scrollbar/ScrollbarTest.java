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
import com.ascendiacore.gui.theme.Themes;
import com.ascendiacore.gui.widget.scrollbar.model.ScrollbarStyle;

class ScrollbarTest {
    private static final ScrollbarStyle STYLE = Themes.BOOK.scrollbar();
    private static final int STEP = 22;
    private static final int LEFT = 10;
    private static final int TOP = 20;
    private static final int HEIGHT = 100;
    private static final int TRACK = HEIGHT - 2;
    private static final int VIEW = 100;
    private static final int OUTSIDE = -1;

    private final Scroll scroll = new Scroll(STEP);

    @Test
    void thumbIsTheShareOfTheContentInView() {
        Scrollbar bar = bar(2 * VIEW);
        assertEquals(TRACK / 2, bar.thumbLength());
        assertEquals(TOP + 1, bar.thumbTop());
    }

    @Test
    void thumbFillsTheTrackWhenEverythingFits() {
        Scrollbar bar = bar(VIEW / 2);
        assertEquals(TRACK, bar.thumbLength());
        assertEquals(TOP + 1, bar.thumbTop());
    }

    @Test
    void thumbNeverGetsShorterThanItsSprite() {
        Scrollbar bar = bar(100 * VIEW);
        assertEquals(14, bar.thumbLength());
    }

    @Test
    void thumbReachesTheEndOfTheTrackAtTheLastOffset() {
        Scrollbar bar = bar(2 * VIEW);
        scroll.scrollTo(scroll.maxOffset());
        assertEquals(TOP + 1 + TRACK - bar.thumbLength(), bar.thumbTop());
    }

    @Test
    void draggingTheThumbMovesTheContent() {
        Scrollbar bar = bar(2 * VIEW);
        int free = TRACK - bar.thumbLength();
        int grabbed = bar.thumbTop() + 5;
        assertTrue(bar.pointerPressed(pointer(grabbed)));
        assertTrue(bar.pointerDragged(pointer(grabbed + free)));
        assertEquals(scroll.maxOffset(), scroll.offset());
        int halfway = free / 2;
        assertTrue(bar.pointerDragged(pointer(grabbed + halfway)));
        assertEquals(Math.round((float) halfway / free * scroll.maxOffset()), scroll.offset());
    }

    @Test
    void dragPastEitherEndStopsAtThatEnd() {
        Scrollbar bar = bar(2 * VIEW);
        int grabbed = bar.thumbTop() + 5;
        bar.pointerPressed(pointer(grabbed));
        bar.pointerDragged(pointer(grabbed + 10 * HEIGHT));
        assertEquals(scroll.maxOffset(), scroll.offset());
        bar.pointerDragged(pointer(grabbed - 10 * HEIGHT));
        assertEquals(0, scroll.offset());
    }

    @Test
    void pressOnTheTrackDoesNotJumpButADragFromThereMovesTheContent() {
        Scrollbar bar = bar(2 * VIEW);
        int below = bar.thumbTop() + bar.thumbLength() + 10;
        assertTrue(bar.pointerPressed(pointer(below)));
        assertEquals(0, scroll.offset());
        bar.pointerDragged(pointer(below + TRACK - bar.thumbLength()));
        assertEquals(scroll.maxOffset(), scroll.offset());
    }

    @Test
    void noDragWithoutAPressAndNoneAfterTheRelease() {
        Scrollbar bar = bar(2 * VIEW);
        assertFalse(bar.pointerDragged(pointer(TOP + 50)));
        int grabbed = bar.thumbTop() + 5;
        bar.pointerPressed(pointer(grabbed));
        assertTrue(bar.pointerReleased(pointer(grabbed)));
        assertFalse(bar.pointerDragged(pointer(grabbed + 20)));
        assertEquals(0, scroll.offset());
    }

    @Test
    void wheelOverTheBarScrolls() {
        Scrollbar bar = bar(2 * VIEW);
        assertTrue(bar.wheelScrolled(new WheelInput(LEFT + 2, TOP + 50, -1)));
        assertEquals(STEP, scroll.offset());
    }

    @Test
    void thumbLightsUpUnderThePointerAndWhileDragged() {
        Scrollbar bar = bar(2 * VIEW);
        assertEquals(STYLE.thumbHover(), thumbSprite(bar, bar.thumbTop() + 2));
        assertEquals(STYLE.thumb(), thumbSprite(bar, bar.thumbTop() + bar.thumbLength() + 5));
        assertEquals(STYLE.thumb(), thumbSprite(bar, OUTSIDE));
        bar.pointerPressed(pointer(bar.thumbTop() + 2));
        assertEquals(STYLE.thumbHover(), thumbSprite(bar, OUTSIDE));
    }

    @Test
    void trackCoversTheBarAndTheThumbSitsOnePixelInside() {
        Scrollbar bar = bar(2 * VIEW);
        RecordingCanvas canvas = new RecordingCanvas();
        bar.draw(canvas, OUTSIDE, OUTSIDE);
        assertEquals(new RecordingCanvas.Sprite(STYLE.track(), LEFT, TOP, Scrollbar.WIDTH, HEIGHT), canvas.sprites().get(0));
        assertEquals(new RecordingCanvas.Sprite(STYLE.thumb(), LEFT + 1, TOP + 1, Scrollbar.WIDTH - 2, bar.thumbLength()),
                canvas.sprites().get(1));
    }

    private Scrollbar bar(int content) {
        scroll.setView(VIEW);
        scroll.setContent(content);
        Scrollbar bar = new Scrollbar(STYLE, scroll);
        bar.setPosition(LEFT, TOP);
        bar.setSize(Scrollbar.WIDTH, HEIGHT);
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

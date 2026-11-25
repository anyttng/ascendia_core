package com.ascendiacore.gui.widget.slider;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.ascendiacore.gui.base.draw.RecordingCanvas;
import com.ascendiacore.gui.base.geometry.Size;
import com.ascendiacore.gui.base.input.KeyInput;
import com.ascendiacore.gui.base.input.PointerInput;
import com.ascendiacore.gui.base.input.model.Key;
import com.ascendiacore.gui.base.input.model.Modifiers;
import com.ascendiacore.gui.base.input.model.PointerButton;
import com.ascendiacore.gui.base.property.Property;
import com.ascendiacore.gui.base.property.Value;
import com.ascendiacore.gui.widget.slider.model.SliderStyle;

class SliderTest {
    private static final SliderStyle STYLE = new SliderStyle("test:track", "test:handle", "test:handle_hover",
            "test:handle_disabled");
    private static final int LEFT = 40;
    private static final int TOP = 30;
    private static final int STEP_COUNT = 11;
    private static final int LAST_STEP = STEP_COUNT - 1;
    private static final int STEP_PIXELS = 10;
    private static final int WIDTH_PIXELS = LAST_STEP * STEP_PIXELS + Slider.HANDLE_WIDTH_PIXELS;
    private static final int ROW_HEIGHT_PIXELS = 20;
    private static final int TRACK_HEIGHT_PIXELS = 6;
    private static final int HANDLE_CENTER_PIXELS = Slider.HANDLE_WIDTH_PIXELS / 2;
    private static final int INSIDE_Y = TOP + 3;
    private static final int OUTSIDE = -1;

    private final Value<Integer> step = Value.of(0);
    private int clicks;

    @Test
    void handleStandsAtTheShareOfItsStepAndReachesBothEnds() {
        Slider slider = slider();
        assertEquals(LEFT, slider.getHandleX());
        step.set(3);
        assertEquals(LEFT + 3 * STEP_PIXELS, slider.getHandleX());
        step.set(LAST_STEP);
        assertEquals(LEFT + WIDTH_PIXELS - Slider.HANDLE_WIDTH_PIXELS, slider.getHandleX());
    }

    @Test
    void pressOnTheTrackPutsTheHandleOnTheNearestStepUnderThePointer() {
        Slider slider = slider();
        assertTrue(slider.tryPressPointer(pointer(centerOf(7) + 2)));
        assertEquals(7, step.get());
        assertTrue(slider.tryPressPointer(pointer(centerOf(2) - 4)));
        assertEquals(2, step.get());
    }

    @Test
    void dragMovesTheHandleAcrossTheStepsAndStopsAtEitherEnd() {
        Slider slider = slider();
        slider.tryPressPointer(pointer(centerOf(0)));
        assertTrue(slider.tryDragPointer(pointer(centerOf(4))));
        assertEquals(4, step.get());
        slider.tryDragPointer(pointer(LEFT + 10 * WIDTH_PIXELS));
        assertEquals(LAST_STEP, step.get());
        slider.tryDragPointer(pointer(LEFT - 10 * WIDTH_PIXELS));
        assertEquals(0, step.get());
    }

    @Test
    void noDragWithoutAPressAndNoneAfterTheRelease() {
        Slider slider = slider();
        assertFalse(slider.tryDragPointer(pointer(centerOf(5))));
        slider.tryPressPointer(pointer(centerOf(1)));
        assertTrue(slider.tryReleasePointer(pointer(centerOf(1))));
        assertFalse(slider.tryDragPointer(pointer(centerOf(5))));
        assertEquals(1, step.get());
    }

    @Test
    void clickSoundsOnceAtTheReleaseOfADragAndNotWithoutOne() {
        Slider slider = slider();
        slider.tryReleasePointer(pointer(centerOf(1)));
        assertEquals(0, clicks);
        slider.tryPressPointer(pointer(centerOf(1)));
        slider.tryDragPointer(pointer(centerOf(6)));
        assertEquals(0, clicks);
        slider.tryReleasePointer(pointer(centerOf(6)));
        assertEquals(1, clicks);
    }

    @Test
    void ownerHearsAStepOnlyWhenItChanges() {
        List<Integer> asked = new ArrayList<>();
        Slider slider = place(Slider.following(STYLE, Property.of(STEP_COUNT), step, asked::add));
        slider.tryPressPointer(pointer(centerOf(0)));
        slider.tryDragPointer(pointer(centerOf(0) + 2));
        assertEquals(List.of(), asked);
        slider.tryDragPointer(pointer(centerOf(3)));
        slider.tryDragPointer(pointer(centerOf(3) + 1));
        assertEquals(List.of(3, 3), asked);
        assertEquals(0, slider.getStep());
    }

    @Test
    void sliderFollowingARuleShowsTheRule() {
        Slider slider = place(Slider.following(STYLE, Property.of(STEP_COUNT), step, picked -> {
        }));
        step.set(6);
        assertEquals(6, slider.getStep());
        assertEquals(LEFT + 6 * STEP_PIXELS, slider.getHandleX());
    }

    @Test
    void arrowKeysMoveOneStepAndStopAtTheEndsAndOtherKeysDoNothing() {
        Slider slider = slider();
        assertTrue(slider.tryPressKey(key(Key.RIGHT)));
        assertTrue(slider.tryPressKey(key(Key.RIGHT)));
        assertEquals(2, step.get());
        assertTrue(slider.tryPressKey(key(Key.LEFT)));
        assertEquals(1, step.get());
        slider.tryPressKey(key(Key.LEFT));
        assertTrue(slider.tryPressKey(key(Key.LEFT)));
        assertEquals(0, step.get());
        step.set(LAST_STEP);
        assertTrue(slider.tryPressKey(key(Key.RIGHT)));
        assertEquals(LAST_STEP, step.get());
        assertFalse(slider.tryPressKey(key(Key.SPACE)));
        assertEquals(0, clicks);
    }

    @Test
    void disabledSliderTakesNeitherAPressNorADragNorAKey() {
        Slider slider = slider();
        slider.setEnabled(false);
        assertFalse(slider.tryPressPointer(pointer(centerOf(5))));
        assertFalse(slider.tryDragPointer(pointer(centerOf(5))));
        assertFalse(slider.tryPressKey(key(Key.RIGHT)));
        assertEquals(0, step.get());
    }

    @Test
    void handleLightsUpUnderThePointerWhileDraggedAndInFocusAndGoesPaleWhenDisabled() {
        Slider slider = slider();
        assertEquals(STYLE.handle(), handleSprite(slider, OUTSIDE));
        assertEquals(STYLE.handle(), handleSprite(slider, centerOf(5)));
        assertEquals(STYLE.handleHover(), handleSprite(slider, centerOf(0)));
        slider.tryPressPointer(pointer(centerOf(0)));
        assertEquals(STYLE.handleHover(), handleSprite(slider, OUTSIDE));
        slider.tryReleasePointer(pointer(centerOf(0)));
        assertEquals(STYLE.handle(), handleSprite(slider, OUTSIDE));
        slider.setFocused(true);
        assertEquals(STYLE.handleHover(), handleSprite(slider, OUTSIDE));
        slider.setEnabled(false);
        assertEquals(STYLE.handleDisabled(), handleSprite(slider, centerOf(0)));
    }

    @Test
    void trackRunsTheWholeWidthCenteredInTheRowWithTheHandleOverIt() {
        Slider slider = slider();
        step.set(3);
        RecordingCanvas canvas = new RecordingCanvas();
        slider.draw(canvas, OUTSIDE, OUTSIDE);
        assertEquals(List.of(
                new RecordingCanvas.Sprite(STYLE.track(), LEFT, TOP + (ROW_HEIGHT_PIXELS - TRACK_HEIGHT_PIXELS) / 2, WIDTH_PIXELS,
                        TRACK_HEIGHT_PIXELS),
                new RecordingCanvas.Sprite(STYLE.handle(), LEFT + 3 * STEP_PIXELS,
                        TOP + (ROW_HEIGHT_PIXELS - Slider.HEIGHT_PIXELS) / 2, Slider.HANDLE_WIDTH_PIXELS, Slider.HEIGHT_PIXELS)),
                canvas.sprites());
    }

    @Test
    void sliderOfOneStepKeepsItsHandleAtTheStartAndReportsNothing() {
        List<Integer> asked = new ArrayList<>();
        Slider slider = place(Slider.following(STYLE, Property.of(1), step, asked::add));
        slider.tryPressPointer(pointer(centerOf(8)));
        slider.tryPressKey(key(Key.RIGHT));
        assertEquals(LEFT, slider.getHandleX());
        assertEquals(List.of(), asked);
    }

    @Test
    void stepPastTheLastOneShowsAsTheLast() {
        Slider slider = slider();
        step.set(LAST_STEP + 5);
        assertEquals(LAST_STEP, slider.getStep());
    }

    @Test
    void naturalSizeIsItsOwnWidthAndTheHandleHeight() {
        assertEquals(new Size(WIDTH_PIXELS, Slider.HEIGHT_PIXELS), slider().naturalSize(new RecordingCanvas()));
    }

    private static int centerOf(int step) {
        return LEFT + step * STEP_PIXELS + HANDLE_CENTER_PIXELS;
    }

    private static String handleSprite(Slider slider, int pointerX) {
        RecordingCanvas canvas = new RecordingCanvas();
        slider.draw(canvas, pointerX, pointerX == OUTSIDE ? OUTSIDE : INSIDE_Y);
        return canvas.sprites().get(1).sprite();
    }

    private Slider slider() {
        return place(Slider.of(STYLE, STEP_COUNT, step));
    }

    private Slider place(Slider slider) {
        slider.setPosition(LEFT, TOP);
        slider.setSize(WIDTH_PIXELS, ROW_HEIGHT_PIXELS);
        slider.attach(() -> clicks++);
        return slider;
    }

    private static PointerInput pointer(double x) {
        return new PointerInput(x, INSIDE_Y, PointerButton.LEFT, Modifiers.NONE);
    }

    private static KeyInput key(Key key) {
        return new KeyInput(key, Modifiers.NONE);
    }
}

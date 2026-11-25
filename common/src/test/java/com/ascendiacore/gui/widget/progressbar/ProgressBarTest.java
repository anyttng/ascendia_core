package com.ascendiacore.gui.widget.progressbar;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.ascendiacore.gui.base.draw.RecordingCanvas;
import com.ascendiacore.gui.base.geometry.Size;
import com.ascendiacore.gui.base.property.Value;
import com.ascendiacore.gui.widget.progressbar.model.ProgressBarStyle;
import com.ascendiacore.gui.widget.progressbar.model.ProgressBarTrack;

class ProgressBarTest {
    private static final ProgressBarStyle STYLE = new ProgressBarStyle("test:track", "test:track_light", "test:fill");
    private static final int LEFT = 40;
    private static final int TOP = 30;
    private static final int WIDTH_PIXELS = 104;
    private static final int ROOM_PIXELS = WIDTH_PIXELS - 2 * ProgressBar.BORDER_PIXELS;
    private static final int FILL_HEIGHT_PIXELS = ProgressBar.HEIGHT_PIXELS - 2 * ProgressBar.BORDER_PIXELS;
    private static final int ROW_HEIGHT_PIXELS = 20;
    private static final int BAR_TOP = TOP + (ROW_HEIGHT_PIXELS - ProgressBar.HEIGHT_PIXELS) / 2;
    private static final int RED = 0xFFC0503A;
    private static final int GREEN = 0xFF5AA84A;
    private static final int OUTSIDE = -1;

    private final Value<Float> fraction = Value.of(0.0f);
    private final Value<Integer> color = Value.of(RED);

    @Test
    void fillIsEmptyAtZeroTakesItsShareInTheMiddleAndTheWholeRoomAtOne() {
        ProgressBar bar = bar(ProgressBarTrack.DARK);
        assertEquals(0, bar.getFillWidthPixels());
        fraction.set(0.4f);
        assertEquals(Math.round(0.4f * ROOM_PIXELS), bar.getFillWidthPixels());
        fraction.set(1.0f);
        assertEquals(ROOM_PIXELS, bar.getFillWidthPixels());
    }

    @Test
    void fractionPastEitherEndStopsAtThatEnd() {
        ProgressBar bar = bar(ProgressBarTrack.DARK);
        fraction.set(-0.5f);
        assertEquals(0, bar.getFillWidthPixels());
        fraction.set(1.5f);
        assertEquals(ROOM_PIXELS, bar.getFillWidthPixels());
    }

    @Test
    void trackRunsTheWholeWidthCenteredInTheRowWithTheFillInsideItsBorder() {
        ProgressBar bar = bar(ProgressBarTrack.DARK);
        fraction.set(0.5f);
        RecordingCanvas canvas = new RecordingCanvas();
        bar.draw(canvas, OUTSIDE, OUTSIDE);
        assertEquals(List.of(
                new RecordingCanvas.Sprite(STYLE.track(), LEFT, BAR_TOP, WIDTH_PIXELS, ProgressBar.HEIGHT_PIXELS),
                new RecordingCanvas.Sprite(STYLE.fill(), LEFT + ProgressBar.BORDER_PIXELS, BAR_TOP + ProgressBar.BORDER_PIXELS,
                        ROOM_PIXELS / 2, FILL_HEIGHT_PIXELS)),
                canvas.sprites());
    }

    @Test
    void emptyBarDrawsItsTrackAlone() {
        RecordingCanvas canvas = new RecordingCanvas();
        bar(ProgressBarTrack.DARK).draw(canvas, OUTSIDE, OUTSIDE);
        assertEquals(List.of(new RecordingCanvas.Sprite(STYLE.track(), LEFT, BAR_TOP, WIDTH_PIXELS, ProgressBar.HEIGHT_PIXELS)),
                canvas.sprites());
    }

    @Test
    void lightTrackDrawsTheLightSprite() {
        RecordingCanvas canvas = new RecordingCanvas();
        bar(ProgressBarTrack.LIGHT).draw(canvas, OUTSIDE, OUTSIDE);
        assertEquals(STYLE.trackLight(), canvas.sprites().get(0).sprite());
    }

    @Test
    void fillAloneIsTintedAndFollowsTheColorRule() {
        ProgressBar bar = bar(ProgressBarTrack.DARK);
        fraction.set(1.0f);
        RecordingCanvas canvas = new RecordingCanvas();
        bar.draw(canvas, OUTSIDE, OUTSIDE);
        color.set(GREEN);
        bar.draw(canvas, OUTSIDE, OUTSIDE);
        assertEquals(List.of(new RecordingCanvas.Tint(STYLE.fill(), RED), new RecordingCanvas.Tint(STYLE.fill(), GREEN)),
                canvas.tints());
    }

    @Test
    void barOfFixedValuesShowsThem() {
        ProgressBar bar = place(ProgressBar.of(STYLE, ProgressBarTrack.DARK, 0.25f, RED));
        assertEquals(ROOM_PIXELS / 4, bar.getFillWidthPixels());
    }

    @Test
    void barNarrowerThanItsBordersHasNoFill() {
        ProgressBar bar = new ProgressBar(STYLE, ProgressBarTrack.DARK, fraction, color);
        bar.setPosition(LEFT, TOP);
        bar.setSize(ProgressBar.BORDER_PIXELS, ROW_HEIGHT_PIXELS);
        fraction.set(1.0f);
        assertEquals(0, bar.getFillWidthPixels());
    }

    @Test
    void naturalSizeIsItsOwnWidthAndTheTrackHeight() {
        assertEquals(new Size(WIDTH_PIXELS, ProgressBar.HEIGHT_PIXELS),
                bar(ProgressBarTrack.DARK).naturalSize(new RecordingCanvas()));
    }

    private ProgressBar bar(ProgressBarTrack track) {
        return place(new ProgressBar(STYLE, track, fraction, color));
    }

    private static ProgressBar place(ProgressBar bar) {
        bar.setPosition(LEFT, TOP);
        bar.setSize(WIDTH_PIXELS, ROW_HEIGHT_PIXELS);
        return bar;
    }
}

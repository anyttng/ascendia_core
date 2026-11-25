package com.ascendiacore.gui.widget.progressbar;

import com.ascendiacore.gui.base.component.GuiComponent;
import com.ascendiacore.gui.base.draw.Canvas;
import com.ascendiacore.gui.base.draw.TextMetrics;
import com.ascendiacore.gui.base.geometry.Size;
import com.ascendiacore.gui.base.property.Property;
import com.ascendiacore.gui.widget.progressbar.model.ProgressBarStyle;
import com.ascendiacore.gui.widget.progressbar.model.ProgressBarTrack;

public final class ProgressBar extends GuiComponent {
    public static final int HEIGHT_PIXELS = 8;
    public static final int BORDER_PIXELS = 2;

    private final ProgressBarStyle style;
    private final ProgressBarTrack track;
    private final Property<Float> fraction;
    private final Property<Integer> color;

    public ProgressBar(ProgressBarStyle style, ProgressBarTrack track, Property<Float> fraction, Property<Integer> color) {
        this.style = style;
        this.track = track;
        this.fraction = fraction;
        this.color = color;
    }

    public static ProgressBar of(ProgressBarStyle style, ProgressBarTrack track, float fraction, int color) {
        return new ProgressBar(style, track, Property.of(fraction), Property.of(color));
    }

    public int getFillWidthPixels() {
        int roomPixels = Math.max(0, getWidthPixels() - 2 * BORDER_PIXELS);
        return Math.round(Math.clamp(fraction.get(), 0.0f, 1.0f) * roomPixels);
    }

    @Override
    public Size naturalSize(TextMetrics metrics) {
        return new Size(getWidthPixels(), HEIGHT_PIXELS);
    }

    @Override
    protected void drawContent(Canvas canvas) {
        int top = getY() + (getHeightPixels() - HEIGHT_PIXELS) / 2;
        canvas.sprite(style.getTrack(track), getX(), top, getWidthPixels(), HEIGHT_PIXELS);
        int fillWidthPixels = getFillWidthPixels();
        if (fillWidthPixels > 0) {
            canvas.sprite(style.fill(), getX() + BORDER_PIXELS, top + BORDER_PIXELS, fillWidthPixels,
                    HEIGHT_PIXELS - 2 * BORDER_PIXELS, color.get());
        }
    }
}

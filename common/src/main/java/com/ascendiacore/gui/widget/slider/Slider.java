package com.ascendiacore.gui.widget.slider;

import java.util.function.Consumer;

import com.ascendiacore.gui.base.component.GuiComponent;
import com.ascendiacore.gui.base.draw.Canvas;
import com.ascendiacore.gui.base.draw.TextMetrics;
import com.ascendiacore.gui.base.geometry.Size;
import com.ascendiacore.gui.base.input.KeyInput;
import com.ascendiacore.gui.base.input.PointerInput;
import com.ascendiacore.gui.base.input.model.Key;
import com.ascendiacore.gui.base.property.Property;
import com.ascendiacore.gui.base.property.Value;
import com.ascendiacore.gui.widget.slider.model.SliderStyle;

public final class Slider extends GuiComponent {
    public static final int HEIGHT_PIXELS = 12;
    public static final int HANDLE_WIDTH_PIXELS = 8;
    private static final int TRACK_HEIGHT_PIXELS = 6;

    private final SliderStyle style;
    private final Property<Integer> stepCount;
    private final Property<Integer> step;
    private final Consumer<Integer> onChange;
    private boolean isDragging;

    private Slider(SliderStyle style, Property<Integer> stepCount, Property<Integer> step, Consumer<Integer> onChange) {
        this.style = style;
        this.stepCount = stepCount;
        this.step = step;
        this.onChange = onChange;
    }

    public static Slider of(SliderStyle style, int stepCount, Value<Integer> step) {
        return new Slider(style, Property.of(stepCount), step, step::set);
    }

    public static Slider following(SliderStyle style, Property<Integer> stepCount, Property<Integer> step,
            Consumer<Integer> onChange) {
        return new Slider(style, stepCount, step, onChange);
    }

    public int getStep() {
        return Math.clamp(step.get(), 0, getLastStep());
    }

    public int getHandleX() {
        int lastStep = getLastStep();
        return lastStep == 0 ? getX() : getX() + Math.round((float) getStep() * getTravelPixels() / lastStep);
    }

    @Override
    public boolean isFollowingDrag() {
        return true;
    }

    @Override
    public Size naturalSize(TextMetrics metrics) {
        return new Size(getWidthPixels(), HEIGHT_PIXELS);
    }

    @Override
    protected void drawContent(Canvas canvas) {
        canvas.sprite(style.track(), getX(), getY() + (getHeightPixels() - TRACK_HEIGHT_PIXELS) / 2, getWidthPixels(),
                TRACK_HEIGHT_PIXELS);
        canvas.sprite(getHandleSprite(), getHandleX(), getY() + (getHeightPixels() - HEIGHT_PIXELS) / 2, HANDLE_WIDTH_PIXELS,
                HEIGHT_PIXELS);
    }

    @Override
    protected void onPress(PointerInput input) {
        isDragging = true;
        moveHandleTo(input.x());
    }

    @Override
    protected boolean tryHandleDrag(PointerInput input) {
        if (!isDragging) {
            return false;
        }
        moveHandleTo(input.x());
        return true;
    }

    @Override
    protected void onRelease(PointerInput input) {
        if (isDragging) {
            isDragging = false;
            playClick();
        }
    }

    @Override
    protected boolean tryHandleKey(KeyInput input) {
        if (input.key() != Key.LEFT && input.key() != Key.RIGHT) {
            return false;
        }
        changeStepTo(getStep() + (input.key() == Key.LEFT ? -1 : 1));
        return true;
    }

    private void moveHandleTo(double pointerX) {
        int travelPixels = getTravelPixels();
        if (travelPixels <= 0) {
            return;
        }
        double share = (pointerX - getX() - HANDLE_WIDTH_PIXELS / 2.0) / travelPixels;
        changeStepTo((int) Math.round(Math.clamp(share, 0.0, 1.0) * getLastStep()));
    }

    private void changeStepTo(int asked) {
        int next = Math.clamp(asked, 0, getLastStep());
        if (next != getStep()) {
            onChange.accept(next);
        }
    }

    private String getHandleSprite() {
        if (!isEnabled()) {
            return style.handleDisabled();
        }
        return isDragging || isFocused() || isPointerOnHandle() ? style.handleHover() : style.handle();
    }

    private boolean isPointerOnHandle() {
        int handleX = getHandleX();
        return isHovered() && getPointerX() >= handleX && getPointerX() < handleX + HANDLE_WIDTH_PIXELS;
    }

    private int getLastStep() {
        return Math.max(0, stepCount.get() - 1);
    }

    private int getTravelPixels() {
        return getWidthPixels() - HANDLE_WIDTH_PIXELS;
    }
}

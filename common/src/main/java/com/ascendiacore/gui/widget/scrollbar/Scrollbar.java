package com.ascendiacore.gui.widget.scrollbar;

import com.ascendiacore.gui.base.component.GuiComponent;
import com.ascendiacore.gui.base.draw.Canvas;
import com.ascendiacore.gui.base.draw.TextMetrics;
import com.ascendiacore.gui.base.geometry.Size;
import com.ascendiacore.gui.base.input.PointerInput;
import com.ascendiacore.gui.base.input.WheelInput;
import com.ascendiacore.gui.widget.scrollbar.model.ScrollbarStyle;

public final class Scrollbar extends GuiComponent {
    public static final int WIDTH_PIXELS = 8;
    private static final int THUMB_INSET_PIXELS = 1;
    private static final int MIN_THUMB_LENGTH_PIXELS = 14;

    private final ScrollbarStyle style;
    private final Scroll scroll;
    private boolean isDragging;
    private double grabPixels;

    public Scrollbar(ScrollbarStyle style, Scroll scroll) {
        this.style = style;
        this.scroll = scroll;
    }

    public int getThumbLengthPixels() {
        int trackPixels = getTrackLengthPixels();
        if (!scroll.isScrollable()) {
            return trackPixels;
        }
        int sharePixels = Math.round((float) trackPixels * scroll.getViewPixels() / scroll.getContentPixels());
        return Math.min(trackPixels, Math.max(MIN_THUMB_LENGTH_PIXELS, sharePixels));
    }

    public int getThumbTop() {
        int maxPixels = scroll.getMaxOffsetPixels();
        int travelPixels = maxPixels == 0 ? 0 : Math.round((float) getFreeTrackPixels() * scroll.getOffsetPixels() / maxPixels);
        return getY() + THUMB_INSET_PIXELS + travelPixels;
    }

    @Override
    public boolean isFollowingDrag() {
        return true;
    }

    @Override
    public Size naturalSize(TextMetrics metrics) {
        return new Size(WIDTH_PIXELS, getHeightPixels());
    }

    @Override
    protected void drawContent(Canvas canvas) {
        canvas.sprite(style.track(), getX(), getY(), getWidthPixels(), getHeightPixels());
        String thumb = isDragging || isPointerOnThumb() ? style.thumbHover() : style.thumb();
        canvas.sprite(thumb, getX() + THUMB_INSET_PIXELS, getThumbTop(), getWidthPixels() - 2 * THUMB_INSET_PIXELS, getThumbLengthPixels());
    }

    @Override
    protected void onPress(PointerInput input) {
        grabPixels = input.y() - getThumbTop();
        isDragging = true;
    }

    @Override
    protected boolean tryHandleDrag(PointerInput input) {
        if (!isDragging) {
            return false;
        }
        int freePixels = getFreeTrackPixels();
        if (freePixels > 0) {
            double share = (input.y() - grabPixels - getY() - THUMB_INSET_PIXELS) / freePixels;
            scroll.scrollTo((int) Math.round(Math.clamp(share, 0.0, 1.0) * scroll.getMaxOffsetPixels()));
        }
        return true;
    }

    @Override
    protected void onRelease(PointerInput input) {
        isDragging = false;
    }

    @Override
    protected boolean tryHandleWheel(WheelInput input) {
        scroll.wheel(input.notches());
        return true;
    }

    private boolean isPointerOnThumb() {
        int thumbTop = getThumbTop();
        return isHovered() && getPointerY() >= thumbTop && getPointerY() < thumbTop + getThumbLengthPixels();
    }

    private int getTrackLengthPixels() {
        return Math.max(0, getHeightPixels() - 2 * THUMB_INSET_PIXELS);
    }

    private int getFreeTrackPixels() {
        return getTrackLengthPixels() - getThumbLengthPixels();
    }
}

package com.ascendiacore.gui.widget.scrollbar;

import com.ascendiacore.gui.base.component.GuiComponent;
import com.ascendiacore.gui.base.draw.Canvas;
import com.ascendiacore.gui.base.draw.TextMetrics;
import com.ascendiacore.gui.base.geometry.Size;
import com.ascendiacore.gui.base.input.PointerInput;
import com.ascendiacore.gui.base.input.WheelInput;
import com.ascendiacore.gui.widget.scrollbar.model.ScrollbarStyle;

public final class Scrollbar extends GuiComponent {
    public static final int WIDTH = 8;
    private static final int THUMB_INSET = 1;
    private static final int MIN_THUMB_LENGTH = 14;

    private final ScrollbarStyle style;
    private final Scroll scroll;
    private boolean dragging;
    private double grab;

    public Scrollbar(ScrollbarStyle style, Scroll scroll) {
        this.style = style;
        this.scroll = scroll;
    }

    public int thumbLength() {
        int track = trackLength();
        if (!scroll.scrollable()) {
            return track;
        }
        int share = Math.round((float) track * scroll.view() / scroll.content());
        return Math.min(track, Math.max(MIN_THUMB_LENGTH, share));
    }

    public int thumbTop() {
        int max = scroll.maxOffset();
        int travel = max == 0 ? 0 : Math.round((float) freeTrack() * scroll.offset() / max);
        return y() + THUMB_INSET + travel;
    }

    @Override
    public boolean followsDrag() {
        return true;
    }

    @Override
    public Size naturalSize(TextMetrics metrics) {
        return new Size(WIDTH, height());
    }

    @Override
    protected void drawContent(Canvas canvas) {
        canvas.sprite(style.track(), x(), y(), width(), height());
        String thumb = dragging || pointerOnThumb() ? style.thumbHover() : style.thumb();
        canvas.sprite(thumb, x() + THUMB_INSET, thumbTop(), width() - 2 * THUMB_INSET, thumbLength());
    }

    @Override
    protected void onPress(PointerInput input) {
        grab = input.y() - thumbTop();
        dragging = true;
    }

    @Override
    protected boolean onDrag(PointerInput input) {
        if (!dragging) {
            return false;
        }
        int free = freeTrack();
        if (free > 0) {
            double share = (input.y() - grab - y() - THUMB_INSET) / free;
            scroll.scrollTo((int) Math.round(Math.clamp(share, 0.0, 1.0) * scroll.maxOffset()));
        }
        return true;
    }

    @Override
    protected void onRelease(PointerInput input) {
        dragging = false;
    }

    @Override
    protected boolean onWheel(WheelInput input) {
        scroll.wheel(input.amount());
        return true;
    }

    private boolean pointerOnThumb() {
        int top = thumbTop();
        return hovered() && pointerY() >= top && pointerY() < top + thumbLength();
    }

    private int trackLength() {
        return Math.max(0, height() - 2 * THUMB_INSET);
    }

    private int freeTrack() {
        return trackLength() - thumbLength();
    }
}

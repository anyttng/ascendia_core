package com.ascendiacore.gui.widget.tabrow;

import java.util.OptionalInt;

import com.ascendiacore.gui.base.component.GuiComponent;
import com.ascendiacore.gui.base.draw.Canvas;
import com.ascendiacore.gui.base.draw.Icon;
import com.ascendiacore.gui.base.draw.Marquee;
import com.ascendiacore.gui.base.draw.TextMetrics;
import com.ascendiacore.gui.base.geometry.Size;
import com.ascendiacore.gui.base.input.KeyInput;
import com.ascendiacore.gui.base.input.PointerInput;
import com.ascendiacore.gui.base.property.Property;
import com.ascendiacore.gui.widget.tabrow.model.TabContent;
import com.ascendiacore.gui.widget.tabrow.model.TabRowStyle;

import net.minecraft.network.chat.Component;

final class Tab extends GuiComponent {
    private static final int TEXT_ROW_HEIGHT_PIXELS = 16;
    private static final int ICON_ROW_HEIGHT_PIXELS = 24;
    private static final int SELECTED_RISE_PIXELS = 2;
    private static final int LINE_HEIGHT_PIXELS = 9;
    private static final int TEXT_ROW_LABEL_TOP_PIXELS = 4;
    private static final int SIDE_PADDING_PIXELS = 6;
    private static final int PART_GAP_PIXELS = 2;
    private static final String NARRATION_KEY = "gui.narrate.tab";

    private final TabRowStyle style;
    private final TabContent content;
    private final OptionalInt fixedWidthPixels;
    private final boolean isInIconRow;
    private final Property<Boolean> selected;
    private final Runnable onPick;

    Tab(TabRowStyle style, TabContent content, OptionalInt fixedWidthPixels, boolean isInIconRow, Property<Boolean> selected,
            Runnable onPick) {
        this.style = style;
        this.content = content;
        this.fixedWidthPixels = fixedWidthPixels;
        this.isInIconRow = isInIconRow;
        this.selected = selected;
        this.onPick = onPick;
    }

    @Override
    public Component getNarration() {
        return Component.translatable(NARRATION_KEY, content.name());
    }

    @Override
    public Size naturalSize(TextMetrics metrics) {
        int rowHeightPixels = isInIconRow ? ICON_ROW_HEIGHT_PIXELS : TEXT_ROW_HEIGHT_PIXELS;
        return new Size(fixedWidthPixels.orElse(contentWidthPixels(metrics) + 2 * SIDE_PADDING_PIXELS),
                rowHeightPixels + (selected.get() ? SELECTED_RISE_PIXELS : 0));
    }

    @Override
    protected void drawContent(Canvas canvas) {
        canvas.sprite(getSprite(), getX(), getY(), getWidthPixels(), getHeightPixels());
        int roomPixels = getWidthPixels() - 2 * SIDE_PADDING_PIXELS;
        int contentPixels = contentWidthPixels(canvas);
        if (contentPixels <= roomPixels) {
            drawParts(canvas, getX() + (getWidthPixels() - contentPixels) / 2);
            return;
        }
        int roomLeft = getX() + SIDE_PADDING_PIXELS;
        canvas.pushClip(roomLeft, getY(), roomPixels, getHeightPixels());
        drawParts(canvas, roomLeft - Marquee.offsetPixels(contentPixels, roomPixels, canvas.getFrameTimeMillis()));
        canvas.popClip();
    }

    @Override
    protected void onPress(PointerInput input) {
        pick();
    }

    @Override
    protected boolean tryHandleKey(KeyInput input) {
        if (!input.isSelection()) {
            return false;
        }
        pick();
        return true;
    }

    private int contentWidthPixels(TextMetrics metrics) {
        int iconCount = content.icons().size();
        int partCount = iconCount + (content.isNameDrawn() ? 1 : 0);
        int textPixels = content.isNameDrawn() ? metrics.textWidthPixels(content.name()) : 0;
        return iconCount * Icon.SIZE_PIXELS + textPixels + Math.max(partCount - 1, 0) * PART_GAP_PIXELS;
    }

    private void drawParts(Canvas canvas, int left) {
        drawLabel(canvas, drawIcons(canvas, left));
    }

    private int drawIcons(Canvas canvas, int left) {
        int iconTop = getY() + (getVisibleHeightPixels() - Icon.SIZE_PIXELS) / 2;
        int nextLeft = left;
        for (Icon icon : content.icons()) {
            icon.draw(canvas, nextLeft, iconTop);
            nextLeft += Icon.SIZE_PIXELS + PART_GAP_PIXELS;
        }
        return nextLeft;
    }

    private void drawLabel(Canvas canvas, int left) {
        if (!content.isNameDrawn()) {
            return;
        }
        int labelTopPixels = isInIconRow ? (getVisibleHeightPixels() - LINE_HEIGHT_PIXELS) / 2 + 1 : TEXT_ROW_LABEL_TOP_PIXELS;
        canvas.text(content.name(), left, getY() + labelTopPixels, getLabelColor(), style.hasLabelShadow());
    }

    private int getVisibleHeightPixels() {
        return getHeightPixels() - TabRow.PANEL_OVERLAP_PIXELS;
    }

    private void pick() {
        if (selected.get()) {
            return;
        }
        playClick();
        onPick.run();
    }

    private boolean isHighlighted() {
        return isHovered() || isFocused();
    }

    private int getLabelColor() {
        if (selected.get()) {
            return style.selectedLabelColor();
        }
        return isHighlighted() ? style.hoverLabelColor() : style.labelColor();
    }

    private String getSprite() {
        if (selected.get()) {
            return isHighlighted() ? style.selectedHover() : style.selected();
        }
        return isHighlighted() ? style.hover() : style.normal();
    }
}

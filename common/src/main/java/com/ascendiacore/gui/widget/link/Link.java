package com.ascendiacore.gui.widget.link;

import org.jspecify.annotations.Nullable;

import com.ascendiacore.gui.base.component.GuiComponent;
import com.ascendiacore.gui.base.draw.Canvas;
import com.ascendiacore.gui.base.draw.TextMetrics;
import com.ascendiacore.gui.base.geometry.Size;
import com.ascendiacore.gui.base.input.KeyInput;
import com.ascendiacore.gui.base.input.PointerInput;
import com.ascendiacore.gui.base.property.Property;
import com.ascendiacore.gui.widget.link.model.LinkColors;

import net.minecraft.network.chat.Component;

public final class Link extends GuiComponent {
    private static final int LINE_HEIGHT_PIXELS = 9;
    private static final int UNDERLINE_HEIGHT_PIXELS = 1;
    private static final int ICON_SIZE_PIXELS = 8;
    private static final int ICON_GAP_PIXELS = 2;

    private final LinkColors colors;
    private final @Nullable String iconSprite;
    private final Runnable action;
    private Property<Component> label;

    private Link(LinkColors colors, @Nullable String iconSprite, Component label, Runnable action) {
        this.colors = colors;
        this.iconSprite = iconSprite;
        this.label = Property.of(label);
        this.action = action;
    }

    public static Link text(LinkColors colors, Component label, Runnable action) {
        return new Link(colors, null, label, action);
    }

    public static Link iconAndText(LinkColors colors, String iconSprite, Component label, Runnable action) {
        return new Link(colors, iconSprite, label, action);
    }

    public void setLabel(Property<Component> label) {
        this.label = label;
    }

    @Override
    public Component getNarration() {
        return label.get();
    }

    @Override
    public Size naturalSize(TextMetrics metrics) {
        return new Size(getIconRoomPixels() + metrics.textWidthPixels(label.get()), LINE_HEIGHT_PIXELS + UNDERLINE_HEIGHT_PIXELS);
    }

    @Override
    protected void drawContent(Canvas canvas) {
        Component text = label.get();
        int color = getColor();
        int textLeft = getX() + getIconRoomPixels();
        if (iconSprite != null) {
            canvas.sprite(iconSprite, getX(), getY() + (LINE_HEIGHT_PIXELS - ICON_SIZE_PIXELS) / 2, ICON_SIZE_PIXELS, ICON_SIZE_PIXELS);
        }
        canvas.text(text, textLeft, getY(), color, false);
        if (isHighlighted()) {
            canvas.fill(textLeft, getY() + LINE_HEIGHT_PIXELS, canvas.textWidthPixels(text), UNDERLINE_HEIGHT_PIXELS, color);
        }
    }

    @Override
    protected void onPress(PointerInput input) {
        activate();
    }

    @Override
    protected boolean tryHandleKey(KeyInput input) {
        if (!input.isSelection()) {
            return false;
        }
        activate();
        return true;
    }

    private int getColor() {
        if (!isEnabled()) {
            return colors.disabled();
        }
        return isHighlighted() ? colors.hover() : colors.normal();
    }

    private boolean isHighlighted() {
        return isEnabled() && (isHovered() || isFocused());
    }

    private int getIconRoomPixels() {
        return iconSprite == null ? 0 : ICON_SIZE_PIXELS + ICON_GAP_PIXELS;
    }

    private void activate() {
        playClick();
        action.run();
    }
}

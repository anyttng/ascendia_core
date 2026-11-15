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
    private static final int LINE_HEIGHT = 9;
    private static final int UNDERLINE_HEIGHT = 1;
    private static final int ICON_SIZE = 8;
    private static final int ICON_GAP = 2;

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
    public Component narration() {
        return label.get();
    }

    @Override
    public Size naturalSize(TextMetrics metrics) {
        return new Size(iconRoom() + metrics.textWidth(label.get()), LINE_HEIGHT + UNDERLINE_HEIGHT);
    }

    @Override
    protected void drawContent(Canvas canvas) {
        Component text = label.get();
        int color = color();
        int textLeft = x() + iconRoom();
        if (iconSprite != null) {
            canvas.sprite(iconSprite, x(), y() + (LINE_HEIGHT - ICON_SIZE) / 2, ICON_SIZE, ICON_SIZE);
        }
        canvas.text(text, textLeft, y(), color, false);
        if (highlighted()) {
            canvas.fill(textLeft, y() + LINE_HEIGHT, canvas.textWidth(text), UNDERLINE_HEIGHT, color);
        }
    }

    @Override
    protected void onPress(PointerInput input) {
        activate();
    }

    @Override
    protected boolean onKey(KeyInput input) {
        if (!input.isSelection()) {
            return false;
        }
        activate();
        return true;
    }

    private int color() {
        if (!enabled()) {
            return colors.disabled();
        }
        return highlighted() ? colors.hover() : colors.normal();
    }

    private boolean highlighted() {
        return enabled() && (hovered() || focused());
    }

    private int iconRoom() {
        return iconSprite == null ? 0 : ICON_SIZE + ICON_GAP;
    }

    private void activate() {
        playClick();
        action.run();
    }
}

package com.ascendiacore.gui.widget.button;

import org.jspecify.annotations.Nullable;

import com.ascendiacore.gui.base.component.GuiComponent;
import com.ascendiacore.gui.base.draw.Canvas;
import com.ascendiacore.gui.base.draw.Icon;
import com.ascendiacore.gui.base.draw.TextMetrics;
import com.ascendiacore.gui.base.geometry.Size;
import com.ascendiacore.gui.base.input.KeyInput;
import com.ascendiacore.gui.base.input.PointerInput;
import com.ascendiacore.gui.base.input.model.Key;
import com.ascendiacore.gui.base.property.Property;
import com.ascendiacore.gui.widget.button.model.ButtonSprites;

import net.minecraft.network.chat.Component;

public final class Button extends GuiComponent {
    private static final int LINE_HEIGHT = 9;
    private static final int ICON_GAP = 4;
    private static final int SIDE_PADDING = 8;
    private static final int HEIGHT = 20;
    private static final int TEXT_COLOR = 0xFFFFFFFF;
    private static final int DISABLED_TEXT_COLOR = 0xFFA0A0A0;

    private final ButtonSprites sprites;
    private final boolean labelDrawn;
    private final @Nullable Icon icon;
    private final Runnable action;
    private Property<Component> label;

    private Button(ButtonSprites sprites, Component label, boolean labelDrawn, @Nullable Icon icon, Runnable action) {
        this.sprites = sprites;
        this.label = Property.of(label);
        this.labelDrawn = labelDrawn;
        this.icon = icon;
        this.action = action;
    }

    public static Button text(ButtonSprites sprites, Component label, Runnable action) {
        return new Button(sprites, label, true, null, action);
    }

    public static Button icon(ButtonSprites sprites, Icon icon, Component name, Runnable action) {
        return new Button(sprites, name, false, icon, action);
    }

    public static Button iconAndText(ButtonSprites sprites, Icon icon, Component label, Runnable action) {
        return new Button(sprites, label, true, icon, action);
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
        int textWidth = labelDrawn ? metrics.textWidth(label.get()) : 0;
        return new Size(contentWidth(textWidth) + 2 * SIDE_PADDING, HEIGHT);
    }

    @Override
    protected void drawContent(Canvas canvas) {
        Component text = label.get();
        canvas.sprite(sprite(), x(), y(), width(), height());
        int textWidth = labelDrawn ? canvas.textWidth(text) : 0;
        int left = x() + (width() - contentWidth(textWidth)) / 2;
        if (icon != null) {
            icon.draw(canvas, left, y() + (height() - Icon.SIZE) / 2);
            left += Icon.SIZE + ICON_GAP;
        }
        if (labelDrawn) {
            canvas.text(text, left, y() + (height() - LINE_HEIGHT) / 2 + 1, enabled() ? TEXT_COLOR : DISABLED_TEXT_COLOR, true);
        }
    }

    @Override
    protected void onPress(PointerInput input) {
        activate();
    }

    @Override
    protected boolean onKey(KeyInput input) {
        if (input.key() != Key.ENTER && input.key() != Key.NUMPAD_ENTER && input.key() != Key.SPACE) {
            return false;
        }
        activate();
        return true;
    }

    private void activate() {
        playClick();
        action.run();
    }

    private String sprite() {
        if (!enabled()) {
            return sprites.disabled();
        }
        return hovered() || focused() ? sprites.hover() : sprites.normal();
    }

    private int contentWidth(int textWidth) {
        if (icon == null) {
            return textWidth;
        }
        return labelDrawn ? Icon.SIZE + ICON_GAP + textWidth : Icon.SIZE;
    }
}

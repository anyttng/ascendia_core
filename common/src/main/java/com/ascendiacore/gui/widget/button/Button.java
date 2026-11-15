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
import com.ascendiacore.gui.widget.button.model.ButtonStyle;

import net.minecraft.network.chat.Component;

public final class Button extends GuiComponent {
    private static final int LINE_HEIGHT = 9;
    private static final int ICON_GAP = 4;
    private static final int SIDE_PADDING = 8;
    private static final int HEIGHT = 20;

    private final ButtonStyle style;
    private final boolean labelDrawn;
    private final @Nullable Icon icon;
    private final Runnable action;
    private Property<Component> label;

    private Button(ButtonStyle style, Component label, boolean labelDrawn, @Nullable Icon icon, Runnable action) {
        this.style = style;
        this.label = Property.of(label);
        this.labelDrawn = labelDrawn;
        this.icon = icon;
        this.action = action;
    }

    public static Button text(ButtonStyle style, Component label, Runnable action) {
        return new Button(style, label, true, null, action);
    }

    public static Button icon(ButtonStyle style, Icon icon, Component name, Runnable action) {
        return new Button(style, name, false, icon, action);
    }

    public static Button iconAndText(ButtonStyle style, Icon icon, Component label, Runnable action) {
        return new Button(style, label, true, icon, action);
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
            canvas.text(text, left, y() + (height() - LINE_HEIGHT) / 2 + 1, enabled() ? style.labelColor() : style.disabledLabelColor(), style.labelShadow());
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
            return style.disabled();
        }
        return hovered() || focused() ? style.hover() : style.normal();
    }

    private int contentWidth(int textWidth) {
        if (icon == null) {
            return textWidth;
        }
        return labelDrawn ? Icon.SIZE + ICON_GAP + textWidth : Icon.SIZE;
    }
}

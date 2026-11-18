package com.ascendiacore.gui.widget.button;

import java.util.function.Consumer;

import org.jspecify.annotations.Nullable;

import com.ascendiacore.gui.base.component.GuiComponent;
import com.ascendiacore.gui.base.draw.Canvas;
import com.ascendiacore.gui.base.draw.Icon;
import com.ascendiacore.gui.base.draw.Marquee;
import com.ascendiacore.gui.base.draw.TextMetrics;
import com.ascendiacore.gui.base.geometry.Size;
import com.ascendiacore.gui.base.input.KeyInput;
import com.ascendiacore.gui.base.input.PointerInput;
import com.ascendiacore.gui.base.input.model.Modifiers;
import com.ascendiacore.gui.base.property.Property;
import com.ascendiacore.gui.widget.button.model.ButtonStyle;

import net.minecraft.network.chat.Component;

public final class Button extends GuiComponent {
    private static final int LINE_HEIGHT = 9;
    private static final int ICON_GAP = 4;
    private static final int SIDE_PADDING = 8;
    private static final int LABEL_MARGIN = 5;
    private static final int HEIGHT = 20;

    private final ButtonStyle style;
    private final boolean labelDrawn;
    private final @Nullable Icon icon;
    private final Consumer<Modifiers> action;
    private Property<Component> label;
    private Property<Boolean> selected = Property.of(false);

    private Button(ButtonStyle style, Component label, boolean labelDrawn, @Nullable Icon icon,
            Consumer<Modifiers> action) {
        this.style = style;
        this.label = Property.of(label);
        this.labelDrawn = labelDrawn;
        this.icon = icon;
        this.action = action;
    }

    public static Button text(ButtonStyle style, Component label, Runnable action) {
        return text(style, label, modifiers -> action.run());
    }

    public static Button text(ButtonStyle style, Component label, Consumer<Modifiers> action) {
        return new Button(style, label, true, null, action);
    }

    public static Button icon(ButtonStyle style, Icon icon, Component name, Runnable action) {
        return new Button(style, name, false, icon, modifiers -> action.run());
    }

    public static Button iconAndText(ButtonStyle style, Icon icon, Component label, Runnable action) {
        return new Button(style, label, true, icon, modifiers -> action.run());
    }

    public void setLabel(Property<Component> label) {
        this.label = label;
    }

    public boolean selected() {
        return selected.get();
    }

    public void setSelected(boolean selected) {
        setSelected(Property.of(selected));
    }

    public void setSelected(Property<Boolean> selected) {
        this.selected = selected;
    }

    @Override
    public Component narration() {
        return label.get();
    }

    @Override
    public Size naturalSize(TextMetrics metrics) {
        if (!labelDrawn) {
            return new Size(HEIGHT, HEIGHT);
        }
        return new Size(iconRoom() + metrics.textWidth(label.get()) + 2 * SIDE_PADDING, HEIGHT);
    }

    @Override
    protected void drawContent(Canvas canvas) {
        canvas.sprite(sprite(), x(), y(), width(), height());
        if (!labelDrawn) {
            drawIcon(canvas, x() + (width() - Icon.SIZE) / 2);
            return;
        }
        Component text = label.get();
        int textWidth = canvas.textWidth(text);
        int room = width() - 2 * LABEL_MARGIN - iconRoom();
        if (textWidth > room) {
            drawScrolling(canvas, text, textWidth, room);
        } else {
            drawCentered(canvas, text, textWidth);
        }
    }

    @Override
    protected void onPress(PointerInput input) {
        activate(input.modifiers());
    }

    @Override
    protected boolean onKey(KeyInput input) {
        if (!input.isSelection()) {
            return false;
        }
        activate(input.modifiers());
        return true;
    }

    private void drawCentered(Canvas canvas, Component text, int textWidth) {
        int left = x() + (width() - iconRoom() - textWidth) / 2;
        drawIcon(canvas, left);
        drawLabel(canvas, text, left + iconRoom());
    }

    private void drawScrolling(Canvas canvas, Component text, int textWidth, int room) {
        int left = x() + LABEL_MARGIN;
        drawIcon(canvas, left);
        int textLeft = left + iconRoom();
        canvas.pushClip(textLeft, y(), room, height());
        drawLabel(canvas, text, textLeft - Marquee.offset(textWidth, room, canvas.frameTimeMillis()));
        canvas.popClip();
    }

    private void drawIcon(Canvas canvas, int left) {
        if (icon != null) {
            icon.draw(canvas, left, y() + (height() - Icon.SIZE) / 2);
        }
    }

    private void drawLabel(Canvas canvas, Component text, int left) {
        canvas.text(text, left, y() + (height() - LINE_HEIGHT) / 2 + 1, labelColor(), style.labelShadow());
    }

    private int labelColor() {
        if (!enabled()) {
            return style.disabledLabelColor();
        }
        return selected() ? style.selectedLabelColor() : style.labelColor();
    }

    private int iconRoom() {
        return icon == null ? 0 : Icon.SIZE + ICON_GAP;
    }

    private void activate(Modifiers modifiers) {
        playClick();
        action.accept(modifiers);
    }

    private String sprite() {
        if (!enabled()) {
            return style.disabled();
        }
        boolean highlighted = hovered() || focused();
        if (selected()) {
            return highlighted ? style.selectedHover() : style.selected();
        }
        return highlighted ? style.hover() : style.normal();
    }
}

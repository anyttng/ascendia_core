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
    private static final int LINE_HEIGHT_PIXELS = 9;
    private static final int ICON_GAP_PIXELS = 4;
    private static final int SIDE_PADDING_PIXELS = 8;
    private static final int LABEL_MARGIN_PIXELS = 5;
    private static final int HEIGHT_PIXELS = 20;

    private final ButtonStyle style;
    private final boolean isLabelDrawn;
    private final @Nullable Icon icon;
    private final Consumer<Modifiers> action;
    private Property<Component> label;
    private Property<Boolean> selected = Property.of(false);

    private Button(ButtonStyle style, Component label, boolean isLabelDrawn, @Nullable Icon icon,
            Consumer<Modifiers> action) {
        this.style = style;
        this.label = Property.of(label);
        this.isLabelDrawn = isLabelDrawn;
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

    public boolean isSelected() {
        return selected.get();
    }

    public void setSelected(boolean isSelected) {
        setSelected(Property.of(isSelected));
    }

    public void setSelected(Property<Boolean> selected) {
        this.selected = selected;
    }

    @Override
    public Component getNarration() {
        return label.get();
    }

    @Override
    public Size naturalSize(TextMetrics metrics) {
        if (!isLabelDrawn) {
            return new Size(HEIGHT_PIXELS, HEIGHT_PIXELS);
        }
        return new Size(getIconRoomPixels() + metrics.textWidthPixels(label.get()) + 2 * SIDE_PADDING_PIXELS, HEIGHT_PIXELS);
    }

    @Override
    protected void drawContent(Canvas canvas) {
        canvas.sprite(getSprite(), getX(), getY(), getWidthPixels(), getHeightPixels());
        if (!isLabelDrawn) {
            drawIcon(canvas, getX() + (getWidthPixels() - Icon.SIZE_PIXELS) / 2);
            return;
        }
        Component text = label.get();
        int textWidthPixels = canvas.textWidthPixels(text);
        int roomPixels = getWidthPixels() - 2 * LABEL_MARGIN_PIXELS - getIconRoomPixels();
        if (textWidthPixels > roomPixels) {
            drawScrolling(canvas, text, textWidthPixels, roomPixels);
        } else {
            drawCentered(canvas, text, textWidthPixels);
        }
    }

    @Override
    protected void onPress(PointerInput input) {
        activate(input.modifiers());
    }

    @Override
    protected boolean tryHandleKey(KeyInput input) {
        if (!input.isSelection()) {
            return false;
        }
        activate(input.modifiers());
        return true;
    }

    private void drawCentered(Canvas canvas, Component text, int textWidthPixels) {
        int left = getX() + (getWidthPixels() - getIconRoomPixels() - textWidthPixels) / 2;
        drawIcon(canvas, left);
        drawLabel(canvas, text, left + getIconRoomPixels());
    }

    private void drawScrolling(Canvas canvas, Component text, int textWidthPixels, int roomPixels) {
        int left = getX() + LABEL_MARGIN_PIXELS;
        drawIcon(canvas, left);
        int textLeft = left + getIconRoomPixels();
        canvas.pushClip(textLeft, getY(), roomPixels, getHeightPixels());
        drawLabel(canvas, text, textLeft - Marquee.offsetPixels(textWidthPixels, roomPixels, canvas.getFrameTimeMillis()));
        canvas.popClip();
    }

    private void drawIcon(Canvas canvas, int left) {
        if (icon != null) {
            icon.draw(canvas, left, getY() + (getHeightPixels() - Icon.SIZE_PIXELS) / 2);
        }
    }

    private void drawLabel(Canvas canvas, Component text, int left) {
        canvas.text(text, left, getY() + (getHeightPixels() - LINE_HEIGHT_PIXELS) / 2 + 1, getLabelColor(), style.hasLabelShadow());
    }

    private int getLabelColor() {
        if (!isEnabled()) {
            return style.disabledLabelColor();
        }
        return isSelected() ? style.selectedLabelColor() : style.labelColor();
    }

    private int getIconRoomPixels() {
        return icon == null ? 0 : Icon.SIZE_PIXELS + ICON_GAP_PIXELS;
    }

    private void activate(Modifiers modifiers) {
        playClick();
        action.accept(modifiers);
    }

    private String getSprite() {
        if (!isEnabled()) {
            return style.disabled();
        }
        boolean isHighlighted = isHovered() || isFocused();
        if (isSelected()) {
            return isHighlighted ? style.selectedHover() : style.selected();
        }
        return isHighlighted ? style.hover() : style.normal();
    }
}

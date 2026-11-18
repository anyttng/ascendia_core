package com.ascendiacore.gui.widget.textfield;

import java.util.function.Consumer;
import java.util.function.IntPredicate;

import com.ascendiacore.gui.base.component.GuiComponent;
import com.ascendiacore.gui.base.draw.Canvas;
import com.ascendiacore.gui.base.draw.TextMetrics;
import com.ascendiacore.gui.base.form.Validated;
import com.ascendiacore.gui.base.geometry.Size;
import com.ascendiacore.gui.base.property.Property;
import com.ascendiacore.gui.widget.textfield.model.TextFieldStyle;

import net.minecraft.network.chat.Component;

public final class TextField extends GuiComponent implements Validated {
    private static final int DEFAULT_MAX_LENGTH = 32;
    private static final int HEIGHT = 20;
    private static final int TEXT_PADDING = 4;
    private static final int TEXT_HEIGHT = 8;

    private final TextFieldStyle style;
    private final Consumer<String> onChange;
    private String value = "";
    private int maxLength = DEFAULT_MAX_LENGTH;
    private IntPredicate accepted = character -> true;
    private Component placeholder = Component.empty();
    private Property<Boolean> valid = Property.of(true);

    public TextField(TextFieldStyle style, Consumer<String> onChange) {
        this.style = style;
        this.onChange = onChange;
    }

    public TextFieldStyle style() {
        return style;
    }

    public String value() {
        return value;
    }

    public void setValue(String value) {
        this.value = cut(value);
    }

    public int maxLength() {
        return maxLength;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
        this.value = cut(value);
    }

    public void setAccepted(IntPredicate accepted) {
        this.accepted = accepted;
    }

    public Component placeholder() {
        return placeholder;
    }

    public void setPlaceholder(Component placeholder) {
        this.placeholder = placeholder;
    }

    public void setValid(Property<Boolean> valid) {
        this.valid = valid;
    }

    @Override
    public boolean valid() {
        return valid.get();
    }

    public String accepted(String input) {
        return input.codePoints().filter(accepted)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    public void edited(String value) {
        if (value.equals(this.value)) {
            return;
        }
        this.value = value;
        onChange.accept(value);
    }

    public int textLeft() {
        return x() + TEXT_PADDING;
    }

    public int textTop() {
        return y() + (height() - TEXT_HEIGHT) / 2;
    }

    public int textRoom() {
        return Math.max(0, width() - 2 * TEXT_PADDING);
    }

    public int textHeight() {
        return TEXT_HEIGHT;
    }

    @Override
    public Component narration() {
        return placeholder;
    }

    @Override
    public boolean takesFocusOnPress() {
        return true;
    }

    @Override
    public Size naturalSize(TextMetrics metrics) {
        return new Size(width(), HEIGHT);
    }

    @Override
    protected void drawContent(Canvas canvas) {
        canvas.sprite(sprite(), x(), y(), width(), height());
        if (value.isEmpty()) {
            drawPlaceholder(canvas);
        }
    }

    private void drawPlaceholder(Canvas canvas) {
        canvas.pushClip(textLeft(), y(), textRoom(), height());
        canvas.text(placeholder, textLeft(), textTop(), style.placeholderColor(), style.textShadow());
        canvas.popClip();
    }

    private String sprite() {
        if (!enabled()) {
            return style.disabled();
        }
        return focused() ? style.focused() : style.normal();
    }

    private String cut(String text) {
        return text.length() > maxLength ? text.substring(0, maxLength) : text;
    }
}

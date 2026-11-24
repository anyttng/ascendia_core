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
    private static final int DEFAULT_MAX_CHARACTER_COUNT = 32;
    private static final int HEIGHT_PIXELS = 20;
    private static final int TEXT_PADDING_PIXELS = 4;
    private static final int TEXT_HEIGHT_PIXELS = 8;

    private final TextFieldStyle style;
    private final Consumer<String> onChange;
    private String value = "";
    private int maxCharacterCount = DEFAULT_MAX_CHARACTER_COUNT;
    private IntPredicate accepted = character -> true;
    private Component placeholder = Component.empty();
    private Property<Boolean> valid = Property.of(true);

    public TextField(TextFieldStyle style, Consumer<String> onChange) {
        this.style = style;
        this.onChange = onChange;
    }

    public TextFieldStyle getStyle() {
        return style;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = cut(value);
    }

    public int getMaxCharacterCount() {
        return maxCharacterCount;
    }

    public void setMaxCharacterCount(int maxCharacterCount) {
        this.maxCharacterCount = maxCharacterCount;
        this.value = cut(value);
    }

    public void setAccepted(IntPredicate accepted) {
        this.accepted = accepted;
    }

    public Component getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(Component placeholder) {
        this.placeholder = placeholder;
    }

    public void setValid(Property<Boolean> valid) {
        this.valid = valid;
    }

    @Override
    public boolean isValid() {
        return valid.get();
    }

    public String filterAccepted(String input) {
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

    public int getTextLeft() {
        return getX() + TEXT_PADDING_PIXELS;
    }

    public int getTextTop() {
        return getY() + (getHeightPixels() - TEXT_HEIGHT_PIXELS) / 2;
    }

    public int getTextRoomPixels() {
        return Math.max(0, getWidthPixels() - 2 * TEXT_PADDING_PIXELS);
    }

    public int getTextHeightPixels() {
        return TEXT_HEIGHT_PIXELS;
    }

    @Override
    public Component getNarration() {
        return placeholder;
    }

    @Override
    public boolean isTakingFocusOnPress() {
        return true;
    }

    @Override
    public Size naturalSize(TextMetrics metrics) {
        return new Size(getWidthPixels(), HEIGHT_PIXELS);
    }

    @Override
    protected void drawContent(Canvas canvas) {
        canvas.sprite(getSprite(), getX(), getY(), getWidthPixels(), getHeightPixels());
        if (value.isEmpty()) {
            drawPlaceholder(canvas);
        }
    }

    private void drawPlaceholder(Canvas canvas) {
        canvas.pushClip(getTextLeft(), getY(), getTextRoomPixels(), getHeightPixels());
        canvas.text(placeholder, getTextLeft(), getTextTop(), style.placeholderColor(), style.hasTextShadow());
        canvas.popClip();
    }

    private String getSprite() {
        if (!isEnabled()) {
            return style.disabled();
        }
        return isFocused() ? style.focused() : style.normal();
    }

    private String cut(String text) {
        return text.length() > maxCharacterCount ? text.substring(0, maxCharacterCount) : text;
    }
}

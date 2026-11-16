package com.ascendiacore.gui.widget.numberfield;

import java.util.Optional;
import java.util.function.IntFunction;

import org.jspecify.annotations.Nullable;

import com.ascendiacore.gui.base.property.Property;
import com.ascendiacore.gui.layout.Container;
import com.ascendiacore.gui.layout.model.Axis;
import com.ascendiacore.gui.widget.field.Field;
import com.ascendiacore.gui.widget.numberfield.model.Bounds;
import com.ascendiacore.gui.widget.panel.model.FillStyle;
import com.ascendiacore.gui.widget.textfield.TextField;
import com.ascendiacore.gui.widget.textfield.model.TextFieldStyle;

import net.minecraft.network.chat.Component;

public final class NumberField extends Container {
    private static final int MINUS = '-';
    private static final String TOO_SMALL_KEY = "gui.ascendia_core.number_field.too_small";
    private static final String TOO_LARGE_KEY = "gui.ascendia_core.number_field.too_large";

    private final Property<Optional<Bounds>> bounds;
    private final TextField text;
    private final Field field;
    private IntFunction<Component> valueLine = value -> Component.empty();

    public NumberField(FillStyle fill, TextFieldStyle style, Component caption, Property<Optional<Bounds>> bounds,
            Runnable onChange) {
        super(Axis.VERTICAL);
        this.bounds = bounds;
        this.text = new TextField(style, edited -> onChange.run());
        this.text.setAccepted(Character::isDigit);
        this.text.setValid(() -> value() != null);
        this.field = new Field(fill, caption, text);
        this.field.setError(this::boundsLine);
        this.field.setValueLine(this::shownValueLine);
        add(field);
    }

    public void setSigned(boolean signed) {
        text.setAccepted(signed ? NumberField::digitOrMinus : Character::isDigit);
    }

    public void setMaxLength(int maxLength) {
        text.setMaxLength(maxLength);
    }

    public void setValueLine(IntFunction<Component> valueLine) {
        this.valueLine = valueLine;
    }

    public void setHint(Property<Component> hint) {
        field.setHint(hint);
    }

    public void setValue(int value) {
        text.setValue(String.valueOf(value));
    }

    public void setText(String shown) {
        text.setValue(shown);
    }

    public @Nullable Integer value() {
        Integer number = parsed();
        Optional<Bounds> known = bounds.get();
        return number != null && known.isPresent() && known.get().contains(number) ? number : null;
    }

    private Component boundsLine() {
        Integer number = parsed();
        Optional<Bounds> known = bounds.get();
        if (number == null || known.isEmpty()) {
            return Component.empty();
        }
        Bounds range = known.get();
        if (range.below(number)) {
            return Component.translatable(TOO_SMALL_KEY, range.min());
        }
        return range.above(number) ? Component.translatable(TOO_LARGE_KEY, range.max()) : Component.empty();
    }

    private Component shownValueLine() {
        Integer number = value();
        return number == null ? Component.empty() : valueLine.apply(number);
    }

    private @Nullable Integer parsed() {
        try {
            return Integer.parseInt(text.value());
        } catch (NumberFormatException notANumber) {
            return null;
        }
    }

    private static boolean digitOrMinus(int character) {
        return Character.isDigit(character) || character == MINUS;
    }
}

package com.ascendiacore.gui.widget.cyclebutton;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import com.ascendiacore.gui.base.component.model.TooltipStyle;
import com.ascendiacore.gui.base.hint.Hint;
import com.ascendiacore.gui.base.hint.model.HintValue;
import com.ascendiacore.gui.base.input.model.Modifiers;
import com.ascendiacore.gui.base.property.Property;
import com.ascendiacore.gui.base.property.Value;
import com.ascendiacore.gui.layout.Container;
import com.ascendiacore.gui.layout.model.Axis;
import com.ascendiacore.gui.widget.button.Button;
import com.ascendiacore.gui.widget.button.model.ButtonStyle;

import net.minecraft.locale.Language;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public final class CycleButton<T> extends Container {
    private static final int FORWARD = 1;
    private static final int BACK = -1;
    private static final String HINT_PREFIX = ".hint.";
    private static final String INTRO = "intro";
    private static final String NOTE = "note";
    private static final String PREVIOUS_VALUE_KEY = "gui.ascendia_core.cycle_button.previous_value";

    private final Component name;
    private final Property<List<T>> values;
    private final Value<T> chosen;
    private final Function<T, Component> valueName;
    private final Button button;
    private boolean valueOnly;
    private Consumer<T> onChange = picked -> {
    };

    public CycleButton(ButtonStyle style, Component name, Property<List<T>> values, Value<T> chosen,
            Function<T, Component> valueName) {
        super(Axis.VERTICAL);
        this.name = name;
        this.values = values;
        this.chosen = chosen;
        this.valueName = valueName;
        this.button = Button.text(style, name, this::step);
        this.button.setLabel(this::label);
        add(button);
    }

    public void setHint(TooltipStyle style, String captionKey, Function<T, String> valueKey) {
        setHint(style, captionKey, valueKey, hint -> {
        });
    }

    public void setHint(TooltipStyle style, String captionKey, Function<T, String> valueKey, Consumer<Hint> otherKinds) {
        String prefix = captionKey + HINT_PREFIX;
        setHintOf(style, prefix, value -> Component.translatable(prefix + valueKey.apply(value)), otherKinds);
    }

    public void setValueHints(TooltipStyle style, String captionKey, Function<T, Component> valueHint) {
        setHintOf(style, captionKey + HINT_PREFIX, valueHint, hint -> {
        });
    }

    private void setHintOf(TooltipStyle style, String prefix, Function<T, Component> valueHint,
            Consumer<Hint> otherKinds) {
        button.setTooltip(style, () -> {
            Hint hint = Hint.of(style)
                    .intro(optional(prefix + INTRO))
                    .values(hintValues(valueHint))
                    .note(optional(prefix + NOTE))
                    .keyHint(keyHint());
            otherKinds.accept(hint);
            return hint.build();
        });
    }

    private List<HintValue> hintValues(Function<T, Component> valueHint) {
        T current = chosen.get();
        return values.get().stream().map(value -> new HintValue(valueName.apply(value), valueHint.apply(value),
                value.equals(current))).toList();
    }

    private Component keyHint() {
        return values.get().size() > 1 ? Component.translatable(PREVIOUS_VALUE_KEY) : Component.empty();
    }

    private static Component optional(String key) {
        return Language.getInstance().has(key) ? Component.translatable(key) : Component.empty();
    }

    public void showValueOnly() {
        valueOnly = true;
    }

    public void setOnChange(Consumer<T> onChange) {
        this.onChange = onChange;
    }

    private Component label() {
        Component value = valueName.apply(chosen.get());
        return valueOnly ? value : CommonComponents.optionNameValue(name, value);
    }

    private void step(Modifiers modifiers) {
        List<T> list = values.get();
        if (list.isEmpty()) {
            return;
        }
        int delta = modifiers.shift() ? BACK : FORWARD;
        T picked = list.get(nextIndex(list.indexOf(chosen.get()), delta, list.size()));
        chosen.set(picked);
        onChange.accept(picked);
    }

    private static int nextIndex(int index, int delta, int size) {
        if (index < 0) {
            return delta == FORWARD ? 0 : size - 1;
        }
        return Math.floorMod(index + delta, size);
    }
}

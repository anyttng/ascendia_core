package com.ascendiacore.gui.widget.cyclebutton;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import com.ascendiacore.gui.base.component.model.TooltipStyle;
import com.ascendiacore.gui.base.hint.ValueHint;
import com.ascendiacore.gui.base.hint.model.HintValue;
import com.ascendiacore.gui.base.input.model.Modifiers;
import com.ascendiacore.gui.base.property.Property;
import com.ascendiacore.gui.base.property.Value;
import com.ascendiacore.gui.layout.Container;
import com.ascendiacore.gui.layout.model.Axis;
import com.ascendiacore.gui.widget.button.Button;
import com.ascendiacore.gui.widget.button.model.ButtonStyle;

import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public final class CycleButton<T> extends Container {
    private static final int FORWARD = 1;
    private static final int BACK = -1;

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
        button.setTooltip(style, () -> ValueHint.of(style, captionKey, hintValues(valueKey)));
    }

    private List<HintValue> hintValues(Function<T, String> valueKey) {
        return values.get().stream().map(value -> new HintValue(valueName.apply(value), valueKey.apply(value))).toList();
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

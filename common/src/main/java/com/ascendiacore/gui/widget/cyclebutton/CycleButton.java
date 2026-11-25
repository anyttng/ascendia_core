package com.ascendiacore.gui.widget.cyclebutton;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import com.ascendiacore.gui.base.component.model.TooltipStyle;
import com.ascendiacore.gui.base.hint.Hint;
import com.ascendiacore.gui.base.hint.ValueHint;
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
    private static final String PREVIOUS_VALUE_KEY = "gui.ascendia_core.cycle_button.previous_value";

    private final Component name;
    private final Property<List<T>> values;
    private final Value<T> chosen;
    private final Function<T, Component> valueName;
    private final Button button;
    private boolean isValueOnly;
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
        this.button.setLabel(this::getLabel);
        add(button);
    }

    public void setHint(TooltipStyle style, String captionKey, Function<T, String> valueKey) {
        setHint(style, captionKey, valueKey, hint -> {
        });
    }

    public void setHint(TooltipStyle style, String captionKey, Function<T, String> valueKey, Consumer<Hint> otherKinds) {
        setHintOf(style, captionKey, value -> ValueHint.text(captionKey, valueKey.apply(value)), otherKinds);
    }

    public void setValueHints(TooltipStyle style, String captionKey, Function<T, Component> valueHint) {
        setHintOf(style, captionKey, valueHint, hint -> {
        });
    }

    private void setHintOf(TooltipStyle style, String captionKey, Function<T, Component> valueHint,
            Consumer<Hint> otherKinds) {
        button.setTooltip(style, () -> {
            Hint hint = ValueHint.of(style, captionKey, values.get(), chosen.get(), valueName, valueHint)
                    .keyHint(getKeyHint());
            otherKinds.accept(hint);
            return hint.build();
        });
    }

    private Component getKeyHint() {
        return values.get().size() > 1 ? Component.translatable(PREVIOUS_VALUE_KEY) : Component.empty();
    }

    public void showValueOnly() {
        isValueOnly = true;
    }

    public void setOnChange(Consumer<T> onChange) {
        this.onChange = onChange;
    }

    private Component getLabel() {
        Component value = valueName.apply(chosen.get());
        return isValueOnly ? value : CommonComponents.optionNameValue(name, value);
    }

    private void step(Modifiers modifiers) {
        List<T> list = values.get();
        if (list.isEmpty()) {
            return;
        }
        int delta = modifiers.isShiftDown() ? BACK : FORWARD;
        T picked = list.get(nextIndex(list.indexOf(chosen.get()), delta, list.size()));
        chosen.set(picked);
        onChange.accept(picked);
    }

    private static int nextIndex(int index, int delta, int valueCount) {
        if (index < 0) {
            return delta == FORWARD ? 0 : valueCount - 1;
        }
        return Math.floorMod(index + delta, valueCount);
    }
}

package com.ascendiacore.gui.base.hint;

import java.util.ArrayList;
import java.util.List;

import com.ascendiacore.gui.base.component.model.LevelColors;
import com.ascendiacore.gui.base.component.model.TooltipStyle;
import com.ascendiacore.gui.base.hint.model.HintLevel;
import com.ascendiacore.gui.base.hint.model.HintValue;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public final class Hint {
    public static final String RULE = " ";
    private static final String PARAGRAPH = "\n\n";
    private static final String LINE = "\n";

    private final TooltipStyle style;
    private Component derived = Component.empty();
    private Component intro = Component.empty();
    private List<HintValue> values = List.of();
    private Component warning = Component.empty();
    private Component danger = Component.empty();
    private Component requirement = Component.empty();
    private Component liveState = Component.empty();
    private Component level = Component.empty();
    private Component note = Component.empty();
    private Component disabledReason = Component.empty();
    private Component keyHint = Component.empty();
    private boolean isDivided;

    private Hint(TooltipStyle style) {
        this.style = style;
    }

    public static Hint of(TooltipStyle style) {
        return new Hint(style);
    }

    public Hint derived(Component text) {
        derived = colored(text, style.derivedColor());
        return this;
    }

    public Hint intro(Component text) {
        intro = text;
        return this;
    }

    public Hint values(List<HintValue> values) {
        this.values = values;
        return this;
    }

    public Hint warning(Component text) {
        warning = colored(text, style.alerts().warning());
        return this;
    }

    public Hint warning(Component word, Component text) {
        warning = named(word, style.alerts().warning(), text);
        return this;
    }

    public Hint danger(Component text) {
        danger = colored(text, style.alerts().danger());
        return this;
    }

    public Hint danger(Component word, Component text) {
        danger = named(word, style.alerts().danger(), text);
        return this;
    }

    public Hint requirement(Component text) {
        requirement = colored(text, style.alerts().requirement());
        return this;
    }

    public Hint requirement(Component word, Component text) {
        requirement = named(word, style.alerts().requirement(), text);
        return this;
    }

    public Hint liveState(Component text) {
        liveState = aside(text);
        return this;
    }

    public Hint divider() {
        isDivided = true;
        return this;
    }

    public Hint level(Component caption, HintLevel level, Component levelName) {
        this.level = CommonComponents.optionNameValue(caption, levelName.copy().withColor(getLevelColor(level)));
        return this;
    }

    public Hint note(Component text) {
        note = colored(text, style.noteColor());
        return this;
    }

    public Hint disabledReason(Component text) {
        disabledReason = colored(text, style.noteColor());
        return this;
    }

    public Hint keyHint(Component text) {
        keyHint = aside(text);
        return this;
    }

    public Component build() {
        MutableComponent hint = Component.empty();
        for (Component paragraph : getBody()) {
            append(hint, PARAGRAPH, paragraph);
        }
        List<Component> footer = dropBlank(List.of(level, note, disabledReason, keyHint));
        String gap = PARAGRAPH;
        if (isDivided && !footer.isEmpty()) {
            append(hint, PARAGRAPH, getRule());
            gap = LINE;
        }
        for (Component paragraph : footer) {
            append(hint, gap, paragraph);
            gap = PARAGRAPH;
        }
        return hint;
    }

    private List<Component> getBody() {
        List<Component> body = new ArrayList<>(List.of(derived, intro));
        for (HintValue value : values) {
            body.add(toValueLine(value));
        }
        body.addAll(List.of(warning, danger, requirement, liveState));
        return dropBlank(body);
    }

    private Component toValueLine(HintValue value) {
        MutableComponent name = value.name().copy().withColor(style.valueColor());
        if (value.isCurrent()) {
            name.withStyle(ChatFormatting.UNDERLINE);
        }
        return CommonComponents.optionNameValue(name, value.text());
    }

    private Component getRule() {
        int color = isBlank(level) ? style.noteColor() : style.dividerColor();
        return Component.literal(RULE).withStyle(ChatFormatting.STRIKETHROUGH).withColor(color);
    }

    private Component aside(Component text) {
        return isBlank(text) ? text : text.copy().withStyle(ChatFormatting.ITALIC).withColor(style.noteColor());
    }

    private int getLevelColor(HintLevel level) {
        LevelColors colors = style.levels();
        return switch (level) {
            case LOW -> colors.low();
            case MEDIUM -> colors.medium();
            case HIGH -> colors.high();
        };
    }

    private static Component colored(Component text, int color) {
        return isBlank(text) ? text : text.copy().withColor(color);
    }

    private static Component named(Component word, int color, Component text) {
        return isBlank(text) ? text : CommonComponents.optionNameValue(word.copy().withColor(color), text);
    }

    private static List<Component> dropBlank(List<Component> paragraphs) {
        return paragraphs.stream().filter(paragraph -> !isBlank(paragraph)).toList();
    }

    private static boolean isBlank(Component text) {
        return text.getString().isEmpty();
    }

    private static void append(MutableComponent hint, String gap, Component paragraph) {
        if (!hint.getSiblings().isEmpty()) {
            hint.append(gap);
        }
        hint.append(paragraph);
    }
}

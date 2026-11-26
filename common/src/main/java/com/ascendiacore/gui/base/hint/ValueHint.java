package com.ascendiacore.gui.base.hint;

import java.util.List;
import java.util.function.Function;

import com.ascendiacore.gui.base.component.model.TooltipStyle;
import com.ascendiacore.gui.base.hint.model.HintValue;

import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;

public final class ValueHint {
    private static final String HINT_PREFIX = ".hint.";
    private static final String INTRO = "intro";
    private static final String NOTE = "note";

    private ValueHint() {
    }

    public static <T> Hint of(TooltipStyle style, String captionKey, List<T> values, T current,
            Function<T, Component> valueName, Function<T, Component> valueText) {
        List<HintValue> hintValues = values.stream()
                .map(value -> new HintValue(valueName.apply(value), valueText.apply(value), value.equals(current)))
                .toList();
        return Hint.of(style)
                .intro(translateOptional(hintKey(captionKey, INTRO)))
                .values(hintValues)
                .note(translateOptional(hintKey(captionKey, NOTE)));
    }

    public static Hint ofOneValue(TooltipStyle style, String captionKey, Component name, Component text) {
        return Hint.of(style)
                .values(List.of(new HintValue(name, text, false)))
                .note(translateOptional(hintKey(captionKey, NOTE)));
    }

    public static Component text(String captionKey, String key) {
        return Component.translatable(hintKey(captionKey, key));
    }

    private static String hintKey(String captionKey, String key) {
        return captionKey + HINT_PREFIX + key;
    }

    private static Component translateOptional(String key) {
        return Language.getInstance().has(key) ? Component.translatable(key) : Component.empty();
    }
}

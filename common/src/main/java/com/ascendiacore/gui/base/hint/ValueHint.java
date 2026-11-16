package com.ascendiacore.gui.base.hint;

import java.util.List;

import com.ascendiacore.gui.base.component.model.TooltipStyle;
import com.ascendiacore.gui.base.hint.model.HintValue;

import net.minecraft.locale.Language;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public final class ValueHint {
    private static final String PARAGRAPH = "\n\n";
    private static final String HINT_PREFIX = ".hint.";
    private static final String INTRO = "intro";
    private static final String NOTE = "note";

    private ValueHint() {
    }

    public static Component of(TooltipStyle style, String captionKey, List<HintValue> values) {
        String prefix = captionKey + HINT_PREFIX;
        Language language = Language.getInstance();
        MutableComponent hint = Component.empty();
        if (language.has(prefix + INTRO)) {
            addParagraph(hint, Component.translatable(prefix + INTRO));
        }
        for (HintValue value : values) {
            addParagraph(hint, valueLine(style, prefix, value));
        }
        if (language.has(prefix + NOTE)) {
            addParagraph(hint, Component.translatable(prefix + NOTE).withColor(style.noteColor()));
        }
        return hint;
    }

    private static Component valueLine(TooltipStyle style, String prefix, HintValue value) {
        return CommonComponents.optionNameValue(value.name().copy().withColor(style.valueColor()),
                Component.translatable(prefix + value.key()));
    }

    private static void addParagraph(MutableComponent hint, Component paragraph) {
        if (!hint.getSiblings().isEmpty()) {
            hint.append(PARAGRAPH);
        }
        hint.append(paragraph);
    }
}

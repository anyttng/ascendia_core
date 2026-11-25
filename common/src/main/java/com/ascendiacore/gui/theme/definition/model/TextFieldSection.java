package com.ascendiacore.gui.theme.definition.model;

import java.util.Optional;

import com.ascendiacore.gui.widget.panel.model.FillKind;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record TextFieldSection(
        FillKind surface,
        String normal,
        String focused,
        String disabled,
        Optional<Integer> text,
        Optional<Integer> disabledText,
        Optional<Integer> placeholder,
        Optional<Boolean> hasTextShadow) {
    private static final String FOCUSED_KEY = "focused";
    private static final String DISABLED_TEXT_KEY = "disabled_text";
    private static final String PLACEHOLDER_KEY = "placeholder";

    public static final Codec<TextFieldSection> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ThemeCodecs.SURFACE.fieldOf(ThemeCodecs.SURFACE_KEY).forGetter(TextFieldSection::surface),
            Codec.STRING.fieldOf(ThemeCodecs.NORMAL_KEY).forGetter(TextFieldSection::normal),
            Codec.STRING.fieldOf(FOCUSED_KEY).forGetter(TextFieldSection::focused),
            Codec.STRING.fieldOf(ThemeCodecs.DISABLED_KEY).forGetter(TextFieldSection::disabled),
            ThemeCodecs.COLOR.optionalFieldOf(ThemeCodecs.TEXT_KEY).forGetter(TextFieldSection::text),
            ThemeCodecs.COLOR.optionalFieldOf(DISABLED_TEXT_KEY).forGetter(TextFieldSection::disabledText),
            ThemeCodecs.COLOR.optionalFieldOf(PLACEHOLDER_KEY).forGetter(TextFieldSection::placeholder),
            Codec.BOOL.optionalFieldOf(ThemeCodecs.HAS_TEXT_SHADOW_KEY).forGetter(TextFieldSection::hasTextShadow))
            .apply(instance, TextFieldSection::new));
}

package com.ascendiacore.gui.theme.definition.model;

import java.util.Optional;

import com.ascendiacore.gui.widget.panel.model.FillKind;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record ButtonSection(
        FillKind surface,
        String normal,
        String hover,
        String disabled,
        String selected,
        String selectedHover,
        Optional<Integer> label,
        Optional<Integer> disabledLabel,
        Optional<Integer> selectedLabel,
        Optional<Boolean> hasLabelShadow) {
    private static final String SELECTED_KEY = "selected";
    private static final String SELECTED_HOVER_KEY = "selected_hover";
    private static final String LABEL_KEY = "label";
    private static final String DISABLED_LABEL_KEY = "disabled_label";
    private static final String SELECTED_LABEL_KEY = "selected_label";
    private static final String HAS_LABEL_SHADOW_KEY = "has_label_shadow";

    public static final Codec<ButtonSection> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ThemeCodecs.SURFACE.fieldOf(ThemeCodecs.SURFACE_KEY).forGetter(ButtonSection::surface),
            Codec.STRING.fieldOf(ThemeCodecs.NORMAL_KEY).forGetter(ButtonSection::normal),
            Codec.STRING.fieldOf(ThemeCodecs.HOVER_KEY).forGetter(ButtonSection::hover),
            Codec.STRING.fieldOf(ThemeCodecs.DISABLED_KEY).forGetter(ButtonSection::disabled),
            Codec.STRING.fieldOf(SELECTED_KEY).forGetter(ButtonSection::selected),
            Codec.STRING.fieldOf(SELECTED_HOVER_KEY).forGetter(ButtonSection::selectedHover),
            ThemeCodecs.COLOR.optionalFieldOf(LABEL_KEY).forGetter(ButtonSection::label),
            ThemeCodecs.COLOR.optionalFieldOf(DISABLED_LABEL_KEY).forGetter(ButtonSection::disabledLabel),
            ThemeCodecs.COLOR.optionalFieldOf(SELECTED_LABEL_KEY).forGetter(ButtonSection::selectedLabel),
            Codec.BOOL.optionalFieldOf(HAS_LABEL_SHADOW_KEY).forGetter(ButtonSection::hasLabelShadow))
            .apply(instance, ButtonSection::new));
}

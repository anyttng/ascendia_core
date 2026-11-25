package com.ascendiacore.gui.theme.definition.model;

import java.util.Optional;

import com.ascendiacore.gui.widget.panel.model.FillKind;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record TooltipSection(
        FillKind surface,
        Optional<String> sprites,
        Optional<Integer> text,
        Optional<Integer> value,
        Optional<Integer> note) {
    private static final String SPRITES_KEY = "sprites";
    private static final String VALUE_KEY = "value";
    private static final String NOTE_KEY = "note";

    public static final Codec<TooltipSection> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ThemeCodecs.SURFACE.fieldOf(ThemeCodecs.SURFACE_KEY).forGetter(TooltipSection::surface),
            Codec.STRING.optionalFieldOf(SPRITES_KEY).forGetter(TooltipSection::sprites),
            ThemeCodecs.COLOR.optionalFieldOf(ThemeCodecs.TEXT_KEY).forGetter(TooltipSection::text),
            ThemeCodecs.COLOR.optionalFieldOf(VALUE_KEY).forGetter(TooltipSection::value),
            ThemeCodecs.COLOR.optionalFieldOf(NOTE_KEY).forGetter(TooltipSection::note))
            .apply(instance, TooltipSection::new));
}

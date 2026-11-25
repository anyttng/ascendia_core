package com.ascendiacore.gui.theme.definition.model;

import java.util.Optional;

import com.ascendiacore.gui.widget.panel.model.FillStyle;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record FillOverride(
        Optional<Integer> fill,
        Optional<Integer> text,
        Optional<Integer> muted,
        Optional<Integer> error,
        Optional<Boolean> hasTextShadow) {
    public static final FillOverride NONE = new FillOverride(Optional.empty(), Optional.empty(), Optional.empty(),
            Optional.empty(), Optional.empty());

    public static final Codec<FillOverride> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ThemeCodecs.COLOR.optionalFieldOf(ThemeCodecs.FILL_KEY).forGetter(FillOverride::fill),
            ThemeCodecs.COLOR.optionalFieldOf(ThemeCodecs.TEXT_KEY).forGetter(FillOverride::text),
            ThemeCodecs.COLOR.optionalFieldOf(ThemeCodecs.MUTED_KEY).forGetter(FillOverride::muted),
            ThemeCodecs.COLOR.optionalFieldOf(ThemeCodecs.ERROR_KEY).forGetter(FillOverride::error),
            Codec.BOOL.optionalFieldOf(ThemeCodecs.HAS_TEXT_SHADOW_KEY).forGetter(FillOverride::hasTextShadow))
            .apply(instance, FillOverride::new));

    public FillStyle applyTo(FillStyle base) {
        return new FillStyle(fill.orElse(base.color()), text.orElse(base.ink()), muted.orElse(base.mutedInk()),
                error.orElse(base.errorInk()), hasTextShadow.orElse(base.hasInkShadow()));
    }
}

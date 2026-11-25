package com.ascendiacore.gui.theme.definition.model;

import java.util.Optional;

import com.ascendiacore.gui.widget.link.model.LinkColors;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record LinkColorsOverride(Optional<Integer> normal, Optional<Integer> hover, Optional<Integer> disabled) {
    public static final LinkColorsOverride NONE = new LinkColorsOverride(Optional.empty(), Optional.empty(), Optional.empty());

    public static final Codec<LinkColorsOverride> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ThemeCodecs.COLOR.optionalFieldOf(ThemeCodecs.NORMAL_KEY).forGetter(LinkColorsOverride::normal),
            ThemeCodecs.COLOR.optionalFieldOf(ThemeCodecs.HOVER_KEY).forGetter(LinkColorsOverride::hover),
            ThemeCodecs.COLOR.optionalFieldOf(ThemeCodecs.DISABLED_KEY).forGetter(LinkColorsOverride::disabled))
            .apply(instance, LinkColorsOverride::new));

    public LinkColors applyTo(LinkColors base) {
        return new LinkColors(normal.orElse(base.normal()), hover.orElse(base.hover()), disabled.orElse(base.disabled()));
    }
}

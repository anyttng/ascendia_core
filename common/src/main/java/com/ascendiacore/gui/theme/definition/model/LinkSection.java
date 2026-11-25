package com.ascendiacore.gui.theme.definition.model;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record LinkSection(LinkColorsOverride onLight, LinkColorsOverride onDark) {
    public static final LinkSection NONE = new LinkSection(LinkColorsOverride.NONE, LinkColorsOverride.NONE);

    private static final String ON_LIGHT_KEY = "on_light";
    private static final String ON_DARK_KEY = "on_dark";

    public static final Codec<LinkSection> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            LinkColorsOverride.CODEC.optionalFieldOf(ON_LIGHT_KEY, LinkColorsOverride.NONE).forGetter(LinkSection::onLight),
            LinkColorsOverride.CODEC.optionalFieldOf(ON_DARK_KEY, LinkColorsOverride.NONE).forGetter(LinkSection::onDark))
            .apply(instance, LinkSection::new));
}

package com.ascendiacore.gui.theme.definition.model;

import com.ascendiacore.gui.widget.panel.model.Fills;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record FillsOverride(FillOverride light, FillOverride mid, FillOverride dark) {
    public static final FillsOverride NONE = new FillsOverride(FillOverride.NONE, FillOverride.NONE, FillOverride.NONE);

    public static final Codec<FillsOverride> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            FillOverride.CODEC.optionalFieldOf(ThemeCodecs.LIGHT_KEY, FillOverride.NONE).forGetter(FillsOverride::light),
            FillOverride.CODEC.optionalFieldOf(ThemeCodecs.MID_KEY, FillOverride.NONE).forGetter(FillsOverride::mid),
            FillOverride.CODEC.optionalFieldOf(ThemeCodecs.DARK_KEY, FillOverride.NONE).forGetter(FillsOverride::dark))
            .apply(instance, FillsOverride::new));

    public Fills applyTo(Fills base) {
        return new Fills(light.applyTo(base.parchment()), mid.applyTo(base.page()), dark.applyTo(base.dark()));
    }
}

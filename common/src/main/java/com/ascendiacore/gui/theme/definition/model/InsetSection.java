package com.ascendiacore.gui.theme.definition.model;

import com.ascendiacore.gui.widget.panel.model.FrameStyle;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record InsetSection(FrameStyle light, FrameStyle dark, FrameStyle rim, FillsOverride fills) {
    private static final String RIM_KEY = "rim";

    public static final Codec<InsetSection> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ThemeCodecs.FRAME.fieldOf(ThemeCodecs.LIGHT_KEY).forGetter(InsetSection::light),
            ThemeCodecs.FRAME.fieldOf(ThemeCodecs.DARK_KEY).forGetter(InsetSection::dark),
            ThemeCodecs.FRAME.fieldOf(RIM_KEY).forGetter(InsetSection::rim),
            FillsOverride.CODEC.optionalFieldOf(ThemeCodecs.FILLS_KEY, FillsOverride.NONE).forGetter(InsetSection::fills))
            .apply(instance, InsetSection::new));
}

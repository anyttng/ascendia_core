package com.ascendiacore.gui.theme.definition.model;

import com.ascendiacore.gui.widget.panel.model.FrameStyle;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record PanelSection(FrameStyle frame, FillsOverride fills) {
    private static final String FRAME_KEY = "frame";

    public static final Codec<PanelSection> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ThemeCodecs.FRAME.fieldOf(FRAME_KEY).forGetter(PanelSection::frame),
            FillsOverride.CODEC.optionalFieldOf(ThemeCodecs.FILLS_KEY, FillsOverride.NONE).forGetter(PanelSection::fills))
            .apply(instance, PanelSection::new));
}

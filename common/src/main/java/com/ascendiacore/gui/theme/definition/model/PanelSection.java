package com.ascendiacore.gui.theme.definition.model;

import java.util.Optional;

import com.ascendiacore.gui.widget.panel.model.FillKind;
import com.ascendiacore.gui.widget.panel.model.FrameStyle;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record PanelSection(FrameStyle frame, Optional<FillKind> surface, FillsOverride fills) {
    private static final String FRAME_KEY = "frame";

    public static final Codec<PanelSection> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ThemeCodecs.FRAME.fieldOf(FRAME_KEY).forGetter(PanelSection::frame),
            ThemeCodecs.SURFACE.optionalFieldOf(ThemeCodecs.SURFACE_KEY).forGetter(PanelSection::surface),
            FillsOverride.CODEC.optionalFieldOf(ThemeCodecs.FILLS_KEY, FillsOverride.NONE).forGetter(PanelSection::fills))
            .apply(instance, PanelSection::new));
}

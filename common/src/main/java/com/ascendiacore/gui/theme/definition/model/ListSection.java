package com.ascendiacore.gui.theme.definition.model;

import com.ascendiacore.gui.widget.panel.model.FillKind;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record ListSection(
        FillKind surface,
        FillKind selectedSurface,
        String row,
        String rowHover,
        String rowSelected,
        FillOverride framedText,
        FillOverride framedSelectedText,
        FillOverride buttonText,
        FillOverride buttonSelectedText) {
    private static final String SELECTED_SURFACE_KEY = "selected_surface";
    private static final String ROW_KEY = "row";
    private static final String ROW_HOVER_KEY = "row_hover";
    private static final String ROW_SELECTED_KEY = "row_selected";
    private static final String FRAMED_TEXT_KEY = "framed_text";
    private static final String FRAMED_SELECTED_TEXT_KEY = "framed_selected_text";
    private static final String BUTTON_TEXT_KEY = "button_text";
    private static final String BUTTON_SELECTED_TEXT_KEY = "button_selected_text";

    public static final Codec<ListSection> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ThemeCodecs.SURFACE.fieldOf(ThemeCodecs.SURFACE_KEY).forGetter(ListSection::surface),
            ThemeCodecs.SURFACE.fieldOf(SELECTED_SURFACE_KEY).forGetter(ListSection::selectedSurface),
            Codec.STRING.fieldOf(ROW_KEY).forGetter(ListSection::row),
            Codec.STRING.fieldOf(ROW_HOVER_KEY).forGetter(ListSection::rowHover),
            Codec.STRING.fieldOf(ROW_SELECTED_KEY).forGetter(ListSection::rowSelected),
            FillOverride.CODEC.optionalFieldOf(FRAMED_TEXT_KEY, FillOverride.NONE).forGetter(ListSection::framedText),
            FillOverride.CODEC.optionalFieldOf(FRAMED_SELECTED_TEXT_KEY, FillOverride.NONE)
                    .forGetter(ListSection::framedSelectedText),
            FillOverride.CODEC.optionalFieldOf(BUTTON_TEXT_KEY, FillOverride.NONE).forGetter(ListSection::buttonText),
            FillOverride.CODEC.optionalFieldOf(BUTTON_SELECTED_TEXT_KEY, FillOverride.NONE)
                    .forGetter(ListSection::buttonSelectedText))
            .apply(instance, ListSection::new));
}

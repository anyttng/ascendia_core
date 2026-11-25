package com.ascendiacore.gui.theme.definition.model;

import com.ascendiacore.gui.widget.panel.model.FillKind;
import com.ascendiacore.gui.widget.panel.model.FillStyle;
import com.ascendiacore.gui.widget.panel.model.Fills;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record ColorRoles(
        FillStyle light,
        FillStyle mid,
        FillStyle dark,
        int accent,
        int accentHover,
        int link,
        int linkHover,
        int success,
        int warning,
        int danger,
        int requirement,
        int derived,
        int divider) {
    private static final String ACCENT_KEY = "accent";
    private static final String ACCENT_HOVER_KEY = "accent_hover";
    private static final String LINK_KEY = "link";
    private static final String LINK_HOVER_KEY = "link_hover";
    private static final String SUCCESS_KEY = "success";
    private static final String WARNING_KEY = "warning";
    private static final String DANGER_KEY = "danger";
    private static final String REQUIREMENT_KEY = "requirement";
    private static final String DERIVED_KEY = "derived";
    private static final String DIVIDER_KEY = "divider";

    public static final Codec<ColorRoles> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ThemeCodecs.FILL_STYLE.fieldOf(ThemeCodecs.LIGHT_KEY).forGetter(ColorRoles::light),
            ThemeCodecs.FILL_STYLE.fieldOf(ThemeCodecs.MID_KEY).forGetter(ColorRoles::mid),
            ThemeCodecs.FILL_STYLE.fieldOf(ThemeCodecs.DARK_KEY).forGetter(ColorRoles::dark),
            ThemeCodecs.COLOR.fieldOf(ACCENT_KEY).forGetter(ColorRoles::accent),
            ThemeCodecs.COLOR.fieldOf(ACCENT_HOVER_KEY).forGetter(ColorRoles::accentHover),
            ThemeCodecs.COLOR.fieldOf(LINK_KEY).forGetter(ColorRoles::link),
            ThemeCodecs.COLOR.fieldOf(LINK_HOVER_KEY).forGetter(ColorRoles::linkHover),
            ThemeCodecs.COLOR.fieldOf(SUCCESS_KEY).forGetter(ColorRoles::success),
            ThemeCodecs.COLOR.fieldOf(WARNING_KEY).forGetter(ColorRoles::warning),
            ThemeCodecs.COLOR.fieldOf(DANGER_KEY).forGetter(ColorRoles::danger),
            ThemeCodecs.COLOR.fieldOf(REQUIREMENT_KEY).forGetter(ColorRoles::requirement),
            ThemeCodecs.COLOR.fieldOf(DERIVED_KEY).forGetter(ColorRoles::derived),
            ThemeCodecs.COLOR.fieldOf(DIVIDER_KEY).forGetter(ColorRoles::divider))
            .apply(instance, ColorRoles::new));

    public Fills getSurfaces() {
        return new Fills(light, mid, dark);
    }

    public FillStyle getSurface(FillKind kind) {
        return getSurfaces().getFill(kind);
    }
}

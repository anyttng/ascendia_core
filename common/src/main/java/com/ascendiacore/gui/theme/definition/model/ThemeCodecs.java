package com.ascendiacore.gui.theme.definition.model;

import com.ascendiacore.AscendiaCore;
import com.ascendiacore.gui.widget.panel.model.FillKind;
import com.ascendiacore.gui.widget.panel.model.FillStyle;
import com.ascendiacore.gui.widget.panel.model.FrameStyle;
import com.ascendiacore.gui.widget.progressbar.model.ProgressBarStyle;
import com.ascendiacore.gui.widget.scrollbar.model.ScrollbarStyle;
import com.ascendiacore.gui.widget.slider.model.SliderStyle;
import com.ascendiacore.gui.widget.tabrow.model.TabRowStyle;
import com.ascendiacore.gui.widget.toggleswitch.model.ToggleSwitchStyle;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.util.ExtraCodecs;

public final class ThemeCodecs {
    public static final String LIGHT_KEY = "light";
    public static final String MID_KEY = "mid";
    public static final String DARK_KEY = "dark";
    public static final String SURFACE_KEY = "surface";
    public static final String FILLS_KEY = "fills";
    public static final String FILL_KEY = "fill";
    public static final String TEXT_KEY = "text";
    public static final String MUTED_KEY = "muted";
    public static final String ERROR_KEY = "error";
    public static final String HAS_TEXT_SHADOW_KEY = "has_text_shadow";
    public static final String NORMAL_KEY = "normal";
    public static final String HOVER_KEY = "hover";
    public static final String DISABLED_KEY = "disabled";
    public static final String SELECTED_KEY = "selected";
    public static final String SELECTED_HOVER_KEY = "selected_hover";
    public static final String LABEL_KEY = "label";
    public static final String SELECTED_LABEL_KEY = "selected_label";
    public static final String HAS_LABEL_SHADOW_KEY = "has_label_shadow";
    public static final String SHARED_PROGRESS_BAR_FILL = AscendiaCore.MODID + ":theme/shared/progress_bar/fill";
    private static final String SPRITE_KEY = "sprite";
    private static final String BORDER_PIXELS_KEY = "border_pixels";
    private static final String TRACK_KEY = "track";
    private static final String THUMB_KEY = "thumb";
    private static final String THUMB_HOVER_KEY = "thumb_hover";
    private static final String OFF_KEY = "off";
    private static final String ON_KEY = "on";
    private static final String OFF_HOVER_KEY = "off_hover";
    private static final String ON_HOVER_KEY = "on_hover";
    private static final String OFF_DISABLED_KEY = "off_disabled";
    private static final String ON_DISABLED_KEY = "on_disabled";
    private static final String HANDLE_KEY = "handle";
    private static final String HANDLE_HOVER_KEY = "handle_hover";
    private static final String HANDLE_DISABLED_KEY = "handle_disabled";
    private static final String HOVER_LABEL_KEY = "hover_label";
    private static final String TRACK_LIGHT_KEY = "track_light";

    public static final Codec<Integer> COLOR = Codec.withAlternative(ExtraCodecs.STRING_RGB_COLOR, ExtraCodecs.STRING_ARGB_COLOR);

    public static final Codec<FillKind> SURFACE = Codec.STRING.comapFlatMap(ThemeCodecs::toSurface, ThemeCodecs::toSurfaceKey);

    public static final Codec<FillStyle> FILL_STYLE = RecordCodecBuilder.create(instance -> instance.group(
            COLOR.fieldOf(FILL_KEY).forGetter(FillStyle::color),
            COLOR.fieldOf(TEXT_KEY).forGetter(FillStyle::ink),
            COLOR.fieldOf(MUTED_KEY).forGetter(FillStyle::mutedInk),
            COLOR.fieldOf(ERROR_KEY).forGetter(FillStyle::errorInk),
            Codec.BOOL.fieldOf(HAS_TEXT_SHADOW_KEY).forGetter(FillStyle::hasInkShadow))
            .apply(instance, FillStyle::new));

    public static final Codec<FrameStyle> FRAME = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf(SPRITE_KEY).forGetter(FrameStyle::sprite),
            Codec.intRange(0, Integer.MAX_VALUE).fieldOf(BORDER_PIXELS_KEY).forGetter(FrameStyle::borderPixels))
            .apply(instance, FrameStyle::new));

    public static final Codec<ScrollbarStyle> SCROLLBAR = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf(TRACK_KEY).forGetter(ScrollbarStyle::track),
            Codec.STRING.fieldOf(THUMB_KEY).forGetter(ScrollbarStyle::thumb),
            Codec.STRING.fieldOf(THUMB_HOVER_KEY).forGetter(ScrollbarStyle::thumbHover))
            .apply(instance, ScrollbarStyle::new));

    public static final Codec<ToggleSwitchStyle> TOGGLE_SWITCH = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf(OFF_KEY).forGetter(ToggleSwitchStyle::off),
            Codec.STRING.fieldOf(ON_KEY).forGetter(ToggleSwitchStyle::on),
            Codec.STRING.fieldOf(OFF_HOVER_KEY).forGetter(ToggleSwitchStyle::offHover),
            Codec.STRING.fieldOf(ON_HOVER_KEY).forGetter(ToggleSwitchStyle::onHover),
            Codec.STRING.fieldOf(OFF_DISABLED_KEY).forGetter(ToggleSwitchStyle::offDisabled),
            Codec.STRING.fieldOf(ON_DISABLED_KEY).forGetter(ToggleSwitchStyle::onDisabled))
            .apply(instance, ToggleSwitchStyle::new));

    public static final Codec<SliderStyle> SLIDER = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf(TRACK_KEY).forGetter(SliderStyle::track),
            Codec.STRING.fieldOf(HANDLE_KEY).forGetter(SliderStyle::handle),
            Codec.STRING.fieldOf(HANDLE_HOVER_KEY).forGetter(SliderStyle::handleHover),
            Codec.STRING.fieldOf(HANDLE_DISABLED_KEY).forGetter(SliderStyle::handleDisabled))
            .apply(instance, SliderStyle::new));

    public static final Codec<TabRowStyle> TAB_ROW = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf(NORMAL_KEY).forGetter(TabRowStyle::normal),
            Codec.STRING.fieldOf(HOVER_KEY).forGetter(TabRowStyle::hover),
            Codec.STRING.fieldOf(SELECTED_KEY).forGetter(TabRowStyle::selected),
            Codec.STRING.fieldOf(SELECTED_HOVER_KEY).forGetter(TabRowStyle::selectedHover),
            COLOR.fieldOf(LABEL_KEY).forGetter(TabRowStyle::labelColor),
            COLOR.fieldOf(HOVER_LABEL_KEY).forGetter(TabRowStyle::hoverLabelColor),
            COLOR.fieldOf(SELECTED_LABEL_KEY).forGetter(TabRowStyle::selectedLabelColor),
            Codec.BOOL.fieldOf(HAS_LABEL_SHADOW_KEY).forGetter(TabRowStyle::hasLabelShadow))
            .apply(instance, TabRowStyle::new));

    public static final Codec<ProgressBarStyle> PROGRESS_BAR = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf(TRACK_KEY).forGetter(ProgressBarStyle::track),
            Codec.STRING.fieldOf(TRACK_LIGHT_KEY).forGetter(ProgressBarStyle::trackLight),
            Codec.STRING.optionalFieldOf(FILL_KEY, SHARED_PROGRESS_BAR_FILL).forGetter(ProgressBarStyle::fill))
            .apply(instance, ProgressBarStyle::new));

    private ThemeCodecs() {
    }

    private static DataResult<FillKind> toSurface(String key) {
        return switch (key) {
            case LIGHT_KEY -> DataResult.success(FillKind.PARCHMENT);
            case MID_KEY -> DataResult.success(FillKind.PAGE);
            case DARK_KEY -> DataResult.success(FillKind.DARK);
            default -> DataResult.error(() -> "Unknown surface " + key + ", expected " + LIGHT_KEY + ", " + MID_KEY + " or " + DARK_KEY);
        };
    }

    private static String toSurfaceKey(FillKind kind) {
        return switch (kind) {
            case PARCHMENT -> LIGHT_KEY;
            case PAGE -> MID_KEY;
            case DARK -> DARK_KEY;
        };
    }
}

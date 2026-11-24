package com.ascendiacore.gui.theme.vanilla;

import com.ascendiacore.gui.base.component.model.AlertColors;
import com.ascendiacore.gui.base.component.model.LevelColors;
import com.ascendiacore.gui.base.component.model.TooltipStyle;
import com.ascendiacore.gui.theme.Theme;
import com.ascendiacore.gui.widget.button.model.ButtonStyle;
import com.ascendiacore.gui.widget.link.model.LinkColors;
import com.ascendiacore.gui.widget.link.model.LinkStyle;
import com.ascendiacore.gui.widget.list.model.ListStyle;
import com.ascendiacore.gui.widget.panel.model.FillStyle;
import com.ascendiacore.gui.widget.panel.model.Fills;
import com.ascendiacore.gui.widget.panel.model.FrameStyle;
import com.ascendiacore.gui.widget.panel.model.InsetStyle;
import com.ascendiacore.gui.widget.panel.model.PanelStyle;
import com.ascendiacore.gui.widget.scrollbar.model.ScrollbarStyle;
import com.ascendiacore.gui.widget.textfield.model.TextFieldStyle;
import com.ascendiacore.gui.widget.toggleswitch.model.ToggleSwitchStyle;

public final class VanillaTheme {
    private static final String NAME = "vanilla";
    private static final String GAME_BUTTON = "minecraft:widget/button";
    private static final String GAME_BUTTON_HOVER = "minecraft:widget/button_highlighted";
    private static final String GAME_BUTTON_DISABLED = "minecraft:widget/button_disabled";
    private static final String GAME_POPUP = "minecraft:popup/background";
    private static final String GAME_SCROLLER = "minecraft:widget/scroller";
    private static final String GAME_SCROLLER_BACKGROUND = "minecraft:widget/scroller_background";
    private static final String GAME_TEXT_FIELD = "minecraft:widget/text_field";
    private static final String GAME_TEXT_FIELD_HIGHLIGHTED = "minecraft:widget/text_field_highlighted";
    private static final String SPRITES = "ascendia_core:theme/vanilla/";
    private static final String BUTTON_SELECTED = SPRITES + "button_selected";
    private static final String BUTTON_SELECTED_HOVER = SPRITES + "button_selected_hover";
    private static final String INSET_LIGHT = SPRITES + "inset_light";
    private static final String INSET_DARK = SPRITES + "inset_dark";
    private static final String INSET_RIM = SPRITES + "inset_rim";
    private static final String ROW = SPRITES + "row";
    private static final String ROW_HOVER = SPRITES + "row_hover";
    private static final String ROW_SELECTED = SPRITES + "row_selected";
    private static final String SWITCH_OFF = SPRITES + "switch_off";
    private static final String SWITCH_ON = SPRITES + "switch_on";
    private static final String SWITCH_OFF_HOVER = SPRITES + "switch_off_hover";
    private static final String SWITCH_ON_HOVER = SPRITES + "switch_on_hover";
    private static final String SWITCH_OFF_DISABLED = SPRITES + "switch_off_disabled";
    private static final String SWITCH_ON_DISABLED = SPRITES + "switch_on_disabled";
    private static final int GAME_POPUP_BORDER_PIXELS = 6;
    private static final int LIGHT_INSET_BORDER_PIXELS = 3;
    private static final int HEAVY_INSET_BORDER_PIXELS = 4;

    public static final Theme THEME = new Theme(NAME, button(), panel(), inset(), link(), list(), scrollbar(), tooltip(),
            textField(), toggleSwitch());

    private VanillaTheme() {
    }

    private static ToggleSwitchStyle toggleSwitch() {
        return new ToggleSwitchStyle(SWITCH_OFF, SWITCH_ON, SWITCH_OFF_HOVER, SWITCH_ON_HOVER, SWITCH_OFF_DISABLED,
                SWITCH_ON_DISABLED);
    }

    private static ButtonStyle button() {
        return new ButtonStyle(GAME_BUTTON, GAME_BUTTON_HOVER, GAME_BUTTON_DISABLED, BUTTON_SELECTED, BUTTON_SELECTED_HOVER,
                VanillaPalette.WHITE, VanillaPalette.DISABLED_LABEL, VanillaPalette.YELLOW, true);
    }

    private static PanelStyle panel() {
        FillStyle inTheSprite = new FillStyle(FillStyle.NO_FILL, VanillaPalette.WHITE, VanillaPalette.DISABLED_LABEL,
                VanillaPalette.RED, true);
        return new PanelStyle(new FrameStyle(GAME_POPUP, GAME_POPUP_BORDER_PIXELS), new Fills(inTheSprite, inTheSprite, inTheSprite));
    }

    private static InsetStyle inset() {
        return new InsetStyle(
                new FrameStyle(INSET_LIGHT, LIGHT_INSET_BORDER_PIXELS),
                new FrameStyle(INSET_DARK, HEAVY_INSET_BORDER_PIXELS),
                new FrameStyle(INSET_RIM, HEAVY_INSET_BORDER_PIXELS),
                new Fills(
                        new FillStyle(VanillaPalette.LIGHT_GRAY, VanillaPalette.CHARCOAL, VanillaPalette.DISABLED_LINK,
                                VanillaPalette.DARK_RED, false),
                        new FillStyle(VanillaPalette.GRAY, VanillaPalette.WHITE, VanillaPalette.PALE_GRAY, VanillaPalette.DARK_RED,
                                true),
                        new FillStyle(VanillaPalette.DARK_GRAY, VanillaPalette.WHITE, VanillaPalette.DISABLED_LABEL,
                                VanillaPalette.RED, true)));
    }

    private static LinkStyle link() {
        return new LinkStyle(
                new LinkColors(VanillaPalette.BLUE, VanillaPalette.BRIGHT_BLUE, VanillaPalette.DISABLED_LINK),
                new LinkColors(VanillaPalette.PALE_BLUE, VanillaPalette.WHITE, VanillaPalette.DISABLED_LINK));
    }

    private static ListStyle list() {
        FillStyle framed = rowText(VanillaPalette.WHITE, VanillaPalette.DISABLED_LABEL);
        return new ListStyle(ROW, ROW_HOVER, ROW_SELECTED, framed, framed,
                rowText(VanillaPalette.WHITE, VanillaPalette.PALE_GRAY), rowText(VanillaPalette.YELLOW, VanillaPalette.PALE_GRAY));
    }

    private static FillStyle rowText(int ink, int mutedInk) {
        return new FillStyle(FillStyle.NO_FILL, ink, mutedInk, VanillaPalette.RED, true);
    }

    private static ScrollbarStyle scrollbar() {
        return new ScrollbarStyle(GAME_SCROLLER_BACKGROUND, GAME_SCROLLER, GAME_SCROLLER);
    }

    private static TooltipStyle tooltip() {
        return new TooltipStyle(null, VanillaPalette.WHITE, VanillaPalette.YELLOW, VanillaPalette.SILVER,
                new AlertColors(VanillaPalette.ORANGE, VanillaPalette.RED, VanillaPalette.PALE_BLUE),
                new LevelColors(VanillaPalette.GREEN, VanillaPalette.YELLOW, VanillaPalette.RED),
                VanillaPalette.GREEN, VanillaPalette.RULE_GRAY);
    }

    private static TextFieldStyle textField() {
        return new TextFieldStyle(GAME_TEXT_FIELD, GAME_TEXT_FIELD_HIGHLIGHTED, GAME_TEXT_FIELD,
                VanillaPalette.PALE_GRAY, VanillaPalette.DISABLED_LINK, VanillaPalette.STONE_GRAY, true);
    }
}

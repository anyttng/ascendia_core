package com.ascendiacore.gui.theme.vanilla;

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

public final class VanillaTheme {
    private static final String NAME = "vanilla";
    private static final String GAME_BUTTON = "minecraft:widget/button";
    private static final String GAME_BUTTON_HOVER = "minecraft:widget/button_highlighted";
    private static final String GAME_BUTTON_DISABLED = "minecraft:widget/button_disabled";
    private static final String GAME_POPUP = "minecraft:popup/background";
    private static final String GAME_SCROLLER = "minecraft:widget/scroller";
    private static final String GAME_SCROLLER_BACKGROUND = "minecraft:widget/scroller_background";
    private static final String SPRITES = "ascendia_core:theme/vanilla/";
    private static final String INSET_LIGHT = SPRITES + "inset_light";
    private static final String INSET_DARK = SPRITES + "inset_dark";
    private static final String INSET_RIM = SPRITES + "inset_rim";
    private static final String ROW_HOVER = SPRITES + "row_hover";
    private static final String ROW_SELECTED = SPRITES + "row_selected";
    private static final int GAME_POPUP_BORDER = 6;
    private static final int LIGHT_INSET_BORDER = 3;
    private static final int HEAVY_INSET_BORDER = 4;

    public static final Theme THEME = new Theme(NAME, button(), panel(), inset(), link(), list(), scrollbar());

    private VanillaTheme() {
    }

    private static ButtonStyle button() {
        return new ButtonStyle(GAME_BUTTON, GAME_BUTTON_HOVER, GAME_BUTTON_DISABLED, VanillaPalette.WHITE,
                VanillaPalette.DISABLED_LABEL, true);
    }

    private static PanelStyle panel() {
        FillStyle inTheSprite = new FillStyle(FillStyle.NO_FILL, VanillaPalette.WHITE);
        return new PanelStyle(new FrameStyle(GAME_POPUP, GAME_POPUP_BORDER), new Fills(inTheSprite, inTheSprite, inTheSprite));
    }

    private static InsetStyle inset() {
        return new InsetStyle(
                new FrameStyle(INSET_LIGHT, LIGHT_INSET_BORDER),
                new FrameStyle(INSET_DARK, HEAVY_INSET_BORDER),
                new FrameStyle(INSET_RIM, HEAVY_INSET_BORDER),
                new Fills(
                        new FillStyle(VanillaPalette.LIGHT_GRAY, VanillaPalette.CHARCOAL),
                        new FillStyle(VanillaPalette.GRAY, VanillaPalette.WHITE),
                        new FillStyle(VanillaPalette.DARK_GRAY, VanillaPalette.WHITE)));
    }

    private static LinkStyle link() {
        return new LinkStyle(
                new LinkColors(VanillaPalette.BLUE, VanillaPalette.BRIGHT_BLUE, VanillaPalette.DISABLED_LINK),
                new LinkColors(VanillaPalette.PALE_BLUE, VanillaPalette.WHITE, VanillaPalette.DISABLED_LINK));
    }

    private static ListStyle list() {
        return new ListStyle(ROW_HOVER, ROW_SELECTED, VanillaPalette.WHITE, true);
    }

    private static ScrollbarStyle scrollbar() {
        return new ScrollbarStyle(GAME_SCROLLER_BACKGROUND, GAME_SCROLLER, GAME_SCROLLER);
    }
}

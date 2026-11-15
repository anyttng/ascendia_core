package com.ascendiacore.gui.theme.book;

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

public final class BookTheme {
    private static final String NAME = "book";
    private static final String SPRITES = "ascendia_core:theme/book/";
    private static final String BUTTON = SPRITES + "button";
    private static final String BUTTON_HOVER = SPRITES + "button_hover";
    private static final String BUTTON_DISABLED = SPRITES + "button_disabled";
    private static final String PANEL_COVER = SPRITES + "panel_cover";
    private static final String INSET_LIGHT = SPRITES + "inset_light";
    private static final String INSET_DARK = SPRITES + "inset_dark";
    private static final String INSET_RIM = SPRITES + "inset_rim";
    private static final String ROW_HOVER = SPRITES + "row_hover";
    private static final String ROW_SELECTED = SPRITES + "row_selected";
    private static final String SCROLL_TRACK = SPRITES + "scroll_track";
    private static final String SCROLL_THUMB = SPRITES + "scroll_thumb";
    private static final String SCROLL_THUMB_HOVER = SPRITES + "scroll_thumb_hover";
    private static final int PANEL_BORDER = 8;
    private static final int LIGHT_INSET_BORDER = 3;
    private static final int HEAVY_INSET_BORDER = 4;

    public static final Theme THEME = new Theme(NAME, button(), panel(), inset(), link(), list(), scrollbar());

    private BookTheme() {
    }

    private static ButtonStyle button() {
        return new ButtonStyle(BUTTON, BUTTON_HOVER, BUTTON_DISABLED, BookPalette.DARK_LEATHER, BookPalette.FADED, false);
    }

    private static PanelStyle panel() {
        return new PanelStyle(new FrameStyle(PANEL_COVER, PANEL_BORDER), fills());
    }

    private static InsetStyle inset() {
        return new InsetStyle(
                new FrameStyle(INSET_LIGHT, LIGHT_INSET_BORDER),
                new FrameStyle(INSET_DARK, HEAVY_INSET_BORDER),
                new FrameStyle(INSET_RIM, HEAVY_INSET_BORDER),
                fills());
    }

    private static Fills fills() {
        return new Fills(
                new FillStyle(BookPalette.PARCHMENT, BookPalette.DARK_LEATHER, BookPalette.FADED, false),
                new FillStyle(BookPalette.PAGE, BookPalette.SLATE, BookPalette.FADED, false),
                new FillStyle(BookPalette.SLATE, BookPalette.PARCHMENT, BookPalette.FADED, false));
    }

    private static LinkStyle link() {
        return new LinkStyle(
                new LinkColors(BookPalette.LEATHER, BookPalette.AMBER, BookPalette.FADED),
                new LinkColors(BookPalette.GOLD, BookPalette.PALE_GOLD, BookPalette.FADED));
    }

    private static ListStyle list() {
        return new ListStyle(ROW_HOVER, ROW_SELECTED, BookPalette.PARCHMENT, false);
    }

    private static ScrollbarStyle scrollbar() {
        return new ScrollbarStyle(SCROLL_TRACK, SCROLL_THUMB, SCROLL_THUMB_HOVER);
    }
}

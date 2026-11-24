package com.ascendiacore.gui.theme.book;

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

public final class BookTheme {
    private static final String NAME = "book";
    private static final String SPRITES = "ascendia_core:theme/book/";
    private static final String BUTTON_SPRITES = SPRITES + "button/";
    private static final String PANEL_SPRITES = SPRITES + "panel/";
    private static final String INSET_SPRITES = SPRITES + "inset/";
    private static final String LIST_SPRITES = SPRITES + "list/";
    private static final String SCROLLBAR_SPRITES = SPRITES + "scrollbar/";
    private static final String TEXT_FIELD_SPRITES = SPRITES + "text_field/";
    private static final String SWITCH_SPRITES = SPRITES + "toggle_switch/";
    private static final String BUTTON = BUTTON_SPRITES + "normal";
    private static final String BUTTON_HOVER = BUTTON_SPRITES + "hover";
    private static final String BUTTON_DISABLED = BUTTON_SPRITES + "disabled";
    private static final String BUTTON_SELECTED = BUTTON_SPRITES + "selected";
    private static final String BUTTON_SELECTED_HOVER = BUTTON_SPRITES + "selected_hover";
    private static final String PANEL_COVER = PANEL_SPRITES + "cover";
    private static final String INSET_LIGHT = INSET_SPRITES + "light";
    private static final String INSET_DARK = INSET_SPRITES + "dark";
    private static final String INSET_RIM = INSET_SPRITES + "rim";
    private static final String ROW = LIST_SPRITES + "row";
    private static final String ROW_HOVER = LIST_SPRITES + "row_hover";
    private static final String ROW_SELECTED = LIST_SPRITES + "row_selected";
    private static final String SCROLL_TRACK = SCROLLBAR_SPRITES + "track";
    private static final String SCROLL_THUMB = SCROLLBAR_SPRITES + "thumb";
    private static final String SCROLL_THUMB_HOVER = SCROLLBAR_SPRITES + "thumb_hover";
    private static final String TEXT_FIELD = TEXT_FIELD_SPRITES + "normal";
    private static final String TEXT_FIELD_FOCUSED = TEXT_FIELD_SPRITES + "focused";
    private static final String TEXT_FIELD_DISABLED = TEXT_FIELD_SPRITES + "disabled";
    private static final String SWITCH_OFF = SWITCH_SPRITES + "off";
    private static final String SWITCH_ON = SWITCH_SPRITES + "on";
    private static final String SWITCH_OFF_HOVER = SWITCH_SPRITES + "off_hover";
    private static final String SWITCH_ON_HOVER = SWITCH_SPRITES + "on_hover";
    private static final String SWITCH_OFF_DISABLED = SWITCH_SPRITES + "off_disabled";
    private static final String SWITCH_ON_DISABLED = SWITCH_SPRITES + "on_disabled";
    private static final String TOOLTIP_SPRITES = "ascendia_core:book";
    private static final int PANEL_BORDER_PIXELS = 8;
    private static final int LIGHT_INSET_BORDER_PIXELS = 3;
    private static final int HEAVY_INSET_BORDER_PIXELS = 4;

    public static final Theme THEME = new Theme(NAME, button(), panel(), inset(), link(), list(), scrollbar(), tooltip(),
            textField(), toggleSwitch());

    private BookTheme() {
    }

    private static ToggleSwitchStyle toggleSwitch() {
        return new ToggleSwitchStyle(SWITCH_OFF, SWITCH_ON, SWITCH_OFF_HOVER, SWITCH_ON_HOVER, SWITCH_OFF_DISABLED,
                SWITCH_ON_DISABLED);
    }

    private static ButtonStyle button() {
        return new ButtonStyle(BUTTON, BUTTON_HOVER, BUTTON_DISABLED, BUTTON_SELECTED, BUTTON_SELECTED_HOVER,
                BookPalette.DARK_LEATHER, BookPalette.FADED, BookPalette.GOLD, false);
    }

    private static PanelStyle panel() {
        return new PanelStyle(new FrameStyle(PANEL_COVER, PANEL_BORDER_PIXELS), fills());
    }

    private static InsetStyle inset() {
        return new InsetStyle(
                new FrameStyle(INSET_LIGHT, LIGHT_INSET_BORDER_PIXELS),
                new FrameStyle(INSET_DARK, HEAVY_INSET_BORDER_PIXELS),
                new FrameStyle(INSET_RIM, HEAVY_INSET_BORDER_PIXELS),
                fills());
    }

    private static Fills fills() {
        return new Fills(
                new FillStyle(BookPalette.PARCHMENT, BookPalette.DARK_LEATHER, BookPalette.FADED, BookPalette.DARK_RED, false),
                new FillStyle(BookPalette.PAGE, BookPalette.SLATE, BookPalette.FADED, BookPalette.DARK_RED, false),
                new FillStyle(BookPalette.SLATE, BookPalette.PARCHMENT, BookPalette.FADED, BookPalette.RED, false));
    }

    private static LinkStyle link() {
        return new LinkStyle(
                new LinkColors(BookPalette.LEATHER, BookPalette.AMBER, BookPalette.FADED),
                new LinkColors(BookPalette.GOLD, BookPalette.PALE_GOLD, BookPalette.FADED));
    }

    private static ListStyle list() {
        FillStyle onLight = new FillStyle(FillStyle.NO_FILL, BookPalette.DARK_LEATHER, BookPalette.FADED, BookPalette.DARK_RED, false);
        return new ListStyle(ROW, ROW_HOVER, ROW_SELECTED, onLight, onLeather(BookPalette.PARCHMENT),
                onLight, onLeather(BookPalette.GOLD));
    }

    private static FillStyle onLeather(int ink) {
        return new FillStyle(FillStyle.NO_FILL, ink, BookPalette.TAN, BookPalette.RED, false);
    }

    private static ScrollbarStyle scrollbar() {
        return new ScrollbarStyle(SCROLL_TRACK, SCROLL_THUMB, SCROLL_THUMB_HOVER);
    }

    private static TooltipStyle tooltip() {
        return new TooltipStyle(TOOLTIP_SPRITES, BookPalette.PARCHMENT, BookPalette.GOLD, BookPalette.FADED,
                new AlertColors(BookPalette.ORANGE, BookPalette.CORAL, BookPalette.PERIWINKLE),
                new LevelColors(BookPalette.SAGE, BookPalette.GOLD, BookPalette.CORAL),
                BookPalette.MIST, BookPalette.UMBER);
    }

    private static TextFieldStyle textField() {
        return new TextFieldStyle(TEXT_FIELD, TEXT_FIELD_FOCUSED, TEXT_FIELD_DISABLED,
                BookPalette.CHARCOAL, BookPalette.FADED, BookPalette.SAND, false);
    }
}

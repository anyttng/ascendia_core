package com.ascendiacore.gui.theme.book;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.ascendiacore.gui.theme.Theme;
import com.ascendiacore.gui.theme.Themes;
import com.ascendiacore.gui.widget.list.model.RowStyle;
import com.ascendiacore.gui.widget.panel.model.FillKind;

class BookThemeTest {
    private static final String OWN_SPRITES = "ascendia_core:theme/book/";
    private static final Theme THEME = BookTheme.THEME;

    @Test
    void themesNamesIt() {
        assertEquals(THEME, Themes.BOOK);
        assertEquals("book", THEME.name());
    }

    @Test
    void everySpriteIsOurOwn() {
        List<String> sprites = List.of(
                THEME.button().normal(), THEME.button().hover(), THEME.button().disabled(),
                THEME.button().selected(), THEME.button().selectedHover(),
                THEME.panel().frame().sprite(),
                THEME.inset().light().sprite(), THEME.inset().dark().sprite(), THEME.inset().rim().sprite(),
                THEME.list().row(), THEME.list().rowHover(), THEME.list().rowSelected(),
                THEME.scrollbar().track(), THEME.scrollbar().thumb(), THEME.scrollbar().thumbHover(),
                THEME.textField().normal(), THEME.textField().focused(), THEME.textField().disabled());
        assertTrue(sprites.stream().allMatch(sprite -> sprite.startsWith(OWN_SPRITES)));
        assertEquals(sprites.size(), Set.copyOf(sprites).size());
    }

    @Test
    void rowTextIsLeatherAndTurnsParchmentOnTheSelectedFramedRowAndGoldOnTheSelectedButtonRow() {
        assertEquals(BookPalette.DARK_LEATHER, THEME.list().getText(RowStyle.FRAMED, false).ink());
        assertEquals(BookPalette.PARCHMENT, THEME.list().getText(RowStyle.FRAMED, true).ink());
        assertEquals(BookPalette.DARK_LEATHER, THEME.list().getText(RowStyle.BUTTON, false).ink());
        assertEquals(BookPalette.GOLD, THEME.list().getText(RowStyle.BUTTON, true).ink());
        assertEquals(BookPalette.TAN, THEME.list().getText(RowStyle.FRAMED, true).mutedInk());
        assertFalse(THEME.list().getText(RowStyle.FRAMED, true).hasInkShadow());
    }

    @Test
    void buttonLabelIsDarkLeatherWithoutAShadow() {
        assertEquals(BookPalette.DARK_LEATHER, THEME.button().labelColor());
        assertEquals(BookPalette.FADED, THEME.button().disabledLabelColor());
        assertEquals(BookPalette.GOLD, THEME.button().selectedLabelColor());
        assertFalse(THEME.button().hasLabelShadow());
    }

    @Test
    void textFieldTextIsCharcoalWithAPalePlaceholderAndNoShadow() {
        assertEquals(BookPalette.CHARCOAL, THEME.textField().textColor());
        assertEquals(BookPalette.FADED, THEME.textField().disabledTextColor());
        assertEquals(BookPalette.SAND, THEME.textField().placeholderColor());
        assertFalse(THEME.textField().hasTextShadow());
    }

    @Test
    void eachFillCarriesTheInkThatReadsOnIt() {
        assertEquals(BookPalette.DARK_LEATHER, THEME.panel().fills().getFill(FillKind.PARCHMENT).ink());
        assertEquals(BookPalette.SLATE, THEME.panel().fills().getFill(FillKind.PAGE).ink());
        assertEquals(BookPalette.PARCHMENT, THEME.panel().fills().getFill(FillKind.DARK).ink());
        assertEquals(THEME.panel().fills(), THEME.inset().fills());
    }

    @Test
    void errorInkIsDarkRedOnTheLightFillsAndRedOnSlate() {
        assertEquals(BookPalette.DARK_RED, THEME.panel().fills().getFill(FillKind.PARCHMENT).errorInk());
        assertEquals(BookPalette.DARK_RED, THEME.panel().fills().getFill(FillKind.PAGE).errorInk());
        assertEquals(BookPalette.RED, THEME.panel().fills().getFill(FillKind.DARK).errorInk());
    }

    @Test
    void linkIsLeatherOnLightAndGoldOnDark() {
        assertEquals(BookPalette.LEATHER, THEME.link().getColors(FillKind.PAGE).normal());
        assertEquals(BookPalette.GOLD, THEME.link().getColors(FillKind.DARK).normal());
    }
}

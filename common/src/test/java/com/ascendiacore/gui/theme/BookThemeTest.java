package com.ascendiacore.gui.theme;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.ascendiacore.gui.widget.list.model.RowStyle;
import com.ascendiacore.gui.widget.panel.model.FillKind;

class BookThemeTest {
    private static final int PARCHMENT = 0xFFFBF3DF;
    private static final int SLATE = 0xFF534E47;
    private static final int DARK_LEATHER = 0xFF4C1A0B;
    private static final int LEATHER = 0xFF853E29;
    private static final int GOLD = 0xFFF7D98A;
    private static final int FADED = 0xFF908067;
    private static final int TAN = 0xFFC5AB88;
    private static final int CHARCOAL = 0xFF3E3A35;
    private static final int SAND = 0xFF9F8661;
    private static final int PALE_RED = 0xFFFFACAC;
    private static final int DARK_RED = 0xFFAA0000;
    private static final String OWN_SPRITES = "ascendia_core:theme/book/";
    private static final Theme THEME = TestThemes.BOOK;

    @Test
    void itsIdIsItsFileName() {
        assertEquals(Themes.BOOK, THEME.id());
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
                THEME.textField().normal(), THEME.textField().focused(), THEME.textField().disabled(),
                THEME.toggleSwitch().off(), THEME.toggleSwitch().on(),
                THEME.toggleSwitch().offHover(), THEME.toggleSwitch().onHover(),
                THEME.toggleSwitch().offDisabled(), THEME.toggleSwitch().onDisabled(),
                THEME.slider().track(), THEME.slider().handle(), THEME.slider().handleHover(),
                THEME.slider().handleDisabled());
        assertTrue(sprites.stream().allMatch(sprite -> sprite.startsWith(OWN_SPRITES)));
        assertEquals(sprites.size(), Set.copyOf(sprites).size());
    }

    @Test
    void rowTextIsLeatherAndTurnsParchmentOnTheSelectedFramedRowAndGoldOnTheSelectedButtonRow() {
        assertEquals(DARK_LEATHER, THEME.list().getText(RowStyle.FRAMED, false).ink());
        assertEquals(PARCHMENT, THEME.list().getText(RowStyle.FRAMED, true).ink());
        assertEquals(DARK_LEATHER, THEME.list().getText(RowStyle.BUTTON, false).ink());
        assertEquals(GOLD, THEME.list().getText(RowStyle.BUTTON, true).ink());
        assertEquals(TAN, THEME.list().getText(RowStyle.FRAMED, true).mutedInk());
        assertFalse(THEME.list().getText(RowStyle.FRAMED, true).hasInkShadow());
    }

    @Test
    void buttonLabelIsDarkLeatherWithoutAShadow() {
        assertEquals(DARK_LEATHER, THEME.button().labelColor());
        assertEquals(FADED, THEME.button().disabledLabelColor());
        assertEquals(GOLD, THEME.button().selectedLabelColor());
        assertFalse(THEME.button().hasLabelShadow());
    }

    @Test
    void textFieldTextIsCharcoalWithAPalePlaceholderAndNoShadow() {
        assertEquals(CHARCOAL, THEME.textField().textColor());
        assertEquals(FADED, THEME.textField().disabledTextColor());
        assertEquals(SAND, THEME.textField().placeholderColor());
        assertFalse(THEME.textField().hasTextShadow());
    }

    @Test
    void eachFillCarriesTheInkThatReadsOnIt() {
        assertEquals(DARK_LEATHER, THEME.panel().fills().getFill(FillKind.PARCHMENT).ink());
        assertEquals(SLATE, THEME.panel().fills().getFill(FillKind.PAGE).ink());
        assertEquals(PARCHMENT, THEME.panel().fills().getFill(FillKind.DARK).ink());
        assertEquals(THEME.panel().fills(), THEME.inset().fills());
    }

    @Test
    void errorInkIsDarkRedOnTheLightFillsAndPaleRedOnSlate() {
        assertEquals(DARK_RED, THEME.panel().fills().getFill(FillKind.PARCHMENT).errorInk());
        assertEquals(DARK_RED, THEME.panel().fills().getFill(FillKind.PAGE).errorInk());
        assertEquals(PALE_RED, THEME.panel().fills().getFill(FillKind.DARK).errorInk());
    }

    @Test
    void linkIsLeatherOnLightAndGoldOnDark() {
        assertEquals(LEATHER, THEME.link().getColors(FillKind.PAGE).normal());
        assertEquals(GOLD, THEME.link().getColors(FillKind.DARK).normal());
    }
}

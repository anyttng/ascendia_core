package com.ascendiacore.gui.theme;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.ascendiacore.gui.widget.list.model.RowStyle;
import com.ascendiacore.gui.widget.panel.model.FillKind;

class VanillaThemeTest {
    private static final int WHITE = 0xFFFFFFFF;
    private static final int YELLOW = 0xFFFFFF55;
    private static final int PALE_GRAY = 0xFFE0E0E0;
    private static final int LIGHT_GRAY = 0xFFC6C6C6;
    private static final int GRAY = 0xFFA0A0A0;
    private static final int DARK_GRAY = 0xFF373737;
    private static final int CHARCOAL = 0xFF3F3F3F;
    private static final int DISABLED_LABEL = 0xFFA0A0A0;
    private static final int DISABLED_FIELD_GRAY = 0xFF9A9A9A;
    private static final int PLACEHOLDER_GRAY = 0xFF838383;
    private static final int BLUE = 0xFF2A3FC0;
    private static final int PALE_BLUE = 0xFFA0A0FF;
    private static final int PALE_RED = 0xFFFF7979;
    private static final int MAROON = 0xFF6A0000;
    private static final int NAVY = 0xFF1B2A8A;
    private static final int DARK_RED = 0xFFAA0000;
    private static final String GAME_SPRITES = "minecraft:";
    private static final String OWN_SPRITES = "ascendia_core:theme/vanilla/";
    private static final Theme THEME = TestThemes.VANILLA;

    @Test
    void itsIdIsItsFileName() {
        assertEquals(Themes.VANILLA, THEME.id());
    }

    @Test
    void buttonAndPanelDrawWithTheGamesOwnSprites() {
        List<String> sprites = List.of(
                THEME.button().normal(), THEME.button().hover(), THEME.button().disabled(),
                THEME.panel().frame().sprite());
        assertTrue(sprites.stream().allMatch(sprite -> sprite.startsWith(GAME_SPRITES)));
        assertEquals(sprites.size(), Set.copyOf(sprites).size());
    }

    @Test
    void selectedButtonInsetFramesAndRowsAreOurOwnSinceTheGameHasNone() {
        List<String> sprites = List.of(
                THEME.button().selected(), THEME.button().selectedHover(),
                THEME.inset().light().sprite(), THEME.inset().dark().sprite(), THEME.inset().rim().sprite(),
                THEME.list().row(), THEME.list().rowHover(), THEME.list().rowSelected());
        assertTrue(sprites.stream().allMatch(sprite -> sprite.startsWith(OWN_SPRITES)));
        assertEquals(sprites.size(), Set.copyOf(sprites).size());
    }

    @Test
    void toggleSwitchSpritesAreOurOwnSinceTheGameHasNone() {
        List<String> sprites = List.of(
                THEME.toggleSwitch().off(), THEME.toggleSwitch().on(),
                THEME.toggleSwitch().offHover(), THEME.toggleSwitch().onHover(),
                THEME.toggleSwitch().offDisabled(), THEME.toggleSwitch().onDisabled());
        assertTrue(sprites.stream().allMatch(sprite -> sprite.startsWith(OWN_SPRITES)));
        assertEquals(sprites.size(), Set.copyOf(sprites).size());
    }

    @Test
    void textFieldDrawsWithTheGamesSpritesAndOurOwnWhenDisabledSinceTheGameHasNone() {
        assertEquals("minecraft:widget/text_field", THEME.textField().normal());
        assertEquals("minecraft:widget/text_field_highlighted", THEME.textField().focused());
        assertEquals(OWN_SPRITES + "text_field/disabled", THEME.textField().disabled());
    }

    @Test
    void textFieldTextIsPaleGrayAndItsDisabledTextIsLighterThanItsPlaceholder() {
        assertEquals(PALE_GRAY, THEME.textField().textColor());
        assertEquals(DISABLED_FIELD_GRAY, THEME.textField().disabledTextColor());
        assertEquals(PLACEHOLDER_GRAY, THEME.textField().placeholderColor());
        assertTrue(THEME.textField().hasTextShadow());
    }

    @Test
    void selectedButtonLabelIsYellow() {
        assertEquals(YELLOW, THEME.button().selectedLabelColor());
    }

    @Test
    void scrollbarDrawsWithTheGamesScrollerWhichHasNoHoverSprite() {
        assertEquals("minecraft:widget/scroller_background", THEME.scrollbar().track());
        assertEquals("minecraft:widget/scroller", THEME.scrollbar().thumb());
        assertEquals(THEME.scrollbar().thumb(), THEME.scrollbar().thumbHover());
    }

    @Test
    void rowTextIsWhiteWithAShadowAndYellowOnTheSelectedButtonRow() {
        assertEquals(WHITE, THEME.list().getText(RowStyle.FRAMED, false).ink());
        assertEquals(WHITE, THEME.list().getText(RowStyle.FRAMED, true).ink());
        assertEquals(WHITE, THEME.list().getText(RowStyle.BUTTON, false).ink());
        assertEquals(YELLOW, THEME.list().getText(RowStyle.BUTTON, true).ink());
        assertEquals(DISABLED_LABEL, THEME.list().getText(RowStyle.FRAMED, false).mutedInk());
        assertTrue(THEME.list().getText(RowStyle.FRAMED, false).hasInkShadow());
    }

    @Test
    void panelDrawsNoFillBecauseTheGamesSpriteCarriesIt() {
        for (FillKind kind : FillKind.values()) {
            assertFalse(THEME.panel().fills().getFill(kind).isDrawn());
            assertEquals(WHITE, THEME.panel().fills().getFill(kind).ink());
        }
    }

    @Test
    void insetFillsAreThreeGraysEachWithItsInk() {
        assertEquals(LIGHT_GRAY, THEME.inset().fills().getFill(FillKind.PARCHMENT).color());
        assertEquals(CHARCOAL, THEME.inset().fills().getFill(FillKind.PARCHMENT).ink());
        assertEquals(GRAY, THEME.inset().fills().getFill(FillKind.PAGE).color());
        assertEquals(DARK_GRAY, THEME.inset().fills().getFill(FillKind.DARK).color());
    }

    @Test
    void errorInkIsDarkRedOnLightMaroonOnMidGrayAndPaleRedOnDark() {
        assertEquals(DARK_RED, THEME.inset().fills().getFill(FillKind.PARCHMENT).errorInk());
        assertEquals(MAROON, THEME.inset().fills().getFill(FillKind.PAGE).errorInk());
        assertEquals(PALE_RED, THEME.inset().fills().getFill(FillKind.DARK).errorInk());
        for (FillKind kind : FillKind.values()) {
            assertEquals(PALE_RED, THEME.panel().fills().getFill(kind).errorInk());
        }
    }

    @Test
    void linkIsBlueOnLightNavyOnMidAndPaleBlueOnDark() {
        assertEquals(BLUE, THEME.link().getColors(FillKind.PARCHMENT).normal());
        assertEquals(NAVY, THEME.link().getColors(FillKind.PAGE).normal());
        assertEquals(NAVY, THEME.link().getColors(FillKind.PAGE).hover());
        assertEquals(PALE_BLUE, THEME.link().getColors(FillKind.DARK).normal());
    }
}

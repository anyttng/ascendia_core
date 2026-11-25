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
    private static final int GRAY = 0xFF8B8B8B;
    private static final int DARK_GRAY = 0xFF373737;
    private static final int CHARCOAL = 0xFF3F3F3F;
    private static final int STONE_GRAY = 0xFF555555;
    private static final int DISABLED_LABEL = 0xFFA0A0A0;
    private static final int DISABLED_LINK = 0xFF707070;
    private static final int BLUE = 0xFF2A3FC0;
    private static final int PALE_BLUE = 0xFFA0A0FF;
    private static final int RED = 0xFFFF5555;
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
    void textFieldDrawsWithTheGamesSpritesAndKeepsItsFrameWhenDisabled() {
        assertEquals("minecraft:widget/text_field", THEME.textField().normal());
        assertEquals("minecraft:widget/text_field_highlighted", THEME.textField().focused());
        assertEquals(THEME.textField().normal(), THEME.textField().disabled());
    }

    @Test
    void textFieldColorsAreTheGamesFieldColors() {
        assertEquals(PALE_GRAY, THEME.textField().textColor());
        assertEquals(DISABLED_LINK, THEME.textField().disabledTextColor());
        assertEquals(STONE_GRAY, THEME.textField().placeholderColor());
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
    void errorInkIsDarkRedOnLightAndMidGrayAndRedOnDark() {
        assertEquals(DARK_RED, THEME.inset().fills().getFill(FillKind.PARCHMENT).errorInk());
        assertEquals(DARK_RED, THEME.inset().fills().getFill(FillKind.PAGE).errorInk());
        assertEquals(RED, THEME.inset().fills().getFill(FillKind.DARK).errorInk());
        for (FillKind kind : FillKind.values()) {
            assertEquals(RED, THEME.panel().fills().getFill(kind).errorInk());
        }
    }

    @Test
    void linkIsBlueOnLightAndPaleBlueOnDark() {
        assertEquals(BLUE, THEME.link().getColors(FillKind.PARCHMENT).normal());
        assertEquals(PALE_BLUE, THEME.link().getColors(FillKind.DARK).normal());
    }
}

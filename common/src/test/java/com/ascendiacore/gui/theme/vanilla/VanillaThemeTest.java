package com.ascendiacore.gui.theme.vanilla;

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

class VanillaThemeTest {
    private static final String GAME_SPRITES = "minecraft:";
    private static final String OWN_SPRITES = "ascendia_core:theme/vanilla/";
    private static final Theme THEME = VanillaTheme.THEME;

    @Test
    void themesNamesIt() {
        assertEquals(THEME, Themes.VANILLA);
        assertEquals("vanilla", THEME.name());
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
        assertEquals(VanillaPalette.PALE_GRAY, THEME.textField().textColor());
        assertEquals(VanillaPalette.DISABLED_LINK, THEME.textField().disabledTextColor());
        assertEquals(VanillaPalette.STONE_GRAY, THEME.textField().placeholderColor());
        assertTrue(THEME.textField().hasTextShadow());
    }

    @Test
    void selectedButtonLabelIsYellow() {
        assertEquals(VanillaPalette.YELLOW, THEME.button().selectedLabelColor());
    }

    @Test
    void scrollbarDrawsWithTheGamesScrollerWhichHasNoHoverSprite() {
        assertEquals("minecraft:widget/scroller_background", THEME.scrollbar().track());
        assertEquals("minecraft:widget/scroller", THEME.scrollbar().thumb());
        assertEquals(THEME.scrollbar().thumb(), THEME.scrollbar().thumbHover());
    }

    @Test
    void rowTextIsWhiteWithAShadowAndYellowOnTheSelectedButtonRow() {
        assertEquals(VanillaPalette.WHITE, THEME.list().getText(RowStyle.FRAMED, false).ink());
        assertEquals(VanillaPalette.WHITE, THEME.list().getText(RowStyle.FRAMED, true).ink());
        assertEquals(VanillaPalette.WHITE, THEME.list().getText(RowStyle.BUTTON, false).ink());
        assertEquals(VanillaPalette.YELLOW, THEME.list().getText(RowStyle.BUTTON, true).ink());
        assertEquals(VanillaPalette.DISABLED_LABEL, THEME.list().getText(RowStyle.FRAMED, false).mutedInk());
        assertTrue(THEME.list().getText(RowStyle.FRAMED, false).hasInkShadow());
    }

    @Test
    void panelDrawsNoFillBecauseTheGamesSpriteCarriesIt() {
        for (FillKind kind : FillKind.values()) {
            assertFalse(THEME.panel().fills().getFill(kind).isDrawn());
            assertEquals(VanillaPalette.WHITE, THEME.panel().fills().getFill(kind).ink());
        }
    }

    @Test
    void insetFillsAreThreeGraysEachWithItsInk() {
        assertEquals(VanillaPalette.LIGHT_GRAY, THEME.inset().fills().getFill(FillKind.PARCHMENT).color());
        assertEquals(VanillaPalette.CHARCOAL, THEME.inset().fills().getFill(FillKind.PARCHMENT).ink());
        assertEquals(VanillaPalette.GRAY, THEME.inset().fills().getFill(FillKind.PAGE).color());
        assertEquals(VanillaPalette.DARK_GRAY, THEME.inset().fills().getFill(FillKind.DARK).color());
    }

    @Test
    void errorInkIsDarkRedOnLightAndMidGrayAndRedOnDark() {
        assertEquals(VanillaPalette.DARK_RED, THEME.inset().fills().getFill(FillKind.PARCHMENT).errorInk());
        assertEquals(VanillaPalette.DARK_RED, THEME.inset().fills().getFill(FillKind.PAGE).errorInk());
        assertEquals(VanillaPalette.RED, THEME.inset().fills().getFill(FillKind.DARK).errorInk());
        for (FillKind kind : FillKind.values()) {
            assertEquals(VanillaPalette.RED, THEME.panel().fills().getFill(kind).errorInk());
        }
    }

    @Test
    void linkIsBlueOnLightAndPaleBlueOnDark() {
        assertEquals(VanillaPalette.BLUE, THEME.link().getColors(FillKind.PARCHMENT).normal());
        assertEquals(VanillaPalette.PALE_BLUE, THEME.link().getColors(FillKind.DARK).normal());
    }
}

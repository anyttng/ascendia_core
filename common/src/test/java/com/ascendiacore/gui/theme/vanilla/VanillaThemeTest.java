package com.ascendiacore.gui.theme.vanilla;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import com.ascendiacore.gui.theme.Theme;
import com.ascendiacore.gui.theme.Themes;
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
    void insetFramesAreOurOwnSinceTheGameHasNone() {
        List<String> sprites = List.of(
                THEME.inset().light().sprite(), THEME.inset().dark().sprite(), THEME.inset().rim().sprite());
        assertTrue(sprites.stream().allMatch(sprite -> sprite.startsWith(OWN_SPRITES)));
        assertEquals(sprites.size(), Set.copyOf(sprites).size());
    }

    @Test
    void panelDrawsNoFillBecauseTheGamesSpriteCarriesIt() {
        for (FillKind kind : FillKind.values()) {
            assertFalse(THEME.panel().fills().of(kind).drawn());
            assertEquals(VanillaPalette.WHITE, THEME.panel().fills().of(kind).ink());
        }
    }

    @Test
    void insetFillsAreThreeGraysEachWithItsInk() {
        assertEquals(VanillaPalette.LIGHT_GRAY, THEME.inset().fills().of(FillKind.PARCHMENT).color());
        assertEquals(VanillaPalette.CHARCOAL, THEME.inset().fills().of(FillKind.PARCHMENT).ink());
        assertEquals(VanillaPalette.GRAY, THEME.inset().fills().of(FillKind.PAGE).color());
        assertEquals(VanillaPalette.DARK_GRAY, THEME.inset().fills().of(FillKind.DARK).color());
    }

    @Test
    void linkIsBlueOnLightAndPaleBlueOnDark() {
        assertEquals(VanillaPalette.BLUE, THEME.link().on(FillKind.PARCHMENT).normal());
        assertEquals(VanillaPalette.PALE_BLUE, THEME.link().on(FillKind.DARK).normal());
    }
}

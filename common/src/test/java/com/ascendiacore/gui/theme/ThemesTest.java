package com.ascendiacore.gui.theme;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.Test;

import com.ascendiacore.gui.widget.button.model.ButtonStyle;

class ThemesTest {
    private static final String GAME_NAMESPACE = "minecraft:";
    private static final String OWN_BOOK_SPRITES = "ascendia_core:theme/book/";
    private static final String OWN_VANILLA_SPRITES = "ascendia_core:theme/vanilla/";

    @Test
    void vanillaButtonDrawsWithTheGamesOwnSprites() {
        assertTrue(sprites(Themes.VANILLA.button()).stream().allMatch(sprite -> sprite.startsWith(GAME_NAMESPACE)));
    }

    @Test
    void bookButtonDrawsWithOurOwnSprites() {
        assertTrue(sprites(Themes.BOOK.button()).stream().allMatch(sprite -> sprite.startsWith(OWN_BOOK_SPRITES)));
    }

    @Test
    void everyStateOfAButtonHasASpriteOfItsOwn() {
        assertEquals(3, sprites(Themes.VANILLA.button()).size());
        assertEquals(3, sprites(Themes.BOOK.button()).size());
    }

    @Test
    void vanillaPanelIsTheGamesPopupAndItsInsetFramesAreOurs() {
        assertEquals("minecraft:popup/background", Themes.VANILLA.panel().frame().sprite());
        assertTrue(Themes.VANILLA.inset().light().sprite().startsWith(OWN_VANILLA_SPRITES));
        assertTrue(Themes.VANILLA.inset().dark().sprite().startsWith(OWN_VANILLA_SPRITES));
        assertTrue(Themes.VANILLA.inset().rim().sprite().startsWith(OWN_VANILLA_SPRITES));
    }

    @Test
    void bookPanelAndInsetFramesAreOurs() {
        assertTrue(Themes.BOOK.panel().frame().sprite().startsWith(OWN_BOOK_SPRITES));
        assertTrue(Themes.BOOK.inset().light().sprite().startsWith(OWN_BOOK_SPRITES));
        assertTrue(Themes.BOOK.inset().dark().sprite().startsWith(OWN_BOOK_SPRITES));
        assertTrue(Themes.BOOK.inset().rim().sprite().startsWith(OWN_BOOK_SPRITES));
    }

    private static Set<String> sprites(ButtonStyle style) {
        return Set.of(style.normal(), style.hover(), style.disabled());
    }
}

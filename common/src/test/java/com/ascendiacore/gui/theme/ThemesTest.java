package com.ascendiacore.gui.theme;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class ThemesTest {
    private static final String UNKNOWN = "some_mod:missing";

    @AfterEach
    void unload() {
        Themes.replaceAll(Map.of());
    }

    @Test
    void aLoadedThemeIsFoundByItsId() {
        Themes.replaceAll(Map.of(Themes.BOOK, TestThemes.BOOK, Themes.VANILLA, TestThemes.VANILLA));
        assertSame(TestThemes.BOOK, Themes.get(Themes.BOOK));
    }

    @Test
    void anUnknownIdGetsVanilla() {
        Themes.replaceAll(Map.of(Themes.BOOK, TestThemes.BOOK, Themes.VANILLA, TestThemes.VANILLA));
        assertSame(TestThemes.VANILLA, Themes.get(UNKNOWN));
    }

    @Test
    void withNoVanillaLoadedAnUnknownIdThrows() {
        Themes.replaceAll(Map.of(Themes.BOOK, TestThemes.BOOK));
        assertThrows(IllegalStateException.class, () -> Themes.get(UNKNOWN));
    }
}

package com.ascendiacore.gui.theme.definition;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.StringReader;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.ascendiacore.gui.theme.TestThemes;
import com.ascendiacore.gui.theme.Theme;
import com.ascendiacore.gui.theme.Themes;
import com.ascendiacore.gui.theme.definition.model.ThemeSource;

class ThemeLoaderTest {
    private static final String INLINE_PACK = "inline";

    @Test
    void aBrokenFileOnTopGivesWayToTheOneBelow() {
        Optional<Theme> theme = ThemeLoader.load(Themes.BOOK, List.of(inlineSource("{ \"roles\": {} }"), TestThemes.source(Themes.BOOK)));
        assertEquals(Optional.of(TestThemes.BOOK), theme);
    }

    @Test
    void textThatIsNotJsonIsSkipped() {
        assertTrue(ThemeLoader.load(Themes.BOOK, List.of(inlineSource("{ not json"))).isEmpty());
    }

    @Test
    void anUnknownSurfaceIsAnError() {
        String file = TestThemes.text(Themes.BOOK).replace("\"surface\": \"light\"", "\"surface\": \"paper\"");
        assertTrue(ThemeLoader.load(Themes.BOOK, List.of(inlineSource(file))).isEmpty());
    }

    @Test
    void anOverrideTakesThePlaceOfItsRole() {
        String file = TestThemes.text(Themes.BOOK).replace("\"text\": \"#3E3A35\",", "\"text\": \"#123456\",");
        Theme theme = ThemeLoader.load(Themes.BOOK, List.of(inlineSource(file))).orElseThrow();
        assertEquals(0xFF123456, theme.textField().textColor());
    }

    private static ThemeSource inlineSource(String json) {
        return new ThemeSource(INLINE_PACK, () -> new StringReader(json));
    }
}

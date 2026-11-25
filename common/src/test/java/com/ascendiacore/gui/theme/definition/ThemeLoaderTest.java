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
    private static final String TRACK_LIGHT_LINE = "\"track_light\": \"ascendia_core:theme/book/progress_bar/track_light\"";
    private static final String OWN_FILL = "example_mod:theme/example/progress_bar/fill";

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

    @Test
    void aNamedProgressBarFillTakesThePlaceOfTheSharedOne() {
        String file = TestThemes.text(Themes.BOOK).replace(TRACK_LIGHT_LINE,
                TRACK_LIGHT_LINE + ", \"fill\": \"" + OWN_FILL + "\"");
        Theme theme = ThemeLoader.load(Themes.BOOK, List.of(inlineSource(file))).orElseThrow();
        assertEquals(OWN_FILL, theme.progressBar().fill());
    }

    private static ThemeSource inlineSource(String json) {
        return new ThemeSource(INLINE_PACK, () -> new StringReader(json));
    }
}

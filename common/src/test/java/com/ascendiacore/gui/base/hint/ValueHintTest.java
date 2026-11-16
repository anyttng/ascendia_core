package com.ascendiacore.gui.base.hint;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.ascendiacore.gui.base.component.model.TooltipStyle;
import com.ascendiacore.gui.base.hint.model.HintValue;

import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.FormattedCharSequence;

class ValueHintTest {
    private static final int VALUE_COLOR = 0xFFFFFF55;
    private static final int NOTE_COLOR = 0xFFAAAAAA;
    private static final TooltipStyle STYLE = new TooltipStyle(null, 0xFFFFFFFF, VALUE_COLOR, NOTE_COLOR);
    private static final String CAPTION_KEY = "gui.example.shape";
    private static final String SEPARATOR_KEY = "options.generic_value";
    private static final String NORMAL = "Normal";
    private static final String TOROIDAL = "Toroidal";
    private static final List<HintValue> VALUES = List.of(new HintValue(Component.literal(NORMAL), "normal"),
            new HintValue(Component.literal(TOROIDAL), "toroidal"));

    private record Segment(String text, Optional<TextColor> color) {
    }

    private final Map<String, String> strings = new HashMap<>();
    private Language previous;

    @BeforeEach
    void injectLanguage() {
        previous = Language.getInstance();
        strings.put(SEPARATOR_KEY, "%s: %s");
        strings.put(CAPTION_KEY + ".hint.normal", "endless.");
        strings.put(CAPTION_KEY + ".hint.toroidal", "loops along X and Z.");
        Language.inject(new MapLanguage(strings));
    }

    @AfterEach
    void restoreLanguage() {
        Language.inject(previous);
    }

    @Test
    void oneParagraphPerValueInTheOrderGiven() {
        assertEquals("Normal: endless.\n\nToroidal: loops along X and Z.", hint().getString());
    }

    @Test
    void introComesFirstAndTheNoteLastWhereTheirKeysExist() {
        strings.put(CAPTION_KEY + ".hint.intro", "How the dimension closes.");
        strings.put(CAPTION_KEY + ".hint.note", "Fixed once the world exists.");
        assertEquals("How the dimension closes.\n\nNormal: endless.\n\nToroidal: loops along X and Z.\n\n"
                + "Fixed once the world exists.", hint().getString());
    }

    @Test
    void valueNamesTakeTheValueColorAndTheNoteItsOwn() {
        strings.put(CAPTION_KEY + ".hint.note", "Fixed once the world exists.");
        List<Segment> segments = segments(hint());
        assertEquals(color(VALUE_COLOR), colorOf(segments, NORMAL));
        assertEquals(color(VALUE_COLOR), colorOf(segments, TOROIDAL));
        assertEquals(color(NOTE_COLOR), colorOf(segments, "Fixed once the world exists."));
        assertEquals(Optional.empty(), colorOf(segments, "endless."));
    }

    @Test
    void nameAndTextAreJoinedAsTheLanguageJoinsThem() {
        strings.put(SEPARATOR_KEY, "%s : %s");
        assertEquals("Normal : endless.\n\nToroidal : loops along X and Z.", hint().getString());
    }

    @Test
    void noValuesAndNoKeysGiveAnEmptyHint() {
        assertEquals("", ValueHint.of(STYLE, CAPTION_KEY, List.of()).getString());
    }

    private static Component hint() {
        return ValueHint.of(STYLE, CAPTION_KEY, VALUES);
    }

    private static List<Segment> segments(Component hint) {
        List<Segment> segments = new ArrayList<>();
        hint.visit((style, text) -> {
            segments.add(new Segment(text, Optional.ofNullable(style.getColor())));
            return Optional.empty();
        }, Style.EMPTY);
        return segments;
    }

    private static Optional<TextColor> colorOf(List<Segment> segments, String text) {
        return segments.stream().filter(segment -> segment.text().equals(text)).findFirst().orElseThrow().color();
    }

    private static Optional<TextColor> color(int color) {
        return Optional.ofNullable(Style.EMPTY.withColor(color).getColor());
    }

    private static final class MapLanguage extends Language {
        private final Map<String, String> strings;

        MapLanguage(Map<String, String> strings) {
            this.strings = strings;
        }

        @Override
        public String getOrDefault(String elementId, String defaultValue) {
            return strings.getOrDefault(elementId, defaultValue);
        }

        @Override
        public boolean has(String elementId) {
            return strings.containsKey(elementId);
        }

        @Override
        public boolean isDefaultRightToLeft() {
            return false;
        }

        @Override
        public FormattedCharSequence getVisualOrder(FormattedText logicalOrderText) {
            return FormattedCharSequence.EMPTY;
        }
    }
}

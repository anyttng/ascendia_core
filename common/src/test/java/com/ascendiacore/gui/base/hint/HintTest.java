package com.ascendiacore.gui.base.hint;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.ascendiacore.gui.base.component.model.AlertColors;
import com.ascendiacore.gui.base.component.model.LevelColors;
import com.ascendiacore.gui.base.component.model.TooltipStyle;
import com.ascendiacore.gui.base.hint.model.HintLevel;
import com.ascendiacore.gui.base.hint.model.HintValue;

import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.FormattedCharSequence;

class HintTest {
    private static final int VALUE_COLOR = 0xFFFFFF55;
    private static final int NOTE_COLOR = 0xFFAAAAAA;
    private static final int WARNING_COLOR = 0xFFFFAA00;
    private static final int DANGER_COLOR = 0xFFFF5555;
    private static final int REQUIREMENT_COLOR = 0xFFA0A0FF;
    private static final int LOW_COLOR = 0xFF55FF55;
    private static final int MEDIUM_COLOR = 0xFFEEEE44;
    private static final int HIGH_COLOR = 0xFFEE4444;
    private static final int DERIVED_COLOR = 0xFF44EE44;
    private static final int DIVIDER_COLOR = 0xFF5A5A5A;
    private static final TooltipStyle STYLE = new TooltipStyle(null, 0xFFFFFFFF, VALUE_COLOR, NOTE_COLOR,
            new AlertColors(WARNING_COLOR, DANGER_COLOR, REQUIREMENT_COLOR),
            new LevelColors(LOW_COLOR, MEDIUM_COLOR, HIGH_COLOR), DERIVED_COLOR, DIVIDER_COLOR);
    private static final String SEPARATOR_KEY = "options.generic_value";
    private static final String DERIVED = "128 chunks = 2048 blocks";
    private static final String INTRO = "How the dimension closes.";
    private static final String NORMAL = "Normal";
    private static final String TOROIDAL = "Toroidal";
    private static final String NORMAL_TEXT = "endless.";
    private static final String TOROIDAL_TEXT = "loops along X and Z.";
    private static final String WARNING = "Most structures do not fit.";
    private static final String DANGER = "It cannot be undone.";
    private static final String REQUIREMENT = "Needs Smooth Lighting on.";
    private static final String WORD = "Warning";
    private static final String LIVE_STATE = "Not reached right now.";
    private static final String LEVEL_CAPTION = "Performance Impact";
    private static final String LEVEL_NAME = "High";
    private static final String NOTE = "Fixed once the world exists.";
    private static final String DISABLED_REASON = "Fixed: one scale only.";
    private static final String KEY_HINT = "Shift + click: previous value";
    private static final List<HintValue> VALUES = List.of(value(NORMAL, NORMAL_TEXT, false),
            value(TOROIDAL, TOROIDAL_TEXT, true));

    private record Segment(String text, Style style) {
    }

    private final Map<String, String> strings = new HashMap<>();
    private Language previous;

    @BeforeEach
    void injectLanguage() {
        previous = Language.getInstance();
        strings.put(SEPARATOR_KEY, "%s: %s");
        Language.inject(new MapLanguage(strings));
    }

    @AfterEach
    void restoreLanguage() {
        Language.inject(previous);
    }

    @Test
    void kindsComeInTheFixedOrderWhateverOrderTheyWereGivenIn() {
        Component hint = Hint.of(STYLE)
                .keyHint(text(KEY_HINT))
                .disabledReason(text(DISABLED_REASON))
                .note(text(NOTE))
                .level(text(LEVEL_CAPTION), HintLevel.HIGH, text(LEVEL_NAME))
                .liveState(text(LIVE_STATE))
                .requirement(text(REQUIREMENT))
                .danger(text(DANGER))
                .warning(text(WARNING))
                .values(VALUES)
                .intro(text(INTRO))
                .derived(text(DERIVED))
                .build();
        assertEquals(String.join("\n\n", DERIVED, INTRO, NORMAL + ": " + NORMAL_TEXT, TOROIDAL + ": " + TOROIDAL_TEXT,
                WARNING, DANGER, REQUIREMENT, LIVE_STATE, LEVEL_CAPTION + ": " + LEVEL_NAME, NOTE, DISABLED_REASON,
                KEY_HINT), hint.getString());
    }

    @Test
    void kindWithNoTextIsLeftOut() {
        Component hint = Hint.of(STYLE)
                .derived(Component.empty())
                .intro(text(INTRO))
                .warning(Component.empty())
                .danger(text(WORD), Component.empty())
                .liveState(Component.empty())
                .note(text(NOTE))
                .keyHint(Component.empty())
                .build();
        assertEquals(INTRO + "\n\n" + NOTE, hint.getString());
    }

    @Test
    void nothingGivenBuildsAnEmptyHint() {
        assertEquals("", Hint.of(STYLE).divider().build().getString());
    }

    @Test
    void eachKindTakesItsColorFromTheStyle() {
        List<Segment> segments = segments(Hint.of(STYLE)
                .derived(text(DERIVED))
                .intro(text(INTRO))
                .values(VALUES)
                .warning(text(WARNING))
                .danger(text(DANGER))
                .requirement(text(REQUIREMENT))
                .liveState(text(LIVE_STATE))
                .level(text(LEVEL_CAPTION), HintLevel.HIGH, text(LEVEL_NAME))
                .note(text(NOTE))
                .disabledReason(text(DISABLED_REASON))
                .keyHint(text(KEY_HINT))
                .build());
        assertEquals(color(DERIVED_COLOR), colorOf(segments, DERIVED));
        assertEquals(Optional.empty(), colorOf(segments, INTRO));
        assertEquals(color(VALUE_COLOR), colorOf(segments, NORMAL));
        assertEquals(Optional.empty(), colorOf(segments, NORMAL_TEXT));
        assertEquals(color(WARNING_COLOR), colorOf(segments, WARNING));
        assertEquals(color(DANGER_COLOR), colorOf(segments, DANGER));
        assertEquals(color(REQUIREMENT_COLOR), colorOf(segments, REQUIREMENT));
        assertEquals(color(NOTE_COLOR), colorOf(segments, LIVE_STATE));
        assertEquals(Optional.empty(), colorOf(segments, LEVEL_CAPTION));
        assertEquals(color(HIGH_COLOR), colorOf(segments, LEVEL_NAME));
        assertEquals(color(NOTE_COLOR), colorOf(segments, NOTE));
        assertEquals(color(NOTE_COLOR), colorOf(segments, DISABLED_REASON));
        assertEquals(color(NOTE_COLOR), colorOf(segments, KEY_HINT));
    }

    @Test
    void eachLevelTakesItsOwnColor() {
        assertEquals(color(LOW_COLOR), levelColor(HintLevel.LOW));
        assertEquals(color(MEDIUM_COLOR), levelColor(HintLevel.MEDIUM));
        assertEquals(color(HIGH_COLOR), levelColor(HintLevel.HIGH));
    }

    @Test
    void alertWithAWordColorsTheWordAndLeavesTheTextPlain() {
        List<Segment> segments = segments(Hint.of(STYLE)
                .warning(text(WORD), text(WARNING))
                .danger(text("Danger"), text(DANGER))
                .requirement(text("Requires"), text(REQUIREMENT))
                .build());
        assertEquals(color(WARNING_COLOR), colorOf(segments, WORD));
        assertEquals(Optional.empty(), colorOf(segments, WARNING));
        assertEquals(color(DANGER_COLOR), colorOf(segments, "Danger"));
        assertEquals(Optional.empty(), colorOf(segments, DANGER));
        assertEquals(color(REQUIREMENT_COLOR), colorOf(segments, "Requires"));
        assertEquals(Optional.empty(), colorOf(segments, REQUIREMENT));
    }

    @Test
    void currentValueAloneIsUnderlined() {
        List<Segment> segments = segments(Hint.of(STYLE).values(VALUES).build());
        assertFalse(styleOf(segments, NORMAL).isUnderlined());
        assertTrue(styleOf(segments, TOROIDAL).isUnderlined());
        assertFalse(styleOf(segments, TOROIDAL_TEXT).isUnderlined());
    }

    @Test
    void liveStateAndKeyHintAreItalicAndTheNoteIsNot() {
        List<Segment> segments = segments(Hint.of(STYLE)
                .liveState(text(LIVE_STATE))
                .note(text(NOTE))
                .keyHint(text(KEY_HINT))
                .build());
        assertTrue(styleOf(segments, LIVE_STATE).isItalic());
        assertTrue(styleOf(segments, KEY_HINT).isItalic());
        assertFalse(styleOf(segments, NOTE).isItalic());
    }

    @Test
    void nameAndTextAreJoinedAsTheLanguageJoinsThem() {
        strings.put(SEPARATOR_KEY, "%s : %s");
        assertEquals(NORMAL + " : " + NORMAL_TEXT + "\n\n" + TOROIDAL + " : " + TOROIDAL_TEXT,
                Hint.of(STYLE).values(VALUES).build().getString());
    }

    @Test
    void dividerStandsBeforeTheFooterWithTheFirstFooterLineRightUnderIt() {
        Component hint = Hint.of(STYLE)
                .intro(text(INTRO))
                .divider()
                .level(text(LEVEL_CAPTION), HintLevel.LOW, text(LEVEL_NAME))
                .note(text(NOTE))
                .build();
        assertEquals(INTRO + "\n\n" + Hint.RULE + "\n" + LEVEL_CAPTION + ": " + LEVEL_NAME + "\n\n" + NOTE,
                hint.getString());
    }

    @Test
    void dividerIsStruckThroughInTheDividerColorAboveALevelLine() {
        Style rule = styleOf(segments(Hint.of(STYLE)
                .intro(text(INTRO))
                .divider()
                .level(text(LEVEL_CAPTION), HintLevel.LOW, text(LEVEL_NAME))
                .build()), Hint.RULE);
        assertTrue(rule.isStrikethrough());
        assertEquals(color(DIVIDER_COLOR), Optional.ofNullable(rule.getColor()));
    }

    @Test
    void dividerTakesTheNoteColorAboveAGrayParagraph() {
        Style rule = styleOf(segments(Hint.of(STYLE).intro(text(INTRO)).divider().note(text(NOTE)).build()), Hint.RULE);
        assertEquals(color(NOTE_COLOR), Optional.ofNullable(rule.getColor()));
    }

    @Test
    void noDividerWithoutAFooterOrWithoutTheCall() {
        assertEquals(INTRO, Hint.of(STYLE).intro(text(INTRO)).divider().build().getString());
        assertEquals(INTRO + "\n\n" + NOTE, Hint.of(STYLE).intro(text(INTRO)).note(text(NOTE)).build().getString());
    }

    private static HintValue value(String name, String text, boolean current) {
        return new HintValue(text(name), text(text), current);
    }

    private static Component text(String text) {
        return Component.literal(text);
    }

    private static Optional<TextColor> levelColor(HintLevel level) {
        return colorOf(segments(Hint.of(STYLE).level(text(LEVEL_CAPTION), level, text(LEVEL_NAME)).build()), LEVEL_NAME);
    }

    private static List<Segment> segments(Component hint) {
        List<Segment> segments = new ArrayList<>();
        hint.visit((style, text) -> {
            segments.add(new Segment(text, style));
            return Optional.empty();
        }, Style.EMPTY);
        return segments;
    }

    private static Style styleOf(List<Segment> segments, String text) {
        return segments.stream().filter(segment -> segment.text().equals(text)).findFirst().orElseThrow().style();
    }

    private static Optional<TextColor> colorOf(List<Segment> segments, String text) {
        return Optional.ofNullable(styleOf(segments, text).getColor());
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

package com.ascendiacore.gui.widget.segmented;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import com.ascendiacore.gui.base.component.model.AlertColors;
import com.ascendiacore.gui.base.component.model.LevelColors;
import com.ascendiacore.gui.base.component.model.TooltipStyle;
import com.ascendiacore.gui.base.draw.RecordingCanvas;
import com.ascendiacore.gui.base.geometry.Rect;
import com.ascendiacore.gui.base.geometry.Size;
import com.ascendiacore.gui.base.hint.Hint;
import com.ascendiacore.gui.base.hint.model.HintValue;
import com.ascendiacore.gui.base.input.KeyInput;
import com.ascendiacore.gui.base.input.PointerInput;
import com.ascendiacore.gui.base.input.model.Key;
import com.ascendiacore.gui.base.input.model.Modifiers;
import com.ascendiacore.gui.base.input.model.PointerButton;
import com.ascendiacore.gui.base.property.Value;
import com.ascendiacore.gui.widget.button.model.ButtonStyle;

import net.minecraft.network.chat.Component;

class SegmentedControlTest {
    private static final int LABEL_COLOR = 0xFF112233;
    private static final int DISABLED_LABEL_COLOR = 0xFF445566;
    private static final int SELECTED_LABEL_COLOR = 0xFF778899;
    private static final ButtonStyle STYLE = new ButtonStyle("test:normal", "test:hover", "test:disabled",
            "test:selected", "test:selected_hover", LABEL_COLOR, DISABLED_LABEL_COLOR, SELECTED_LABEL_COLOR, false);
    private static final List<String> VALUES = List.of("Off", "Auto", "Strong");
    private static final Component NAME = Component.literal("Compact biomes");
    private static final TooltipStyle TOOLTIP = new TooltipStyle(null, 0xFFFFFFFF, 0xFFFFFF55, 0xFFAAAAAA,
            new AlertColors(0xFFFFAA00, 0xFFFF5555, 0xFFA0A0FF), new LevelColors(0xFF55FF55, 0xFFFFFF55, 0xFFFF5555),
            0xFF55FF55, 0xFF5A5A5A);
    private static final String CAPTION_KEY = "gui.example.compact_biomes";
    private static final int LEFT = 100;
    private static final int TOP = 50;
    private static final int WIDTH_PIXELS = 120;
    private static final int HEIGHT_PIXELS = 20;
    private static final int SECOND_LEFT = 140;
    private static final int THIRD_LEFT = 180;
    private static final int RIGHT = LEFT + WIDTH_PIXELS;
    private static final int OVERDRAW_PIXELS = 10;
    private static final int TEXT_WIDTH_PIXELS = 10;
    private static final int SIDE_PADDING_PIXELS = 8;
    private static final int INSIDE_Y = TOP + 5;
    private static final int ON_SECOND_X = SECOND_LEFT + 10;
    private static final int OUTSIDE_X = LEFT - 5;

    private final RecordingCanvas canvas = new RecordingCanvas();
    private final Value<String> chosen = Value.of("Off");
    private final List<String> changes = new ArrayList<>();
    private int clicks;

    @Test
    void pressPicksTheSegmentUnderThePointerOnce() {
        SegmentedControl<String> control = control();
        assertTrue(control.tryPressPointer(press(ON_SECOND_X)));
        assertEquals("Auto", chosen.get());
        assertEquals(List.of("Auto"), changes);
        assertEquals(1, clicks);
    }

    @Test
    void pressOnTheChosenSegmentChangesNothing() {
        SegmentedControl<String> control = control();
        assertTrue(control.tryPressPointer(press(LEFT + 5)));
        assertEquals("Off", chosen.get());
        assertEquals(List.of(), changes);
        assertEquals(0, clicks);
    }

    @Test
    void arrowsMoveTheChoiceAndStopAtTheEnds() {
        SegmentedControl<String> control = control();
        assertTrue(control.tryPressKey(key(Key.LEFT)));
        assertEquals("Off", chosen.get());
        assertTrue(control.tryPressKey(key(Key.RIGHT)));
        assertTrue(control.tryPressKey(key(Key.RIGHT)));
        assertTrue(control.tryPressKey(key(Key.RIGHT)));
        assertEquals("Strong", chosen.get());
        assertEquals(List.of("Auto", "Strong"), changes);
        assertEquals(2, clicks);
    }

    @Test
    void otherKeysAreLeft() {
        SegmentedControl<String> control = control();
        assertFalse(control.tryPressKey(key(Key.ENTER)));
        assertFalse(control.tryPressKey(key(Key.UP)));
        assertEquals("Off", chosen.get());
    }

    @Test
    void valueSetFromOutsideMovesTheSelection() {
        SegmentedControl<String> control = control();
        chosen.set("Strong");
        control.draw(canvas, OUTSIDE_X, INSIDE_Y);
        assertEquals(List.of("Strong"), textsColored(SELECTED_LABEL_COLOR));
        assertEquals(List.of("test:normal", "test:normal", "test:selected"), bodySprites());
    }

    @Test
    void segmentsShareTheWidthAndOneFrameColumnBetweenNeighbors() {
        SegmentedControl<String> control = control();
        chosen.set("Strong");
        control.draw(canvas, OUTSIDE_X, INSIDE_Y);
        List<Rect> bodies = canvas.clips().stream().filter(clip -> clip.widthPixels() > 1).toList();
        assertEquals(List.of(new Rect(LEFT, TOP, SECOND_LEFT + 1 - LEFT, HEIGHT_PIXELS),
                new Rect(SECOND_LEFT, TOP, THIRD_LEFT + 1 - SECOND_LEFT, HEIGHT_PIXELS),
                new Rect(THIRD_LEFT, TOP, RIGHT - THIRD_LEFT, HEIGHT_PIXELS)), bodies);
        List<Integer> columns = canvas.clips().stream().filter(clip -> clip.widthPixels() == 1).map(Rect::x).toList();
        assertEquals(List.of(SECOND_LEFT, SECOND_LEFT, THIRD_LEFT, THIRD_LEFT), columns);
    }

    @Test
    void bodySpriteReachesPastTheInnerEdgesOnly() {
        SegmentedControl<String> control = control();
        chosen.set("Strong");
        control.draw(canvas, OUTSIDE_X, INSIDE_Y);
        List<RecordingCanvas.Sprite> bodies = bodySpriteRecords();
        assertEquals(LEFT, bodies.get(0).x());
        assertEquals(SECOND_LEFT + 1 + OVERDRAW_PIXELS - LEFT, bodies.get(0).widthPixels());
        assertEquals(SECOND_LEFT - OVERDRAW_PIXELS, bodies.get(1).x());
        assertEquals(THIRD_LEFT + 1 + OVERDRAW_PIXELS - (SECOND_LEFT - OVERDRAW_PIXELS), bodies.get(1).widthPixels());
        assertEquals(THIRD_LEFT - OVERDRAW_PIXELS, bodies.get(2).x());
        assertEquals(RIGHT - (THIRD_LEFT - OVERDRAW_PIXELS), bodies.get(2).widthPixels());
    }

    @Test
    void chosenAndHoveredSegmentsDrawLast() {
        SegmentedControl<String> control = control();
        control.draw(canvas, ON_SECOND_X, INSIDE_Y);
        assertEquals(List.of("test:normal", "test:selected", "test:hover"), bodySprites());
    }

    @Test
    void focusHighlightsTheChosenSegment() {
        SegmentedControl<String> control = control();
        control.setFocused(true);
        control.draw(canvas, OUTSIDE_X, INSIDE_Y);
        assertEquals(List.of("test:normal", "test:normal", "test:selected_hover"), bodySprites());
    }

    @Test
    void disabledGroupKeepsTheChosenLabelReadable() {
        SegmentedControl<String> control = control();
        control.setEnabled(false);
        control.draw(canvas, ON_SECOND_X, INSIDE_Y);
        assertEquals(List.of("test:disabled", "test:disabled", "test:disabled"), bodySprites());
        assertEquals(List.of("Off"), textsColored(LABEL_COLOR));
        assertEquals(List.of("Auto", "Strong"), textsColored(DISABLED_LABEL_COLOR));
        assertFalse(control.tryPressPointer(press(ON_SECOND_X)));
        assertEquals("Off", chosen.get());
    }

    @Test
    void naturalWidthFitsTheWidestLabelInEverySegment() {
        SegmentedControl<String> control = control();
        Size natural = control.naturalSize(text -> text.getString().length());
        assertEquals(new Size(VALUES.size() * ("Strong".length() + 2 * SIDE_PADDING_PIXELS), HEIGHT_PIXELS), natural);
    }

    @Test
    void hintNamesTheSegmentUnderThePointer() {
        SegmentedControl<String> control = control();
        control.setHint(TOOLTIP, CAPTION_KEY, SegmentedControlTest::valueKey);
        assertEquals(hint("Off"), control.getTooltipAt(LEFT + 5, INSIDE_Y));
        assertEquals(hint("Auto"), control.getTooltipAt(ON_SECOND_X, INSIDE_Y));
        assertEquals(hint("Strong"), control.getTooltipAt(THIRD_LEFT + 5, INSIDE_Y));
        assertEquals(TOOLTIP, control.getTooltipStyle());
    }

    @Test
    void hintFollowsThePointerNotThePick() {
        SegmentedControl<String> control = control();
        control.setHint(TOOLTIP, CAPTION_KEY, SegmentedControlTest::valueKey);
        control.tryPressPointer(press(ON_SECOND_X));
        assertEquals(hint("Off"), control.getTooltipAt(LEFT + 5, INSIDE_Y));
    }

    @Test
    void hintAwayFromThePointerNamesTheChosenValue() {
        SegmentedControl<String> control = control();
        control.setHint(TOOLTIP, CAPTION_KEY, SegmentedControlTest::valueKey);
        chosen.set("Strong");
        assertEquals(hint("Strong"), control.getTooltipAt(OUTSIDE_X, INSIDE_Y));
    }

    private static Component hint(String value) {
        return Hint.of(TOOLTIP).values(List.of(new HintValue(Component.literal(value),
                Component.translatable(CAPTION_KEY + ".hint." + valueKey(value)), false))).build();
    }

    private static String valueKey(String value) {
        return value.toLowerCase(Locale.ROOT);
    }

    private SegmentedControl<String> control() {
        SegmentedControl<String> control = new SegmentedControl<>(STYLE, NAME, () -> VALUES, chosen, Component::literal);
        control.setOnChange(changes::add);
        control.attach(() -> clicks++);
        control.setPosition(LEFT, TOP);
        control.setSize(WIDTH_PIXELS, HEIGHT_PIXELS);
        canvas.setTextWidth(TEXT_WIDTH_PIXELS);
        return control;
    }

    private List<RecordingCanvas.Sprite> bodySpriteRecords() {
        List<RecordingCanvas.Sprite> sprites = canvas.sprites();
        List<Rect> clips = canvas.clips();
        assertEquals(clips.size(), sprites.size());
        List<RecordingCanvas.Sprite> bodies = new ArrayList<>();
        for (int index = 0; index < sprites.size(); index++) {
            if (clips.get(index).widthPixels() > 1) {
                bodies.add(sprites.get(index));
            }
        }
        return bodies;
    }

    private List<String> bodySprites() {
        return bodySpriteRecords().stream().map(RecordingCanvas.Sprite::sprite).toList();
    }

    private List<String> textsColored(int color) {
        return canvas.texts().stream().filter(text -> text.color() == color).map(text -> text.text().getString()).toList();
    }

    private static PointerInput press(int x) {
        return new PointerInput(x, INSIDE_Y, PointerButton.LEFT, Modifiers.NONE);
    }

    private static KeyInput key(Key key) {
        return new KeyInput(key, Modifiers.NONE);
    }
}

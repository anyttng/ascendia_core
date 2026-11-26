package com.ascendiacore.gui.widget.textfield;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.ascendiacore.gui.base.draw.RecordingCanvas;
import com.ascendiacore.gui.base.geometry.Size;
import com.ascendiacore.gui.widget.textfield.model.TextFieldStyle;

import net.minecraft.network.chat.Component;

class TextFieldTest {
    private static final int LEFT = 100;
    private static final int TOP = 50;
    private static final int WIDTH_PIXELS = 120;
    private static final int HEIGHT_PIXELS = 20;
    private static final int TEXT_PADDING_PIXELS = 4;
    private static final int TEXT_HEIGHT_PIXELS = 8;
    private static final int OUTSIDE = Integer.MIN_VALUE;
    private static final int TEXT = 0xFF112233;
    private static final int DISABLED_TEXT = 0xFF445566;
    private static final int PLACEHOLDER = 0xFF778899;
    private static final Component HINT = Component.literal("Type here");
    private static final TextFieldStyle STYLE = new TextFieldStyle("test:normal", "test:focused", "test:disabled",
            TEXT, DISABLED_TEXT, PLACEHOLDER, false);

    private final List<String> reported = new ArrayList<>();

    @Test
    void frameFollowsFocusAndDisabledWinsOverFocus() {
        TextField field = field();
        assertEquals("test:normal", drawnSprite(field));
        field.setFocused(true);
        assertEquals("test:focused", drawnSprite(field));
        field.setEnabled(false);
        assertEquals("test:disabled", drawnSprite(field));
    }

    @Test
    void placeholderIsDrawnInItsColorInsideThePaddingWhileEmpty() {
        TextField field = field();
        RecordingCanvas canvas = draw(field);
        assertEquals(List.of(new RecordingCanvas.Text(HINT, LEFT + TEXT_PADDING_PIXELS, TOP + (HEIGHT_PIXELS - TEXT_HEIGHT_PIXELS) / 2,
                PLACEHOLDER, false)), canvas.texts());
    }

    @Test
    void placeholderStaysWhileFocusedAndDisabled() {
        TextField field = field();
        field.setFocused(true);
        assertEquals(1, draw(field).texts().size());
        field.setEnabled(false);
        assertEquals(1, draw(field).texts().size());
    }

    @Test
    void placeholderGoesOnceThereIsText() {
        TextField field = field();
        field.setValue("Filled");
        assertTrue(draw(field).texts().isEmpty());
    }

    @Test
    void anEditIsReportedOnceAndAnUnchangedOneNotAtAll() {
        TextField field = field();
        field.edited("a");
        field.edited("a");
        field.edited("ab");
        assertEquals(List.of("a", "ab"), reported);
        assertEquals("ab", field.getValue());
    }

    @Test
    void aValueTheScreenSetsIsNotReportedBack() {
        TextField field = field();
        field.setValue("Set");
        field.edited("Set");
        assertTrue(reported.isEmpty());
    }

    @Test
    void disabledFieldShowsTheDisabledTextAndEnabledItsOwnValue() {
        TextField field = field();
        field.setValue("6");
        field.setDisabledText(() -> "6.24");
        assertEquals("6", field.getShownValue());
        field.setEnabled(false);
        assertEquals("6.24", field.getShownValue());
        assertEquals("6", field.getValue());
        assertTrue(draw(field).texts().isEmpty());
    }

    @Test
    void anEditWhileDisabledChangesNothing() {
        TextField field = field();
        field.setValue("6");
        field.setEnabled(false);
        field.edited("6.24");
        assertEquals("6", field.getValue());
        assertTrue(reported.isEmpty());
    }

    @Test
    void acceptedKeepsOnlyTheCharactersTheRuleLets() {
        TextField field = field();
        assertEquals("a1-", field.filterAccepted("a1-"));
        field.setAccepted(Character::isDigit);
        assertEquals("12", field.filterAccepted("1a2"));
        assertEquals("", field.filterAccepted("ab"));
    }

    @Test
    void maxLengthCutsTheValue() {
        TextField field = field();
        field.setValue("abcdef");
        field.setMaxCharacterCount(3);
        assertEquals("abc", field.getValue());
        field.setValue("wxyz");
        assertEquals("wxy", field.getValue());
    }

    @Test
    void textAreaSitsInsideThePaddingAndCentersTheLine() {
        TextField field = field();
        assertEquals(LEFT + TEXT_PADDING_PIXELS, field.getTextLeft());
        assertEquals(TOP + (HEIGHT_PIXELS - TEXT_HEIGHT_PIXELS) / 2, field.getTextTop());
        assertEquals(WIDTH_PIXELS - 2 * TEXT_PADDING_PIXELS, field.getTextRoomPixels());
    }

    @Test
    void naturalHeightIsTheGamesFieldHeight() {
        TextField field = new TextField(STYLE, reported::add);
        assertEquals(new Size(0, HEIGHT_PIXELS), field.naturalSize(text -> 0));
    }

    private TextField field() {
        TextField field = new TextField(STYLE, reported::add);
        field.setPlaceholder(HINT);
        field.setPosition(LEFT, TOP);
        field.setSize(WIDTH_PIXELS, HEIGHT_PIXELS);
        return field;
    }

    private static String drawnSprite(TextField field) {
        return draw(field).sprites().getFirst().sprite();
    }

    private static RecordingCanvas draw(TextField field) {
        RecordingCanvas canvas = new RecordingCanvas();
        field.draw(canvas, OUTSIDE, OUTSIDE);
        return canvas;
    }
}

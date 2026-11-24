package com.ascendiacore.gui.widget.numberfield;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.ascendiacore.gui.base.draw.RecordingCanvas;
import com.ascendiacore.gui.base.property.Property;
import com.ascendiacore.gui.base.property.Value;
import com.ascendiacore.gui.layout.Column;
import com.ascendiacore.gui.widget.numberfield.model.Bounds;
import com.ascendiacore.gui.widget.panel.model.FillStyle;
import com.ascendiacore.gui.widget.textfield.TextField;
import com.ascendiacore.gui.widget.textfield.model.TextFieldStyle;

import net.minecraft.network.chat.Component;

class NumberFieldTest {
    private static final FillStyle FILL = new FillStyle(0xFF000000, 0xFF111111, 0xFF222222, 0xFF333333, true);
    private static final TextFieldStyle STYLE = new TextFieldStyle("test:normal", "test:focused", "test:disabled",
            0xFF444444, 0xFF555555, 0xFF666666, false);
    private static final String TOO_SMALL_KEY = "gui.ascendia_core.number_field.too_small";
    private static final String TOO_LARGE_KEY = "gui.ascendia_core.number_field.too_large";
    private static final Component CAPTION = Component.literal("Size along X in chunks");
    private static final Component HINT = Component.literal("Plain torus");
    private static final Bounds SIZE_PIXELS = new Bounds(8, 4096);
    private static final int CHAR_WIDTH_PIXELS = 6;
    private static final int WIDTH_PIXELS = 300;
    private static final int HEIGHT_PIXELS = 42;
    private static final int TEXT_FIELD = 1;
    private static final int LINE_INDEX = 2;

    private final RecordingCanvas canvas = canvas();
    private int changes;

    @Test
    void takesDigitsOnly() {
        assertEquals("123", textField(field()).filterAccepted("1-2a3"));
    }

    @Test
    void signedTakesTheMinusToo() {
        NumberField field = field();
        field.setSigned(true);
        assertEquals("-1-23", textField(field).filterAccepted("-1-2a3"));
    }

    @Test
    void numberInsideTheBoundsIsTheValue() {
        NumberField field = field();
        field.setText("64");
        assertEquals(64, field.getValue());
        assertTrue(textField(field).isValid());
    }

    @Test
    void bothEndsAreInside() {
        NumberField field = field();
        field.setValue(SIZE_PIXELS.min());
        assertEquals(SIZE_PIXELS.min(), field.getValue());
        field.setValue(SIZE_PIXELS.max());
        assertEquals(SIZE_PIXELS.max(), field.getValue());
    }

    @Test
    void numberBelowTheBoundsNamesTheMinimumAsAnError() {
        NumberField field = field();
        field.setText("7");
        assertNull(field.getValue());
        assertFalse(textField(field).isValid());
        assertLine(Component.translatable(TOO_SMALL_KEY, SIZE_PIXELS.min()), FILL.errorInk(), field);
    }

    @Test
    void numberAboveTheBoundsNamesTheMaximumAsAnError() {
        NumberField field = field();
        field.setText("4097");
        assertNull(field.getValue());
        assertLine(Component.translatable(TOO_LARGE_KEY, SIZE_PIXELS.max()), FILL.errorInk(), field);
    }

    @Test
    void textThatIsNoNumberIsInvalidAndShowsTheHint() {
        NumberField field = field();
        field.setSigned(true);
        field.setHint(Property.of(HINT));
        for (String text : new String[] {"", "-", "1-2"}) {
            field.setText(text);
            assertNull(field.getValue());
            assertLine(HINT, FILL.mutedInk(), field);
        }
    }

    @Test
    void unknownBoundsLeaveTheFieldInvalidAndShowTheHint() {
        NumberField field = new NumberField(FILL, STYLE, CAPTION, Optional::empty, () -> changes++);
        field.setHint(Property.of(HINT));
        field.setText("64");
        assertNull(field.getValue());
        assertLine(HINT, FILL.mutedInk(), field);
    }

    @Test
    void boundsFollowTheirRule() {
        Value<Optional<Bounds>> bounds = Value.of(Optional.of(SIZE_PIXELS));
        NumberField field = new NumberField(FILL, STYLE, CAPTION, bounds, () -> changes++);
        field.setText("10");
        assertEquals(10, field.getValue());
        bounds.set(Optional.of(new Bounds(0, 9)));
        assertNull(field.getValue());
        assertLine(Component.translatable(TOO_LARGE_KEY, 9), FILL.errorInk(), field);
    }

    @Test
    void valueLineShowsOnlyForANumberInsideTheBounds() {
        NumberField field = field();
        field.setValueLine(value -> Component.literal(value + " chunks"));
        field.setHint(Property.of(HINT));
        field.setText("64");
        assertEquals("64 chunks", drawnLine(field).text().getString());
        field.setText("7");
        assertLine(Component.translatable(TOO_SMALL_KEY, SIZE_PIXELS.min()), FILL.errorInk(), field);
    }

    @Test
    void anEditIsReportedAndSettingTheValueIsNot() {
        NumberField field = field();
        field.setValue(64);
        assertEquals(0, changes);
        textField(field).edited("65");
        assertEquals(1, changes);
    }

    @Test
    void formHoldsDoneBackWhileTheNumberIsOutOfBounds() {
        NumberField field = field();
        Column form = new Column();
        form.add(field);
        field.setText("7");
        assertFalse(form.areFieldsValid());
        field.setText("64");
        assertTrue(form.areFieldsValid());
    }

    @Test
    void disabledFieldDoesNotHoldTheFormBack() {
        NumberField field = field();
        Column form = new Column();
        form.add(field);
        field.setText("7");
        field.setEnabled(false);
        assertTrue(form.areFieldsValid());
    }

    @Test
    void boundsRefuseAMinimumAboveTheMaximum() {
        assertThrows(IllegalArgumentException.class, () -> new Bounds(2, 1));
    }

    private NumberField field() {
        return new NumberField(FILL, STYLE, CAPTION, Property.of(Optional.of(SIZE_PIXELS)), () -> changes++);
    }

    private void assertLine(Component text, int color, NumberField field) {
        RecordingCanvas.Text drawn = drawnLine(field);
        assertEquals(text, drawn.text());
        assertEquals(color, drawn.color());
    }

    private RecordingCanvas.Text drawnLine(NumberField field) {
        field.setPosition(0, 0);
        field.setSize(WIDTH_PIXELS, HEIGHT_PIXELS);
        field.layout(canvas);
        field.getMountedComponents().get(LINE_INDEX).draw(canvas, 0, 0);
        return canvas.texts().getLast();
    }

    private static TextField textField(NumberField field) {
        return (TextField) field.getMountedComponents().get(TEXT_FIELD);
    }

    private static RecordingCanvas canvas() {
        RecordingCanvas canvas = new RecordingCanvas();
        canvas.setCharWidthPixels(CHAR_WIDTH_PIXELS);
        return canvas;
    }
}

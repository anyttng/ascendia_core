package com.ascendiacore.gui.widget.field;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.ascendiacore.gui.base.component.GuiComponent;
import com.ascendiacore.gui.base.draw.Canvas;
import com.ascendiacore.gui.base.draw.RecordingCanvas;
import com.ascendiacore.gui.layout.Row;
import com.ascendiacore.gui.layout.model.Length;
import com.ascendiacore.gui.widget.panel.model.FillStyle;

import net.minecraft.network.chat.Component;

class FieldTest {
    private static final FillStyle FILL = new FillStyle(0xFF000000, 0xFF111111, 0xFF222222, 0xFF333333, true);
    private static final int CHAR_WIDTH_PIXELS = 6;
    private static final int LINE_HEIGHT_PIXELS = 9;
    private static final int CONTROL_HEIGHT_PIXELS = 20;
    private static final int GAP_PIXELS = 2;
    private static final int FIELD_HEIGHT_PIXELS = LINE_HEIGHT_PIXELS + GAP_PIXELS + CONTROL_HEIGHT_PIXELS + GAP_PIXELS + LINE_HEIGHT_PIXELS;
    private static final int LEFT = 10;
    private static final int TOP = 20;
    private static final int WIDTH_PIXELS = 120;
    private static final String CAPTION = "Size along X";
    private static final String ERROR = "Too large";
    private static final String VALUE = "2048 blocks";
    private static final String HINT = "Plain torus";
    private static final String NONE = "";

    private final RecordingCanvas canvas = canvas();

    @Test
    void errorWinsOverTheValueAndTheHint() {
        assertLine(ERROR, FILL.errorInk(), line(ERROR, VALUE, HINT));
        assertLine(ERROR, FILL.errorInk(), line(ERROR, VALUE, NONE));
        assertLine(ERROR, FILL.errorInk(), line(ERROR, NONE, HINT));
        assertLine(ERROR, FILL.errorInk(), line(ERROR, NONE, NONE));
    }

    @Test
    void valueWinsOverTheHint() {
        assertLine(VALUE, FILL.mutedInk(), line(NONE, VALUE, HINT));
        assertLine(VALUE, FILL.mutedInk(), line(NONE, VALUE, NONE));
    }

    @Test
    void hintShowsWhenNothingElseDoes() {
        assertLine(HINT, FILL.mutedInk(), line(NONE, NONE, HINT));
    }

    @Test
    void emptyLineKeepsItsRoom() {
        Field empty = field(new Control());
        Field filled = field(new Control());
        filled.setHint(() -> Component.literal(HINT));
        assertEquals(FIELD_HEIGHT_PIXELS, empty.naturalSize(canvas).heightPixels());
        assertEquals(FIELD_HEIGHT_PIXELS, filled.naturalSize(canvas).heightPixels());
        assertEquals(NONE, drawnLine(empty).text().getString());
    }

    @Test
    void captionAboveTheControlAcrossTheWidthAndTheLineUnderIt() {
        Control control = new Control();
        Field field = laidOut(field(control));
        List<GuiComponent> parts = field.getMountedComponents();
        assertBounds(parts.get(0), TOP, LINE_HEIGHT_PIXELS);
        assertBounds(control, TOP + LINE_HEIGHT_PIXELS + GAP_PIXELS, CONTROL_HEIGHT_PIXELS);
        assertBounds(parts.get(2), TOP + LINE_HEIGHT_PIXELS + GAP_PIXELS + CONTROL_HEIGHT_PIXELS + GAP_PIXELS, LINE_HEIGHT_PIXELS);
        assertEquals(control, parts.get(1));
    }

    @Test
    void captionTakesTheNormalInkAndFollowsItsRule() {
        Field field = field(new Control());
        field.setCaption(() -> Component.literal("Size along Z"));
        RecordingCanvas.Text caption = drawnCaption(field);
        assertEquals("Size along Z", caption.text().getString());
        assertEquals(FILL.ink(), caption.color());
    }

    @Test
    void twoFieldsInARowTakeHalfEach() {
        Row row = new Row();
        row.setPosition(LEFT, TOP);
        row.setSize(2 * WIDTH_PIXELS + GAP_PIXELS, FIELD_HEIGHT_PIXELS);
        row.setGapPixels(GAP_PIXELS);
        Field first = field(new Control());
        Field second = field(new Control());
        row.add(first, Length.fill());
        row.add(second, Length.fill());
        row.layout(canvas);
        assertEquals(WIDTH_PIXELS, first.getWidthPixels());
        assertEquals(WIDTH_PIXELS, second.getWidthPixels());
        assertEquals(LEFT + WIDTH_PIXELS + GAP_PIXELS, second.getX());
    }

    @Test
    void disabledControlMutesTheCaptionAndTheLineAndDropsTheError() {
        Control control = new Control();
        control.setEnabled(false);
        Field field = withLines(field(control), ERROR, VALUE, HINT);
        assertEquals(FILL.mutedInk(), drawnCaption(field).color());
        assertLine(VALUE, FILL.mutedInk(), drawnLine(field));
    }

    @Test
    void disabledFieldDisablesItsControlAndReadsTheSame() {
        Field field = withLines(field(new Control()), ERROR, NONE, HINT);
        field.setEnabled(false);
        assertEquals(FILL.mutedInk(), drawnCaption(field).color());
        assertLine(HINT, FILL.mutedInk(), drawnLine(field));
    }

    private RecordingCanvas.Text line(String error, String value, String hint) {
        return drawnLine(withLines(field(new Control()), error, value, hint));
    }

    private static Field withLines(Field field, String error, String value, String hint) {
        field.setError(() -> Component.literal(error));
        field.setValueLine(() -> Component.literal(value));
        field.setHint(() -> Component.literal(hint));
        return field;
    }

    private RecordingCanvas.Text drawnCaption(Field field) {
        return isDrawn(field, 0);
    }

    private RecordingCanvas.Text drawnLine(Field field) {
        return isDrawn(field, 2);
    }

    private RecordingCanvas.Text isDrawn(Field field, int part) {
        laidOut(field).getMountedComponents().get(part).draw(canvas, 0, 0);
        return canvas.texts().getLast();
    }

    private Field laidOut(Field field) {
        field.setPosition(LEFT, TOP);
        field.setSize(WIDTH_PIXELS, FIELD_HEIGHT_PIXELS);
        field.layout(canvas);
        return field;
    }

    private static Field field(Control control) {
        return new Field(FILL, Component.literal(CAPTION), control);
    }

    private static void assertLine(String text, int color, RecordingCanvas.Text drawn) {
        assertEquals(text, drawn.text().getString());
        assertEquals(color, drawn.color());
    }

    private static void assertBounds(GuiComponent part, int top, int heightPixels) {
        assertEquals(LEFT, part.getX());
        assertEquals(top, part.getY());
        assertEquals(WIDTH_PIXELS, part.getWidthPixels());
        assertEquals(heightPixels, part.getHeightPixels());
    }

    private static RecordingCanvas canvas() {
        RecordingCanvas canvas = new RecordingCanvas();
        canvas.setCharWidthPixels(CHAR_WIDTH_PIXELS);
        return canvas;
    }

    private static final class Control extends GuiComponent {
        Control() {
            setSize(0, CONTROL_HEIGHT_PIXELS);
        }

        @Override
        protected void drawContent(Canvas canvas) {
        }
    }
}

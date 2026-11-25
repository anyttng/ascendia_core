package com.ascendiacore.gui.widget.label;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.ascendiacore.gui.base.draw.RecordingCanvas;
import com.ascendiacore.gui.base.geometry.Size;
import com.ascendiacore.gui.base.input.PointerInput;
import com.ascendiacore.gui.base.input.model.Modifiers;
import com.ascendiacore.gui.base.input.model.PointerButton;
import com.ascendiacore.gui.widget.label.model.LabelRole;
import com.ascendiacore.gui.widget.label.model.TextAlign;
import com.ascendiacore.gui.widget.panel.model.FillStyle;

import net.minecraft.network.chat.Component;

class LabelTest {
    private static final FillStyle FILL = new FillStyle(0xFF000000, 0xFF111111, 0xFF222222, 0xFF333333, true);
    private static final Component TEXT = Component.literal("Value");
    private static final int CHAR_WIDTH_PIXELS = 6;
    private static final int TEXT_WIDTH_PIXELS = 5 * CHAR_WIDTH_PIXELS;
    private static final int LINE_HEIGHT_PIXELS = 9;
    private static final int LEFT = 40;
    private static final int TOP = 30;
    private static final int BOX_WIDTH_PIXELS = 60;
    private static final int BOX_HEIGHT_PIXELS = 20;
    private static final int TEXT_TOP = TOP + (BOX_HEIGHT_PIXELS - LINE_HEIGHT_PIXELS) / 2;

    private final RecordingCanvas canvas = canvas();

    @Test
    void naturalSizeIsTheWidthOfTheTextOnOneLine() {
        assertEquals(new Size(TEXT_WIDTH_PIXELS, LINE_HEIGHT_PIXELS), new Label(FILL, LabelRole.NORMAL, TEXT).naturalSize(canvas));
    }

    @Test
    void leftAlignedTextStartsAtTheLeftEdgeCenteredVertically() {
        RecordingCanvas.Text drawn = isDrawn(TextAlign.LEFT);
        assertEquals(LEFT, drawn.x());
        assertEquals(TEXT_TOP, drawn.y());
    }

    @Test
    void centeredTextSplitsTheSpareWidth() {
        assertEquals(LEFT + (BOX_WIDTH_PIXELS - TEXT_WIDTH_PIXELS) / 2, isDrawn(TextAlign.CENTER).x());
    }

    @Test
    void rightAlignedTextEndsAtTheRightEdge() {
        assertEquals(LEFT + BOX_WIDTH_PIXELS - TEXT_WIDTH_PIXELS, isDrawn(TextAlign.RIGHT).x());
    }

    @Test
    void textWiderThanTheBoxIsCutWithAnEllipsisAndFillsTheBox() {
        Label label = place(new Label(FILL, LabelRole.NORMAL, Component.literal("Settings")), 5 * CHAR_WIDTH_PIXELS);
        label.setAlign(TextAlign.RIGHT);
        label.draw(canvas, 0, 0);
        RecordingCanvas.Text drawn = canvas.texts().getFirst();
        assertEquals("Se...", drawn.text().getString());
        assertEquals(LEFT, drawn.x());
    }

    @Test
    void eachRoleTakesItsInkFromTheFillAndTheFillDecidesTheShadow() {
        place(new Label(FILL, LabelRole.NORMAL, TEXT), BOX_WIDTH_PIXELS).draw(canvas, 0, 0);
        place(new Label(FILL, LabelRole.MUTED, TEXT), BOX_WIDTH_PIXELS).draw(canvas, 0, 0);
        place(new Label(FILL, LabelRole.ERROR, TEXT), BOX_WIDTH_PIXELS).draw(canvas, 0, 0);
        assertEquals(FILL.ink(), canvas.texts().get(0).color());
        assertEquals(FILL.mutedInk(), canvas.texts().get(1).color());
        assertEquals(FILL.errorInk(), canvas.texts().get(2).color());
        assertTrue(canvas.texts().get(0).hasShadow());
        FillStyle flat = new FillStyle(0xFF000000, 0xFF111111, 0xFF222222, 0xFF333333, false);
        place(new Label(flat, LabelRole.MUTED, TEXT), BOX_WIDTH_PIXELS).draw(canvas, 0, 0);
        assertFalse(canvas.texts().get(3).hasShadow());
    }

    @Test
    void roleFollowsItsRule() {
        Label label = place(new Label(FILL, LabelRole.NORMAL, TEXT), BOX_WIDTH_PIXELS);
        LabelRole[] current = {LabelRole.NORMAL};
        label.setRole(() -> current[0]);
        current[0] = LabelRole.ERROR;
        label.draw(canvas, 0, 0);
        assertEquals(FILL.errorInk(), canvas.texts().getFirst().color());
    }

    @Test
    void textFollowsItsRule() {
        Label label = place(new Label(FILL, LabelRole.NORMAL, TEXT), BOX_WIDTH_PIXELS);
        Component[] current = {TEXT};
        label.setText(() -> current[0]);
        current[0] = Component.literal("Other");
        label.draw(canvas, 0, 0);
        assertEquals("Other", canvas.texts().getFirst().text().getString());
        assertEquals("Other", label.getNarration().getString());
    }

    @Test
    void labelTakesNoInput() {
        Label label = place(new Label(FILL, LabelRole.NORMAL, TEXT), BOX_WIDTH_PIXELS);
        assertFalse(label.isInteractive());
        assertFalse(label.tryPressPointer(new PointerInput(LEFT + 1, TOP + 1, PointerButton.LEFT, Modifiers.NONE)));
    }

    private RecordingCanvas.Text isDrawn(TextAlign align) {
        Label label = place(new Label(FILL, LabelRole.NORMAL, TEXT), BOX_WIDTH_PIXELS);
        label.setAlign(align);
        label.draw(canvas, 0, 0);
        return canvas.texts().getFirst();
    }

    private static Label place(Label label, int widthPixels) {
        label.setPosition(LEFT, TOP);
        label.setSize(widthPixels, BOX_HEIGHT_PIXELS);
        return label;
    }

    private static RecordingCanvas canvas() {
        RecordingCanvas canvas = new RecordingCanvas();
        canvas.setCharWidthPixels(CHAR_WIDTH_PIXELS);
        return canvas;
    }
}

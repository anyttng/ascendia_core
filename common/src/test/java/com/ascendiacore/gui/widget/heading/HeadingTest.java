package com.ascendiacore.gui.widget.heading;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.ascendiacore.gui.base.draw.RecordingCanvas;
import com.ascendiacore.gui.widget.label.Label;
import com.ascendiacore.gui.widget.panel.model.FillStyle;

import net.minecraft.network.chat.Component;

class HeadingTest {
    private static final FillStyle FILL = new FillStyle(0xFF000000, 0xFF111111, 0xFF222222, 0xFF333333, true);
    private static final String TEXT = "Additional options";
    private static final int CHAR_WIDTH_PIXELS = 6;
    private static final int LEFT = 10;
    private static final int TOP = 20;
    private static final int WIDTH_PIXELS = 300;
    private static final int LINE_HEIGHT_PIXELS = 9;

    @Test
    void boldTextCenteredInTheFillsNormalInk() {
        RecordingCanvas canvas = new RecordingCanvas();
        canvas.setCharWidthPixels(CHAR_WIDTH_PIXELS);
        Label heading = Heading.of(FILL, Component.literal(TEXT));
        heading.setPosition(LEFT, TOP);
        heading.setSize(WIDTH_PIXELS, LINE_HEIGHT_PIXELS);
        heading.draw(canvas, 0, 0);
        RecordingCanvas.Text drawn = canvas.texts().getFirst();
        assertEquals(TEXT, drawn.text().getString());
        assertTrue(drawn.text().getStyle().isBold());
        assertEquals(LEFT + (WIDTH_PIXELS - TEXT.length() * CHAR_WIDTH_PIXELS) / 2, drawn.x());
        assertEquals(FILL.ink(), drawn.color());
    }
}

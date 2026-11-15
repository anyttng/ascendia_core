package com.ascendiacore.gui.widget.panel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.ascendiacore.gui.base.component.GuiComponent;
import com.ascendiacore.gui.base.draw.RecordingCanvas;
import com.ascendiacore.gui.base.draw.TextMetrics;
import com.ascendiacore.gui.layout.Row;
import com.ascendiacore.gui.layout.model.Length;
import com.ascendiacore.gui.theme.Themes;
import com.ascendiacore.gui.widget.panel.model.FillKind;
import com.ascendiacore.gui.widget.panel.model.FillStyle;
import com.ascendiacore.gui.widget.panel.model.InsetFrame;
import com.ascendiacore.gui.widget.panel.model.InsetStyle;
import com.ascendiacore.gui.widget.panel.model.PanelStyle;

class PanelTest {
    private static final TextMetrics NO_TEXT = text -> 0;
    private static final int LEFT = 10;
    private static final int TOP = 20;
    private static final int WIDTH = 120;
    private static final int HEIGHT = 80;
    private static final int OUTSIDE = -1;

    private final RecordingCanvas canvas = new RecordingCanvas();

    @Test
    void panelDrawsItsFillOnePixelInsideAndItsFrameOverTheWholeArea() {
        PanelStyle style = Themes.BOOK.panel();
        Panel panel = new Panel(style, FillKind.PAGE);
        place(panel);
        panel.draw(canvas, OUTSIDE, OUTSIDE);
        FillStyle page = style.fills().page();
        assertEquals(List.of(new RecordingCanvas.Fill(LEFT + 1, TOP + 1, WIDTH - 2, HEIGHT - 2, page.color())), canvas.fills());
        assertEquals(List.of(new RecordingCanvas.Sprite(style.frame().sprite(), LEFT, TOP, WIDTH, HEIGHT)), canvas.sprites());
    }

    @Test
    void panelWhoseSpriteCarriesItsOwnFillDrawsNoFill() {
        Panel panel = new Panel(Themes.VANILLA.panel(), FillKind.PARCHMENT);
        place(panel);
        panel.draw(canvas, OUTSIDE, OUTSIDE);
        assertTrue(canvas.fills().isEmpty());
        assertEquals(1, canvas.sprites().size());
    }

    @Test
    void contentStartsInsideTheFrame() {
        PanelStyle style = Themes.BOOK.panel();
        Panel panel = new Panel(style, FillKind.PARCHMENT);
        place(panel);
        Row content = new Row();
        panel.add(content, Length.fill());
        panel.layout(NO_TEXT);
        int border = style.frame().border();
        assertEquals(LEFT + border, content.x());
        assertEquals(TOP + border, content.y());
        assertEquals(WIDTH - 2 * border, content.width());
        assertEquals(HEIGHT - 2 * border, content.height());
    }

    @Test
    void insetDrawsTheFrameAndTheFillItWasGiven() {
        InsetStyle style = Themes.BOOK.inset();
        Inset inset = new Inset(style, InsetFrame.DARK, FillKind.DARK);
        place(inset);
        inset.draw(canvas, OUTSIDE, OUTSIDE);
        assertEquals(style.dark().sprite(), canvas.sprites().getFirst().sprite());
        assertEquals(style.fills().dark().color(), canvas.fills().getFirst().color());
        assertEquals(style.fills().dark().ink(), inset.ink());
    }

    @Test
    void eachInsetFrameKindNamesItsOwnFrame() {
        InsetStyle style = Themes.BOOK.inset();
        assertEquals(style.light(), style.frame(InsetFrame.LIGHT));
        assertEquals(style.dark(), style.frame(InsetFrame.DARK));
        assertEquals(style.rim(), style.frame(InsetFrame.RIM));
    }

    private static void place(GuiComponent component) {
        component.setPosition(LEFT, TOP);
        component.setSize(WIDTH, HEIGHT);
    }
}

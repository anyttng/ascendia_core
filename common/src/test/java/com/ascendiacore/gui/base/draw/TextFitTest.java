package com.ascendiacore.gui.base.draw;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

class TextFitTest {
    private static final int CHAR_WIDTH = 6;
    private static final Component TEXT = Component.literal("Settings");
    private static final int ELLIPSIS_WIDTH = 3 * CHAR_WIDTH;

    private final RecordingCanvas metrics = metrics();

    @Test
    void textThatFitsComesBackUnchanged() {
        assertSame(TEXT, TextFit.fit(TEXT, 8 * CHAR_WIDTH, metrics));
    }

    @Test
    void widerTextKeepsTheLongestPrefixThatFitsWithTheEllipsis() {
        assertEquals("Sett...", TextFit.fit(TEXT, 8 * CHAR_WIDTH - 1, metrics).getString());
        assertEquals("Se...", TextFit.fit(TEXT, 5 * CHAR_WIDTH, metrics).getString());
    }

    @Test
    void roomForTheEllipsisAloneShowsTheEllipsis() {
        assertEquals("...", TextFit.fit(TEXT, ELLIPSIS_WIDTH, metrics).getString());
    }

    @Test
    void roomNarrowerThanTheEllipsisShowsNothing() {
        assertEquals("", TextFit.fit(TEXT, ELLIPSIS_WIDTH - 1, metrics).getString());
    }

    @Test
    void cutTextKeepsItsStyle() {
        Style bold = Style.EMPTY.withBold(true);
        Component styled = Component.literal("Settings").withStyle(bold);
        assertEquals(bold, TextFit.fit(styled, 5 * CHAR_WIDTH, metrics).getStyle());
    }

    @Test
    void cutNeverSplitsASurrogatePair() {
        Component text = Component.literal("a😀b😀cd");
        assertEquals("a😀...", TextFit.fit(text, 5 * CHAR_WIDTH, metrics).getString());
    }

    private static RecordingCanvas metrics() {
        RecordingCanvas canvas = new RecordingCanvas();
        canvas.setCharWidth(CHAR_WIDTH);
        return canvas;
    }
}

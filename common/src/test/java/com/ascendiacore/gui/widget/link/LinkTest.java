package com.ascendiacore.gui.widget.link;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.ascendiacore.gui.base.draw.RecordingCanvas;
import com.ascendiacore.gui.base.geometry.Size;
import com.ascendiacore.gui.base.input.KeyInput;
import com.ascendiacore.gui.base.input.PointerInput;
import com.ascendiacore.gui.base.input.model.Key;
import com.ascendiacore.gui.base.input.model.Modifiers;
import com.ascendiacore.gui.base.input.model.PointerButton;
import com.ascendiacore.gui.theme.Themes;
import com.ascendiacore.gui.widget.link.model.LinkColors;
import com.ascendiacore.gui.widget.link.model.LinkStyle;
import com.ascendiacore.gui.widget.panel.model.FillKind;

import net.minecraft.network.chat.Component;

class LinkTest {
    private static final LinkColors COLORS = new LinkColors(0xFF111111, 0xFF222222, 0xFF333333);
    private static final String ICON = "test:icon";
    private static final Component LABEL = Component.literal("Open");
    private static final int LEFT = 40;
    private static final int TOP = 30;
    private static final int TEXT_WIDTH_PIXELS = 25;
    private static final int LINE_HEIGHT_PIXELS = 9;
    private static final int ICON_SIZE_PIXELS = 8;
    private static final int ICON_GAP_PIXELS = 2;
    private static final int INSIDE_X = LEFT + 3;
    private static final int INSIDE_Y = TOP + 3;
    private static final int OUTSIDE = -1;

    private final RecordingCanvas canvas = canvas();
    private int runs;

    @Test
    void restingLinkDrawsItsTextInTheNormalColorWithNoLine() {
        link().draw(canvas, OUTSIDE, OUTSIDE);
        assertEquals(List.of(new RecordingCanvas.Text(LABEL, LEFT, TOP, COLORS.normal(), false)), canvas.texts());
        assertTrue(canvas.fills().isEmpty());
    }

    @Test
    void hoveredLinkTakesTheHoverColorAndALineUnderItsText() {
        link().draw(canvas, INSIDE_X, INSIDE_Y);
        assertEquals(COLORS.hover(), canvas.texts().getFirst().color());
        assertEquals(List.of(new RecordingCanvas.Fill(LEFT, TOP + LINE_HEIGHT_PIXELS, TEXT_WIDTH_PIXELS, 1, COLORS.hover())), canvas.fills());
    }

    @Test
    void focusedLinkLooksHovered() {
        Link link = link();
        link.setFocused(true);
        link.draw(canvas, OUTSIDE, OUTSIDE);
        assertEquals(COLORS.hover(), canvas.texts().getFirst().color());
        assertEquals(1, canvas.fills().size());
    }

    @Test
    void disabledLinkIsGrayAndTakesNeitherTheLineNorAPress() {
        Link link = link();
        link.setEnabled(false);
        link.draw(canvas, INSIDE_X, INSIDE_Y);
        assertEquals(COLORS.disabled(), canvas.texts().getFirst().color());
        assertTrue(canvas.fills().isEmpty());
        assertFalse(link.tryPressPointer(new PointerInput(INSIDE_X, INSIDE_Y, PointerButton.LEFT, Modifiers.NONE)));
        assertEquals(0, runs);
    }

    @Test
    void iconSitsInFrontOfTheTextAndTheLineStaysUnderTheTextAlone() {
        Link link = place(Link.iconAndText(COLORS, ICON, LABEL, () -> runs++));
        link.draw(canvas, INSIDE_X, INSIDE_Y);
        assertEquals(List.of(new RecordingCanvas.Sprite(ICON, LEFT, TOP, ICON_SIZE_PIXELS, ICON_SIZE_PIXELS)), canvas.sprites());
        int textLeft = LEFT + ICON_SIZE_PIXELS + ICON_GAP_PIXELS;
        assertEquals(textLeft, canvas.texts().getFirst().x());
        assertEquals(textLeft, canvas.fills().getFirst().x());
    }

    @Test
    void naturalSizeIsTheTextWithItsLineAndTheIconWhenThereIsOne() {
        assertEquals(new Size(TEXT_WIDTH_PIXELS, LINE_HEIGHT_PIXELS + 1), link().naturalSize(canvas));
        assertEquals(new Size(ICON_SIZE_PIXELS + ICON_GAP_PIXELS + TEXT_WIDTH_PIXELS, LINE_HEIGHT_PIXELS + 1),
                Link.iconAndText(COLORS, ICON, LABEL, () -> runs++).naturalSize(canvas));
    }

    @Test
    void pressAndSelectionKeysRunTheAction() {
        Link link = link();
        assertTrue(link.tryPressPointer(new PointerInput(INSIDE_X, INSIDE_Y, PointerButton.LEFT, Modifiers.NONE)));
        assertTrue(link.tryPressKey(new KeyInput(Key.ENTER, Modifiers.NONE)));
        assertTrue(link.tryPressKey(new KeyInput(Key.SPACE, Modifiers.NONE)));
        assertFalse(link.tryPressKey(new KeyInput(Key.TAB, Modifiers.NONE)));
        assertEquals(3, runs);
    }

    @Test
    void styleGivesTheDarkColorsOnADarkFillAndTheLightOnesElsewhere() {
        LinkStyle style = Themes.BOOK.link();
        assertEquals(style.onDark(), style.getColors(FillKind.DARK));
        assertEquals(style.onLight(), style.getColors(FillKind.PARCHMENT));
        assertEquals(style.onLight(), style.getColors(FillKind.PAGE));
    }

    private Link link() {
        return place(Link.text(COLORS, LABEL, () -> runs++));
    }

    private static Link place(Link link) {
        link.setPosition(LEFT, TOP);
        link.setSize(TEXT_WIDTH_PIXELS + ICON_SIZE_PIXELS + ICON_GAP_PIXELS, LINE_HEIGHT_PIXELS + 1);
        return link;
    }

    private static RecordingCanvas canvas() {
        RecordingCanvas canvas = new RecordingCanvas();
        canvas.setTextWidth(TEXT_WIDTH_PIXELS);
        return canvas;
    }
}

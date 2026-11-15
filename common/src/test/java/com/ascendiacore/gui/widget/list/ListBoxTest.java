package com.ascendiacore.gui.widget.list;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.ascendiacore.gui.base.component.GuiComponent;
import com.ascendiacore.gui.base.draw.RecordingCanvas;
import com.ascendiacore.gui.base.draw.TextMetrics;
import com.ascendiacore.gui.base.input.KeyInput;
import com.ascendiacore.gui.base.input.PointerInput;
import com.ascendiacore.gui.base.input.model.Key;
import com.ascendiacore.gui.base.input.model.Modifiers;
import com.ascendiacore.gui.base.input.model.PointerButton;
import com.ascendiacore.gui.theme.Theme;
import com.ascendiacore.gui.theme.Themes;
import com.ascendiacore.gui.widget.button.Button;
import com.ascendiacore.gui.widget.label.Label;
import com.ascendiacore.gui.widget.label.model.LabelRole;
import com.ascendiacore.gui.widget.list.model.RowStyle;
import com.ascendiacore.gui.widget.panel.model.FillKind;
import com.ascendiacore.gui.widget.panel.model.FillStyle;

import net.minecraft.network.chat.Component;

class ListBoxTest {
    private static final Theme THEME = Themes.BOOK;
    private static final FillStyle FILL = THEME.panel().fills().of(FillKind.PARCHMENT);
    private static final TextMetrics NO_TEXT = text -> 0;
    private static final int LEFT = 10;
    private static final int TOP = 20;
    private static final int WIDTH = 120;
    private static final int HEIGHT = 200;
    private static final int LINE = 9;
    private static final int BUTTON_HEIGHT = 20;
    private static final int ENTRY_GAP = 3;
    private static final int OUTSIDE = -1;

    private final List<Integer> picked = new ArrayList<>();
    private int buttonPresses;

    @Test
    void theFirstRowStartsSelectedAndAnEmptyListHasNone() {
        ListBox list = new ListBox(THEME, FILL, RowStyle.FRAMED, picked::add);
        assertEquals(ListBox.NONE, list.selected());
        list.addHeading(Component.literal("Group"));
        list.addRow(Component.literal("First"));
        list.addRow(Component.literal("Second"));
        assertEquals(0, list.selected());
    }

    @Test
    void aPressOnARowSelectsItAndTakesTheSelectionOffTheOther() {
        Stage stage = stage(RowStyle.FRAMED);
        assertTrue(stage.second.pointerPressed(press(stage.second)));
        assertEquals(1, stage.list.selected());
        assertEquals(List.of(1), picked);
        assertEquals(THEME.list().row(), sprite(stage.first, false));
        assertEquals(THEME.list().rowSelected(), sprite(stage.second, false));
    }

    @Test
    void enterOnARowSelectsIt() {
        Stage stage = stage(RowStyle.FRAMED);
        assertTrue(stage.second.keyPressed(new KeyInput(Key.ENTER, Modifiers.NONE)));
        assertEquals(1, stage.list.selected());
    }

    @Test
    void aHeadingTakesNeitherAPressNorTheSelection() {
        Stage stage = stage(RowStyle.FRAMED);
        assertFalse(stage.heading.pointerPressed(press(stage.heading)));
        assertEquals(0, stage.list.selected());
        assertTrue(picked.isEmpty());
        RecordingCanvas canvas = new RecordingCanvas();
        stage.heading.draw(canvas, stage.heading.x() + 1, stage.heading.y() + 1);
        assertTrue(canvas.sprites().isEmpty());
    }

    @Test
    void aRowIsAsTallAsItsContent() {
        Stage stage = stage(RowStyle.FRAMED);
        assertEquals(LINE + 2 * ListRow.PADDING, stage.first.height());
        assertEquals(2 * LINE + BUTTON_HEIGHT + 2 * ListRow.PADDING, stage.second.height());
        assertEquals(stage.first.y() + stage.first.height() + ENTRY_GAP, stage.second.y());
    }

    @Test
    void aFramedRowDrawsItsOwnSpriteByState() {
        Stage stage = stage(RowStyle.FRAMED);
        assertEquals(THEME.list().rowSelected(), sprite(stage.first, false));
        assertEquals(THEME.list().rowSelected(), sprite(stage.first, true));
        assertEquals(THEME.list().row(), sprite(stage.second, false));
        assertEquals(THEME.list().rowHover(), sprite(stage.second, true));
    }

    @Test
    void aButtonRowDrawsTheButtonSpritesByState() {
        Stage stage = stage(RowStyle.BUTTON);
        assertEquals(THEME.button().selected(), sprite(stage.first, false));
        assertEquals(THEME.button().selectedHover(), sprite(stage.first, true));
        assertEquals(THEME.button().normal(), sprite(stage.second, false));
        assertEquals(THEME.button().hover(), sprite(stage.second, true));
    }

    @Test
    void aFocusedRowDrawsAsHovered() {
        Stage stage = stage(RowStyle.FRAMED);
        stage.second.setFocused(true);
        assertEquals(THEME.list().rowHover(), sprite(stage.second, false));
    }

    @Test
    void textInARowFollowsItsState() {
        Stage stage = stage(RowStyle.FRAMED);
        assertEquals(THEME.list().text(RowStyle.FRAMED, true), stage.first.fill().get());
        assertEquals(THEME.list().text(RowStyle.FRAMED, false), stage.second.fill().get());
        stage.second.pointerPressed(press(stage.second));
        RecordingCanvas canvas = new RecordingCanvas();
        stage.secondName.draw(canvas, OUTSIDE, OUTSIDE);
        assertEquals(THEME.list().text(RowStyle.FRAMED, true).ink(), canvas.texts().getFirst().color());
    }

    @Test
    void aButtonInsideARowTakesItsOwnPress() {
        Stage stage = stage(RowStyle.FRAMED);
        PointerInput onButton = new PointerInput(stage.button.x() + 1, stage.button.y() + 1, PointerButton.LEFT, Modifiers.NONE);
        assertFalse(stage.second.pointerPressed(onButton));
        assertTrue(stage.button.pointerPressed(onButton));
        assertEquals(1, buttonPresses);
        assertEquals(0, stage.list.selected());
    }

    private Stage stage(RowStyle rowStyle) {
        ListBox list = new ListBox(THEME, FILL, rowStyle, picked::add);
        list.addHeading(Component.literal("Group"));
        ListRow first = list.addRow(Component.literal("First"));
        first.add(new Label(first.fill(), LabelRole.NORMAL, Component.literal("First")));
        ListRow second = list.addRow(Component.literal("Second"));
        Label secondName = new Label(second.fill(), LabelRole.NORMAL, Component.literal("Second"));
        Button button = Button.text(THEME.button(), Component.literal("Inside"), () -> buttonPresses++);
        second.add(secondName);
        second.add(new Label(second.fill(), LabelRole.MUTED, Component.literal("Summary")));
        second.add(button);
        list.setPosition(LEFT, TOP);
        list.setSize(WIDTH, HEIGHT);
        list.layout(NO_TEXT);
        GuiComponent heading = list.mountedComponents().get(1);
        return new Stage(list, heading, first, second, secondName, button);
    }

    private static String sprite(ListRow row, boolean hovered) {
        RecordingCanvas canvas = new RecordingCanvas();
        int pointerX = hovered ? row.x() + 1 : OUTSIDE;
        int pointerY = hovered ? row.y() + 1 : OUTSIDE;
        row.draw(canvas, pointerX, pointerY);
        return canvas.sprites().getFirst().sprite();
    }

    private static PointerInput press(GuiComponent target) {
        return new PointerInput(target.x() + 1, target.y() + 1, PointerButton.LEFT, Modifiers.NONE);
    }

    private record Stage(ListBox list, GuiComponent heading, ListRow first, ListRow second, Label secondName, Button button) {
    }
}

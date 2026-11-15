package com.ascendiacore.gui.widget.list;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import com.ascendiacore.gui.base.component.GuiComponent;
import com.ascendiacore.gui.base.draw.Icon;
import com.ascendiacore.gui.base.draw.RecordingCanvas;
import com.ascendiacore.gui.base.draw.TextMetrics;
import com.ascendiacore.gui.base.input.PointerInput;
import com.ascendiacore.gui.base.input.WheelInput;
import com.ascendiacore.gui.base.input.model.Modifiers;
import com.ascendiacore.gui.base.input.model.PointerButton;
import com.ascendiacore.gui.theme.Theme;
import com.ascendiacore.gui.theme.Themes;
import com.ascendiacore.gui.widget.list.model.ListRow;
import com.ascendiacore.gui.widget.panel.Inset;
import com.ascendiacore.gui.widget.scrollbar.Scrollbar;

import net.minecraft.network.chat.Component;

class ListBoxTest {
    private static final Theme THEME = Themes.BOOK;
    private static final TextMetrics NO_TEXT = text -> 0;
    private static final Icon NO_ICON = (canvas, x, y) -> {
    };
    private static final int LEFT = 10;
    private static final int TOP = 20;
    private static final int WIDTH = 110;
    private static final int HEIGHT = 100;
    private static final int ROW = ListRows.ROW_HEIGHT;
    private static final int BORDER = 3;
    private static final int ROWS_TOP = TOP + BORDER;
    private static final int VIEW = HEIGHT - 2 * BORDER;
    private static final int OUTSIDE = -1;

    private final List<Integer> picked = new ArrayList<>();

    @Test
    void rowsSitInsideALightPageInsetWithTheScrollbarBesideIt() {
        ListBox list = list(10);
        List<GuiComponent> mounted = list.mountedComponents();
        assertTrue(mounted.get(0) instanceof Inset);
        assertTrue(mounted.get(1) instanceof ListRows);
        Scrollbar bar = (Scrollbar) mounted.get(2);
        assertEquals(LEFT + WIDTH - Scrollbar.WIDTH, bar.x());
        assertEquals(HEIGHT, bar.height());
        ListRows rows = rows(list);
        assertEquals(LEFT + BORDER, rows.x());
        assertEquals(ROWS_TOP, rows.y());
        assertEquals(WIDTH - Scrollbar.WIDTH - 2 - 2 * BORDER, rows.width());
        assertEquals(VIEW, rows.height());
    }

    @Test
    void theFirstRowStartsSelected() {
        assertEquals(0, list(3).selected());
        assertEquals(ListBox.NONE, list(0).selected());
    }

    @Test
    void aPressSelectsTheRowUnderThePointer() {
        ListBox list = list(10);
        assertTrue(rows(list).pointerPressed(press(ROWS_TOP + 2 * ROW + 5)));
        assertEquals(2, list.selected());
        assertEquals(List.of(2), picked);
    }

    @Test
    void aPressBelowTheLastRowSelectsNothing() {
        ListBox list = list(2);
        rows(list).pointerPressed(press(ROWS_TOP + 3 * ROW));
        assertEquals(0, list.selected());
        assertTrue(picked.isEmpty());
    }

    @Test
    void theWheelScrollsTheRowsAndAPressReadsTheScrolledRow() {
        ListBox list = list(10);
        ListRows rows = rows(list);
        assertTrue(rows.wheelScrolled(new WheelInput(LEFT + 20, ROWS_TOP + 5, -1)));
        rows.pointerPressed(press(ROWS_TOP + 1));
        assertEquals(1, list.selected());
    }

    @Test
    void theWheelStopsAtTheLastRow() {
        ListBox list = list(10);
        ListRows rows = rows(list);
        rows.wheelScrolled(new WheelInput(LEFT + 20, ROWS_TOP + 5, -100));
        assertEquals(9, rows.rowAt(LEFT + 20, ROWS_TOP + VIEW - 1));
    }

    @Test
    void theScrollbarFollowsTheWheelOverTheRows() {
        ListBox list = list(10);
        Scrollbar bar = (Scrollbar) list.mountedComponents().get(2);
        int before = bar.thumbTop();
        rows(list).wheelScrolled(new WheelInput(LEFT + 20, ROWS_TOP + 5, -1));
        assertTrue(bar.thumbTop() > before);
    }

    @Test
    void aShortListFillsTheTrackWithTheThumb() {
        ListBox list = list(2);
        Scrollbar bar = (Scrollbar) list.mountedComponents().get(2);
        assertEquals(HEIGHT - 2, bar.thumbLength());
    }

    @Test
    void theSelectedRowAndTheHoveredRowDrawTheirSprites() {
        ListBox list = list(10);
        ListRows rows = rows(list);
        RecordingCanvas canvas = new RecordingCanvas();
        rows.draw(canvas, LEFT + 20, ROWS_TOP + 2 * ROW + 5);
        assertEquals(List.of(
                new RecordingCanvas.Sprite(THEME.list().rowSelected(), rows.x(), ROWS_TOP, rows.width(), ROW),
                new RecordingCanvas.Sprite(THEME.list().rowHover(), rows.x(), ROWS_TOP + 2 * ROW, rows.width(), ROW)),
                canvas.sprites());
    }

    @Test
    void theSelectedRowIsNotDrawnAsHovered() {
        ListBox list = list(3);
        RecordingCanvas canvas = new RecordingCanvas();
        rows(list).draw(canvas, LEFT + 20, ROWS_TOP + 5);
        assertEquals(1, canvas.sprites().size());
        assertEquals(THEME.list().rowSelected(), canvas.sprites().getFirst().sprite());
    }

    @Test
    void labelsReadOnThePageAndTheSelectedOneOnItsSprite() {
        ListBox list = list(3);
        RecordingCanvas canvas = new RecordingCanvas();
        rows(list).draw(canvas, OUTSIDE, OUTSIDE);
        int pageInk = THEME.inset().fills().page().ink();
        assertEquals(List.of(THEME.list().selectedLabelColor(), pageInk, pageInk),
                canvas.texts().stream().map(RecordingCanvas.Text::color).toList());
        assertFalse(canvas.texts().getFirst().shadow());
    }

    @Test
    void onlyTheRowsInViewAreDrawn() {
        ListBox list = list(10);
        RecordingCanvas canvas = new RecordingCanvas();
        rows(list).draw(canvas, OUTSIDE, OUTSIDE);
        assertEquals((VIEW + ROW - 1) / ROW, canvas.texts().size());
    }

    private ListBox list(int count) {
        List<ListRow> rows = IntStream.range(0, count)
                .mapToObj(index -> new ListRow(NO_ICON, Component.literal("Row " + index)))
                .toList();
        ListBox list = new ListBox(THEME, rows, picked::add);
        list.setPosition(LEFT, TOP);
        list.setSize(WIDTH, HEIGHT);
        list.layout(NO_TEXT);
        return list;
    }

    private static ListRows rows(ListBox list) {
        return (ListRows) list.mountedComponents().get(1);
    }

    private static PointerInput press(double y) {
        return new PointerInput(LEFT + 20, y, PointerButton.LEFT, Modifiers.NONE);
    }
}

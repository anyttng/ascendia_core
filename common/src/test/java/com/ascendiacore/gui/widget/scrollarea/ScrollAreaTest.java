package com.ascendiacore.gui.widget.scrollarea;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import com.ascendiacore.gui.base.component.GuiComponent;
import com.ascendiacore.gui.base.draw.Canvas;
import com.ascendiacore.gui.base.draw.RecordingCanvas;
import com.ascendiacore.gui.base.draw.TextMetrics;
import com.ascendiacore.gui.base.geometry.Rect;
import com.ascendiacore.gui.base.input.PointerInput;
import com.ascendiacore.gui.base.input.WheelInput;
import com.ascendiacore.gui.base.input.model.Modifiers;
import com.ascendiacore.gui.base.input.model.PointerButton;
import com.ascendiacore.gui.layout.Column;
import com.ascendiacore.gui.theme.Themes;
import com.ascendiacore.gui.widget.scrollbar.Scrollbar;

class ScrollAreaTest {
    private static final TextMetrics NO_TEXT = text -> 0;
    private static final int LEFT = 10;
    private static final int TOP = 20;
    private static final int WIDTH = 100;
    private static final int HEIGHT = 60;
    private static final int BOTTOM = TOP + HEIGHT;
    private static final int ITEM = 20;
    private static final int STEP = ScrollArea.WHEEL_STEP;
    private static final int SCROLLBAR_GAP = 2;
    private static final int POINT_X = LEFT + 5;
    private static final int OUTSIDE = -1;

    @Test
    void theScrollbarStandsBesideContentTallerThanTheWindow() {
        Stage stage = stage(10, true);
        assertTrue(stage.scrollbar().visible());
        assertEquals(LEFT + WIDTH - Scrollbar.WIDTH, stage.scrollbar().x());
        assertEquals(WIDTH - Scrollbar.WIDTH - SCROLLBAR_GAP, stage.viewport().width());
    }

    @Test
    void theScrollbarIsAbsentWhenEverythingFits() {
        Stage stage = stage(2, true);
        assertFalse(stage.scrollbar().visible());
        assertEquals(WIDTH, stage.viewport().width());
    }

    @Test
    void theWheelOverAChildScrollsTheArea() {
        Stage stage = stage(10, true);
        assertTrue(stage.item(0).wheelScrolled(new WheelInput(POINT_X, TOP + 5, -1)));
        stage.layout();
        assertEquals(TOP - STEP, stage.item(0).y());
    }

    @Test
    void theWheelOverContentThatTakesNoInputScrollsTheArea() {
        Stage stage = stage(10, false);
        assertTrue(stage.viewport().wheelScrolled(new WheelInput(POINT_X, TOP + 5, -1)));
        stage.layout();
        assertEquals(TOP - STEP, stage.item(0).y());
    }

    @Test
    void theWheelStopsAtTheLastChild() {
        Stage stage = stage(10, true);
        stage.item(0).wheelScrolled(new WheelInput(POINT_X, TOP + 5, -100));
        stage.layout();
        assertEquals(BOTTOM, stage.item(9).y() + ITEM);
    }

    @Test
    void theWheelIsNotTakenWhenEverythingFits() {
        Stage stage = stage(2, true);
        assertFalse(stage.item(0).wheelScrolled(new WheelInput(POINT_X, TOP + 5, -1)));
    }

    @Test
    void aPressOutsideTheWindowDoesNotReachAChild() {
        Stage stage = stage(10, true);
        Item below = stage.item(3);
        assertEquals(BOTTOM, below.y());
        assertFalse(below.pointerPressed(press(BOTTOM + 5)));
        assertEquals(0, below.presses);
    }

    @Test
    void aChildAtTheWindowEdgeTakesAPressOnItsVisiblePartOnly() {
        Stage stage = stage(10, true);
        stage.item(0).wheelScrolled(new WheelInput(POINT_X, TOP + 5, -1));
        stage.layout();
        Item edge = stage.item(3);
        assertTrue(edge.pointerPressed(press(BOTTOM - 5)));
        assertFalse(edge.pointerPressed(press(BOTTOM + 5)));
        assertEquals(1, edge.presses);
    }

    @Test
    void theWindowLeavesThePointerToAChildThatTakesInput() {
        assertFalse(stage(10, true).viewport().takesPointerAt(POINT_X, TOP + 5));
        assertTrue(stage(10, false).viewport().takesPointerAt(POINT_X, TOP + 5));
    }

    @Test
    void aFocusedChildIsScrolledIntoTheWindow() {
        Stage stage = stage(10, true);
        Item far = stage.item(5);
        far.setFocused(true);
        stage.layout();
        assertEquals(BOTTOM - STEP, far.y() + ITEM);
    }

    @Test
    void aFocusedChildAboveTheWindowIsScrolledBack() {
        Stage stage = stage(10, true);
        stage.item(0).wheelScrolled(new WheelInput(POINT_X, TOP + 5, -100));
        stage.layout();
        Item first = stage.item(1);
        first.setFocused(true);
        stage.layout();
        assertEquals(TOP + STEP, first.y());
    }

    @Test
    void aChildDrawsInsideTheWindowAndIsNotHoveredOutsideIt() {
        Stage stage = stage(10, true);
        RecordingCanvas canvas = new RecordingCanvas();
        Item below = stage.item(3);
        below.draw(canvas, POINT_X, BOTTOM + 5);
        assertEquals(List.of(new Rect(LEFT, TOP, WIDTH - Scrollbar.WIDTH - SCROLLBAR_GAP, HEIGHT)), canvas.clips());
        assertFalse(below.hovered());
    }

    @Test
    void theScrollbarDrawsOutsideTheWindow() {
        Stage stage = stage(10, true);
        RecordingCanvas canvas = new RecordingCanvas();
        stage.scrollbar().draw(canvas, OUTSIDE, OUTSIDE);
        assertTrue(canvas.clips().isEmpty());
    }

    private static Stage stage(int count, boolean interactive) {
        Column content = new Column();
        List<Item> items = IntStream.range(0, count).mapToObj(index -> new Item(interactive)).toList();
        items.forEach(content::add);
        ScrollArea area = new ScrollArea(Themes.BOOK.scrollbar(), content);
        area.setPosition(LEFT, TOP);
        area.setSize(WIDTH, HEIGHT);
        Stage stage = new Stage(area, items);
        stage.layout();
        return stage;
    }

    private static PointerInput press(double y) {
        return new PointerInput(POINT_X, y, PointerButton.LEFT, Modifiers.NONE);
    }

    private record Stage(ScrollArea area, List<Item> items) {
        void layout() {
            area.layout(NO_TEXT);
        }

        Item item(int index) {
            return items.get(index);
        }

        Viewport viewport() {
            return (Viewport) area.mountedComponents().getFirst();
        }

        Scrollbar scrollbar() {
            return (Scrollbar) area.mountedComponents().getLast();
        }
    }

    private static final class Item extends GuiComponent {
        private final boolean interactive;
        private int presses;

        private Item(boolean interactive) {
            this.interactive = interactive;
            setSize(0, ITEM);
        }

        @Override
        public boolean interactive() {
            return interactive;
        }

        @Override
        protected void drawContent(Canvas canvas) {
        }

        @Override
        protected void onPress(PointerInput input) {
            presses++;
        }
    }
}

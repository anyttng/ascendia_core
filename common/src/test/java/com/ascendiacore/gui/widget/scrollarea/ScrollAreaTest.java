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
    private static final int WIDTH_PIXELS = 100;
    private static final int HEIGHT_PIXELS = 60;
    private static final int BOTTOM = TOP + HEIGHT_PIXELS;
    private static final int ITEM_HEIGHT_PIXELS = 20;
    private static final int STEP_PIXELS = ScrollArea.WHEEL_STEP_PIXELS;
    private static final int SCROLLBAR_GAP_PIXELS = 2;
    private static final int POINT_X = LEFT + 5;
    private static final int OUTSIDE = -1;

    @Test
    void theScrollbarStandsBesideContentTallerThanTheWindow() {
        Stage stage = stage(10, true);
        assertTrue(stage.scrollbar().isVisible());
        assertEquals(LEFT + WIDTH_PIXELS - Scrollbar.WIDTH_PIXELS, stage.scrollbar().getX());
        assertEquals(WIDTH_PIXELS - Scrollbar.WIDTH_PIXELS - SCROLLBAR_GAP_PIXELS, stage.viewport().getWidthPixels());
    }

    @Test
    void theScrollbarIsAbsentWhenEverythingFits() {
        Stage stage = stage(2, true);
        assertFalse(stage.scrollbar().isVisible());
        assertEquals(WIDTH_PIXELS, stage.viewport().getWidthPixels());
    }

    @Test
    void theWheelOverAChildScrollsTheArea() {
        Stage stage = stage(10, true);
        assertTrue(stage.item(0).tryScrollWheel(new WheelInput(POINT_X, TOP + 5, -1)));
        stage.layout();
        assertEquals(TOP - STEP_PIXELS, stage.item(0).getY());
    }

    @Test
    void theWheelOverContentThatTakesNoInputScrollsTheArea() {
        Stage stage = stage(10, false);
        assertTrue(stage.viewport().tryScrollWheel(new WheelInput(POINT_X, TOP + 5, -1)));
        stage.layout();
        assertEquals(TOP - STEP_PIXELS, stage.item(0).getY());
    }

    @Test
    void theWheelStopsAtTheLastChild() {
        Stage stage = stage(10, true);
        stage.item(0).tryScrollWheel(new WheelInput(POINT_X, TOP + 5, -100));
        stage.layout();
        assertEquals(BOTTOM, stage.item(9).getY() + ITEM_HEIGHT_PIXELS);
    }

    @Test
    void theWheelIsNotTakenWhenEverythingFits() {
        Stage stage = stage(2, true);
        assertFalse(stage.item(0).tryScrollWheel(new WheelInput(POINT_X, TOP + 5, -1)));
    }

    @Test
    void aPressOutsideTheWindowDoesNotReachAChild() {
        Stage stage = stage(10, true);
        Item below = stage.item(3);
        assertEquals(BOTTOM, below.getY());
        assertFalse(below.tryPressPointer(press(BOTTOM + 5)));
        assertEquals(0, below.presses);
    }

    @Test
    void aChildAtTheWindowEdgeTakesAPressOnItsVisiblePartOnly() {
        Stage stage = stage(10, true);
        stage.item(0).tryScrollWheel(new WheelInput(POINT_X, TOP + 5, -1));
        stage.layout();
        Item edge = stage.item(3);
        assertTrue(edge.tryPressPointer(press(BOTTOM - 5)));
        assertFalse(edge.tryPressPointer(press(BOTTOM + 5)));
        assertEquals(1, edge.presses);
    }

    @Test
    void theWindowLeavesThePointerToAChildThatTakesInput() {
        assertFalse(stage(10, true).viewport().isTakingPointerAt(POINT_X, TOP + 5));
        assertTrue(stage(10, false).viewport().isTakingPointerAt(POINT_X, TOP + 5));
    }

    @Test
    void aFocusedChildIsScrolledIntoTheWindow() {
        Stage stage = stage(10, true);
        Item far = stage.item(5);
        far.setFocused(true);
        stage.layout();
        assertEquals(BOTTOM - STEP_PIXELS, far.getY() + ITEM_HEIGHT_PIXELS);
    }

    @Test
    void aFocusedChildAboveTheWindowIsScrolledBack() {
        Stage stage = stage(10, true);
        stage.item(0).tryScrollWheel(new WheelInput(POINT_X, TOP + 5, -100));
        stage.layout();
        Item first = stage.item(1);
        first.setFocused(true);
        stage.layout();
        assertEquals(TOP + STEP_PIXELS, first.getY());
    }

    @Test
    void aChildDrawsInsideTheWindowAndIsNotHoveredOutsideIt() {
        Stage stage = stage(10, true);
        RecordingCanvas canvas = new RecordingCanvas();
        Item below = stage.item(3);
        below.draw(canvas, POINT_X, BOTTOM + 5);
        assertEquals(List.of(new Rect(LEFT, TOP, WIDTH_PIXELS - Scrollbar.WIDTH_PIXELS - SCROLLBAR_GAP_PIXELS, HEIGHT_PIXELS)), canvas.clips());
        assertFalse(below.isHovered());
    }

    @Test
    void theScrollbarDrawsOutsideTheWindow() {
        Stage stage = stage(10, true);
        RecordingCanvas canvas = new RecordingCanvas();
        stage.scrollbar().draw(canvas, OUTSIDE, OUTSIDE);
        assertTrue(canvas.clips().isEmpty());
    }

    private static Stage stage(int count, boolean isInteractive) {
        Column content = new Column();
        List<Item> items = IntStream.range(0, count).mapToObj(index -> new Item(isInteractive)).toList();
        items.forEach(content::add);
        ScrollArea area = new ScrollArea(Themes.BOOK.scrollbar(), content);
        area.setPosition(LEFT, TOP);
        area.setSize(WIDTH_PIXELS, HEIGHT_PIXELS);
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
            return (Viewport) area.getMountedComponents().getFirst();
        }

        Scrollbar scrollbar() {
            return (Scrollbar) area.getMountedComponents().getLast();
        }
    }

    private static final class Item extends GuiComponent {
        private final boolean isInteractive;
        private int presses;

        private Item(boolean isInteractive) {
            this.isInteractive = isInteractive;
            setSize(0, ITEM_HEIGHT_PIXELS);
        }

        @Override
        public boolean isInteractive() {
            return isInteractive;
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

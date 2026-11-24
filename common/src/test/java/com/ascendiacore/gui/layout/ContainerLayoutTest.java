package com.ascendiacore.gui.layout;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.ascendiacore.gui.base.component.GuiComponent;
import com.ascendiacore.gui.base.draw.TextMetrics;
import com.ascendiacore.gui.base.geometry.Size;
import com.ascendiacore.gui.layout.model.Align;
import com.ascendiacore.gui.layout.model.Length;

class ContainerLayoutTest {
    private static final TextMetrics NO_TEXT = text -> 0;
    private static final int LEFT = 10;
    private static final int TOP = 20;
    private static final int GAP_PIXELS = 5;

    @Test
    void rowPlacesChildrenLeftToRightAndStretchesThemAcross() {
        Row row = row(100, 30);
        Box first = new Box(20, 10);
        Box second = new Box(0, 0);
        row.add(first);
        row.add(second, Length.fill());
        row.layout(NO_TEXT);
        assertEquals(new Bounds(LEFT, TOP, 20, 30), Bounds.of(first));
        assertEquals(new Bounds(LEFT + 20 + GAP_PIXELS, TOP, 75, 30), Bounds.of(second));
    }

    @Test
    void columnPlacesChildrenTopToBottom() {
        Column column = new Column();
        column.setPosition(LEFT, TOP);
        column.setSize(60, 100);
        column.setGapPixels(GAP_PIXELS);
        Box first = new Box(20, 10);
        Box second = new Box(0, 0);
        column.add(first);
        column.add(second, Length.fill());
        column.layout(NO_TEXT);
        assertEquals(new Bounds(LEFT, TOP, 60, 10), Bounds.of(first));
        assertEquals(new Bounds(LEFT, TOP + 10 + GAP_PIXELS, 60, 85), Bounds.of(second));
    }

    @Test
    void alignmentKeepsTheNaturalBreadthAndMovesTheChildAcross() {
        assertEquals(new Bounds(LEFT, TOP, 20, 10), aligned(Align.START));
        assertEquals(new Bounds(LEFT, TOP + 10, 20, 10), aligned(Align.CENTER));
        assertEquals(new Bounds(LEFT, TOP + 20, 20, 10), aligned(Align.END));
    }

    @Test
    void hiddenChildTakesNoRoomAndLeavesNoGap() {
        Row row = row(100, 30);
        Box first = new Box(20, 10);
        Box hidden = new Box(40, 10);
        Box last = new Box(20, 10);
        hidden.setVisible(false);
        row.add(first);
        row.add(hidden);
        row.add(last);
        row.layout(NO_TEXT);
        assertEquals(LEFT + 20 + GAP_PIXELS, last.getX());
    }

    @Test
    void nestedContainerIsLaidOutInsideItsOwnBounds() {
        Column column = new Column();
        column.setPosition(LEFT, TOP);
        column.setSize(100, 60);
        Row inner = new Row();
        Box cell = new Box(0, 0);
        inner.add(cell, Length.fill());
        column.add(new Box(100, 20));
        column.add(inner, Length.fixed(30));
        column.layout(NO_TEXT);
        assertEquals(new Bounds(LEFT, TOP + 20, 100, 30), Bounds.of(cell));
    }

    @Test
    void mountedComponentsOfPlainContainersAreTheLeavesInTreeOrder() {
        Column column = new Column();
        Row inner = new Row();
        Box first = new Box(0, 0);
        Box second = new Box(0, 0);
        Box third = new Box(0, 0);
        inner.add(second);
        column.add(first);
        column.add(inner);
        column.add(third);
        assertEquals(List.of(first, second, third), column.getMountedComponents());
    }

    @Test
    void paddingMovesChildrenInAndTakesItsRoomFromBothSides() {
        Row row = row(100, 30);
        row.setPaddingPixels(5);
        Box box = new Box(0, 0);
        row.add(box, Length.fill());
        row.layout(NO_TEXT);
        assertEquals(new Bounds(LEFT + 5, TOP + 5, 90, 20), Bounds.of(box));
    }

    @Test
    void naturalSizeIncludesThePadding() {
        Row row = row(0, 0);
        row.setPaddingPixels(5);
        row.add(new Box(20, 10));
        assertEquals(new Size(30, 20), row.naturalSize(NO_TEXT));
    }

    @Test
    void naturalSizeIsTheChildrenAlongAndTheLargestAcross() {
        Row row = row(0, 0);
        row.add(new Box(20, 10));
        row.add(new Box(30, 15));
        assertEquals(new Size(20 + GAP_PIXELS + 30, 15), row.naturalSize(NO_TEXT));
    }

    private static Bounds aligned(Align align) {
        Row row = row(100, 30);
        row.setAlign(align);
        Box box = new Box(20, 10);
        row.add(box);
        row.layout(NO_TEXT);
        return Bounds.of(box);
    }

    private static Row row(int widthPixels, int heightPixels) {
        Row row = new Row();
        row.setPosition(LEFT, TOP);
        row.setSize(widthPixels, heightPixels);
        row.setGapPixels(GAP_PIXELS);
        return row;
    }

    private record Bounds(int x, int y, int widthPixels, int heightPixels) {
        static Bounds of(GuiComponent component) {
            return new Bounds(component.getX(), component.getY(), component.getWidthPixels(), component.getHeightPixels());
        }
    }
}

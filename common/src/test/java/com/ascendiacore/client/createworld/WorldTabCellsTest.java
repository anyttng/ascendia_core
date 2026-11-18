package com.ascendiacore.client.createworld;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.ascendiacore.client.createworld.WorldTabCells.Cell;
import com.ascendiacore.gui.base.component.GuiComponent;
import com.ascendiacore.gui.base.draw.Canvas;
import com.ascendiacore.gui.layout.Row;

class WorldTabCellsTest {
    private static final int FULL_WIDTH = 310;
    private static final int HALF_WIDTH = 150;

    @Test
    void anEmptyRowTakesNoCell() {
        assertEquals(List.of(), WorldTabCells.of(List.of()));
    }

    @Test
    void oneComponentSpansBothColumns() {
        Leaf leaf = new Leaf();
        List<Cell> cells = WorldTabCells.of(List.of(leaf));

        assertEquals(List.of(new Cell(leaf, 2)), cells, "a lone component keeps the grid's next row in its first column");
        assertEquals(FULL_WIDTH, cells.getFirst().width(), "two cells and the gap between them, as the seed field");
    }

    @Test
    void twoComponentsTakeOneCellEach() {
        Leaf left = new Leaf();
        Leaf right = new Leaf();
        List<Cell> cells = WorldTabCells.of(List.of(left, right));

        assertEquals(List.of(new Cell(left, 1), new Cell(right, 1)), cells);
        assertEquals(HALF_WIDTH, cells.getFirst().width(), "as wide as the world type button");
    }

    @Test
    void threeComponentsAreRefused() {
        assertThrows(IllegalArgumentException.class, () -> WorldTabCells.of(List.of(new Leaf(), new Leaf(), new Leaf())));
    }

    @Test
    void aContainerIsRefused() {
        assertThrows(IllegalArgumentException.class, () -> WorldTabCells.of(List.of(new Row())));
    }

    private static final class Leaf extends GuiComponent {
        @Override
        protected void drawContent(Canvas canvas) {
        }
    }
}

package com.ascendiacore.client.createworld;

import java.util.List;

import com.ascendiacore.gui.base.component.GuiComponent;
import com.ascendiacore.gui.layout.Container;

final class WorldTabCells {
    static final int COLUMNS = 2;
    static final int CELL_WIDTH_PIXELS = 150;
    static final int COLUMN_GAP_PIXELS = 10;

    record Cell(GuiComponent component, int columnCount) {
        int getWidthPixels() {
            return columnCount * CELL_WIDTH_PIXELS + (columnCount - 1) * COLUMN_GAP_PIXELS;
        }
    }

    static List<Cell> of(List<GuiComponent> components) {
        if (components.size() > COLUMNS) {
            throw new IllegalArgumentException(
                    "A World tab row holds at most " + COLUMNS + " components, got " + components.size());
        }
        for (GuiComponent component : components) {
            if (component instanceof Container) {
                throw new IllegalArgumentException("A World tab row takes leaf components, got a container: "
                        + component.getClass().getName());
            }
        }
        int columnCount = components.size() == 1 ? COLUMNS : 1;
        return components.stream().map(component -> new Cell(component, columnCount)).toList();
    }

    private WorldTabCells() {
    }
}

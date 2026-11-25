package com.ascendiacore.client.createworld;

import com.ascendiacore.client.createworld.WorldTabCells.Cell;
import com.ascendiacore.client.gui.bridge.ComponentTree;
import com.ascendiacore.gui.base.component.GuiComponent;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;

public final class WorldTabMount {
    public static void insert(GridLayout.RowHelper helper, WorldCreationUiState state) {
        for (WorldTabRow row : WorldTabRows.getRows()) {
            for (Cell cell : WorldTabCells.of(row.build(state))) {
                GuiComponent component = cell.component();
                component.setSize(cell.getWidthPixels(), component.naturalSize(Minecraft.getInstance().font::width).heightPixels());
                helper.addChild(ComponentTree.toWidget(component), cell.columnCount());
            }
        }
    }

    private WorldTabMount() {
    }
}

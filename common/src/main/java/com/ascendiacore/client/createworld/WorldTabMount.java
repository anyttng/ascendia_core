package com.ascendiacore.client.createworld;

import com.ascendiacore.client.createworld.WorldTabCells.Cell;
import com.ascendiacore.client.gui.bridge.ComponentTree;
import com.ascendiacore.gui.base.component.GuiComponent;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.layouts.GridLayout;
import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;

public final class WorldTabMount {
    public static void insert(GridLayout.RowHelper helper, WorldCreationUiState state) {
        for (WorldTabRow row : WorldTabRows.rows()) {
            for (Cell cell : WorldTabCells.of(row.build(state))) {
                GuiComponent component = cell.component();
                component.setSize(cell.width(), component.naturalSize(Minecraft.getInstance().font::width).height());
                helper.addChild(ComponentTree.widgetOf(component), cell.span());
            }
        }
    }

    private WorldTabMount() {
    }
}

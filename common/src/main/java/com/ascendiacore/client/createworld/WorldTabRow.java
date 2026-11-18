package com.ascendiacore.client.createworld;

import java.util.List;

import com.ascendiacore.gui.base.component.GuiComponent;

import net.minecraft.client.gui.screens.worldselection.WorldCreationUiState;

@FunctionalInterface
public interface WorldTabRow {
    List<GuiComponent> build(WorldCreationUiState state);
}

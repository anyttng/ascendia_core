package com.ascendiacore.gui.layout;

import com.ascendiacore.gui.base.component.GuiComponent;
import com.ascendiacore.gui.layout.model.Axis;
import com.ascendiacore.gui.layout.model.Length;

public abstract class OpenContainer extends Container {
    protected OpenContainer(Axis axis) {
        super(axis);
    }

    @Override
    public void add(GuiComponent child) {
        super.add(child);
    }

    @Override
    public void add(GuiComponent child, Length length) {
        super.add(child, length);
    }
}

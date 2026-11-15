package com.ascendiacore.gui.layout;

import com.ascendiacore.gui.base.component.GuiComponent;
import com.ascendiacore.gui.base.draw.Canvas;
import com.ascendiacore.gui.base.form.Validated;

final class Box extends GuiComponent implements Validated {
    private boolean valid = true;

    Box(int width, int height) {
        setSize(width, height);
    }

    static Box invalid() {
        Box box = new Box(0, 0);
        box.valid = false;
        return box;
    }

    @Override
    public boolean valid() {
        return valid;
    }

    @Override
    protected void drawContent(Canvas canvas) {
    }
}

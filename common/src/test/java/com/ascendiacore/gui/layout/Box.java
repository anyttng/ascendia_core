package com.ascendiacore.gui.layout;

import com.ascendiacore.gui.base.component.GuiComponent;
import com.ascendiacore.gui.base.draw.Canvas;
import com.ascendiacore.gui.base.form.Validated;

final class Box extends GuiComponent implements Validated {
    private boolean isValid = true;

    Box(int widthPixels, int heightPixels) {
        setSize(widthPixels, heightPixels);
    }

    static Box invalid() {
        Box box = new Box(0, 0);
        box.isValid = false;
        return box;
    }

    @Override
    public boolean isValid() {
        return isValid;
    }

    @Override
    protected void drawContent(Canvas canvas) {
    }
}

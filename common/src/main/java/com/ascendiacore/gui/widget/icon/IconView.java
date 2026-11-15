package com.ascendiacore.gui.widget.icon;

import com.ascendiacore.gui.base.component.GuiComponent;
import com.ascendiacore.gui.base.draw.Canvas;
import com.ascendiacore.gui.base.draw.Icon;
import com.ascendiacore.gui.base.draw.TextMetrics;
import com.ascendiacore.gui.base.geometry.Size;

public final class IconView extends GuiComponent {
    private final Icon icon;

    public IconView(Icon icon) {
        this.icon = icon;
    }

    @Override
    public boolean interactive() {
        return false;
    }

    @Override
    public Size naturalSize(TextMetrics metrics) {
        return new Size(Icon.SIZE, Icon.SIZE);
    }

    @Override
    protected void drawContent(Canvas canvas) {
        icon.draw(canvas, x() + (width() - Icon.SIZE) / 2, y() + (height() - Icon.SIZE) / 2);
    }
}

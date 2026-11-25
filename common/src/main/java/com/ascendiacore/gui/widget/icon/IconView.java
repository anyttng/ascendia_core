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
    public boolean isInteractive() {
        return false;
    }

    @Override
    public Size naturalSize(TextMetrics metrics) {
        return new Size(Icon.SIZE_PIXELS, Icon.SIZE_PIXELS);
    }

    @Override
    protected void drawContent(Canvas canvas) {
        icon.draw(canvas, getX() + (getWidthPixels() - Icon.SIZE_PIXELS) / 2, getY() + (getHeightPixels() - Icon.SIZE_PIXELS) / 2);
    }
}

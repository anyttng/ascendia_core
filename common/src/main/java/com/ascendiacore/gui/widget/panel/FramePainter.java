package com.ascendiacore.gui.widget.panel;

import com.ascendiacore.gui.base.component.GuiComponent;
import com.ascendiacore.gui.base.draw.Canvas;
import com.ascendiacore.gui.widget.panel.model.FillStyle;
import com.ascendiacore.gui.widget.panel.model.FrameStyle;

final class FramePainter {
    private static final int FILL_INSET_PIXELS = 1;

    private FramePainter() {
    }

    static void paint(Canvas canvas, GuiComponent area, FrameStyle frame, FillStyle fill) {
        if (fill.isDrawn()) {
            canvas.fill(area.getX() + FILL_INSET_PIXELS, area.getY() + FILL_INSET_PIXELS, area.getWidthPixels() - 2 * FILL_INSET_PIXELS,
                    area.getHeightPixels() - 2 * FILL_INSET_PIXELS, fill.color());
        }
        canvas.sprite(frame.sprite(), area.getX(), area.getY(), area.getWidthPixels(), area.getHeightPixels());
    }
}

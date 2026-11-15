package com.ascendiacore.gui.widget.panel;

import com.ascendiacore.gui.base.component.GuiComponent;
import com.ascendiacore.gui.base.draw.Canvas;
import com.ascendiacore.gui.widget.panel.model.FillStyle;
import com.ascendiacore.gui.widget.panel.model.FrameStyle;

final class FramePainter {
    private static final int FILL_INSET = 1;

    private FramePainter() {
    }

    static void paint(Canvas canvas, GuiComponent area, FrameStyle frame, FillStyle fill) {
        if (fill.drawn()) {
            canvas.fill(area.x() + FILL_INSET, area.y() + FILL_INSET, area.width() - 2 * FILL_INSET,
                    area.height() - 2 * FILL_INSET, fill.color());
        }
        canvas.sprite(frame.sprite(), area.x(), area.y(), area.width(), area.height());
    }
}

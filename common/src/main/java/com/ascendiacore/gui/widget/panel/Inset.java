package com.ascendiacore.gui.widget.panel;

import com.ascendiacore.gui.base.draw.Canvas;
import com.ascendiacore.gui.layout.Container;
import com.ascendiacore.gui.layout.model.Axis;
import com.ascendiacore.gui.widget.panel.model.FillKind;
import com.ascendiacore.gui.widget.panel.model.FillStyle;
import com.ascendiacore.gui.widget.panel.model.FrameStyle;
import com.ascendiacore.gui.widget.panel.model.InsetFrame;
import com.ascendiacore.gui.widget.panel.model.InsetStyle;

public final class Inset extends Container {
    private final FrameStyle frame;
    private final FillStyle fill;

    public Inset(InsetStyle style, InsetFrame frame, FillKind fill) {
        super(Axis.VERTICAL);
        this.frame = style.frame(frame);
        this.fill = style.fills().of(fill);
        setPadding(this.frame.border());
    }

    public FillStyle fill() {
        return fill;
    }

    @Override
    public boolean paints() {
        return true;
    }

    @Override
    protected void drawContent(Canvas canvas) {
        FramePainter.paint(canvas, this, frame, fill);
    }
}

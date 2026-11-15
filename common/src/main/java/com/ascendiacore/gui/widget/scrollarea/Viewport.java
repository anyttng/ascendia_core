package com.ascendiacore.gui.widget.scrollarea;

import com.ascendiacore.gui.base.component.GuiComponent;
import com.ascendiacore.gui.base.draw.TextMetrics;
import com.ascendiacore.gui.base.geometry.Rect;
import com.ascendiacore.gui.base.input.WheelInput;
import com.ascendiacore.gui.layout.Container;
import com.ascendiacore.gui.layout.model.Axis;
import com.ascendiacore.gui.widget.scrollbar.Scroll;

final class Viewport extends Container {
    private final Container content;
    private final Scroll scroll;

    Viewport(Container content, Scroll scroll) {
        super(Axis.VERTICAL);
        this.content = content;
        this.scroll = scroll;
        add(content);
    }

    @Override
    public boolean mountsItself() {
        return true;
    }

    @Override
    public boolean interactive() {
        return true;
    }

    @Override
    public boolean tabStop() {
        return false;
    }

    @Override
    public void layout(TextMetrics metrics) {
        content.setPosition(x(), y() - scroll.offset());
        content.setSize(width(), scroll.content());
        content.layout(metrics);
    }

    @Override
    protected Rect windowOfChildren() {
        Rect own = new Rect(x(), y(), width(), height());
        Rect outer = window();
        return outer == null ? own : own.intersect(outer);
    }

    @Override
    protected boolean onWheel(WheelInput input) {
        if (!scroll.scrollable()) {
            return false;
        }
        scroll.wheel(input.amount());
        return true;
    }

    @Override
    protected void reveal(GuiComponent descendant) {
        int above = y() - descendant.y();
        int below = descendant.y() + descendant.height() - (y() + height());
        if (above > 0) {
            scroll.scrollBy(-above - ScrollArea.WHEEL_STEP);
        } else if (below > 0) {
            scroll.scrollBy(below + ScrollArea.WHEEL_STEP);
        }
    }
}

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
    public boolean isMountingItself() {
        return true;
    }

    @Override
    public boolean isInteractive() {
        return true;
    }

    @Override
    public boolean isTabStop() {
        return false;
    }

    @Override
    public void layout(TextMetrics metrics) {
        content.setPosition(getX(), getY() - scroll.getOffsetPixels());
        content.setSize(getWidthPixels(), scroll.getContentPixels());
        content.layout(metrics);
    }

    @Override
    protected Rect getWindowOfChildren() {
        Rect own = new Rect(getX(), getY(), getWidthPixels(), getHeightPixels());
        Rect outer = getWindow();
        return outer == null ? own : own.intersection(outer);
    }

    @Override
    protected boolean tryHandleWheel(WheelInput input) {
        if (!scroll.isScrollable()) {
            return false;
        }
        scroll.wheel(input.notches());
        return true;
    }

    @Override
    protected void reveal(GuiComponent descendant) {
        int abovePixels = getY() - descendant.getY();
        int belowPixels = descendant.getY() + descendant.getHeightPixels() - (getY() + getHeightPixels());
        if (abovePixels > 0) {
            scroll.scrollBy(-abovePixels - ScrollArea.WHEEL_STEP_PIXELS);
        } else if (belowPixels > 0) {
            scroll.scrollBy(belowPixels + ScrollArea.WHEEL_STEP_PIXELS);
        }
    }
}

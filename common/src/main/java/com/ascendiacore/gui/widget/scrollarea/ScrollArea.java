package com.ascendiacore.gui.widget.scrollarea;

import com.ascendiacore.gui.base.draw.TextMetrics;
import com.ascendiacore.gui.layout.Container;
import com.ascendiacore.gui.layout.model.Axis;
import com.ascendiacore.gui.layout.model.Length;
import com.ascendiacore.gui.widget.scrollbar.Scroll;
import com.ascendiacore.gui.widget.scrollbar.Scrollbar;
import com.ascendiacore.gui.widget.scrollbar.model.ScrollbarStyle;

public final class ScrollArea extends Container {
    static final int WHEEL_STEP_PIXELS = 10;
    private static final int SCROLLBAR_GAP_PIXELS = 2;

    private final Container content;
    private final Scroll scroll = new Scroll(WHEEL_STEP_PIXELS);

    public ScrollArea(ScrollbarStyle style, Container content) {
        super(Axis.HORIZONTAL);
        this.content = content;
        Scrollbar scrollbar = new Scrollbar(style, scroll);
        scrollbar.setVisible(scroll::isScrollable);
        setGapPixels(SCROLLBAR_GAP_PIXELS);
        add(new Viewport(content, scroll), Length.fill());
        add(scrollbar, Length.fixed(Scrollbar.WIDTH_PIXELS));
    }

    @Override
    public void layout(TextMetrics metrics) {
        scroll.setContentPixels(content.naturalSize(metrics).heightPixels());
        scroll.setViewPixels(getHeightPixels());
        super.layout(metrics);
    }
}

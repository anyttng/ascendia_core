package com.ascendiacore.gui.widget.tabrow;

import java.util.List;
import java.util.OptionalInt;
import java.util.function.IntConsumer;

import com.ascendiacore.gui.base.property.Value;
import com.ascendiacore.gui.layout.Container;
import com.ascendiacore.gui.layout.model.Align;
import com.ascendiacore.gui.layout.model.Axis;
import com.ascendiacore.gui.widget.tabrow.model.TabContent;
import com.ascendiacore.gui.widget.tabrow.model.TabRowStyle;

public final class TabRow extends Container {
    public static final int PANEL_INSET_PIXELS = 10;
    public static final int PANEL_OVERLAP_PIXELS = 2;
    private static final int GAP_PIXELS = 2;

    private final Value<Integer> selectedIndex;
    private IntConsumer onChange = index -> {
    };

    public TabRow(TabRowStyle style, List<TabContent> tabs, Value<Integer> selectedIndex, OptionalInt tabWidthPixels) {
        super(Axis.HORIZONTAL);
        this.selectedIndex = selectedIndex;
        setGapPixels(GAP_PIXELS);
        setAlign(Align.END);
        boolean isIconRow = tabs.stream().anyMatch(TabContent::hasIcons);
        for (int index = 0; index < tabs.size(); index++) {
            int tabIndex = index;
            add(new Tab(style, tabs.get(index), tabWidthPixels, isIconRow, () -> selectedIndex.get() == tabIndex,
                    () -> select(tabIndex)));
        }
    }

    public void setOnChange(IntConsumer onChange) {
        this.onChange = onChange;
    }

    private void select(int index) {
        selectedIndex.set(index);
        onChange.accept(index);
    }
}

package com.ascendiacore.gui.widget.list;

import java.util.function.IntConsumer;

import com.ascendiacore.gui.layout.Column;
import com.ascendiacore.gui.layout.Container;
import com.ascendiacore.gui.layout.model.Axis;
import com.ascendiacore.gui.layout.model.Length;
import com.ascendiacore.gui.theme.Theme;
import com.ascendiacore.gui.widget.label.Label;
import com.ascendiacore.gui.widget.label.model.LabelRole;
import com.ascendiacore.gui.widget.list.model.RowStyle;
import com.ascendiacore.gui.widget.panel.model.FillStyle;
import com.ascendiacore.gui.widget.scrollarea.ScrollArea;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public final class ListBox extends Container {
    public static final int NONE = -1;
    private static final int ENTRY_GAP_PIXELS = 3;

    private final Theme theme;
    private final FillStyle fill;
    private final RowStyle rowStyle;
    private final IntConsumer onSelect;
    private final Column entries = new Column();
    private int rowCount;
    private int selectedIndex = NONE;

    public ListBox(Theme theme, FillStyle fill, RowStyle rowStyle, IntConsumer onSelect) {
        super(Axis.VERTICAL);
        this.theme = theme;
        this.fill = fill;
        this.rowStyle = rowStyle;
        this.onSelect = onSelect;
        entries.setGapPixels(ENTRY_GAP_PIXELS);
        add(new ScrollArea(theme.scrollbar(), entries), Length.fill());
    }

    public ListRow addRow(Component name) {
        int index = rowCount++;
        if (selectedIndex == NONE) {
            selectedIndex = index;
        }
        ListRow row = new ListRow(theme.list(), theme.button(), rowStyle, name, () -> selectedIndex == index, () -> select(index));
        entries.add(row);
        return row;
    }

    public void addHeading(Component text) {
        entries.add(new Label(fill, LabelRole.NORMAL, text.copy().withStyle(ChatFormatting.BOLD)));
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    private void select(int index) {
        selectedIndex = index;
        onSelect.accept(index);
    }
}

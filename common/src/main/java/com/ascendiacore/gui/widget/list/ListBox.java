package com.ascendiacore.gui.widget.list;

import java.util.List;
import java.util.function.IntConsumer;

import com.ascendiacore.gui.layout.Container;
import com.ascendiacore.gui.layout.model.Axis;
import com.ascendiacore.gui.layout.model.Length;
import com.ascendiacore.gui.theme.Theme;
import com.ascendiacore.gui.widget.list.model.ListRow;
import com.ascendiacore.gui.widget.panel.Inset;
import com.ascendiacore.gui.widget.panel.model.FillKind;
import com.ascendiacore.gui.widget.panel.model.InsetFrame;
import com.ascendiacore.gui.widget.scrollbar.Scroll;
import com.ascendiacore.gui.widget.scrollbar.Scrollbar;

public final class ListBox extends Container {
    public static final int NONE = ListRows.NONE;
    private static final int SCROLLBAR_GAP = 2;

    private final ListRows rows;

    public ListBox(Theme theme, List<ListRow> rows, IntConsumer onSelect) {
        super(Axis.HORIZONTAL);
        Scroll scroll = new Scroll(ListRows.ROW_HEIGHT);
        Inset page = new Inset(theme.inset(), InsetFrame.LIGHT, FillKind.PAGE);
        this.rows = new ListRows(theme.list(), page.fill().ink(), rows, scroll, onSelect);
        page.add(this.rows, Length.fill());
        setGap(SCROLLBAR_GAP);
        add(page, Length.fill());
        add(new Scrollbar(theme.scrollbar(), scroll), Length.fixed(Scrollbar.WIDTH));
    }

    public int selected() {
        return rows.selected();
    }
}

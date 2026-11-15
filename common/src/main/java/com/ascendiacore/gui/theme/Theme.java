package com.ascendiacore.gui.theme;

import com.ascendiacore.gui.widget.button.model.ButtonStyle;
import com.ascendiacore.gui.widget.link.model.LinkStyle;
import com.ascendiacore.gui.widget.list.model.ListStyle;
import com.ascendiacore.gui.widget.panel.model.InsetStyle;
import com.ascendiacore.gui.widget.panel.model.PanelStyle;
import com.ascendiacore.gui.widget.scrollbar.model.ScrollbarStyle;

public record Theme(
        String name,
        ButtonStyle button,
        PanelStyle panel,
        InsetStyle inset,
        LinkStyle link,
        ListStyle list,
        ScrollbarStyle scrollbar) {
}

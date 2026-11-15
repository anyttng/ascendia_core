package com.ascendiacore.gui.theme;

import com.ascendiacore.gui.widget.button.model.ButtonStyle;
import com.ascendiacore.gui.widget.link.model.LinkStyle;
import com.ascendiacore.gui.widget.panel.model.InsetStyle;
import com.ascendiacore.gui.widget.panel.model.PanelStyle;

public record Theme(String name, ButtonStyle button, PanelStyle panel, InsetStyle inset, LinkStyle link) {
}

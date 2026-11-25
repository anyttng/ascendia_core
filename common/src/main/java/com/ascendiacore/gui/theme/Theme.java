package com.ascendiacore.gui.theme;

import com.ascendiacore.gui.base.component.model.TooltipStyle;
import com.ascendiacore.gui.widget.button.model.ButtonStyle;
import com.ascendiacore.gui.widget.link.model.LinkStyle;
import com.ascendiacore.gui.widget.list.model.ListStyle;
import com.ascendiacore.gui.widget.panel.model.InsetStyle;
import com.ascendiacore.gui.widget.panel.model.PanelStyle;
import com.ascendiacore.gui.widget.scrollbar.model.ScrollbarStyle;
import com.ascendiacore.gui.widget.slider.model.SliderStyle;
import com.ascendiacore.gui.widget.tabrow.model.TabRowStyle;
import com.ascendiacore.gui.widget.textfield.model.TextFieldStyle;
import com.ascendiacore.gui.widget.toggleswitch.model.ToggleSwitchStyle;

public record Theme(
        String id,
        ButtonStyle button,
        PanelStyle panel,
        InsetStyle inset,
        LinkStyle link,
        ListStyle list,
        ScrollbarStyle scrollbar,
        TooltipStyle tooltip,
        TextFieldStyle textField,
        ToggleSwitchStyle toggleSwitch,
        SliderStyle slider,
        TabRowStyle tabRow) {
}

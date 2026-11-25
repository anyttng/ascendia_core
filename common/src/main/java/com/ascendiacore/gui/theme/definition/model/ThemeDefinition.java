package com.ascendiacore.gui.theme.definition.model;

import com.ascendiacore.gui.widget.scrollbar.model.ScrollbarStyle;
import com.ascendiacore.gui.widget.slider.model.SliderStyle;
import com.ascendiacore.gui.widget.tabrow.model.TabRowStyle;
import com.ascendiacore.gui.widget.toggleswitch.model.ToggleSwitchStyle;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record ThemeDefinition(
        ColorRoles roles,
        ButtonSection button,
        PanelSection panel,
        InsetSection inset,
        LinkSection link,
        ListSection list,
        ScrollbarStyle scrollbar,
        TooltipSection tooltip,
        TextFieldSection textField,
        ToggleSwitchStyle toggleSwitch,
        SliderStyle slider,
        TabRowStyle tabRow) {
    private static final String ROLES_KEY = "roles";
    private static final String BUTTON_KEY = "button";
    private static final String PANEL_KEY = "panel";
    private static final String INSET_KEY = "inset";
    private static final String LINK_KEY = "link";
    private static final String LIST_KEY = "list";
    private static final String SCROLLBAR_KEY = "scrollbar";
    private static final String TOOLTIP_KEY = "tooltip";
    private static final String TEXT_FIELD_KEY = "text_field";
    private static final String TOGGLE_SWITCH_KEY = "toggle_switch";
    private static final String SLIDER_KEY = "slider";
    private static final String TAB_ROW_KEY = "tab_row";

    public static final Codec<ThemeDefinition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ColorRoles.CODEC.fieldOf(ROLES_KEY).forGetter(ThemeDefinition::roles),
            ButtonSection.CODEC.fieldOf(BUTTON_KEY).forGetter(ThemeDefinition::button),
            PanelSection.CODEC.fieldOf(PANEL_KEY).forGetter(ThemeDefinition::panel),
            InsetSection.CODEC.fieldOf(INSET_KEY).forGetter(ThemeDefinition::inset),
            LinkSection.CODEC.optionalFieldOf(LINK_KEY, LinkSection.NONE).forGetter(ThemeDefinition::link),
            ListSection.CODEC.fieldOf(LIST_KEY).forGetter(ThemeDefinition::list),
            ThemeCodecs.SCROLLBAR.fieldOf(SCROLLBAR_KEY).forGetter(ThemeDefinition::scrollbar),
            TooltipSection.CODEC.fieldOf(TOOLTIP_KEY).forGetter(ThemeDefinition::tooltip),
            TextFieldSection.CODEC.fieldOf(TEXT_FIELD_KEY).forGetter(ThemeDefinition::textField),
            ThemeCodecs.TOGGLE_SWITCH.fieldOf(TOGGLE_SWITCH_KEY).forGetter(ThemeDefinition::toggleSwitch),
            ThemeCodecs.SLIDER.fieldOf(SLIDER_KEY).forGetter(ThemeDefinition::slider),
            ThemeCodecs.TAB_ROW.fieldOf(TAB_ROW_KEY).forGetter(ThemeDefinition::tabRow))
            .apply(instance, ThemeDefinition::new));
}

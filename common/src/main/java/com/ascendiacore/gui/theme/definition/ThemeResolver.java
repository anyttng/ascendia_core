package com.ascendiacore.gui.theme.definition;

import com.ascendiacore.gui.base.component.model.AlertColors;
import com.ascendiacore.gui.base.component.model.LevelColors;
import com.ascendiacore.gui.base.component.model.TooltipStyle;
import com.ascendiacore.gui.theme.Theme;
import com.ascendiacore.gui.theme.definition.model.ButtonSection;
import com.ascendiacore.gui.theme.definition.model.ColorRoles;
import com.ascendiacore.gui.theme.definition.model.InsetSection;
import com.ascendiacore.gui.theme.definition.model.LinkSection;
import com.ascendiacore.gui.theme.definition.model.ListSection;
import com.ascendiacore.gui.theme.definition.model.PanelSection;
import com.ascendiacore.gui.theme.definition.model.TextFieldSection;
import com.ascendiacore.gui.theme.definition.model.ThemeDefinition;
import com.ascendiacore.gui.theme.definition.model.TooltipSection;
import com.ascendiacore.gui.widget.button.model.ButtonStyle;
import com.ascendiacore.gui.widget.link.model.LinkColors;
import com.ascendiacore.gui.widget.link.model.LinkStyle;
import com.ascendiacore.gui.widget.list.model.ListStyle;
import com.ascendiacore.gui.widget.panel.model.FillKind;
import com.ascendiacore.gui.widget.panel.model.FillStyle;
import com.ascendiacore.gui.widget.panel.model.Fills;
import com.ascendiacore.gui.widget.panel.model.InsetStyle;
import com.ascendiacore.gui.widget.panel.model.PanelStyle;
import com.ascendiacore.gui.widget.textfield.model.TextFieldStyle;

public final class ThemeResolver {
    private ThemeResolver() {
    }

    public static Theme resolve(String id, ThemeDefinition definition) {
        ColorRoles roles = definition.roles();
        return new Theme(id, button(definition.button(), roles), panel(definition.panel(), roles),
                inset(definition.inset(), roles), link(definition.link(), roles),
                list(definition.list(), definition.button().surface(), roles), definition.scrollbar(),
                tooltip(definition.tooltip(), roles), textField(definition.textField(), roles), definition.toggleSwitch(),
                definition.slider(), definition.tabRow());
    }

    private static ButtonStyle button(ButtonSection section, ColorRoles roles) {
        FillStyle surface = roles.getSurface(section.surface());
        return new ButtonStyle(section.normal(), section.hover(), section.disabled(), section.selected(),
                section.selectedHover(), section.label().orElse(surface.ink()),
                section.disabledLabel().orElse(surface.mutedInk()), section.selectedLabel().orElse(roles.accent()),
                section.hasLabelShadow().orElse(surface.hasInkShadow()));
    }

    private static PanelStyle panel(PanelSection section, ColorRoles roles) {
        Fills base = section.surface().map(kind -> textOnSprite(roles.getSurface(kind))).orElse(roles.getSurfaces());
        return new PanelStyle(section.frame(), section.fills().applyTo(base));
    }

    private static Fills textOnSprite(FillStyle surface) {
        FillStyle text = textOn(surface);
        return new Fills(text, text, text);
    }

    private static InsetStyle inset(InsetSection section, ColorRoles roles) {
        return new InsetStyle(section.light(), section.dark(), section.rim(), section.fills().applyTo(roles.getSurfaces()));
    }

    private static LinkStyle link(LinkSection section, ColorRoles roles) {
        return new LinkStyle(
                section.onLight().applyTo(new LinkColors(roles.link(), roles.linkHover(), roles.light().mutedInk())),
                section.onMid().applyTo(new LinkColors(roles.link(), roles.linkHover(), roles.mid().mutedInk())),
                section.onDark().applyTo(new LinkColors(roles.accent(), roles.accentHover(), roles.dark().mutedInk())));
    }

    private static ListStyle list(ListSection section, FillKind buttonSurface, ColorRoles roles) {
        FillStyle selected = textOn(roles.getSurface(section.selectedSurface()));
        FillStyle selectedButton = new FillStyle(FillStyle.NO_FILL, roles.accent(), selected.mutedInk(), selected.errorInk(),
                selected.hasInkShadow());
        return new ListStyle(section.row(), section.rowHover(), section.rowSelected(),
                section.framedText().applyTo(textOn(roles.getSurface(section.surface()))),
                section.framedSelectedText().applyTo(selected),
                section.buttonText().applyTo(textOn(roles.getSurface(buttonSurface))),
                section.buttonSelectedText().applyTo(selectedButton));
    }

    private static FillStyle textOn(FillStyle surface) {
        return new FillStyle(FillStyle.NO_FILL, surface.ink(), surface.mutedInk(), surface.errorInk(), surface.hasInkShadow());
    }

    private static TooltipStyle tooltip(TooltipSection section, ColorRoles roles) {
        FillStyle surface = roles.getSurface(section.surface());
        return new TooltipStyle(section.sprites().orElse(null), section.text().orElse(surface.ink()),
                section.value().orElse(roles.accent()), section.note().orElse(surface.mutedInk()),
                new AlertColors(roles.warning(), roles.danger(), roles.requirement()),
                new LevelColors(roles.success(), roles.accent(), roles.danger()), roles.derived(), roles.divider());
    }

    private static TextFieldStyle textField(TextFieldSection section, ColorRoles roles) {
        FillStyle surface = roles.getSurface(section.surface());
        return new TextFieldStyle(section.normal(), section.focused(), section.disabled(), section.text().orElse(surface.ink()),
                section.disabledText().orElse(surface.mutedInk()), section.placeholder().orElse(surface.mutedInk()),
                section.hasTextShadow().orElse(surface.hasInkShadow()));
    }
}

package com.ascendiacore.client.gui.bridge;

import java.util.Optional;

import org.jspecify.annotations.Nullable;

import com.ascendiacore.gui.base.component.GuiComponent;
import com.ascendiacore.gui.base.component.model.TooltipStyle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

final class ComponentTooltip {
    private final AbstractWidget widget;
    private final GuiComponent component;
    private Component text = Component.empty();

    ComponentTooltip(AbstractWidget widget, GuiComponent component) {
        this.widget = widget;
        this.component = component;
    }

    void follow(int mouseX, int mouseY) {
        Component next = component.tooltipAt(mouseX, mouseY);
        if (next.equals(text)) {
            return;
        }
        text = next;
        TooltipStyle style = component.tooltipStyle();
        if (style == null || next.getString().isEmpty()) {
            widget.setTooltip(null);
            return;
        }
        Component colored = TooltipRule.stretched(Minecraft.getInstance(),
                Component.empty().withColor(style.textColor()).append(next));
        @Nullable Identifier sprites = style.sprites() == null ? null : Identifier.parse(style.sprites());
        widget.setTooltip(Tooltip.create(colored, Optional.empty(), sprites));
    }
}

package com.ascendiacore.gui.widget.tabrow.model;

import java.util.List;

import com.ascendiacore.gui.base.draw.Icon;

import net.minecraft.network.chat.Component;

public record TabContent(Component name, boolean isNameDrawn, List<Icon> icons) {
    public TabContent {
        icons = List.copyOf(icons);
    }

    public static TabContent text(Component name) {
        return new TabContent(name, true, List.of());
    }

    public static TabContent icons(Component name, Icon... icons) {
        return new TabContent(name, false, List.of(icons));
    }

    public static TabContent iconsAndText(Component name, Icon... icons) {
        return new TabContent(name, true, List.of(icons));
    }

    public boolean hasIcons() {
        return !icons.isEmpty();
    }
}

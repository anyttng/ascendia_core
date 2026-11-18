package com.ascendiacore.gui.widget.heading;

import com.ascendiacore.gui.widget.label.Label;
import com.ascendiacore.gui.widget.label.model.LabelRole;
import com.ascendiacore.gui.widget.label.model.TextAlign;
import com.ascendiacore.gui.widget.panel.model.FillStyle;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public final class Heading {
    private Heading() {
    }

    public static Label of(FillStyle fill, Component text) {
        Label label = new Label(fill, LabelRole.NORMAL, text.copy().withStyle(ChatFormatting.BOLD));
        label.setAlign(TextAlign.CENTER);
        return label;
    }
}

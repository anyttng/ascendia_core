package com.ascendiacore.gui.base.hint.model;

import net.minecraft.network.chat.Component;

public record HintValue(Component name, Component text, boolean current) {
}

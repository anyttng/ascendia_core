package com.ascendiacore.gui.base.draw;

import net.minecraft.network.chat.Component;

@FunctionalInterface
public interface TextMetrics {
    int textWidthPixels(Component text);
}

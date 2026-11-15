package com.ascendiacore.gui.base.draw;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public interface Canvas extends TextMetrics {
    void sprite(String sprite, int x, int y, int width, int height);

    void sprite(String sprite, int x, int y, int width, int height, int tint);

    void fill(int x, int y, int width, int height, int color);

    void text(Component text, int x, int y, int color, boolean shadow);

    void item(ItemStack stack, int x, int y);

    void pushClip(int x, int y, int width, int height);

    void popClip();

    long frameTimeMillis();
}

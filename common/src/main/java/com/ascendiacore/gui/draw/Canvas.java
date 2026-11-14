package com.ascendiacore.gui.draw;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public interface Canvas {
    void sprite(String sprite, int x, int y, int width, int height);

    void sprite(String sprite, int x, int y, int width, int height, int tint);

    void fill(int x, int y, int width, int height, int color);

    void text(Component text, int x, int y, int color, boolean shadow);

    int textWidth(Component text);

    void item(ItemStack stack, int x, int y);
}

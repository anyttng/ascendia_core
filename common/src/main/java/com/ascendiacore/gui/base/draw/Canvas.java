package com.ascendiacore.gui.base.draw;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public interface Canvas extends TextMetrics {
    void sprite(String sprite, int x, int y, int widthPixels, int heightPixels);

    void sprite(String sprite, int x, int y, int widthPixels, int heightPixels, int tint);

    void fill(int x, int y, int widthPixels, int heightPixels, int color);

    void text(Component text, int x, int y, int color, boolean hasShadow);

    void item(ItemStack stack, int x, int y);

    void pushClip(int x, int y, int widthPixels, int heightPixels);

    void popClip();

    long getFrameTimeMillis();
}

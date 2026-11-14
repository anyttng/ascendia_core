package com.ascendiacore.gui.icon;

import com.ascendiacore.gui.draw.Canvas;

import net.minecraft.world.item.ItemStack;

@FunctionalInterface
public interface Icon {
    int SIZE = 16;

    void draw(Canvas canvas, int x, int y);

    static Icon item(ItemStack stack) {
        return (canvas, x, y) -> canvas.item(stack, x, y);
    }

    static Icon sprite(String sprite) {
        return (canvas, x, y) -> canvas.sprite(sprite, x, y, SIZE, SIZE);
    }
}

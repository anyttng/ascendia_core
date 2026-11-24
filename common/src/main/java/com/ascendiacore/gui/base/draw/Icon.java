package com.ascendiacore.gui.base.draw;

import net.minecraft.world.item.ItemStack;

@FunctionalInterface
public interface Icon {
    int SIZE_PIXELS = 16;

    void draw(Canvas canvas, int x, int y);

    static Icon item(ItemStack stack) {
        return (canvas, x, y) -> canvas.item(stack, x, y);
    }

    static Icon sprite(String sprite) {
        return (canvas, x, y) -> canvas.sprite(sprite, x, y, SIZE_PIXELS, SIZE_PIXELS);
    }
}

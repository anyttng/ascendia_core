package com.ascendia.core.client.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

@FunctionalInterface
public interface Icon {
    int SIZE = 16;

    void extract(GuiGraphicsExtractor graphics, int x, int y);

    static Icon item(ItemStack stack) {
        return (graphics, x, y) -> graphics.item(stack, x, y);
    }

    static Icon texture(Identifier texture) {
        return (graphics, x, y) -> graphics.blit(RenderPipelines.GUI_TEXTURED, texture, x, y, 0.0F, 0.0F,
                SIZE, SIZE, SIZE, SIZE);
    }
}

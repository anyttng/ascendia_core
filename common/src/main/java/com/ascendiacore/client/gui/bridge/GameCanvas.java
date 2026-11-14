package com.ascendiacore.client.gui.bridge;

import java.util.HashMap;
import java.util.Map;

import com.ascendiacore.gui.draw.Canvas;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

public final class GameCanvas implements Canvas {
    private static final Map<String, Identifier> SPRITE_IDS = new HashMap<>();

    private final GuiGraphicsExtractor graphics;
    private final Font font;

    public GameCanvas(GuiGraphicsExtractor graphics, Font font) {
        this.graphics = graphics;
        this.font = font;
    }

    @Override
    public void sprite(String sprite, int x, int y, int width, int height) {
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, spriteId(sprite), x, y, width, height);
    }

    @Override
    public void sprite(String sprite, int x, int y, int width, int height, int tint) {
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, spriteId(sprite), x, y, width, height, tint);
    }

    @Override
    public void fill(int x, int y, int width, int height, int color) {
        graphics.fill(x, y, x + width, y + height, color);
    }

    @Override
    public void text(Component text, int x, int y, int color, boolean shadow) {
        graphics.text(font, text, x, y, color, shadow);
    }

    @Override
    public int textWidth(Component text) {
        return font.width(text);
    }

    @Override
    public void item(ItemStack stack, int x, int y) {
        graphics.item(stack, x, y);
    }

    private static Identifier spriteId(String sprite) {
        return SPRITE_IDS.computeIfAbsent(sprite, Identifier::parse);
    }
}

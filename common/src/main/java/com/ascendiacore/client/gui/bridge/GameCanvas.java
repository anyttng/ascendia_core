package com.ascendiacore.client.gui.bridge;

import java.util.HashMap;
import java.util.Map;

import com.mojang.blaze3d.systems.RenderSystem;

import com.ascendiacore.gui.base.draw.Canvas;

import net.minecraft.Util;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.ItemStack;

public final class GameCanvas implements Canvas {
    private static final Map<String, ResourceLocation> SPRITE_IDS = new HashMap<>();
    private static final float CHANNEL_MAX = 255.0F;

    private final GuiGraphics graphics;
    private final Font font;

    public GameCanvas(GuiGraphics graphics, Font font) {
        this.graphics = graphics;
        this.font = font;
    }

    @Override
    public void sprite(String sprite, int x, int y, int width, int height) {
        RenderSystem.enableBlend();
        graphics.blitSprite(spriteId(sprite), x, y, width, height);
    }

    @Override
    public void sprite(String sprite, int x, int y, int width, int height, int tint) {
        graphics.setColor(FastColor.ARGB32.red(tint) / CHANNEL_MAX, FastColor.ARGB32.green(tint) / CHANNEL_MAX,
                FastColor.ARGB32.blue(tint) / CHANNEL_MAX, FastColor.ARGB32.alpha(tint) / CHANNEL_MAX);
        sprite(sprite, x, y, width, height);
        graphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    public void fill(int x, int y, int width, int height, int color) {
        graphics.fill(x, y, x + width, y + height, color);
    }

    @Override
    public void text(Component text, int x, int y, int color, boolean shadow) {
        graphics.drawString(font, text, x, y, color, shadow);
    }

    @Override
    public int textWidth(Component text) {
        return font.width(text);
    }

    @Override
    public void item(ItemStack stack, int x, int y) {
        graphics.renderItem(stack, x, y);
    }

    @Override
    public void pushClip(int x, int y, int width, int height) {
        graphics.enableScissor(x, y, x + width, y + height);
    }

    @Override
    public void popClip() {
        graphics.disableScissor();
    }

    @Override
    public long frameTimeMillis() {
        return Util.getMillis();
    }

    private static ResourceLocation spriteId(String sprite) {
        return SPRITE_IDS.computeIfAbsent(sprite, ResourceLocation::parse);
    }
}

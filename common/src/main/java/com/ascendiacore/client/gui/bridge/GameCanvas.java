package com.ascendiacore.client.gui.bridge;

import java.util.HashMap;
import java.util.Map;

import com.ascendiacore.gui.base.draw.Canvas;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Util;
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
    public void sprite(String sprite, int x, int y, int widthPixels, int heightPixels) {
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, toSpriteId(sprite), x, y, widthPixels, heightPixels);
    }

    @Override
    public void sprite(String sprite, int x, int y, int widthPixels, int heightPixels, int tint) {
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, toSpriteId(sprite), x, y, widthPixels, heightPixels, tint);
    }

    @Override
    public void fill(int x, int y, int widthPixels, int heightPixels, int color) {
        graphics.fill(x, y, x + widthPixels, y + heightPixels, color);
    }

    @Override
    public void text(Component text, int x, int y, int color, boolean hasShadow) {
        graphics.text(font, text, x, y, color, hasShadow);
    }

    @Override
    public int textWidthPixels(Component text) {
        return font.width(text);
    }

    @Override
    public void item(ItemStack stack, int x, int y) {
        graphics.item(stack, x, y);
    }

    @Override
    public void pushClip(int x, int y, int widthPixels, int heightPixels) {
        graphics.enableScissor(x, y, x + widthPixels, y + heightPixels);
    }

    @Override
    public void popClip() {
        graphics.disableScissor();
    }

    @Override
    public long getFrameTimeMillis() {
        return Util.getMillis();
    }

    private static Identifier toSpriteId(String sprite) {
        return SPRITE_IDS.computeIfAbsent(sprite, Identifier::parse);
    }
}

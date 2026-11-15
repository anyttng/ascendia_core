package com.ascendiacore.gui.base.draw;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public final class RecordingCanvas implements Canvas {
    public record Sprite(String sprite, int x, int y, int width, int height) {
    }

    public record Fill(int x, int y, int width, int height, int color) {
    }

    public record Text(Component text, int x, int y, int color, boolean shadow) {
    }

    private final List<Sprite> sprites = new ArrayList<>();
    private final List<Fill> fills = new ArrayList<>();
    private final List<Text> texts = new ArrayList<>();
    private int textWidth;

    public void setTextWidth(int textWidth) {
        this.textWidth = textWidth;
    }

    public List<Sprite> sprites() {
        return sprites;
    }

    public List<Fill> fills() {
        return fills;
    }

    public List<Text> texts() {
        return texts;
    }

    @Override
    public void sprite(String sprite, int x, int y, int width, int height) {
        sprites.add(new Sprite(sprite, x, y, width, height));
    }

    @Override
    public void sprite(String sprite, int x, int y, int width, int height, int tint) {
        sprite(sprite, x, y, width, height);
    }

    @Override
    public void fill(int x, int y, int width, int height, int color) {
        fills.add(new Fill(x, y, width, height, color));
    }

    @Override
    public void text(Component text, int x, int y, int color, boolean shadow) {
        texts.add(new Text(text, x, y, color, shadow));
    }

    @Override
    public int textWidth(Component text) {
        return textWidth;
    }

    @Override
    public void item(ItemStack stack, int x, int y) {
    }

    @Override
    public void pushClip(int x, int y, int width, int height) {
    }

    @Override
    public void popClip() {
    }

    @Override
    public long frameTimeMillis() {
        return 0;
    }
}

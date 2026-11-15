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

    private final List<Sprite> sprites = new ArrayList<>();
    private final List<Fill> fills = new ArrayList<>();

    public List<Sprite> sprites() {
        return sprites;
    }

    public List<Fill> fills() {
        return fills;
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
    }

    @Override
    public int textWidth(Component text) {
        return 0;
    }

    @Override
    public void item(ItemStack stack, int x, int y) {
    }
}

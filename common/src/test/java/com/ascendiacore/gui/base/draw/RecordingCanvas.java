package com.ascendiacore.gui.base.draw;

import java.util.ArrayList;
import java.util.List;

import com.ascendiacore.gui.base.geometry.Rect;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public final class RecordingCanvas implements Canvas {
    public record Sprite(String sprite, int x, int y, int widthPixels, int heightPixels) {
    }

    public record Fill(int x, int y, int widthPixels, int heightPixels, int color) {
    }

    public record Text(Component text, int x, int y, int color, boolean hasShadow) {
    }

    private final List<Sprite> sprites = new ArrayList<>();
    private final List<Fill> fills = new ArrayList<>();
    private final List<Text> texts = new ArrayList<>();
    private final List<Rect> clips = new ArrayList<>();
    private int textWidthPixels;
    private int charWidthPixels;
    private long frameTimeMillis;

    public void setTextWidth(int textWidthPixels) {
        this.textWidthPixels = textWidthPixels;
    }

    public void setCharWidthPixels(int charWidthPixels) {
        this.charWidthPixels = charWidthPixels;
    }

    public void setFrameTimeMillis(long frameTimeMillis) {
        this.frameTimeMillis = frameTimeMillis;
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

    public List<Rect> clips() {
        return clips;
    }

    @Override
    public void sprite(String sprite, int x, int y, int widthPixels, int heightPixels) {
        sprites.add(new Sprite(sprite, x, y, widthPixels, heightPixels));
    }

    @Override
    public void sprite(String sprite, int x, int y, int widthPixels, int heightPixels, int tint) {
        sprite(sprite, x, y, widthPixels, heightPixels);
    }

    @Override
    public void fill(int x, int y, int widthPixels, int heightPixels, int color) {
        fills.add(new Fill(x, y, widthPixels, heightPixels, color));
    }

    @Override
    public void text(Component text, int x, int y, int color, boolean hasShadow) {
        texts.add(new Text(text, x, y, color, hasShadow));
    }

    @Override
    public int textWidthPixels(Component text) {
        if (charWidthPixels == 0) {
            return textWidthPixels;
        }
        String plain = text.getString();
        return plain.codePointCount(0, plain.length()) * charWidthPixels;
    }

    @Override
    public void item(ItemStack stack, int x, int y) {
    }

    @Override
    public void pushClip(int x, int y, int widthPixels, int heightPixels) {
        clips.add(new Rect(x, y, widthPixels, heightPixels));
    }

    @Override
    public void popClip() {
    }

    @Override
    public long getFrameTimeMillis() {
        return frameTimeMillis;
    }
}

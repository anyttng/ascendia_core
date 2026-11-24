package com.ascendiacore.gui.base.geometry;

public record Rect(int x, int y, int widthPixels, int heightPixels) {
    public int getRight() {
        return x + widthPixels;
    }

    public int getBottom() {
        return y + heightPixels;
    }

    public boolean isContaining(double pointX, double pointY) {
        return pointX >= x && pointY >= y && pointX < getRight() && pointY < getBottom();
    }

    public Rect intersection(Rect other) {
        int left = Math.max(x, other.x);
        int top = Math.max(y, other.y);
        return new Rect(left, top, Math.max(0, Math.min(getRight(), other.getRight()) - left),
                Math.max(0, Math.min(getBottom(), other.getBottom()) - top));
    }
}

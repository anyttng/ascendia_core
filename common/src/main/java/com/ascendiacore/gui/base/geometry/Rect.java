package com.ascendiacore.gui.base.geometry;

public record Rect(int x, int y, int width, int height) {
    public int right() {
        return x + width;
    }

    public int bottom() {
        return y + height;
    }

    public boolean contains(double pointX, double pointY) {
        return pointX >= x && pointY >= y && pointX < right() && pointY < bottom();
    }

    public Rect intersect(Rect other) {
        int left = Math.max(x, other.x);
        int top = Math.max(y, other.y);
        return new Rect(left, top, Math.max(0, Math.min(right(), other.right()) - left),
                Math.max(0, Math.min(bottom(), other.bottom()) - top));
    }
}

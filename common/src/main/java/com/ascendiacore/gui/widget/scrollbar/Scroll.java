package com.ascendiacore.gui.widget.scrollbar;

public final class Scroll {
    private final int wheelStepPixels;
    private int contentPixels;
    private int viewPixels;
    private int offsetPixels;

    public Scroll(int wheelStepPixels) {
        this.wheelStepPixels = wheelStepPixels;
    }

    public int getContentPixels() {
        return contentPixels;
    }

    public int getViewPixels() {
        return viewPixels;
    }

    public int getOffsetPixels() {
        return offsetPixels;
    }

    public int getMaxOffsetPixels() {
        return Math.max(0, contentPixels - viewPixels);
    }

    public boolean isScrollable() {
        return getMaxOffsetPixels() > 0;
    }

    public void setContentPixels(int contentPixels) {
        this.contentPixels = Math.max(0, contentPixels);
        scrollTo(offsetPixels);
    }

    public void setViewPixels(int viewPixels) {
        this.viewPixels = Math.max(0, viewPixels);
        scrollTo(offsetPixels);
    }

    public void scrollTo(int offsetPixels) {
        this.offsetPixels = Math.clamp(offsetPixels, 0, getMaxOffsetPixels());
    }

    public void scrollBy(int deltaPixels) {
        scrollTo(offsetPixels + deltaPixels);
    }

    public void wheel(double notches) {
        scrollBy((int) Math.round(-notches * wheelStepPixels));
    }
}

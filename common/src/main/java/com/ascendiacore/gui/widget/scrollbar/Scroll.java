package com.ascendiacore.gui.widget.scrollbar;

public final class Scroll {
    private final int wheelStep;
    private int content;
    private int view;
    private int offset;

    public Scroll(int wheelStep) {
        this.wheelStep = wheelStep;
    }

    public int content() {
        return content;
    }

    public int view() {
        return view;
    }

    public int offset() {
        return offset;
    }

    public int maxOffset() {
        return Math.max(0, content - view);
    }

    public boolean scrollable() {
        return maxOffset() > 0;
    }

    public void setContent(int content) {
        this.content = Math.max(0, content);
        scrollTo(offset);
    }

    public void setView(int view) {
        this.view = Math.max(0, view);
        scrollTo(offset);
    }

    public void scrollTo(int offset) {
        this.offset = Math.clamp(offset, 0, maxOffset());
    }

    public void scrollBy(int delta) {
        scrollTo(offset + delta);
    }

    public void wheel(double amount) {
        scrollBy((int) Math.round(-amount * wheelStep));
    }
}

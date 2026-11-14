package com.ascendiacore.gui.component;

import com.ascendiacore.gui.draw.Canvas;
import com.ascendiacore.gui.input.KeyInput;
import com.ascendiacore.gui.input.PointerButton;
import com.ascendiacore.gui.input.PointerInput;
import com.ascendiacore.gui.input.TextInput;

import net.minecraft.network.chat.Component;

public abstract class GuiComponent {
    private static final ComponentHost NO_HOST = new ComponentHost() {
        @Override
        public void stateChanged() {
        }

        @Override
        public void clickSound() {
        }
    };

    private ComponentHost host = NO_HOST;
    private int x;
    private int y;
    private int width;
    private int height;
    private boolean visible = true;
    private boolean enabled = true;
    private boolean hovered;
    private boolean focused;

    public void attach(ComponentHost host) {
        this.host = host;
    }

    public Component narration() {
        return Component.empty();
    }

    public boolean takesFocusOnPress() {
        return false;
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    public int width() {
        return width;
    }

    public int height() {
        return height;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public boolean visible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
        if (!visible) {
            hovered = false;
        }
        host.stateChanged();
    }

    public boolean enabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        host.stateChanged();
    }

    public boolean hovered() {
        return hovered;
    }

    public boolean focused() {
        return focused;
    }

    public void setFocused(boolean focused) {
        this.focused = focused;
    }

    public boolean contains(double pointX, double pointY) {
        return pointX >= x && pointY >= y && pointX < x + width && pointY < y + height;
    }

    public final void draw(Canvas canvas, int pointerX, int pointerY) {
        if (!visible) {
            return;
        }
        hovered = contains(pointerX, pointerY);
        drawContent(canvas);
    }

    public final boolean pointerPressed(PointerInput input) {
        if (!takesInput() || input.button() != PointerButton.LEFT || !contains(input.x(), input.y())) {
            return false;
        }
        onPress(input);
        return true;
    }

    public final boolean keyPressed(KeyInput input) {
        return takesInput() && onKey(input);
    }

    public final boolean textTyped(TextInput input) {
        return takesInput() && onText(input);
    }

    protected abstract void drawContent(Canvas canvas);

    protected void playClick() {
        host.clickSound();
    }

    protected void onPress(PointerInput input) {
    }

    protected boolean onKey(KeyInput input) {
        return false;
    }

    protected boolean onText(TextInput input) {
        return false;
    }

    private boolean takesInput() {
        return visible && enabled;
    }
}

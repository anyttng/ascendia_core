package com.ascendiacore.gui.base.component;

import com.ascendiacore.gui.base.draw.Canvas;
import com.ascendiacore.gui.base.draw.TextMetrics;
import com.ascendiacore.gui.base.geometry.Size;
import com.ascendiacore.gui.base.input.KeyInput;
import com.ascendiacore.gui.base.input.PointerInput;
import com.ascendiacore.gui.base.input.TextInput;
import com.ascendiacore.gui.base.input.model.PointerButton;
import com.ascendiacore.gui.base.property.Property;

import net.minecraft.network.chat.Component;

public abstract class GuiComponent {
    private static final ComponentHost NO_HOST = () -> {
    };

    private ComponentHost host = NO_HOST;
    private int x;
    private int y;
    private int width;
    private int height;
    private Property<Boolean> visible = Property.of(true);
    private Property<Boolean> enabled = Property.of(true);
    private boolean countedWhenInactive;
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

    public Size naturalSize(TextMetrics metrics) {
        return new Size(width, height);
    }

    public boolean countedWhenInactive() {
        return countedWhenInactive;
    }

    public void setCountedWhenInactive(boolean countedWhenInactive) {
        this.countedWhenInactive = countedWhenInactive;
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
        return visible.get();
    }

    public void setVisible(boolean visible) {
        setVisible(Property.of(visible));
    }

    public void setVisible(Property<Boolean> visible) {
        this.visible = visible;
    }

    public boolean enabled() {
        return enabled.get();
    }

    public void setEnabled(boolean enabled) {
        setEnabled(Property.of(enabled));
    }

    public void setEnabled(Property<Boolean> enabled) {
        this.enabled = enabled;
    }

    public boolean hovered() {
        return hovered && visible();
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
        if (!visible()) {
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
        return visible() && enabled();
    }
}

package com.ascendiacore.gui.base.component;

import org.jspecify.annotations.Nullable;

import com.ascendiacore.gui.base.component.model.TooltipStyle;
import com.ascendiacore.gui.base.draw.Canvas;
import com.ascendiacore.gui.base.draw.TextMetrics;
import com.ascendiacore.gui.base.geometry.Rect;
import com.ascendiacore.gui.base.geometry.Size;
import com.ascendiacore.gui.base.input.KeyInput;
import com.ascendiacore.gui.base.input.PointerInput;
import com.ascendiacore.gui.base.input.TextInput;
import com.ascendiacore.gui.base.input.WheelInput;
import com.ascendiacore.gui.base.input.model.PointerButton;
import com.ascendiacore.gui.base.property.Property;

import net.minecraft.network.chat.Component;

public abstract class GuiComponent {
    private static final ComponentHost NO_HOST = () -> {
    };

    private ComponentHost host = NO_HOST;
    private @Nullable GuiComponent parent;
    private int x;
    private int y;
    private int width;
    private int height;
    private Property<Boolean> visible = Property.of(true);
    private Property<Boolean> enabled = Property.of(true);
    private Property<Component> tooltip = Property.of(Component.empty());
    private @Nullable TooltipStyle tooltipStyle;
    private boolean countedWhenInactive;
    private boolean hovered;
    private boolean focused;
    private int pointerX;
    private int pointerY;

    public void attach(ComponentHost host) {
        this.host = host;
    }

    public Component narration() {
        return Component.empty();
    }

    public boolean takesFocusOnPress() {
        return false;
    }

    public boolean followsDrag() {
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

    public boolean interactive() {
        return true;
    }

    public boolean visible() {
        return visibleByOwnRule() && (parent == null || parent.visible());
    }

    public boolean visibleByOwnRule() {
        return visible.get();
    }

    public void setVisible(boolean visible) {
        setVisible(Property.of(visible));
    }

    public void setVisible(Property<Boolean> visible) {
        this.visible = visible;
    }

    public boolean enabled() {
        return enabledByOwnRule() && (parent == null || parent.enabled());
    }

    public boolean enabledByOwnRule() {
        return enabled.get();
    }

    public void setEnabled(boolean enabled) {
        setEnabled(Property.of(enabled));
    }

    public void setEnabled(Property<Boolean> enabled) {
        this.enabled = enabled;
    }

    public Component tooltip() {
        return tooltip.get();
    }

    public @Nullable TooltipStyle tooltipStyle() {
        return tooltipStyle;
    }

    public Component tooltipAt(double pointX, double pointY) {
        return tooltip();
    }

    public void setTooltip(TooltipStyle style, Component text) {
        setTooltip(style, Property.of(text));
    }

    public void setTooltip(TooltipStyle style, Property<Component> text) {
        this.tooltipStyle = style;
        this.tooltip = text;
    }

    public boolean hovered() {
        return hovered && visible();
    }

    public boolean focused() {
        return focused;
    }

    public void setFocused(boolean focused) {
        this.focused = focused;
        if (focused) {
            revealToAncestors();
        }
    }

    public boolean tabStop() {
        return true;
    }

    public boolean contains(double pointX, double pointY) {
        return pointX >= x && pointY >= y && pointX < x + width && pointY < y + height;
    }

    public @Nullable Rect window() {
        return parent == null ? null : parent.windowOfChildren();
    }

    public boolean shows(double pointX, double pointY) {
        Rect window = window();
        return contains(pointX, pointY) && (window == null || window.contains(pointX, pointY));
    }

    public boolean takesPointerAt(double pointX, double pointY) {
        return takesInput() && shows(pointX, pointY);
    }

    public final void draw(Canvas canvas, int pointerX, int pointerY) {
        if (!visible()) {
            return;
        }
        this.pointerX = pointerX;
        this.pointerY = pointerY;
        hovered = shows(pointerX, pointerY);
        drawInWindow(canvas);
    }

    public final boolean pointerPressed(PointerInput input) {
        if (input.button() != PointerButton.LEFT || !takesPointerAt(input.x(), input.y())) {
            return false;
        }
        onPress(input);
        return true;
    }

    public final boolean pointerDragged(PointerInput input) {
        return takesInput() && input.button() == PointerButton.LEFT && onDrag(input);
    }

    public final boolean pointerReleased(PointerInput input) {
        if (input.button() != PointerButton.LEFT) {
            return false;
        }
        onRelease(input);
        return true;
    }

    public final boolean wheelScrolled(WheelInput input) {
        if (takesInput() && shows(input.x(), input.y()) && onWheel(input)) {
            return true;
        }
        return parent != null && parent.wheelScrolled(input);
    }

    public final boolean keyPressed(KeyInput input) {
        return takesInput() && onKey(input);
    }

    public final boolean textTyped(TextInput input) {
        return takesInput() && onText(input);
    }

    protected abstract void drawContent(Canvas canvas);

    protected @Nullable Rect windowOfChildren() {
        return window();
    }

    protected void reveal(GuiComponent descendant) {
    }

    protected final void adopt(GuiComponent child) {
        child.parent = this;
    }

    protected void playClick() {
        host.clickSound();
    }

    protected int pointerX() {
        return pointerX;
    }

    protected int pointerY() {
        return pointerY;
    }

    protected void onPress(PointerInput input) {
    }

    protected boolean onDrag(PointerInput input) {
        return false;
    }

    protected void onRelease(PointerInput input) {
    }

    protected boolean onWheel(WheelInput input) {
        return false;
    }

    protected boolean onKey(KeyInput input) {
        return false;
    }

    protected boolean onText(TextInput input) {
        return false;
    }

    private void drawInWindow(Canvas canvas) {
        Rect window = window();
        if (window == null) {
            drawContent(canvas);
            return;
        }
        canvas.pushClip(window.x(), window.y(), window.width(), window.height());
        drawContent(canvas);
        canvas.popClip();
    }

    private void revealToAncestors() {
        for (GuiComponent ancestor = parent; ancestor != null; ancestor = ancestor.parent) {
            ancestor.reveal(this);
        }
    }

    private boolean takesInput() {
        return interactive() && visible() && enabled();
    }
}

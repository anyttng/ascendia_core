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
    private int widthPixels;
    private int heightPixels;
    private Property<Boolean> visible = Property.of(true);
    private Property<Boolean> enabled = Property.of(true);
    private Property<Component> tooltip = Property.of(Component.empty());
    private @Nullable TooltipStyle tooltipStyle;
    private boolean isCountedWhenInactive;
    private boolean isHovered;
    private boolean isFocused;
    private int pointerX;
    private int pointerY;

    public void attach(ComponentHost host) {
        this.host = host;
    }

    public Component getNarration() {
        return Component.empty();
    }

    public boolean isTakingFocusOnPress() {
        return false;
    }

    public boolean isFollowingDrag() {
        return false;
    }

    public Size naturalSize(TextMetrics metrics) {
        return new Size(widthPixels, heightPixels);
    }

    public boolean isCountedWhenInactive() {
        return isCountedWhenInactive;
    }

    public void setCountedWhenInactive(boolean isCountedWhenInactive) {
        this.isCountedWhenInactive = isCountedWhenInactive;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidthPixels() {
        return widthPixels;
    }

    public int getHeightPixels() {
        return heightPixels;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setSize(int widthPixels, int heightPixels) {
        this.widthPixels = widthPixels;
        this.heightPixels = heightPixels;
    }

    public boolean isInteractive() {
        return true;
    }

    public boolean isVisible() {
        return isVisibleByOwnRule() && (parent == null || parent.isVisible());
    }

    public boolean isVisibleByOwnRule() {
        return visible.get();
    }

    public void setVisible(boolean isVisible) {
        setVisible(Property.of(isVisible));
    }

    public void setVisible(Property<Boolean> visible) {
        this.visible = visible;
    }

    public boolean isEnabled() {
        return isEnabledByOwnRule() && (parent == null || parent.isEnabled());
    }

    public boolean isEnabledByOwnRule() {
        return enabled.get();
    }

    public void setEnabled(boolean isEnabled) {
        setEnabled(Property.of(isEnabled));
    }

    public void setEnabled(Property<Boolean> enabled) {
        this.enabled = enabled;
    }

    public Component getTooltip() {
        return tooltip.get();
    }

    public @Nullable TooltipStyle getTooltipStyle() {
        return tooltipStyle;
    }

    public Component getTooltipAt(double pointX, double pointY) {
        return getTooltip();
    }

    public void setTooltip(TooltipStyle style, Component text) {
        setTooltip(style, Property.of(text));
    }

    public void setTooltip(TooltipStyle style, Property<Component> text) {
        this.tooltipStyle = style;
        this.tooltip = text;
    }

    public boolean isHovered() {
        return isHovered && isVisible();
    }

    public boolean isFocused() {
        return isFocused;
    }

    public void setFocused(boolean isFocused) {
        this.isFocused = isFocused;
        if (isFocused) {
            revealToAncestors();
        }
    }

    public boolean isTabStop() {
        return true;
    }

    public boolean isContaining(double pointX, double pointY) {
        return pointX >= x && pointY >= y && pointX < x + widthPixels && pointY < y + heightPixels;
    }

    public @Nullable Rect getWindow() {
        return parent == null ? null : parent.getWindowOfChildren();
    }

    public boolean isShowing(double pointX, double pointY) {
        Rect window = getWindow();
        return isContaining(pointX, pointY) && (window == null || window.isContaining(pointX, pointY));
    }

    public boolean isTakingPointerAt(double pointX, double pointY) {
        return isTakingInput() && isShowing(pointX, pointY);
    }

    public final void draw(Canvas canvas, int pointerX, int pointerY) {
        if (!isVisible()) {
            return;
        }
        this.pointerX = pointerX;
        this.pointerY = pointerY;
        isHovered = isShowing(pointerX, pointerY);
        drawInWindow(canvas);
    }

    public final boolean tryPressPointer(PointerInput input) {
        if (input.button() != PointerButton.LEFT || !isTakingPointerAt(input.x(), input.y())) {
            return false;
        }
        onPress(input);
        return true;
    }

    public final boolean tryDragPointer(PointerInput input) {
        return isTakingInput() && input.button() == PointerButton.LEFT && tryHandleDrag(input);
    }

    public final boolean tryReleasePointer(PointerInput input) {
        if (input.button() != PointerButton.LEFT) {
            return false;
        }
        onRelease(input);
        return true;
    }

    public final boolean tryScrollWheel(WheelInput input) {
        if (isTakingInput() && isShowing(input.x(), input.y()) && tryHandleWheel(input)) {
            return true;
        }
        return parent != null && parent.tryScrollWheel(input);
    }

    public final boolean tryPressKey(KeyInput input) {
        return isTakingInput() && tryHandleKey(input);
    }

    public final boolean tryTypeText(TextInput input) {
        return isTakingInput() && tryHandleText(input);
    }

    protected abstract void drawContent(Canvas canvas);

    protected @Nullable Rect getWindowOfChildren() {
        return getWindow();
    }

    protected void reveal(GuiComponent descendant) {
    }

    protected final void adopt(GuiComponent child) {
        child.parent = this;
    }

    protected void playClick() {
        host.clickSound();
    }

    protected int getPointerX() {
        return pointerX;
    }

    protected int getPointerY() {
        return pointerY;
    }

    protected void onPress(PointerInput input) {
    }

    protected boolean tryHandleDrag(PointerInput input) {
        return false;
    }

    protected void onRelease(PointerInput input) {
    }

    protected boolean tryHandleWheel(WheelInput input) {
        return false;
    }

    protected boolean tryHandleKey(KeyInput input) {
        return false;
    }

    protected boolean tryHandleText(TextInput input) {
        return false;
    }

    private void drawInWindow(Canvas canvas) {
        Rect window = getWindow();
        if (window == null) {
            drawContent(canvas);
            return;
        }
        canvas.pushClip(window.x(), window.y(), window.widthPixels(), window.heightPixels());
        drawContent(canvas);
        canvas.popClip();
    }

    private void revealToAncestors() {
        for (GuiComponent ancestor = parent; ancestor != null; ancestor = ancestor.parent) {
            ancestor.reveal(this);
        }
    }

    private boolean isTakingInput() {
        return isInteractive() && isVisible() && isEnabled();
    }
}

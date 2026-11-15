package com.ascendiacore.client.gui.bridge;

import com.ascendiacore.gui.base.component.ComponentHost;
import com.ascendiacore.gui.base.component.GuiComponent;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import org.jspecify.annotations.Nullable;

public final class ComponentWidget extends AbstractWidget implements ComponentHost {
    static final int OUTSIDE = Integer.MIN_VALUE;

    private final GuiComponent component;
    private final ComponentTooltip tooltip;
    private @Nullable Tooltip shownTooltip;

    public ComponentWidget(GuiComponent component) {
        super(component.x(), component.y(), component.width(), component.height(), component.narration());
        this.component = component;
        this.tooltip = new ComponentTooltip(this, component);
        component.attach(this);
    }

    @Override
    public void setTooltip(@Nullable Tooltip tooltip) {
        this.shownTooltip = tooltip;
        super.setTooltip(tooltip);
    }

    @Override
    public boolean isActive() {
        return component.interactive() && component.visible() && component.enabled();
    }

    @Override
    public NarratableEntry.NarrationPriority narrationPriority() {
        return isActive() ? super.narrationPriority() : NarratableEntry.NarrationPriority.NONE;
    }

    @Override
    public void clickSound() {
        playButtonClickSound(Minecraft.getInstance().getSoundManager());
    }

    @Override
    public Component getMessage() {
        return component.narration();
    }

    @Override
    public void setFocused(boolean focused) {
        super.setFocused(focused);
        component.setFocused(focused);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        placeComponent();
        boolean taken = component.pointerPressed(GameInput.pointer(event));
        if (taken && !shouldTakeFocusAfterInteraction()) {
            dropScreenFocus();
        }
        return taken;
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
        placeComponent();
        return component.pointerDragged(GameInput.pointer(event));
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        placeComponent();
        boolean taken = component.pointerReleased(GameInput.pointer(event));
        if (!component.takesFocusOnPress()) {
            dropScreenFocus();
        }
        return taken;
    }

    @Override
    public boolean mouseScrolled(double x, double y, double scrollX, double scrollY) {
        placeComponent();
        return component.wheelScrolled(GameInput.wheel(x, y, scrollY));
    }

    @Override
    public boolean shouldTakeFocusAfterInteraction() {
        return component.takesFocusOnPress() || component.followsDrag();
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        return component.keyPressed(GameInput.key(event));
    }

    @Override
    public boolean charTyped(CharacterEvent event) {
        return component.textTyped(GameInput.text(event));
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        placeComponent();
        boolean pointerShown = graphics.containsPointInScissor(mouseX, mouseY);
        component.draw(new GameCanvas(graphics, Minecraft.getInstance().font),
                pointerShown ? mouseX : OUTSIDE, pointerShown ? mouseY : OUTSIDE);
        if (component.visible() && (isHovered() || isFocused())) {
            tooltip.follow(mouseX, mouseY);
        }
        super.setTooltip(component.visible() ? shownTooltip : null);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        output.add(NarratedElementType.TITLE, component.narration());
    }

    void followComponent() {
        setRectangle(component.width(), component.height(), component.x(), component.y());
    }

    private static void dropScreenFocus() {
        Screen screen = Minecraft.getInstance().screen;
        if (screen != null) {
            screen.clearFocus();
        }
    }

    private void placeComponent() {
        component.setPosition(getX(), getY());
        component.setSize(getWidth(), getHeight());
    }
}

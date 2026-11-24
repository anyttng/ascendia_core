package com.ascendiacore.client.gui.bridge;

import org.jspecify.annotations.Nullable;

import com.ascendiacore.gui.base.component.ComponentHost;
import com.ascendiacore.gui.base.component.GuiComponent;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.FocusNavigationEvent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

public final class ComponentWidget extends AbstractWidget implements ComponentHost {
    static final int OUTSIDE = Integer.MIN_VALUE;

    private final GuiComponent component;
    private final ComponentTooltip tooltip;

    public ComponentWidget(GuiComponent component) {
        super(component.getX(), component.getY(), component.getWidthPixels(), component.getHeightPixels(), component.getNarration());
        this.component = component;
        this.tooltip = new ComponentTooltip(this, component);
        component.attach(this);
    }

    @Override
    public boolean isActive() {
        return component.isInteractive() && component.isVisible() && component.isEnabled();
    }

    @Override
    public boolean isMouseOver(double mouseX, double mouseY) {
        return component.isTakingPointerAt(mouseX, mouseY);
    }

    @Override
    public boolean isHovered() {
        return component.isHovered();
    }

    @Override
    public @Nullable ComponentPath nextFocusPath(FocusNavigationEvent navigationEvent) {
        return component.isTabStop() ? super.nextFocusPath(navigationEvent) : null;
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
        return component.getNarration();
    }

    @Override
    public void setFocused(boolean focused) {
        super.setFocused(focused);
        component.setFocused(focused);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        placeComponent();
        boolean isTaken = component.tryPressPointer(GameInput.toPointerInput(event));
        if (isTaken && !shouldTakeFocusAfterInteraction()) {
            dropScreenFocus();
        }
        return isTaken;
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
        placeComponent();
        return component.tryDragPointer(GameInput.toPointerInput(event));
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        placeComponent();
        boolean isTaken = component.tryReleasePointer(GameInput.toPointerInput(event));
        if (!component.isTakingFocusOnPress()) {
            dropScreenFocus();
        }
        return isTaken;
    }

    @Override
    public boolean mouseScrolled(double x, double y, double scrollX, double scrollY) {
        placeComponent();
        return component.tryScrollWheel(GameInput.toWheelInput(x, y, scrollY));
    }

    @Override
    public boolean shouldTakeFocusAfterInteraction() {
        return component.isTakingFocusOnPress() || component.isFollowingDrag();
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        return component.tryPressKey(GameInput.toKeyInput(event));
    }

    @Override
    public boolean charTyped(CharacterEvent event) {
        return component.tryTypeText(GameInput.toTextInput(event));
    }

    @Override
    protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
        placeComponent();
        boolean isPointerShown = graphics.containsPointInScissor(mouseX, mouseY);
        component.draw(new GameCanvas(graphics, Minecraft.getInstance().font),
                isPointerShown ? mouseX : OUTSIDE, isPointerShown ? mouseY : OUTSIDE);
    }

    @Override
    protected void extractTooltipForNextRenderPass(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        if (component.isVisible()) {
            if (isHovered() || isFocused()) {
                tooltip.follow(mouseX, mouseY);
            }
            super.extractTooltipForNextRenderPass(graphics, mouseX, mouseY);
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        output.add(NarratedElementType.TITLE, component.getNarration());
    }

    void followComponent() {
        setRectangle(component.getWidthPixels(), component.getHeightPixels(), component.getX(), component.getY());
    }

    private static void dropScreenFocus() {
        Screen screen = Minecraft.getInstance().gui.screen();
        if (screen != null) {
            screen.clearFocus();
        }
    }

    private void placeComponent() {
        component.setPosition(getX(), getY());
        component.setSize(getWidth(), getHeight());
    }
}

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

public final class ComponentWidget extends AbstractWidget implements ComponentHost {
    private static final int OUTSIDE = Integer.MIN_VALUE;

    private final GuiComponent component;

    public ComponentWidget(GuiComponent component) {
        super(component.x(), component.y(), component.width(), component.height(), component.narration());
        this.component = component;
        component.attach(this);
    }

    public void tooltip(Component text) {
        setTooltip(Tooltip.create(text));
    }

    @Override
    public boolean isActive() {
        return component.visible() && component.enabled();
    }

    @Override
    public NarratableEntry.NarrationPriority narrationPriority() {
        return component.visible() ? super.narrationPriority() : NarratableEntry.NarrationPriority.NONE;
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
        if (taken && !component.takesFocusOnPress()) {
            dropScreenFocus();
        }
        return taken;
    }

    @Override
    public boolean shouldTakeFocusAfterInteraction() {
        return component.takesFocusOnPress();
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
    }

    @Override
    protected void extractTooltipForNextRenderPass(GuiGraphicsExtractor graphics, int mouseX, int mouseY) {
        if (component.visible()) {
            super.extractTooltipForNextRenderPass(graphics, mouseX, mouseY);
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput output) {
        output.add(NarratedElementType.TITLE, component.narration());
    }

    void followComponent() {
        setRectangle(component.width(), component.height(), component.x(), component.y());
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

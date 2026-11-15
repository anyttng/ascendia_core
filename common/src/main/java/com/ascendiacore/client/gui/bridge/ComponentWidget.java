package com.ascendiacore.client.gui.bridge;

import org.jspecify.annotations.Nullable;

import com.ascendiacore.gui.base.component.ComponentHost;
import com.ascendiacore.gui.base.component.GuiComponent;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarratedElementType;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class ComponentWidget extends AbstractWidget implements ComponentHost {
    private static final int OUTSIDE = Integer.MIN_VALUE;

    private final GuiComponent component;
    private @Nullable Tooltip tooltip;

    public ComponentWidget(GuiComponent component) {
        super(component.x(), component.y(), component.width(), component.height(), component.narration());
        this.component = component;
        component.attach(this);
    }

    public void tooltip(Component text) {
        setTooltip(Tooltip.create(text));
    }

    @Override
    public void setTooltip(@Nullable Tooltip tooltip) {
        this.tooltip = tooltip;
        super.setTooltip(tooltip);
    }

    public boolean shouldTakeFocusAfterInteraction() {
        return component.takesFocusOnPress() || component.followsDrag();
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
        playDownSound(Minecraft.getInstance().getSoundManager());
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
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        placeComponent();
        return component.pointerPressed(GameInput.pointer(mouseX, mouseY, button, GameInput.heldModifiers()));
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        placeComponent();
        return component.pointerDragged(GameInput.pointer(mouseX, mouseY, button, GameInput.heldModifiers()));
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        placeComponent();
        boolean taken = component.pointerReleased(GameInput.pointer(mouseX, mouseY, button, GameInput.heldModifiers()));
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
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return component.keyPressed(GameInput.key(keyCode, modifiers));
    }

    @Override
    public boolean charTyped(char character, int modifiers) {
        return component.textTyped(GameInput.text(character));
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        placeComponent();
        boolean pointerShown = graphics.containsPointInScissor(mouseX, mouseY);
        component.draw(new GameCanvas(graphics, Minecraft.getInstance().font),
                pointerShown ? mouseX : OUTSIDE, pointerShown ? mouseY : OUTSIDE);
        super.setTooltip(component.visible() ? tooltip : null);
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

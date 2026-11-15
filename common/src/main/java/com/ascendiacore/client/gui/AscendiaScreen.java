package com.ascendiacore.client.gui;

import com.ascendiacore.client.gui.bridge.ComponentWidget;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public abstract class AscendiaScreen extends Screen {
    private final int panelWidth;
    private final int panelHeight;

    protected AscendiaScreen(Component title, int panelWidth, int panelHeight) {
        super(title);
        this.panelWidth = panelWidth;
        this.panelHeight = panelHeight;
    }

    protected int panelWidth() {
        return panelWidth;
    }

    protected int panelHeight() {
        return panelHeight;
    }

    protected int panelLeft() {
        return (this.width - panelWidth) / 2;
    }

    protected int panelTop() {
        return (this.height - panelHeight) / 2;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (Minecraft.getInstance().options.keyInventory.matches(keyCode, scanCode)) {
            this.onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean taken = super.mouseClicked(mouseX, mouseY, button);
        if (taken && getFocused() instanceof ComponentWidget widget && !widget.takesFocusOnPress()) {
            clearFocus();
        }
        return taken;
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}

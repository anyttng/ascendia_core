package com.ascendiacore.client.gui;

import com.ascendiacore.client.gui.bridge.ComponentTree;
import com.ascendiacore.client.gui.bridge.ComponentWidget;
import com.ascendiacore.gui.theme.Theme;
import com.ascendiacore.gui.widget.panel.Panel;
import com.ascendiacore.gui.widget.panel.model.FillKind;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ComponentPath;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public abstract class AscendiaScreen extends Screen {
    private final Theme theme;
    private final int panelWidth;
    private final int panelHeight;

    protected AscendiaScreen(Component title, Theme theme, int panelWidth, int panelHeight) {
        super(title);
        this.theme = theme;
        this.panelWidth = panelWidth;
        this.panelHeight = panelHeight;
    }

    protected Theme theme() {
        return theme;
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
    protected final void init() {
        Panel panel = new Panel(theme.panel(), FillKind.PARCHMENT);
        fillPanel(panel);
        ComponentTree tree = new ComponentTree(panel);
        tree.setSize(panelWidth, panelHeight);
        tree.setPosition(panelLeft(), panelTop());
        tree.visitWidgets(this::addRenderableWidget);
    }

    protected abstract void fillPanel(Panel panel);

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!isInputCaptured() && Minecraft.getInstance().options.keyInventory.matches(keyCode, scanCode)) {
            this.onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean taken = super.mouseClicked(mouseX, mouseY, button);
        if (taken && getFocused() instanceof ComponentWidget widget && !widget.shouldTakeFocusAfterInteraction()) {
            clearFocus();
        }
        return taken;
    }

    private boolean isInputCaptured() {
        ComponentPath path = getCurrentFocusPath();
        while (path instanceof ComponentPath.Path nested) {
            path = nested.childPath();
        }
        return path instanceof ComponentPath.Leaf leaf && leaf.component() instanceof EditBox box
                && box.canConsumeInput();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}

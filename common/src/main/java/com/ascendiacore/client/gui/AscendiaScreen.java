package com.ascendiacore.client.gui;

import com.ascendiacore.client.gui.bridge.ComponentTree;
import com.ascendiacore.gui.theme.Theme;
import com.ascendiacore.gui.widget.panel.Panel;
import com.ascendiacore.gui.widget.panel.model.FillKind;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
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
    public boolean keyPressed(KeyEvent event) {
        if (Minecraft.getInstance().options.keyInventory.matches(event)) {
            this.onClose();
            return true;
        }
        return super.keyPressed(event);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}

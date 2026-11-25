package com.ascendiacore.client.gui;

import com.ascendiacore.client.gui.bridge.ComponentTree;
import com.ascendiacore.gui.theme.Theme;
import com.ascendiacore.gui.theme.Themes;
import com.ascendiacore.gui.widget.panel.Panel;
import com.ascendiacore.gui.widget.panel.model.FillKind;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;

public abstract class AscendiaScreen extends Screen {
    private final String themeId;
    private final int panelWidthPixels;
    private final int panelHeightPixels;

    protected AscendiaScreen(Component title, String themeId, int panelWidthPixels, int panelHeightPixels) {
        super(title);
        this.themeId = themeId;
        this.panelWidthPixels = panelWidthPixels;
        this.panelHeightPixels = panelHeightPixels;
    }

    protected Theme getTheme() {
        return Themes.get(themeId);
    }

    protected int getPanelWidthPixels() {
        return panelWidthPixels;
    }

    protected int getPanelHeightPixels() {
        return panelHeightPixels;
    }

    protected int getPanelLeft() {
        return (this.width - panelWidthPixels) / 2;
    }

    protected int getPanelTop() {
        return (this.height - panelHeightPixels) / 2;
    }

    @Override
    protected final void init() {
        Panel panel = new Panel(getTheme().panel(), FillKind.PARCHMENT);
        fillPanel(panel);
        ComponentTree tree = new ComponentTree(panel);
        tree.setSize(panelWidthPixels, panelHeightPixels);
        tree.setPosition(getPanelLeft(), getPanelTop());
        tree.visitWidgets(this::addRenderableWidget);
    }

    protected abstract void fillPanel(Panel panel);

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (!isInputCaptured() && Minecraft.getInstance().options.keyInventory.matches(event)) {
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

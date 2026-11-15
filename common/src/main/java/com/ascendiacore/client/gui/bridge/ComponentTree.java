package com.ascendiacore.client.gui.bridge;

import java.util.List;
import java.util.function.Consumer;

import org.jspecify.annotations.Nullable;

import com.ascendiacore.gui.base.component.GuiComponent;
import com.ascendiacore.gui.base.draw.TextMetrics;
import com.ascendiacore.gui.base.geometry.Size;
import com.ascendiacore.gui.layout.Container;
import com.ascendiacore.gui.widget.textfield.TextField;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;

public final class ComponentTree implements LayoutElement {
    private final Container root;
    private final List<Mount> mounted;
    private final FrameStart frameStart = new FrameStart();
    private int x;
    private int y;
    private @Nullable Size size;

    public ComponentTree(Container root) {
        this.root = root;
        this.mounted = root.mountedComponents().stream().map(ComponentTree::mount).toList();
    }

    public void setSize(int width, int height) {
        this.size = new Size(width, height);
    }

    @Override
    public void setX(int x) {
        this.x = x;
    }

    @Override
    public void setY(int y) {
        this.y = y;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public int getWidth() {
        return size().width();
    }

    @Override
    public int getHeight() {
        return size().height();
    }

    @Override
    public void visitWidgets(Consumer<AbstractWidget> widgetVisitor) {
        widgetVisitor.accept(frameStart);
        mounted.forEach(mount -> widgetVisitor.accept(mount.widget()));
    }

    private static Mount mount(GuiComponent component) {
        if (component instanceof TextField field) {
            TextFieldWidget widget = new TextFieldWidget(field);
            return new Mount(widget, widget::followComponent);
        }
        ComponentWidget widget = new ComponentWidget(component);
        return new Mount(widget, widget::followComponent);
    }

    private void arrange() {
        Size bounds = size();
        root.setPosition(x, y);
        root.setSize(bounds.width(), bounds.height());
        root.layout(metrics());
        mounted.forEach(mount -> mount.follow().run());
    }

    private Size size() {
        return size != null ? size : root.naturalSize(metrics());
    }

    private static TextMetrics metrics() {
        return Minecraft.getInstance().font::width;
    }

    private record Mount(AbstractWidget widget, Runnable follow) {
    }

    private final class FrameStart extends AbstractWidget {
        private FrameStart() {
            super(0, 0, 0, 0, Component.empty());
        }

        @Override
        public boolean isActive() {
            return false;
        }

        @Override
        public NarratableEntry.NarrationPriority narrationPriority() {
            return NarratableEntry.NarrationPriority.NONE;
        }

        @Override
        protected void extractWidgetRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTick) {
            arrange();
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput output) {
        }
    }
}

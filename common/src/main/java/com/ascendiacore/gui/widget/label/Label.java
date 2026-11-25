package com.ascendiacore.gui.widget.label;

import com.ascendiacore.gui.base.component.GuiComponent;
import com.ascendiacore.gui.base.draw.Canvas;
import com.ascendiacore.gui.base.draw.TextFit;
import com.ascendiacore.gui.base.draw.TextMetrics;
import com.ascendiacore.gui.base.geometry.Size;
import com.ascendiacore.gui.base.property.Property;
import com.ascendiacore.gui.widget.label.model.LabelRole;
import com.ascendiacore.gui.widget.label.model.TextAlign;
import com.ascendiacore.gui.widget.panel.model.FillStyle;

import net.minecraft.network.chat.Component;

public final class Label extends GuiComponent {
    private static final int LINE_HEIGHT_PIXELS = 9;

    private final Property<FillStyle> fill;
    private Property<LabelRole> role;
    private Property<Component> text;
    private TextAlign align = TextAlign.LEFT;

    public Label(FillStyle fill, LabelRole role, Component text) {
        this(Property.of(fill), role, text);
    }

    public Label(Property<FillStyle> fill, LabelRole role, Component text) {
        this.fill = fill;
        this.role = Property.of(role);
        this.text = Property.of(text);
    }

    public void setText(Property<Component> text) {
        this.text = text;
    }

    public void setRole(Property<LabelRole> role) {
        this.role = role;
    }

    public void setAlign(TextAlign align) {
        this.align = align;
    }

    @Override
    public boolean isInteractive() {
        return false;
    }

    @Override
    public Component getNarration() {
        return text.get();
    }

    @Override
    public Size naturalSize(TextMetrics metrics) {
        return new Size(metrics.textWidthPixels(text.get()), LINE_HEIGHT_PIXELS);
    }

    @Override
    protected void drawContent(Canvas canvas) {
        Component shown = TextFit.fit(text.get(), getWidthPixels(), canvas);
        FillStyle palette = fill.get();
        int sparePixels = getWidthPixels() - canvas.textWidthPixels(shown);
        int left = switch (align) {
            case LEFT -> getX();
            case CENTER -> getX() + sparePixels / 2;
            case RIGHT -> getX() + sparePixels;
        };
        canvas.text(shown, left, getY() + (getHeightPixels() - LINE_HEIGHT_PIXELS) / 2, getColor(palette), palette.hasInkShadow());
    }

    private int getColor(FillStyle palette) {
        return switch (role.get()) {
            case NORMAL -> palette.ink();
            case MUTED -> palette.mutedInk();
            case ERROR -> palette.errorInk();
        };
    }
}

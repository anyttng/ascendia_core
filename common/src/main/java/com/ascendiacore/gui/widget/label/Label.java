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
    private static final int LINE_HEIGHT = 9;

    private final FillStyle fill;
    private final LabelRole role;
    private Property<Component> text;
    private TextAlign align = TextAlign.LEFT;

    public Label(FillStyle fill, LabelRole role, Component text) {
        this.fill = fill;
        this.role = role;
        this.text = Property.of(text);
    }

    public void setText(Property<Component> text) {
        this.text = text;
    }

    public void setAlign(TextAlign align) {
        this.align = align;
    }

    @Override
    public boolean interactive() {
        return false;
    }

    @Override
    public Component narration() {
        return text.get();
    }

    @Override
    public Size naturalSize(TextMetrics metrics) {
        return new Size(metrics.textWidth(text.get()), LINE_HEIGHT);
    }

    @Override
    protected void drawContent(Canvas canvas) {
        Component shown = TextFit.fit(text.get(), width(), canvas);
        int spare = width() - canvas.textWidth(shown);
        int left = switch (align) {
            case LEFT -> x();
            case CENTER -> x() + spare / 2;
            case RIGHT -> x() + spare;
        };
        int color = role == LabelRole.MUTED ? fill.mutedInk() : fill.ink();
        canvas.text(shown, left, y() + (height() - LINE_HEIGHT) / 2, color, fill.inkShadow());
    }
}

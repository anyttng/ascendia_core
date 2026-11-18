package com.ascendiacore.gui.widget.list;

import java.util.function.BooleanSupplier;

import com.ascendiacore.gui.base.draw.Canvas;
import com.ascendiacore.gui.base.input.KeyInput;
import com.ascendiacore.gui.base.input.PointerInput;
import com.ascendiacore.gui.base.property.Property;
import com.ascendiacore.gui.layout.OpenContainer;
import com.ascendiacore.gui.layout.model.Axis;
import com.ascendiacore.gui.widget.button.model.ButtonStyle;
import com.ascendiacore.gui.widget.list.model.ListStyle;
import com.ascendiacore.gui.widget.list.model.RowStyle;
import com.ascendiacore.gui.widget.panel.model.FillStyle;

import net.minecraft.network.chat.Component;

public final class ListRow extends OpenContainer {
    static final int PADDING = 4;

    private final ListStyle style;
    private final ButtonStyle button;
    private final RowStyle rowStyle;
    private final Component name;
    private final BooleanSupplier selected;
    private final Runnable select;

    ListRow(ListStyle style, ButtonStyle button, RowStyle rowStyle, Component name, BooleanSupplier selected, Runnable select) {
        super(Axis.VERTICAL);
        this.style = style;
        this.button = button;
        this.rowStyle = rowStyle;
        this.name = name;
        this.selected = selected;
        this.select = select;
        setPadding(PADDING);
    }

    public Property<FillStyle> fill() {
        return () -> style.text(rowStyle, selected.getAsBoolean());
    }

    @Override
    public boolean mountsItself() {
        return true;
    }

    @Override
    public boolean interactive() {
        return true;
    }

    @Override
    public Component narration() {
        return name;
    }

    @Override
    protected void drawContent(Canvas canvas) {
        canvas.sprite(sprite(), x(), y(), width(), height());
    }

    @Override
    protected void onPress(PointerInput input) {
        choose();
    }

    @Override
    protected boolean onKey(KeyInput input) {
        if (!input.isSelection()) {
            return false;
        }
        choose();
        return true;
    }

    private void choose() {
        playClick();
        select.run();
    }

    private String sprite() {
        boolean chosen = selected.getAsBoolean();
        boolean highlighted = hovered() || focused();
        return switch (rowStyle) {
            case FRAMED -> chosen ? style.rowSelected() : highlighted ? style.rowHover() : style.row();
            case BUTTON -> chosen
                    ? (highlighted ? button.selectedHover() : button.selected())
                    : (highlighted ? button.hover() : button.normal());
        };
    }
}

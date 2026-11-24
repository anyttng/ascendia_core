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
    static final int PADDING_PIXELS = 4;

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
        setPaddingPixels(PADDING_PIXELS);
    }

    public Property<FillStyle> getFill() {
        return () -> style.getText(rowStyle, selected.getAsBoolean());
    }

    @Override
    public boolean isMountingItself() {
        return true;
    }

    @Override
    public boolean isInteractive() {
        return true;
    }

    @Override
    public Component getNarration() {
        return name;
    }

    @Override
    protected void drawContent(Canvas canvas) {
        canvas.sprite(getSprite(), getX(), getY(), getWidthPixels(), getHeightPixels());
    }

    @Override
    protected void onPress(PointerInput input) {
        choose();
    }

    @Override
    protected boolean tryHandleKey(KeyInput input) {
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

    private String getSprite() {
        boolean isChosen = selected.getAsBoolean();
        boolean isHighlighted = isHovered() || isFocused();
        return switch (rowStyle) {
            case FRAMED -> isChosen ? style.rowSelected() : isHighlighted ? style.rowHover() : style.row();
            case BUTTON -> isChosen
                    ? (isHighlighted ? button.selectedHover() : button.selected())
                    : (isHighlighted ? button.hover() : button.normal());
        };
    }
}

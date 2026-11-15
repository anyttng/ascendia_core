package com.ascendiacore.gui.widget.field;

import com.ascendiacore.gui.base.component.GuiComponent;
import com.ascendiacore.gui.base.property.Property;
import com.ascendiacore.gui.layout.Container;
import com.ascendiacore.gui.layout.model.Axis;
import com.ascendiacore.gui.widget.label.Label;
import com.ascendiacore.gui.widget.label.model.LabelRole;
import com.ascendiacore.gui.widget.panel.model.FillStyle;

import net.minecraft.network.chat.Component;

public final class Field extends Container {
    private static final int GAP = 2;

    private final GuiComponent control;
    private final Label caption;
    private Property<Component> error = Property.of(Component.empty());
    private Property<Component> valueLine = Property.of(Component.empty());
    private Property<Component> hint = Property.of(Component.empty());

    public Field(FillStyle fill, Component caption, GuiComponent control) {
        super(Axis.VERTICAL);
        this.control = control;
        this.caption = captionLabel(fill, caption);
        setGap(GAP);
        add(this.caption);
        add(control);
        add(lineLabel(fill));
    }

    public void setCaption(Property<Component> caption) {
        this.caption.setText(caption);
    }

    public void setError(Property<Component> error) {
        this.error = error;
    }

    public void setValueLine(Property<Component> valueLine) {
        this.valueLine = valueLine;
    }

    public void setHint(Property<Component> hint) {
        this.hint = hint;
    }

    private Label captionLabel(FillStyle fill, Component text) {
        Label label = new Label(fill, LabelRole.NORMAL, text);
        label.setRole(() -> control.enabled() ? LabelRole.NORMAL : LabelRole.MUTED);
        return label;
    }

    private Label lineLabel(FillStyle fill) {
        Label label = new Label(fill, LabelRole.MUTED, Component.empty());
        label.setText(this::line);
        label.setRole(() -> showsError() ? LabelRole.ERROR : LabelRole.MUTED);
        return label;
    }

    private Component line() {
        if (showsError()) {
            return error.get();
        }
        Component value = valueLine.get();
        return present(value) ? value : hint.get();
    }

    private boolean showsError() {
        return control.enabled() && present(error.get());
    }

    private static boolean present(Component text) {
        return !text.getString().isEmpty();
    }
}

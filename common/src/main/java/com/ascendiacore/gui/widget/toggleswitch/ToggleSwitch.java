package com.ascendiacore.gui.widget.toggleswitch;

import java.util.List;
import java.util.function.Consumer;

import com.ascendiacore.gui.base.component.GuiComponent;
import com.ascendiacore.gui.base.component.model.TooltipStyle;
import com.ascendiacore.gui.base.draw.Canvas;
import com.ascendiacore.gui.base.draw.TextMetrics;
import com.ascendiacore.gui.base.geometry.Size;
import com.ascendiacore.gui.base.hint.ValueHint;
import com.ascendiacore.gui.base.input.KeyInput;
import com.ascendiacore.gui.base.input.PointerInput;
import com.ascendiacore.gui.base.property.Property;
import com.ascendiacore.gui.base.property.Value;
import com.ascendiacore.gui.widget.panel.model.FillStyle;
import com.ascendiacore.gui.widget.toggleswitch.model.ToggleSwitchStyle;

import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public final class ToggleSwitch extends GuiComponent {
    private static final int TRACK_WIDTH_PIXELS = 20;
    private static final int TRACK_HEIGHT_PIXELS = 10;
    private static final int LABEL_GAP_PIXELS = 4;
    private static final int LINE_HEIGHT_PIXELS = 9;
    private static final List<Boolean> VALUES = List.of(false, true);
    private static final String HINT_ON = "on";
    private static final String HINT_OFF = "off";

    private final ToggleSwitchStyle style;
    private final FillStyle fill;
    private final Property<Boolean> switchedOn;
    private final Consumer<Boolean> onToggle;
    private Property<Component> label;

    private ToggleSwitch(ToggleSwitchStyle style, FillStyle fill, Component label, Property<Boolean> switchedOn,
            Consumer<Boolean> onToggle) {
        this.style = style;
        this.fill = fill;
        this.label = Property.of(label);
        this.switchedOn = switchedOn;
        this.onToggle = onToggle;
    }

    public static ToggleSwitch of(ToggleSwitchStyle style, FillStyle fill, Component label, Value<Boolean> switchedOn) {
        return new ToggleSwitch(style, fill, label, switchedOn, switchedOn::set);
    }

    public static ToggleSwitch following(ToggleSwitchStyle style, FillStyle fill, Component label,
            Property<Boolean> switchedOn, Consumer<Boolean> onToggle) {
        return new ToggleSwitch(style, fill, label, switchedOn, onToggle);
    }

    public boolean isOn() {
        return switchedOn.get();
    }

    public void setLabel(Property<Component> label) {
        this.label = label;
    }

    public void setHint(TooltipStyle style, String captionKey) {
        setTooltip(style, () -> ValueHint.of(style, captionKey, VALUES, isOn(), ToggleSwitch::toValueName,
                isOn -> ValueHint.text(captionKey, isOn ? HINT_ON : HINT_OFF)).build());
    }

    private static Component toValueName(boolean isOn) {
        return isOn ? CommonComponents.OPTION_ON : CommonComponents.OPTION_OFF;
    }

    @Override
    public Component getNarration() {
        return label.get();
    }

    @Override
    public Size naturalSize(TextMetrics metrics) {
        return new Size(TRACK_WIDTH_PIXELS + LABEL_GAP_PIXELS + metrics.textWidthPixels(label.get()), TRACK_HEIGHT_PIXELS);
    }

    @Override
    protected void drawContent(Canvas canvas) {
        canvas.sprite(getSprite(), getX(), getY() + (getHeightPixels() - TRACK_HEIGHT_PIXELS) / 2, TRACK_WIDTH_PIXELS,
                TRACK_HEIGHT_PIXELS);
        canvas.text(label.get(), getX() + TRACK_WIDTH_PIXELS + LABEL_GAP_PIXELS,
                getY() + (getHeightPixels() - LINE_HEIGHT_PIXELS) / 2 + 1, isEnabled() ? fill.ink() : fill.mutedInk(),
                fill.hasInkShadow());
    }

    @Override
    protected void onPress(PointerInput input) {
        toggle();
    }

    @Override
    protected boolean tryHandleKey(KeyInput input) {
        if (!input.isSelection()) {
            return false;
        }
        toggle();
        return true;
    }

    private void toggle() {
        playClick();
        onToggle.accept(!isOn());
    }

    private String getSprite() {
        boolean isOn = isOn();
        if (!isEnabled()) {
            return isOn ? style.onDisabled() : style.offDisabled();
        }
        if (isHovered() || isFocused()) {
            return isOn ? style.onHover() : style.offHover();
        }
        return isOn ? style.on() : style.off();
    }
}

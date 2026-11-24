package com.ascendiacore.gui.widget.toggleswitch;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.ascendiacore.gui.base.draw.RecordingCanvas;
import com.ascendiacore.gui.base.geometry.Size;
import com.ascendiacore.gui.base.input.KeyInput;
import com.ascendiacore.gui.base.input.PointerInput;
import com.ascendiacore.gui.base.input.model.Key;
import com.ascendiacore.gui.base.input.model.Modifiers;
import com.ascendiacore.gui.base.input.model.PointerButton;
import com.ascendiacore.gui.base.property.Value;
import com.ascendiacore.gui.widget.panel.model.FillStyle;
import com.ascendiacore.gui.widget.toggleswitch.model.ToggleSwitchStyle;

import net.minecraft.network.chat.Component;

class ToggleSwitchTest {
    private static final ToggleSwitchStyle STYLE = new ToggleSwitchStyle("test:off", "test:on", "test:off_hover",
            "test:on_hover", "test:off_disabled", "test:on_disabled");
    private static final int INK = 0xFF111111;
    private static final int MUTED_INK = 0xFF222222;
    private static final FillStyle FILL = new FillStyle(FillStyle.NO_FILL, INK, MUTED_INK, 0xFF333333, true);
    private static final Component LABEL = Component.literal("Guaranteed land");
    private static final int LEFT = 40;
    private static final int TOP = 30;
    private static final int TEXT_WIDTH_PIXELS = 25;
    private static final int TRACK_WIDTH_PIXELS = 20;
    private static final int TRACK_HEIGHT_PIXELS = 10;
    private static final int LABEL_GAP_PIXELS = 4;
    private static final int ROW_HEIGHT_PIXELS = 20;
    private static final int ON_TRACK_X = LEFT + 3;
    private static final int ON_LABEL_X = LEFT + TRACK_WIDTH_PIXELS + LABEL_GAP_PIXELS + 3;
    private static final int INSIDE_Y = TOP + 3;
    private static final int OUTSIDE = -1;

    private final RecordingCanvas canvas = canvas();
    private final Value<Boolean> switchedOn = Value.of(false);

    @Test
    void pressOnTheTrackAndOnTheLabelTogglesTheValue() {
        ToggleSwitch toggle = toggle();
        assertTrue(toggle.tryPressPointer(press(ON_TRACK_X)));
        assertTrue(switchedOn.get());
        assertTrue(toggle.tryPressPointer(press(ON_LABEL_X)));
        assertFalse(switchedOn.get());
    }

    @Test
    void selectionKeysToggleTheValueAndOtherKeysDoNot() {
        ToggleSwitch toggle = toggle();
        assertTrue(toggle.tryPressKey(new KeyInput(Key.ENTER, Modifiers.NONE)));
        assertTrue(switchedOn.get());
        assertTrue(toggle.tryPressKey(new KeyInput(Key.NUMPAD_ENTER, Modifiers.NONE)));
        assertFalse(switchedOn.get());
        assertTrue(toggle.tryPressKey(new KeyInput(Key.SPACE, Modifiers.NONE)));
        assertTrue(switchedOn.get());
        assertFalse(toggle.tryPressKey(new KeyInput(Key.TAB, Modifiers.NONE)));
        assertTrue(switchedOn.get());
    }

    @Test
    void disabledSwitchTakesNeitherAPressNorAKey() {
        ToggleSwitch toggle = toggle();
        toggle.setEnabled(false);
        assertFalse(toggle.tryPressPointer(press(ON_TRACK_X)));
        assertFalse(toggle.tryPressKey(new KeyInput(Key.SPACE, Modifiers.NONE)));
        assertFalse(switchedOn.get());
    }

    @Test
    void spriteFollowsTheValueAtRestUnderThePointerInFocusAndWhenDisabled() {
        ToggleSwitch toggle = toggle();
        assertEquals(STYLE.off(), spriteOf(toggle, OUTSIDE));
        assertEquals(STYLE.offHover(), spriteOf(toggle, ON_TRACK_X));
        switchedOn.set(true);
        assertEquals(STYLE.on(), spriteOf(toggle, OUTSIDE));
        assertEquals(STYLE.onHover(), spriteOf(toggle, ON_TRACK_X));
        toggle.setFocused(true);
        assertEquals(STYLE.onHover(), spriteOf(toggle, OUTSIDE));
        toggle.setEnabled(false);
        assertEquals(STYLE.onDisabled(), spriteOf(toggle, ON_TRACK_X));
        switchedOn.set(false);
        assertEquals(STYLE.offDisabled(), spriteOf(toggle, ON_TRACK_X));
    }

    @Test
    void trackStandsLeftCenteredInTheRowWithTheLabelToItsRight() {
        toggle().draw(canvas, OUTSIDE, OUTSIDE);
        assertEquals(List.of(new RecordingCanvas.Sprite(STYLE.off(), LEFT, TOP + (ROW_HEIGHT_PIXELS - TRACK_HEIGHT_PIXELS) / 2,
                TRACK_WIDTH_PIXELS, TRACK_HEIGHT_PIXELS)), canvas.sprites());
        assertEquals(LEFT + TRACK_WIDTH_PIXELS + LABEL_GAP_PIXELS, canvas.texts().getFirst().x());
        assertEquals(LABEL, canvas.texts().getFirst().text());
    }

    @Test
    void labelTakesTheInkOfItsFillAndTheMutedInkWhenDisabled() {
        ToggleSwitch toggle = toggle();
        toggle.draw(canvas, OUTSIDE, OUTSIDE);
        assertEquals(INK, canvas.texts().getFirst().color());
        assertTrue(canvas.texts().getFirst().hasShadow());
        toggle.setEnabled(false);
        toggle.draw(canvas, OUTSIDE, OUTSIDE);
        assertEquals(MUTED_INK, canvas.texts().getLast().color());
    }

    @Test
    void switchFollowingARuleShowsTheRuleAndHandsAPressToItsOwner() {
        List<Boolean> asked = new ArrayList<>();
        ToggleSwitch toggle = place(ToggleSwitch.following(STYLE, FILL, LABEL, switchedOn, asked::add));
        assertTrue(toggle.tryPressPointer(press(ON_TRACK_X)));
        assertEquals(List.of(true), asked);
        assertFalse(toggle.isOn());
        switchedOn.set(true);
        assertTrue(toggle.isOn());
        assertEquals(STYLE.on(), spriteOf(toggle, OUTSIDE));
    }

    @Test
    void naturalSizeIsTheTrackTheGapAndTheLabel() {
        assertEquals(new Size(TRACK_WIDTH_PIXELS + LABEL_GAP_PIXELS + TEXT_WIDTH_PIXELS, TRACK_HEIGHT_PIXELS),
                toggle().naturalSize(canvas));
    }

    private String spriteOf(ToggleSwitch toggle, int pointerX) {
        toggle.draw(canvas, pointerX, pointerX == OUTSIDE ? OUTSIDE : INSIDE_Y);
        return canvas.sprites().getLast().sprite();
    }

    private ToggleSwitch toggle() {
        return place(ToggleSwitch.of(STYLE, FILL, LABEL, switchedOn));
    }

    private static ToggleSwitch place(ToggleSwitch toggle) {
        toggle.setPosition(LEFT, TOP);
        toggle.setSize(TRACK_WIDTH_PIXELS + LABEL_GAP_PIXELS + TEXT_WIDTH_PIXELS, ROW_HEIGHT_PIXELS);
        return toggle;
    }

    private static PointerInput press(int x) {
        return new PointerInput(x, INSIDE_Y, PointerButton.LEFT, Modifiers.NONE);
    }

    private static RecordingCanvas canvas() {
        RecordingCanvas canvas = new RecordingCanvas();
        canvas.setTextWidth(TEXT_WIDTH_PIXELS);
        return canvas;
    }
}

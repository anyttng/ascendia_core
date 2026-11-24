package com.ascendiacore.gui.base.component;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.ascendiacore.gui.base.component.model.AlertColors;
import com.ascendiacore.gui.base.component.model.LevelColors;
import com.ascendiacore.gui.base.component.model.TooltipStyle;
import com.ascendiacore.gui.base.draw.Canvas;
import com.ascendiacore.gui.base.input.KeyInput;
import com.ascendiacore.gui.base.input.PointerInput;
import com.ascendiacore.gui.base.input.TextInput;
import com.ascendiacore.gui.base.input.WheelInput;
import com.ascendiacore.gui.base.input.model.Key;
import com.ascendiacore.gui.base.input.model.Modifiers;
import com.ascendiacore.gui.base.input.model.PointerButton;
import com.ascendiacore.gui.base.property.Value;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

class GuiComponentTest {
    private static final int LEFT = 10;
    private static final int TOP = 20;
    private static final int WIDTH_PIXELS = 30;
    private static final int HEIGHT_PIXELS = 12;
    private static final int RIGHT = LEFT + WIDTH_PIXELS;
    private static final int BOTTOM = TOP + HEIGHT_PIXELS;
    private static final Canvas CANVAS = new BlankCanvas();
    private static final TooltipStyle TOOLTIP_STYLE = new TooltipStyle("example:sprites", 0xFFFFFFFF, 0xFFFFFF55,
            0xFFAAAAAA, new AlertColors(0xFFFFAA00, 0xFFFF5555, 0xFFA0A0FF), new LevelColors(0xFF55FF55, 0xFFFFFF55, 0xFFFF5555),
            0xFF55FF55, 0xFF5A5A5A);

    @Test
    void hoveredWhenThePointerIsInside() {
        Probe probe = probe();
        probe.draw(CANVAS, LEFT + 5, TOP + 5);
        assertTrue(probe.isHovered());
    }

    @Test
    void hoverTakesTheLeftAndTopEdgesAndLeavesTheRightAndBottomOnes() {
        Probe probe = probe();
        probe.draw(CANVAS, LEFT, TOP);
        assertTrue(probe.isHovered());
        probe.draw(CANVAS, RIGHT - 1, BOTTOM - 1);
        assertTrue(probe.isHovered());
        probe.draw(CANVAS, RIGHT, TOP);
        assertFalse(probe.isHovered());
        probe.draw(CANVAS, LEFT, BOTTOM);
        assertFalse(probe.isHovered());
    }

    @Test
    void hoverEndsWhenThePointerLeaves() {
        Probe probe = probe();
        probe.draw(CANVAS, LEFT + 5, TOP + 5);
        probe.draw(CANVAS, LEFT - 1, TOP + 5);
        assertFalse(probe.isHovered());
    }

    @Test
    void hiddenComponentIsNeitherDrawnNorHovered() {
        Probe probe = probe();
        probe.draw(CANVAS, LEFT + 5, TOP + 5);
        probe.setVisible(false);
        assertFalse(probe.isHovered());
        probe.draw(CANVAS, LEFT + 5, TOP + 5);
        assertFalse(probe.isHovered());
        assertEquals(1, probe.draws);
    }

    @Test
    void disabledComponentIsStillHovered() {
        Probe probe = probe();
        probe.setEnabled(false);
        probe.draw(CANVAS, LEFT + 5, TOP + 5);
        assertTrue(probe.isHovered());
    }

    @Test
    void leftPressInsideIsTaken() {
        Probe probe = probe();
        assertTrue(probe.tryPressPointer(press(LEFT + 5, TOP + 5, PointerButton.LEFT)));
        assertEquals(1, probe.presses);
    }

    @Test
    void pressOutsideIsLeft() {
        Probe probe = probe();
        assertFalse(probe.tryPressPointer(press(RIGHT, TOP + 5, PointerButton.LEFT)));
        assertEquals(0, probe.presses);
    }

    @Test
    void otherButtonsAreLeft() {
        Probe probe = probe();
        assertFalse(probe.tryPressPointer(press(LEFT + 5, TOP + 5, PointerButton.RIGHT)));
        assertFalse(probe.tryPressPointer(press(LEFT + 5, TOP + 5, PointerButton.MIDDLE)));
        assertEquals(0, probe.presses);
    }

    @Test
    void disabledComponentTakesNoInput() {
        Probe probe = probe();
        probe.setEnabled(false);
        assertFalse(probe.tryPressPointer(press(LEFT + 5, TOP + 5, PointerButton.LEFT)));
        assertFalse(probe.tryPressKey(new KeyInput(Key.ENTER, Modifiers.NONE)));
        assertFalse(probe.tryTypeText(new TextInput('a')));
        assertEquals(0, probe.presses + probe.keys + probe.texts);
    }

    @Test
    void hiddenComponentTakesNoInput() {
        Probe probe = probe();
        probe.setVisible(false);
        assertFalse(probe.tryPressPointer(press(LEFT + 5, TOP + 5, PointerButton.LEFT)));
        assertFalse(probe.tryPressKey(new KeyInput(Key.ENTER, Modifiers.NONE)));
        assertFalse(probe.tryTypeText(new TextInput('a')));
        assertEquals(0, probe.presses + probe.keys + probe.texts);
    }

    @Test
    void noTooltipByDefault() {
        Probe probe = probe();
        assertEquals("", probe.getTooltip().getString());
        assertNull(probe.getTooltipStyle());
    }

    @Test
    void fixedTooltipIsReadBackWithItsStyle() {
        Probe probe = probe();
        probe.setTooltip(TOOLTIP_STYLE, Component.literal("Counts a press"));
        assertEquals("Counts a press", probe.getTooltip().getString());
        assertEquals("Counts a press", probe.getTooltipAt(LEFT + 5, TOP + 5).getString());
        assertEquals(TOOLTIP_STYLE, probe.getTooltipStyle());
    }

    @Test
    void tooltipRuleIsReadOnEveryCall() {
        Probe probe = probe();
        Value<Component> text = Value.of(Component.literal("On"));
        probe.setTooltip(TOOLTIP_STYLE, text);
        assertEquals("On", probe.getTooltip().getString());
        text.set(Component.literal("Off"));
        assertEquals("Off", probe.getTooltip().getString());
    }

    @Test
    void enabledComponentReportsWhatItsHandlersTook() {
        Probe probe = probe();
        assertTrue(probe.tryPressKey(new KeyInput(Key.ENTER, Modifiers.NONE)));
        assertTrue(probe.tryTypeText(new TextInput('a')));
        assertEquals(1, probe.keys);
        assertEquals(1, probe.texts);
    }

    @Test
    void aPressDoesNotFocusByDefault() {
        assertFalse(probe().isTakingFocusOnPress());
    }

    @Test
    void aDragIsNotFollowedByDefault() {
        assertFalse(probe().isFollowingDrag());
    }

    @Test
    void leftDragAndWheelInsideReachTheirHandlers() {
        Probe probe = probe();
        assertTrue(probe.tryDragPointer(press(RIGHT + 50, TOP + 5, PointerButton.LEFT)));
        assertTrue(probe.tryScrollWheel(new WheelInput(LEFT + 5, TOP + 5, 1)));
        assertEquals(1, probe.drags);
        assertEquals(1, probe.wheels);
    }

    @Test
    void otherButtonsDragNothingAndReleaseNothing() {
        Probe probe = probe();
        assertFalse(probe.tryDragPointer(press(LEFT + 5, TOP + 5, PointerButton.RIGHT)));
        assertFalse(probe.tryReleasePointer(press(LEFT + 5, TOP + 5, PointerButton.RIGHT)));
        assertEquals(0, probe.drags + probe.releases);
    }

    @Test
    void wheelOutsideIsLeft() {
        Probe probe = probe();
        assertFalse(probe.tryScrollWheel(new WheelInput(RIGHT, TOP + 5, 1)));
        assertEquals(0, probe.wheels);
    }

    @Test
    void hiddenOrDisabledComponentTakesNoDragAndNoWheel() {
        Probe hidden = probe();
        hidden.setVisible(false);
        Probe disabled = probe();
        disabled.setEnabled(false);
        for (Probe probe : new Probe[] {hidden, disabled}) {
            assertFalse(probe.tryDragPointer(press(LEFT + 5, TOP + 5, PointerButton.LEFT)));
            assertFalse(probe.tryScrollWheel(new WheelInput(LEFT + 5, TOP + 5, 1)));
            assertEquals(0, probe.drags + probe.wheels);
        }
    }

    @Test
    void releaseReachesAComponentHiddenOrDisabledMidDrag() {
        Probe hidden = probe();
        hidden.setVisible(false);
        Probe disabled = probe();
        disabled.setEnabled(false);
        assertTrue(hidden.tryReleasePointer(press(LEFT + 5, TOP + 5, PointerButton.LEFT)));
        assertTrue(disabled.tryReleasePointer(press(RIGHT + 50, TOP + 5, PointerButton.LEFT)));
        assertEquals(1, hidden.releases);
        assertEquals(1, disabled.releases);
    }

    @Test
    void theFramesPointerIsKeptForTheContent() {
        Probe probe = probe();
        probe.draw(CANVAS, LEFT + 7, TOP + 3);
        assertEquals(LEFT + 7, probe.getPointerX());
        assertEquals(TOP + 3, probe.getPointerY());
    }

    @Test
    void visibilityFollowsItsRule() {
        Probe probe = probe();
        Value<Boolean> shown = Value.of(true);
        probe.setVisible(shown);
        probe.draw(CANVAS, LEFT + 5, TOP + 5);
        shown.set(false);
        assertFalse(probe.isVisible());
        assertFalse(probe.isHovered());
        assertFalse(probe.tryPressPointer(press(LEFT + 5, TOP + 5, PointerButton.LEFT)));
        shown.set(true);
        assertTrue(probe.isVisible());
        assertTrue(probe.tryPressPointer(press(LEFT + 5, TOP + 5, PointerButton.LEFT)));
    }

    @Test
    void enabledFollowsItsRule() {
        Probe probe = probe();
        Value<Integer> count = Value.of(0);
        probe.setEnabled(() -> count.get() > 0);
        assertFalse(probe.isEnabled());
        count.set(1);
        assertTrue(probe.isEnabled());
    }

    private static Probe probe() {
        Probe probe = new Probe();
        probe.setPosition(LEFT, TOP);
        probe.setSize(WIDTH_PIXELS, HEIGHT_PIXELS);
        return probe;
    }

    private static PointerInput press(double x, double y, PointerButton button) {
        return new PointerInput(x, y, button, Modifiers.NONE);
    }

    private static final class Probe extends GuiComponent {
        private int draws;
        private int presses;
        private int keys;
        private int texts;
        private int drags;
        private int releases;
        private int wheels;

        @Override
        protected void drawContent(Canvas canvas) {
            draws++;
        }

        @Override
        protected void onPress(PointerInput input) {
            presses++;
        }

        @Override
        protected boolean tryHandleDrag(PointerInput input) {
            drags++;
            return true;
        }

        @Override
        protected void onRelease(PointerInput input) {
            releases++;
        }

        @Override
        protected boolean tryHandleWheel(WheelInput input) {
            wheels++;
            return true;
        }

        @Override
        protected boolean tryHandleKey(KeyInput input) {
            keys++;
            return true;
        }

        @Override
        protected boolean tryHandleText(TextInput input) {
            texts++;
            return true;
        }
    }

    private static final class BlankCanvas implements Canvas {
        @Override
        public void sprite(String sprite, int x, int y, int widthPixels, int heightPixels) {
        }

        @Override
        public void sprite(String sprite, int x, int y, int widthPixels, int heightPixels, int tint) {
        }

        @Override
        public void fill(int x, int y, int widthPixels, int heightPixels, int color) {
        }

        @Override
        public void text(Component text, int x, int y, int color, boolean hasShadow) {
        }

        @Override
        public int textWidthPixels(Component text) {
            return 0;
        }

        @Override
        public void item(ItemStack stack, int x, int y) {
        }

        @Override
        public void pushClip(int x, int y, int widthPixels, int heightPixels) {
        }

        @Override
        public void popClip() {
        }

        @Override
        public long getFrameTimeMillis() {
            return 0;
        }
    }
}

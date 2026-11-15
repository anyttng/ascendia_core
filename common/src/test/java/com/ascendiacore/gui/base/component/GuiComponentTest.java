package com.ascendiacore.gui.base.component;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

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
    private static final int WIDTH = 30;
    private static final int HEIGHT = 12;
    private static final int RIGHT = LEFT + WIDTH;
    private static final int BOTTOM = TOP + HEIGHT;
    private static final Canvas CANVAS = new BlankCanvas();

    @Test
    void hoveredWhenThePointerIsInside() {
        Probe probe = probe();
        probe.draw(CANVAS, LEFT + 5, TOP + 5);
        assertTrue(probe.hovered());
    }

    @Test
    void hoverTakesTheLeftAndTopEdgesAndLeavesTheRightAndBottomOnes() {
        Probe probe = probe();
        probe.draw(CANVAS, LEFT, TOP);
        assertTrue(probe.hovered());
        probe.draw(CANVAS, RIGHT - 1, BOTTOM - 1);
        assertTrue(probe.hovered());
        probe.draw(CANVAS, RIGHT, TOP);
        assertFalse(probe.hovered());
        probe.draw(CANVAS, LEFT, BOTTOM);
        assertFalse(probe.hovered());
    }

    @Test
    void hoverEndsWhenThePointerLeaves() {
        Probe probe = probe();
        probe.draw(CANVAS, LEFT + 5, TOP + 5);
        probe.draw(CANVAS, LEFT - 1, TOP + 5);
        assertFalse(probe.hovered());
    }

    @Test
    void hiddenComponentIsNeitherDrawnNorHovered() {
        Probe probe = probe();
        probe.draw(CANVAS, LEFT + 5, TOP + 5);
        probe.setVisible(false);
        assertFalse(probe.hovered());
        probe.draw(CANVAS, LEFT + 5, TOP + 5);
        assertFalse(probe.hovered());
        assertEquals(1, probe.draws);
    }

    @Test
    void disabledComponentIsStillHovered() {
        Probe probe = probe();
        probe.setEnabled(false);
        probe.draw(CANVAS, LEFT + 5, TOP + 5);
        assertTrue(probe.hovered());
    }

    @Test
    void leftPressInsideIsTaken() {
        Probe probe = probe();
        assertTrue(probe.pointerPressed(press(LEFT + 5, TOP + 5, PointerButton.LEFT)));
        assertEquals(1, probe.presses);
    }

    @Test
    void pressOutsideIsLeft() {
        Probe probe = probe();
        assertFalse(probe.pointerPressed(press(RIGHT, TOP + 5, PointerButton.LEFT)));
        assertEquals(0, probe.presses);
    }

    @Test
    void otherButtonsAreLeft() {
        Probe probe = probe();
        assertFalse(probe.pointerPressed(press(LEFT + 5, TOP + 5, PointerButton.RIGHT)));
        assertFalse(probe.pointerPressed(press(LEFT + 5, TOP + 5, PointerButton.MIDDLE)));
        assertEquals(0, probe.presses);
    }

    @Test
    void disabledComponentTakesNoInput() {
        Probe probe = probe();
        probe.setEnabled(false);
        assertFalse(probe.pointerPressed(press(LEFT + 5, TOP + 5, PointerButton.LEFT)));
        assertFalse(probe.keyPressed(new KeyInput(Key.ENTER, Modifiers.NONE)));
        assertFalse(probe.textTyped(new TextInput('a')));
        assertEquals(0, probe.presses + probe.keys + probe.texts);
    }

    @Test
    void hiddenComponentTakesNoInput() {
        Probe probe = probe();
        probe.setVisible(false);
        assertFalse(probe.pointerPressed(press(LEFT + 5, TOP + 5, PointerButton.LEFT)));
        assertFalse(probe.keyPressed(new KeyInput(Key.ENTER, Modifiers.NONE)));
        assertFalse(probe.textTyped(new TextInput('a')));
        assertEquals(0, probe.presses + probe.keys + probe.texts);
    }

    @Test
    void enabledComponentReportsWhatItsHandlersTook() {
        Probe probe = probe();
        assertTrue(probe.keyPressed(new KeyInput(Key.ENTER, Modifiers.NONE)));
        assertTrue(probe.textTyped(new TextInput('a')));
        assertEquals(1, probe.keys);
        assertEquals(1, probe.texts);
    }

    @Test
    void aPressDoesNotFocusByDefault() {
        assertFalse(probe().takesFocusOnPress());
    }

    @Test
    void aDragIsNotFollowedByDefault() {
        assertFalse(probe().followsDrag());
    }

    @Test
    void leftDragAndWheelInsideReachTheirHandlers() {
        Probe probe = probe();
        assertTrue(probe.pointerDragged(press(RIGHT + 50, TOP + 5, PointerButton.LEFT)));
        assertTrue(probe.wheelScrolled(new WheelInput(LEFT + 5, TOP + 5, 1)));
        assertEquals(1, probe.drags);
        assertEquals(1, probe.wheels);
    }

    @Test
    void otherButtonsDragNothingAndReleaseNothing() {
        Probe probe = probe();
        assertFalse(probe.pointerDragged(press(LEFT + 5, TOP + 5, PointerButton.RIGHT)));
        assertFalse(probe.pointerReleased(press(LEFT + 5, TOP + 5, PointerButton.RIGHT)));
        assertEquals(0, probe.drags + probe.releases);
    }

    @Test
    void wheelOutsideIsLeft() {
        Probe probe = probe();
        assertFalse(probe.wheelScrolled(new WheelInput(RIGHT, TOP + 5, 1)));
        assertEquals(0, probe.wheels);
    }

    @Test
    void hiddenOrDisabledComponentTakesNoDragAndNoWheel() {
        Probe hidden = probe();
        hidden.setVisible(false);
        Probe disabled = probe();
        disabled.setEnabled(false);
        for (Probe probe : new Probe[] {hidden, disabled}) {
            assertFalse(probe.pointerDragged(press(LEFT + 5, TOP + 5, PointerButton.LEFT)));
            assertFalse(probe.wheelScrolled(new WheelInput(LEFT + 5, TOP + 5, 1)));
            assertEquals(0, probe.drags + probe.wheels);
        }
    }

    @Test
    void releaseReachesAComponentHiddenOrDisabledMidDrag() {
        Probe hidden = probe();
        hidden.setVisible(false);
        Probe disabled = probe();
        disabled.setEnabled(false);
        assertTrue(hidden.pointerReleased(press(LEFT + 5, TOP + 5, PointerButton.LEFT)));
        assertTrue(disabled.pointerReleased(press(RIGHT + 50, TOP + 5, PointerButton.LEFT)));
        assertEquals(1, hidden.releases);
        assertEquals(1, disabled.releases);
    }

    @Test
    void theFramesPointerIsKeptForTheContent() {
        Probe probe = probe();
        probe.draw(CANVAS, LEFT + 7, TOP + 3);
        assertEquals(LEFT + 7, probe.pointerX());
        assertEquals(TOP + 3, probe.pointerY());
    }

    @Test
    void visibilityFollowsItsRule() {
        Probe probe = probe();
        Value<Boolean> shown = Value.of(true);
        probe.setVisible(shown);
        probe.draw(CANVAS, LEFT + 5, TOP + 5);
        shown.set(false);
        assertFalse(probe.visible());
        assertFalse(probe.hovered());
        assertFalse(probe.pointerPressed(press(LEFT + 5, TOP + 5, PointerButton.LEFT)));
        shown.set(true);
        assertTrue(probe.visible());
        assertTrue(probe.pointerPressed(press(LEFT + 5, TOP + 5, PointerButton.LEFT)));
    }

    @Test
    void enabledFollowsItsRule() {
        Probe probe = probe();
        Value<Integer> count = Value.of(0);
        probe.setEnabled(() -> count.get() > 0);
        assertFalse(probe.enabled());
        count.set(1);
        assertTrue(probe.enabled());
    }

    private static Probe probe() {
        Probe probe = new Probe();
        probe.setPosition(LEFT, TOP);
        probe.setSize(WIDTH, HEIGHT);
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
        protected boolean onDrag(PointerInput input) {
            drags++;
            return true;
        }

        @Override
        protected void onRelease(PointerInput input) {
            releases++;
        }

        @Override
        protected boolean onWheel(WheelInput input) {
            wheels++;
            return true;
        }

        @Override
        protected boolean onKey(KeyInput input) {
            keys++;
            return true;
        }

        @Override
        protected boolean onText(TextInput input) {
            texts++;
            return true;
        }
    }

    private static final class BlankCanvas implements Canvas {
        @Override
        public void sprite(String sprite, int x, int y, int width, int height) {
        }

        @Override
        public void sprite(String sprite, int x, int y, int width, int height, int tint) {
        }

        @Override
        public void fill(int x, int y, int width, int height, int color) {
        }

        @Override
        public void text(Component text, int x, int y, int color, boolean shadow) {
        }

        @Override
        public int textWidth(Component text) {
            return 0;
        }

        @Override
        public void item(ItemStack stack, int x, int y) {
        }

        @Override
        public void pushClip(int x, int y, int width, int height) {
        }

        @Override
        public void popClip() {
        }

        @Override
        public long frameTimeMillis() {
            return 0;
        }
    }
}

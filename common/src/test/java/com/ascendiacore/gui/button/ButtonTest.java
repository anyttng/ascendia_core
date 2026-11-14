package com.ascendiacore.gui.button;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.ascendiacore.gui.component.ComponentHost;
import com.ascendiacore.gui.draw.Canvas;
import com.ascendiacore.gui.icon.Icon;
import com.ascendiacore.gui.input.Key;
import com.ascendiacore.gui.input.KeyInput;
import com.ascendiacore.gui.input.Modifiers;
import com.ascendiacore.gui.input.PointerButton;
import com.ascendiacore.gui.input.PointerInput;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

class ButtonTest {
    private static final ButtonSprites SPRITES = new ButtonSprites("test:normal", "test:hover", "test:disabled");
    private static final String ICON_SPRITE = "test:icon";
    private static final Component LABEL = Component.literal("Apply");
    private static final int LEFT = 100;
    private static final int TOP = 50;
    private static final int WIDTH = 120;
    private static final int HEIGHT = 20;
    private static final int TEXT_WIDTH = 30;
    private static final int LINE_HEIGHT = 9;
    private static final int ICON_GAP = 4;
    private static final int INSIDE_X = LEFT + 5;
    private static final int INSIDE_Y = TOP + 5;
    private static final int OUTSIDE_X = LEFT - 5;
    private static final int ENABLED_TEXT = 0xFFFFFFFF;
    private static final int DISABLED_TEXT = 0xFFA0A0A0;

    private final Recorder recorder = new Recorder();
    private int runs;

    @Test
    void leftPressRunsTheActionAndAsksForTheClick() {
        Button button = textButton();
        assertTrue(button.pointerPressed(new PointerInput(INSIDE_X, INSIDE_Y, PointerButton.LEFT, Modifiers.NONE)));
        assertEquals(1, runs);
        assertEquals(1, recorder.clicks);
    }

    @Test
    void enterNumpadEnterAndSpaceRunTheAction() {
        Button button = textButton();
        assertTrue(button.keyPressed(new KeyInput(Key.ENTER, Modifiers.NONE)));
        assertTrue(button.keyPressed(new KeyInput(Key.NUMPAD_ENTER, Modifiers.NONE)));
        assertTrue(button.keyPressed(new KeyInput(Key.SPACE, Modifiers.NONE)));
        assertEquals(3, runs);
        assertEquals(3, recorder.clicks);
    }

    @Test
    void otherKeysAreLeft() {
        Button button = textButton();
        assertFalse(button.keyPressed(new KeyInput(Key.TAB, Modifiers.NONE)));
        assertFalse(button.keyPressed(new KeyInput(Key.OTHER, Modifiers.NONE)));
        assertEquals(0, runs);
        assertEquals(0, recorder.clicks);
    }

    @Test
    void disabledButtonNeitherRunsNorClicks() {
        Button button = textButton();
        button.setEnabled(false);
        assertFalse(button.pointerPressed(new PointerInput(INSIDE_X, INSIDE_Y, PointerButton.LEFT, Modifiers.NONE)));
        assertFalse(button.keyPressed(new KeyInput(Key.ENTER, Modifiers.NONE)));
        assertEquals(0, runs);
        assertEquals(0, recorder.clicks);
    }

    @Test
    void spriteFollowsTheState() {
        Button button = textButton();
        button.draw(recorder, OUTSIDE_X, INSIDE_Y);
        button.draw(recorder, INSIDE_X, INSIDE_Y);
        button.draw(recorder, OUTSIDE_X, INSIDE_Y);
        button.setFocused(true);
        button.draw(recorder, OUTSIDE_X, INSIDE_Y);
        button.setEnabled(false);
        button.draw(recorder, INSIDE_X, INSIDE_Y);
        assertEquals(List.of(SPRITES.normal(), SPRITES.hover(), SPRITES.normal(), SPRITES.hover(), SPRITES.disabled()),
                recorder.sprites);
    }

    @Test
    void spriteCoversTheWholeButton() {
        textButton().draw(recorder, OUTSIDE_X, INSIDE_Y);
        assertEquals(List.of(new Rect(LEFT, TOP, WIDTH, HEIGHT)), recorder.spriteRects);
    }

    @Test
    void textIsCenteredAndWhite() {
        textButton().draw(recorder, OUTSIDE_X, INSIDE_Y);
        assertEquals(List.of(new Text(LEFT + (WIDTH - TEXT_WIDTH) / 2, textTop(), ENABLED_TEXT)), recorder.texts);
    }

    @Test
    void disabledTextIsGray() {
        Button button = textButton();
        button.setEnabled(false);
        button.draw(recorder, OUTSIDE_X, INSIDE_Y);
        assertEquals(DISABLED_TEXT, recorder.texts.getFirst().color());
    }

    @Test
    void iconAloneIsCenteredAndItsNameIsNotDrawn() {
        Button button = place(Button.icon(SPRITES, Icon.sprite(ICON_SPRITE), LABEL, () -> runs++));
        button.draw(recorder, OUTSIDE_X, INSIDE_Y);
        assertEquals(new Rect(LEFT + (WIDTH - Icon.SIZE) / 2, iconTop(), Icon.SIZE, Icon.SIZE), recorder.spriteRects.getLast());
        assertTrue(recorder.texts.isEmpty());
        assertEquals(LABEL, button.narration());
    }

    @Test
    void iconSitsLeftOfTheTextAndThePairIsCentered() {
        Button button = place(Button.iconAndText(SPRITES, Icon.sprite(ICON_SPRITE), LABEL, () -> runs++));
        button.draw(recorder, OUTSIDE_X, INSIDE_Y);
        int contentLeft = LEFT + (WIDTH - Icon.SIZE - ICON_GAP - TEXT_WIDTH) / 2;
        assertEquals(new Rect(contentLeft, iconTop(), Icon.SIZE, Icon.SIZE), recorder.spriteRects.getLast());
        assertEquals(List.of(new Text(contentLeft + Icon.SIZE + ICON_GAP, textTop(), ENABLED_TEXT)), recorder.texts);
    }

    private Button textButton() {
        return place(Button.text(SPRITES, LABEL, () -> runs++));
    }

    private Button place(Button button) {
        button.setPosition(LEFT, TOP);
        button.setSize(WIDTH, HEIGHT);
        button.attach(recorder);
        return button;
    }

    private static int textTop() {
        return TOP + (HEIGHT - LINE_HEIGHT) / 2 + 1;
    }

    private static int iconTop() {
        return TOP + (HEIGHT - Icon.SIZE) / 2;
    }

    private record Rect(int x, int y, int width, int height) {
    }

    private record Text(int x, int y, int color) {
    }

    private static final class Recorder implements Canvas, ComponentHost {
        private final List<String> sprites = new ArrayList<>();
        private final List<Rect> spriteRects = new ArrayList<>();
        private final List<Text> texts = new ArrayList<>();
        private int clicks;

        @Override
        public void sprite(String sprite, int x, int y, int width, int height) {
            if (!sprite.equals(ICON_SPRITE)) {
                sprites.add(sprite);
            }
            spriteRects.add(new Rect(x, y, width, height));
        }

        @Override
        public void sprite(String sprite, int x, int y, int width, int height, int tint) {
            sprite(sprite, x, y, width, height);
        }

        @Override
        public void fill(int x, int y, int width, int height, int color) {
        }

        @Override
        public void text(Component text, int x, int y, int color, boolean shadow) {
            texts.add(new Text(x, y, color));
        }

        @Override
        public int textWidth(Component text) {
            return TEXT_WIDTH;
        }

        @Override
        public void item(ItemStack stack, int x, int y) {
        }

        @Override
        public void stateChanged() {
        }

        @Override
        public void clickSound() {
            clicks++;
        }
    }
}

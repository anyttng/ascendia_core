package com.ascendiacore.gui.widget.button;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.ascendiacore.gui.base.component.ComponentHost;
import com.ascendiacore.gui.base.draw.Canvas;
import com.ascendiacore.gui.base.draw.Icon;
import com.ascendiacore.gui.base.geometry.Size;
import com.ascendiacore.gui.base.input.KeyInput;
import com.ascendiacore.gui.base.input.PointerInput;
import com.ascendiacore.gui.base.input.model.Key;
import com.ascendiacore.gui.base.input.model.Modifiers;
import com.ascendiacore.gui.base.input.model.PointerButton;
import com.ascendiacore.gui.base.property.Value;
import com.ascendiacore.gui.widget.button.model.ButtonStyle;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

class ButtonTest {
    private static final String ICON_SPRITE = "test:icon";
    private static final Component LABEL = Component.literal("Apply");
    private static final Component OTHER_LABEL = Component.literal("Done");
    private static final int LEFT = 100;
    private static final int TOP = 50;
    private static final int WIDTH_PIXELS = 120;
    private static final int HEIGHT_PIXELS = 20;
    private static final int TEXT_WIDTH_PIXELS = 30;
    private static final int NARROW_WIDTH_PIXELS = 24;
    private static final int LABEL_MARGIN_PIXELS = 5;
    private static final long SCROLLED_TO_THE_END_MILLIS = 2400;
    private static final int LINE_HEIGHT_PIXELS = 9;
    private static final int ICON_GAP_PIXELS = 4;
    private static final int SIDE_PADDING_PIXELS = 8;
    private static final int INSIDE_X = LEFT + 5;
    private static final int INSIDE_Y = TOP + 5;
    private static final int OUTSIDE_X = LEFT - 5;
    private static final int ENABLED_TEXT = 0xFF112233;
    private static final int DISABLED_TEXT = 0xFF445566;
    private static final int SELECTED_TEXT = 0xFF778899;
    private static final ButtonStyle STYLE = new ButtonStyle("test:normal", "test:hover", "test:disabled", "test:selected",
            "test:selected_hover", ENABLED_TEXT, DISABLED_TEXT, SELECTED_TEXT, false);

    private final Recorder recorder = new Recorder();
    private int runs;

    @Test
    void leftPressRunsTheActionAndAsksForTheClick() {
        Button button = textButton();
        assertTrue(button.tryPressPointer(new PointerInput(INSIDE_X, INSIDE_Y, PointerButton.LEFT, Modifiers.NONE)));
        assertEquals(1, runs);
        assertEquals(1, recorder.clicks);
    }

    @Test
    void enterNumpadEnterAndSpaceRunTheAction() {
        Button button = textButton();
        assertTrue(button.tryPressKey(new KeyInput(Key.ENTER, Modifiers.NONE)));
        assertTrue(button.tryPressKey(new KeyInput(Key.NUMPAD_ENTER, Modifiers.NONE)));
        assertTrue(button.tryPressKey(new KeyInput(Key.SPACE, Modifiers.NONE)));
        assertEquals(3, runs);
        assertEquals(3, recorder.clicks);
    }

    @Test
    void otherKeysAreLeft() {
        Button button = textButton();
        assertFalse(button.tryPressKey(new KeyInput(Key.TAB, Modifiers.NONE)));
        assertFalse(button.tryPressKey(new KeyInput(Key.OTHER, Modifiers.NONE)));
        assertEquals(0, runs);
        assertEquals(0, recorder.clicks);
    }

    @Test
    void disabledButtonNeitherRunsNorClicks() {
        Button button = textButton();
        button.setEnabled(false);
        assertFalse(button.tryPressPointer(new PointerInput(INSIDE_X, INSIDE_Y, PointerButton.LEFT, Modifiers.NONE)));
        assertFalse(button.tryPressKey(new KeyInput(Key.ENTER, Modifiers.NONE)));
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
        assertEquals(List.of(STYLE.normal(), STYLE.hover(), STYLE.normal(), STYLE.hover(), STYLE.disabled()),
                recorder.sprites);
    }

    @Test
    void selectedSpriteFollowsHoverAndFocus() {
        Button button = textButton();
        button.setSelected(true);
        button.draw(recorder, OUTSIDE_X, INSIDE_Y);
        button.draw(recorder, INSIDE_X, INSIDE_Y);
        button.setFocused(true);
        button.draw(recorder, OUTSIDE_X, INSIDE_Y);
        assertEquals(List.of(STYLE.selected(), STYLE.selectedHover(), STYLE.selectedHover()), recorder.sprites);
    }

    @Test
    void selectedLabelTakesTheSelectedColorHoveredOrNot() {
        Button button = textButton();
        button.setSelected(true);
        button.draw(recorder, OUTSIDE_X, INSIDE_Y);
        button.draw(recorder, INSIDE_X, INSIDE_Y);
        assertEquals(List.of(SELECTED_TEXT, SELECTED_TEXT), recorder.texts.stream().map(Text::color).toList());
    }

    @Test
    void disabledLookWinsOverSelected() {
        Button button = textButton();
        button.setSelected(true);
        button.setEnabled(false);
        button.draw(recorder, INSIDE_X, INSIDE_Y);
        assertEquals(List.of(STYLE.disabled()), recorder.sprites);
        assertEquals(DISABLED_TEXT, recorder.texts.getFirst().color());
    }

    @Test
    void selectedFollowsItsRule() {
        Button button = textButton();
        Value<Boolean> selected = Value.of(false);
        button.setSelected(selected);
        button.draw(recorder, OUTSIDE_X, INSIDE_Y);
        selected.set(true);
        button.draw(recorder, OUTSIDE_X, INSIDE_Y);
        assertEquals(List.of(STYLE.normal(), STYLE.selected()), recorder.sprites);
    }

    @Test
    void selectedButtonStillRunsItsAction() {
        Button button = textButton();
        button.setSelected(true);
        assertTrue(button.tryPressPointer(new PointerInput(INSIDE_X, INSIDE_Y, PointerButton.LEFT, Modifiers.NONE)));
        assertTrue(button.tryPressKey(new KeyInput(Key.ENTER, Modifiers.NONE)));
        assertEquals(2, runs);
        assertEquals(2, recorder.clicks);
    }

    @Test
    void spriteCoversTheWholeButton() {
        textButton().draw(recorder, OUTSIDE_X, INSIDE_Y);
        assertEquals(List.of(new Rect(LEFT, TOP, WIDTH_PIXELS, HEIGHT_PIXELS)), recorder.spriteRects);
    }

    @Test
    void textIsCenteredAndWhite() {
        textButton().draw(recorder, OUTSIDE_X, INSIDE_Y);
        assertEquals(List.of(new Text(LEFT + (WIDTH_PIXELS - TEXT_WIDTH_PIXELS) / 2, getTextTop(), ENABLED_TEXT, false)), recorder.texts);
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
        Button button = place(Button.icon(STYLE, Icon.sprite(ICON_SPRITE), LABEL, () -> runs++));
        button.draw(recorder, OUTSIDE_X, INSIDE_Y);
        assertEquals(new Rect(LEFT + (WIDTH_PIXELS - Icon.SIZE_PIXELS) / 2, iconTop(), Icon.SIZE_PIXELS, Icon.SIZE_PIXELS), recorder.spriteRects.getLast());
        assertTrue(recorder.texts.isEmpty());
        assertEquals(LABEL, button.getNarration());
    }

    @Test
    void iconSitsLeftOfTheTextAndThePairIsCentered() {
        Button button = place(Button.iconAndText(STYLE, Icon.sprite(ICON_SPRITE), LABEL, () -> runs++));
        button.draw(recorder, OUTSIDE_X, INSIDE_Y);
        int contentLeft = LEFT + (WIDTH_PIXELS - Icon.SIZE_PIXELS - ICON_GAP_PIXELS - TEXT_WIDTH_PIXELS) / 2;
        assertEquals(new Rect(contentLeft, iconTop(), Icon.SIZE_PIXELS, Icon.SIZE_PIXELS), recorder.spriteRects.getLast());
        assertEquals(List.of(new Text(contentLeft + Icon.SIZE_PIXELS + ICON_GAP_PIXELS, getTextTop(), ENABLED_TEXT, false)), recorder.texts);
    }

    @Test
    void labelWiderThanTheButtonIsClippedToItsRoomAndScrolledByTheClock() {
        Button button = textButton();
        button.setSize(NARROW_WIDTH_PIXELS, HEIGHT_PIXELS);
        int roomPixels = NARROW_WIDTH_PIXELS - 2 * LABEL_MARGIN_PIXELS;
        button.draw(recorder, OUTSIDE_X, INSIDE_Y);
        recorder.timeMillis = SCROLLED_TO_THE_END_MILLIS;
        button.draw(recorder, OUTSIDE_X, INSIDE_Y);
        Rect clip = new Rect(LEFT + LABEL_MARGIN_PIXELS, TOP, roomPixels, HEIGHT_PIXELS);
        assertEquals(List.of(clip, clip), recorder.clips);
        assertEquals(LEFT + LABEL_MARGIN_PIXELS, recorder.texts.get(0).x());
        assertEquals(LEFT + LABEL_MARGIN_PIXELS - (TEXT_WIDTH_PIXELS - roomPixels), recorder.texts.get(1).x());
        assertEquals(0, recorder.openClips);
    }

    @Test
    void labelThatFitsIsNotClipped() {
        textButton().draw(recorder, OUTSIDE_X, INSIDE_Y);
        assertTrue(recorder.clips.isEmpty());
    }

    @Test
    void naturalSizeIsTheContentBetweenTheSidePaddings() {
        Icon icon = Icon.sprite(ICON_SPRITE);
        assertEquals(new Size(TEXT_WIDTH_PIXELS + 2 * SIDE_PADDING_PIXELS, HEIGHT_PIXELS), textButton().naturalSize(recorder));
        assertEquals(new Size(HEIGHT_PIXELS, HEIGHT_PIXELS),
                Button.icon(STYLE, icon, LABEL, () -> runs++).naturalSize(recorder));
        assertEquals(new Size(Icon.SIZE_PIXELS + ICON_GAP_PIXELS + TEXT_WIDTH_PIXELS + 2 * SIDE_PADDING_PIXELS, HEIGHT_PIXELS),
                Button.iconAndText(STYLE, icon, LABEL, () -> runs++).naturalSize(recorder));
    }

    @Test
    void labelFollowsItsRule() {
        Button button = textButton();
        Value<Component> label = Value.of(LABEL);
        button.setLabel(label);
        label.set(OTHER_LABEL);
        assertEquals(OTHER_LABEL, button.getNarration());
    }

    @Test
    void buttonDisabledByARuleNeitherRunsNorClicks() {
        Button button = textButton();
        Value<Boolean> ready = Value.of(false);
        button.setEnabled(ready);
        assertFalse(button.tryPressPointer(new PointerInput(INSIDE_X, INSIDE_Y, PointerButton.LEFT, Modifiers.NONE)));
        ready.set(true);
        assertTrue(button.tryPressPointer(new PointerInput(INSIDE_X, INSIDE_Y, PointerButton.LEFT, Modifiers.NONE)));
        assertEquals(1, runs);
    }

    private Button textButton() {
        return place(Button.text(STYLE, LABEL, () -> runs++));
    }

    private Button place(Button button) {
        button.setPosition(LEFT, TOP);
        button.setSize(WIDTH_PIXELS, HEIGHT_PIXELS);
        button.attach(recorder);
        return button;
    }

    private static int getTextTop() {
        return TOP + (HEIGHT_PIXELS - LINE_HEIGHT_PIXELS) / 2 + 1;
    }

    private static int iconTop() {
        return TOP + (HEIGHT_PIXELS - Icon.SIZE_PIXELS) / 2;
    }

    private record Rect(int x, int y, int widthPixels, int heightPixels) {
    }

    private record Text(int x, int y, int color, boolean hasShadow) {
    }

    private static final class Recorder implements Canvas, ComponentHost {
        private final List<String> sprites = new ArrayList<>();
        private final List<Rect> spriteRects = new ArrayList<>();
        private final List<Text> texts = new ArrayList<>();
        private final List<Rect> clips = new ArrayList<>();
        private int openClips;
        private long timeMillis;
        private int clicks;

        @Override
        public void sprite(String sprite, int x, int y, int widthPixels, int heightPixels) {
            if (!sprite.equals(ICON_SPRITE)) {
                sprites.add(sprite);
            }
            spriteRects.add(new Rect(x, y, widthPixels, heightPixels));
        }

        @Override
        public void sprite(String sprite, int x, int y, int widthPixels, int heightPixels, int tint) {
            sprite(sprite, x, y, widthPixels, heightPixels);
        }

        @Override
        public void fill(int x, int y, int widthPixels, int heightPixels, int color) {
        }

        @Override
        public void text(Component text, int x, int y, int color, boolean hasShadow) {
            texts.add(new Text(x, y, color, hasShadow));
        }

        @Override
        public int textWidthPixels(Component text) {
            return TEXT_WIDTH_PIXELS;
        }

        @Override
        public void item(ItemStack stack, int x, int y) {
        }

        @Override
        public void pushClip(int x, int y, int widthPixels, int heightPixels) {
            clips.add(new Rect(x, y, widthPixels, heightPixels));
            openClips++;
        }

        @Override
        public void popClip() {
            openClips--;
        }

        @Override
        public long getFrameTimeMillis() {
            return timeMillis;
        }

        @Override
        public void clickSound() {
            clicks++;
        }
    }
}

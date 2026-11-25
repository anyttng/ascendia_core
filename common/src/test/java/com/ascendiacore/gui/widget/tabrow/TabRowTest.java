package com.ascendiacore.gui.widget.tabrow;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;

import org.junit.jupiter.api.Test;

import com.ascendiacore.gui.base.component.GuiComponent;
import com.ascendiacore.gui.base.draw.Icon;
import com.ascendiacore.gui.base.draw.RecordingCanvas;
import com.ascendiacore.gui.base.geometry.Rect;
import com.ascendiacore.gui.base.geometry.Size;
import com.ascendiacore.gui.base.input.KeyInput;
import com.ascendiacore.gui.base.input.PointerInput;
import com.ascendiacore.gui.base.input.model.Key;
import com.ascendiacore.gui.base.input.model.Modifiers;
import com.ascendiacore.gui.base.input.model.PointerButton;
import com.ascendiacore.gui.base.property.Value;
import com.ascendiacore.gui.widget.tabrow.model.TabContent;
import com.ascendiacore.gui.widget.tabrow.model.TabRowStyle;

import net.minecraft.network.chat.Component;

class TabRowTest {
    private static final int LABEL_COLOR = 0xFF112233;
    private static final int HOVER_LABEL_COLOR = 0xFF445566;
    private static final int SELECTED_LABEL_COLOR = 0xFF778899;
    private static final TabRowStyle STYLE = new TabRowStyle("test:normal", "test:hover", "test:selected",
            "test:selected_hover", LABEL_COLOR, HOVER_LABEL_COLOR, SELECTED_LABEL_COLOR, false);
    private static final List<Component> NAMES = List.of(Component.literal("Combat"), Component.literal("Mining"),
            Component.literal("Magic"));
    private static final List<TabContent> TEXT_TABS = NAMES.stream().map(TabContent::text).toList();
    private static final String ICON_SPRITE = "test:icon";
    private static final String OTHER_ICON_SPRITE = "test:other_icon";
    private static final List<TabContent> ICON_TABS = List.of(
            TabContent.iconsAndText(NAMES.get(0), Icon.sprite(ICON_SPRITE)),
            TabContent.text(NAMES.get(1)),
            TabContent.icons(NAMES.get(2), Icon.sprite(ICON_SPRITE), Icon.sprite(OTHER_ICON_SPRITE)));
    private static final int ICON_ROW_HEIGHT_PIXELS = 24;
    private static final int ICON_ROW_SELECTED_HEIGHT_PIXELS = 26;
    private static final int SIDE_PADDING_PIXELS = 6;
    private static final int PART_GAP_PIXELS = 2;
    private static final int PANEL_OVERLAP_PIXELS = 2;
    private static final int LINE_HEIGHT_PIXELS = 9;
    private static final int WIDE_ROW_WIDTH_PIXELS = 400;
    private static final long SCROLLED_TO_THE_END_MILLIS = 1700;
    private static final int LEFT = 100;
    private static final int TOP = 50;
    private static final int TAB_WIDTH_PIXELS = 56;
    private static final int TAB_HEIGHT_PIXELS = 16;
    private static final int SELECTED_HEIGHT_PIXELS = 18;
    private static final int GAP_PIXELS = 2;
    private static final int STEP_PIXELS = TAB_WIDTH_PIXELS + GAP_PIXELS;
    private static final int ROW_WIDTH_PIXELS = 3 * TAB_WIDTH_PIXELS + 2 * GAP_PIXELS;
    private static final int LOWER_TOP = TOP + SELECTED_HEIGHT_PIXELS - TAB_HEIGHT_PIXELS;
    private static final int LABEL_TOP_PIXELS = 4;
    private static final int TEXT_WIDTH_PIXELS = 30;
    private static final int WIDE_TEXT_WIDTH_PIXELS = 60;
    private static final int SECOND_X = LEFT + STEP_PIXELS + 5;
    private static final int INSIDE_Y = TOP + 10;
    private static final int OUTSIDE_X = LEFT - 5;

    private final RecordingCanvas canvas = new RecordingCanvas();
    private final Value<Integer> selectedIndex = Value.of(0);
    private final List<Integer> changes = new ArrayList<>();
    private int clicks;

    @Test
    void naturalSizeIsTheTabsWithTheirGapsAtTheSelectedHeight() {
        assertEquals(new Size(ROW_WIDTH_PIXELS, SELECTED_HEIGHT_PIXELS), new TabRow(STYLE, TEXT_TABS, selectedIndex, OptionalInt.of(TAB_WIDTH_PIXELS)).naturalSize(canvas));
    }

    @Test
    void selectedTabIsTallerAndTheOthersShareItsBottom() {
        List<GuiComponent> tabs = tabs(row());
        assertEquals(List.of(new Rect(LEFT, TOP, TAB_WIDTH_PIXELS, SELECTED_HEIGHT_PIXELS),
                new Rect(LEFT + STEP_PIXELS, LOWER_TOP, TAB_WIDTH_PIXELS, TAB_HEIGHT_PIXELS),
                new Rect(LEFT + 2 * STEP_PIXELS, LOWER_TOP, TAB_WIDTH_PIXELS, TAB_HEIGHT_PIXELS)), bounds(tabs));
    }

    @Test
    void pressSelectsTheTabAndReportsItsIndexOnce() {
        TabRow row = row();
        assertTrue(tabs(row).get(1).tryPressPointer(press(SECOND_X)));
        assertEquals(1, selectedIndex.get());
        assertEquals(List.of(1), changes);
        assertEquals(1, clicks);
        row.layout(canvas);
        assertEquals(new Rect(LEFT + STEP_PIXELS, TOP, TAB_WIDTH_PIXELS, SELECTED_HEIGHT_PIXELS), bounds(tabs(row)).get(1));
    }

    @Test
    void pressOnTheSelectedTabChangesNothing() {
        assertTrue(tabs(row()).get(0).tryPressPointer(press(LEFT + 5)));
        assertEquals(0, selectedIndex.get());
        assertEquals(List.of(), changes);
        assertEquals(0, clicks);
    }

    @Test
    void enterAndSpaceSelectTheTabAndArrowsAreLeft() {
        List<GuiComponent> tabs = tabs(row());
        assertFalse(tabs.get(1).tryPressKey(key(Key.RIGHT)));
        assertFalse(tabs.get(1).tryPressKey(key(Key.LEFT)));
        assertEquals(0, selectedIndex.get());
        assertTrue(tabs.get(1).tryPressKey(key(Key.ENTER)));
        assertTrue(tabs.get(2).tryPressKey(key(Key.SPACE)));
        assertEquals(List.of(1, 2), changes);
        assertEquals(2, clicks);
    }

    @Test
    void valueSetFromOutsideMovesTheSelection() {
        TabRow row = row();
        selectedIndex.set(2);
        row.layout(canvas);
        draw(row, OUTSIDE_X);
        assertEquals(List.of("test:normal", "test:normal", "test:selected"), sprites());
        assertEquals(List.of(), changes);
    }

    @Test
    void spriteCoversTheWholeTab() {
        draw(row(), OUTSIDE_X);
        assertEquals(new RecordingCanvas.Sprite("test:selected", LEFT, TOP, TAB_WIDTH_PIXELS, SELECTED_HEIGHT_PIXELS),
                canvas.sprites().get(0));
        assertEquals(new RecordingCanvas.Sprite("test:normal", LEFT + STEP_PIXELS, LOWER_TOP, TAB_WIDTH_PIXELS, TAB_HEIGHT_PIXELS),
                canvas.sprites().get(1));
    }

    @Test
    void hoveredTabTakesItsHoverSpriteAndLabelColor() {
        draw(row(), SECOND_X);
        assertEquals(List.of("test:selected", "test:hover", "test:normal"), sprites());
        assertEquals(List.of(SELECTED_LABEL_COLOR, HOVER_LABEL_COLOR, LABEL_COLOR), labelColors());
    }

    @Test
    void focusedTabLooksHovered() {
        TabRow row = row();
        tabs(row).get(0).setFocused(true);
        tabs(row).get(2).setFocused(true);
        draw(row, OUTSIDE_X);
        assertEquals(List.of("test:selected_hover", "test:normal", "test:hover"), sprites());
        assertEquals(List.of(SELECTED_LABEL_COLOR, LABEL_COLOR, HOVER_LABEL_COLOR), labelColors());
    }

    @Test
    void labelIsCenteredAtTheSameDepthFromItsTabsTop() {
        draw(row(), OUTSIDE_X);
        RecordingCanvas.Text first = canvas.texts().get(0);
        RecordingCanvas.Text second = canvas.texts().get(1);
        assertEquals(LEFT + (TAB_WIDTH_PIXELS - TEXT_WIDTH_PIXELS) / 2, first.x());
        assertEquals(TOP + LABEL_TOP_PIXELS, first.y());
        assertEquals(LOWER_TOP + LABEL_TOP_PIXELS, second.y());
        assertTrue(canvas.clips().isEmpty());
    }

    @Test
    void labelWiderThanItsTabIsClippedToItsRoom() {
        TabRow row = row();
        canvas.setTextWidth(WIDE_TEXT_WIDTH_PIXELS);
        draw(row, OUTSIDE_X);
        assertEquals(new Rect(LEFT + SIDE_PADDING_PIXELS, TOP, TAB_WIDTH_PIXELS - 2 * SIDE_PADDING_PIXELS, SELECTED_HEIGHT_PIXELS),
                canvas.clips().get(0));
        assertEquals(LEFT + SIDE_PADDING_PIXELS, canvas.texts().get(0).x());
    }

    @Test
    void withoutAGivenWidthEveryTabIsAsWideAsItsContent() {
        TabRow row = iconRow(OptionalInt.empty(), WIDE_ROW_WIDTH_PIXELS);
        int iconAndTextPixels = Icon.SIZE_PIXELS + PART_GAP_PIXELS + TEXT_WIDTH_PIXELS + 2 * SIDE_PADDING_PIXELS;
        int textPixels = TEXT_WIDTH_PIXELS + 2 * SIDE_PADDING_PIXELS;
        int twoIconsPixels = 2 * Icon.SIZE_PIXELS + PART_GAP_PIXELS + 2 * SIDE_PADDING_PIXELS;
        assertEquals(List.of(iconAndTextPixels, textPixels, twoIconsPixels),
                tabs(row).stream().map(GuiComponent::getWidthPixels).toList());
        assertEquals(new Size(iconAndTextPixels + textPixels + twoIconsPixels + 2 * GAP_PIXELS, ICON_ROW_SELECTED_HEIGHT_PIXELS),
                row.naturalSize(canvas));
        draw(row, OUTSIDE_X);
        assertTrue(canvas.clips().isEmpty());
    }

    @Test
    void rowThatHoldsAnIconIsTallerInEveryTab() {
        TabRow row = iconRow(OptionalInt.of(TAB_WIDTH_PIXELS), ROW_WIDTH_PIXELS);
        assertEquals(List.of(ICON_ROW_SELECTED_HEIGHT_PIXELS, ICON_ROW_HEIGHT_PIXELS, ICON_ROW_HEIGHT_PIXELS),
                tabs(row).stream().map(GuiComponent::getHeightPixels).toList());
    }

    @Test
    void iconsStandLeftOfTheTextCenteredOverTheVisiblePartOfTheirTab() {
        TabRow row = iconRow(OptionalInt.empty(), WIDE_ROW_WIDTH_PIXELS);
        draw(row, OUTSIDE_X);
        List<RecordingCanvas.Sprite> icons = icons();
        int pickedIconTop = TOP + (ICON_ROW_SELECTED_HEIGHT_PIXELS - PANEL_OVERLAP_PIXELS - Icon.SIZE_PIXELS) / 2;
        assertEquals(new RecordingCanvas.Sprite(ICON_SPRITE, LEFT + SIDE_PADDING_PIXELS, pickedIconTop, Icon.SIZE_PIXELS, Icon.SIZE_PIXELS),
                icons.get(0));
        RecordingCanvas.Text label = canvas.texts().get(0);
        assertEquals(LEFT + SIDE_PADDING_PIXELS + Icon.SIZE_PIXELS + PART_GAP_PIXELS, label.x());
        assertEquals(TOP + (ICON_ROW_SELECTED_HEIGHT_PIXELS - PANEL_OVERLAP_PIXELS - LINE_HEIGHT_PIXELS) / 2 + 1, label.y());
        GuiComponent twoIcons = tabs(row).get(2);
        int idleIconTop = twoIcons.getY() + (ICON_ROW_HEIGHT_PIXELS - PANEL_OVERLAP_PIXELS - Icon.SIZE_PIXELS) / 2;
        assertEquals(new RecordingCanvas.Sprite(ICON_SPRITE, twoIcons.getX() + SIDE_PADDING_PIXELS, idleIconTop, Icon.SIZE_PIXELS, Icon.SIZE_PIXELS),
                icons.get(1));
        assertEquals(new RecordingCanvas.Sprite(OTHER_ICON_SPRITE, twoIcons.getX() + SIDE_PADDING_PIXELS + Icon.SIZE_PIXELS + PART_GAP_PIXELS,
                idleIconTop, Icon.SIZE_PIXELS, Icon.SIZE_PIXELS), icons.get(2));
    }

    @Test
    void nameOfAnIconTabIsNarratedAndNotDrawn() {
        TabRow row = iconRow(OptionalInt.empty(), WIDE_ROW_WIDTH_PIXELS);
        draw(row, OUTSIDE_X);
        assertEquals(List.of("Combat", "Mining"), canvas.texts().stream().map(text -> text.text().getString()).toList());
        assertEquals(Component.translatable("gui.narrate.tab", NAMES.get(2)), tabs(row).get(2).getNarration());
    }

    @Test
    void contentThatDoesNotFitAGivenWidthScrollsAsOnePieceIconAndText() {
        TabRow row = iconRow(OptionalInt.of(TAB_WIDTH_PIXELS), ROW_WIDTH_PIXELS);
        draw(row, OUTSIDE_X);
        canvas.setFrameTimeMillis(SCROLLED_TO_THE_END_MILLIS);
        draw(row, OUTSIDE_X);
        int roomLeft = LEFT + SIDE_PADDING_PIXELS;
        int roomPixels = TAB_WIDTH_PIXELS - 2 * SIDE_PADDING_PIXELS;
        int overflowPixels = Icon.SIZE_PIXELS + PART_GAP_PIXELS + TEXT_WIDTH_PIXELS - roomPixels;
        Rect clip = new Rect(roomLeft, TOP, roomPixels, ICON_ROW_SELECTED_HEIGHT_PIXELS);
        assertEquals(List.of(clip, clip), canvas.clips());
        List<RecordingCanvas.Sprite> icons = icons();
        assertEquals(roomLeft, icons.get(0).x());
        assertEquals(roomLeft - overflowPixels, icons.get(icons.size() / 2).x());
        List<RecordingCanvas.Text> texts = canvas.texts();
        int textLeft = roomLeft + Icon.SIZE_PIXELS + PART_GAP_PIXELS;
        assertEquals(textLeft, texts.get(0).x());
        assertEquals(textLeft - overflowPixels, texts.get(texts.size() / 2).x());
    }

    @Test
    void iconsAloneThatDoNotFitAGivenWidthAreClippedToTheirRoom() {
        Icon icon = Icon.sprite(ICON_SPRITE);
        TabRow row = place(new TabRow(STYLE, List.of(TabContent.icons(NAMES.get(0), icon, icon, icon, icon)), selectedIndex,
                OptionalInt.of(TAB_WIDTH_PIXELS)), TAB_WIDTH_PIXELS, ICON_ROW_SELECTED_HEIGHT_PIXELS);
        draw(row, OUTSIDE_X);
        assertEquals(List.of(new Rect(LEFT + SIDE_PADDING_PIXELS, TOP, TAB_WIDTH_PIXELS - 2 * SIDE_PADDING_PIXELS,
                ICON_ROW_SELECTED_HEIGHT_PIXELS)), canvas.clips());
        assertEquals(4, icons().size());
        assertEquals(LEFT + SIDE_PADDING_PIXELS, icons().get(0).x());
    }

    @Test
    void narrationNamesTheTab() {
        assertEquals(Component.translatable("gui.narrate.tab", NAMES.get(1)), tabs(row()).get(1).getNarration());
    }

    private TabRow row() {
        return place(new TabRow(STYLE, TEXT_TABS, selectedIndex, OptionalInt.of(TAB_WIDTH_PIXELS)), ROW_WIDTH_PIXELS, SELECTED_HEIGHT_PIXELS);
    }

    private TabRow iconRow(OptionalInt tabWidthPixels, int rowWidthPixels) {
        return place(new TabRow(STYLE, ICON_TABS, selectedIndex, tabWidthPixels), rowWidthPixels, ICON_ROW_SELECTED_HEIGHT_PIXELS);
    }

    private TabRow place(TabRow row, int widthPixels, int heightPixels) {
        row.setOnChange(changes::add);
        row.getMountedComponents().forEach(tab -> tab.attach(() -> clicks++));
        canvas.setTextWidth(TEXT_WIDTH_PIXELS);
        row.setPosition(LEFT, TOP);
        row.setSize(widthPixels, heightPixels);
        row.layout(canvas);
        return row;
    }

    private List<RecordingCanvas.Sprite> icons() {
        return canvas.sprites().stream().filter(sprite -> sprite.sprite().equals(ICON_SPRITE) || sprite.sprite().equals(OTHER_ICON_SPRITE))
                .toList();
    }

    private void draw(TabRow row, int pointerX) {
        tabs(row).forEach(tab -> tab.draw(canvas, pointerX, INSIDE_Y));
    }

    private static List<GuiComponent> tabs(TabRow row) {
        return row.getMountedComponents();
    }

    private static List<Rect> bounds(List<GuiComponent> tabs) {
        return tabs.stream().map(tab -> new Rect(tab.getX(), tab.getY(), tab.getWidthPixels(), tab.getHeightPixels())).toList();
    }

    private List<String> sprites() {
        return canvas.sprites().stream().map(RecordingCanvas.Sprite::sprite).toList();
    }

    private List<Integer> labelColors() {
        return canvas.texts().stream().map(RecordingCanvas.Text::color).toList();
    }

    private static PointerInput press(int x) {
        return new PointerInput(x, INSIDE_Y, PointerButton.LEFT, Modifiers.NONE);
    }

    private static KeyInput key(Key key) {
        return new KeyInput(key, Modifiers.NONE);
    }
}

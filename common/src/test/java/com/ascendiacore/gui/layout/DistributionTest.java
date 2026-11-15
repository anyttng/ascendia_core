package com.ascendiacore.gui.layout;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.ascendiacore.gui.layout.model.Length;

class DistributionTest {
    private static final int GAP = 8;
    private static final Distribution.Item LIST = new Distribution.Item(Length.flexible(100, 150), 0);
    private static final Distribution.Item PANEL = new Distribution.Item(Length.flexible(200, 310), 0);

    @Test
    void everythingKeepsItsPreferredSizeWhenItFits() {
        assertArrayEquals(new int[] {150, 310}, Distribution.sizes(468, GAP, List.of(LIST, PANEL)));
    }

    @Test
    void flexibleItemsShrinkInProportionToTheirRoom() {
        assertArrayEquals(new int[] {139, 283}, Distribution.sizes(430, GAP, List.of(LIST, PANEL)));
    }

    @Test
    void flexibleItemsStopAtTheirMinimum() {
        assertArrayEquals(new int[] {100, 200}, Distribution.sizes(300, GAP, List.of(LIST, PANEL)));
    }

    @Test
    void fixedAndNaturalItemsNeverShrink() {
        List<Distribution.Item> items = List.of(
                new Distribution.Item(Length.fixed(60), 0),
                new Distribution.Item(Length.natural(), 40),
                LIST);
        assertArrayEquals(new int[] {60, 40, 100}, Distribution.sizes(150, 0, items));
    }

    @Test
    void fillItemsShareWhatIsLeftByWeight() {
        List<Distribution.Item> items = List.of(
                new Distribution.Item(Length.fixed(20), 0),
                new Distribution.Item(Length.fill(), 0),
                new Distribution.Item(Length.fill(3), 0));
        assertArrayEquals(new int[] {20, 20, 60}, Distribution.sizes(100, 0, items));
    }

    @Test
    void gapsComeOutOfTheSpaceAndTheSharesAddUpExactly() {
        Distribution.Item fill = new Distribution.Item(Length.fill(), 0);
        assertArrayEquals(new int[] {26, 27, 27}, Distribution.sizes(100, 10, List.of(fill, fill, fill)));
    }

    @Test
    void nothingToPlaceGivesNoSizes() {
        assertArrayEquals(new int[0], Distribution.sizes(100, GAP, List.of()));
    }
}

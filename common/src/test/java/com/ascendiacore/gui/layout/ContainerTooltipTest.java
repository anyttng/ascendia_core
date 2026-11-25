package com.ascendiacore.gui.layout;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.ascendiacore.gui.theme.TestThemes;
import com.ascendiacore.gui.widget.panel.Panel;
import com.ascendiacore.gui.widget.panel.model.FillKind;

import net.minecraft.network.chat.Component;

class ContainerTooltipTest {
    private static final int PANEL_SIZE_PIXELS = 100;
    private static final int CHILD_SIZE_PIXELS = 10;
    private static final int ON_CHILD = 5;
    private static final int PAST_CHILD = 50;
    private static final String PANEL_TEXT = "Panel";
    private static final String CHILD_TEXT = "Child";

    @Test
    void panelShowsItsTooltipWhereNoChildHasOne() {
        Panel panel = panel();
        panel.add(new Box(CHILD_SIZE_PIXELS, CHILD_SIZE_PIXELS));
        assertEquals(PANEL_TEXT, panel.getTooltipAt(ON_CHILD, ON_CHILD).getString());
    }

    @Test
    void childWithATooltipCoversThePanelsOnlyOverItself() {
        Panel panel = panel();
        panel.add(childWithTooltip());
        assertEquals("", panel.getTooltipAt(ON_CHILD, ON_CHILD).getString());
        assertEquals(PANEL_TEXT, panel.getTooltipAt(PAST_CHILD, PAST_CHILD).getString());
    }

    @Test
    void childInsideAPlainRowCoversThePanels() {
        Panel panel = panel();
        Row row = new Row();
        row.setSize(CHILD_SIZE_PIXELS, CHILD_SIZE_PIXELS);
        row.add(childWithTooltip());
        panel.add(row);
        assertEquals("", panel.getTooltipAt(ON_CHILD, ON_CHILD).getString());
    }

    @Test
    void plainRowsOwnTooltipCoversNothing() {
        Panel panel = panel();
        Row row = new Row();
        row.setSize(CHILD_SIZE_PIXELS, CHILD_SIZE_PIXELS);
        row.setTooltip(TestThemes.VANILLA.tooltip(), Component.literal(CHILD_TEXT));
        panel.add(row);
        assertEquals(PANEL_TEXT, panel.getTooltipAt(ON_CHILD, ON_CHILD).getString());
    }

    @Test
    void hiddenChildCoversNothing() {
        Panel panel = panel();
        Box child = childWithTooltip();
        child.setVisible(false);
        panel.add(child);
        assertEquals(PANEL_TEXT, panel.getTooltipAt(ON_CHILD, ON_CHILD).getString());
    }

    private static Panel panel() {
        Panel panel = new Panel(TestThemes.BOOK.panel(), FillKind.PARCHMENT);
        panel.setSize(PANEL_SIZE_PIXELS, PANEL_SIZE_PIXELS);
        panel.setTooltip(TestThemes.BOOK.tooltip(), Component.literal(PANEL_TEXT));
        return panel;
    }

    private static Box childWithTooltip() {
        Box child = new Box(CHILD_SIZE_PIXELS, CHILD_SIZE_PIXELS);
        child.setTooltip(TestThemes.BOOK.tooltip(), Component.literal(CHILD_TEXT));
        return child;
    }
}

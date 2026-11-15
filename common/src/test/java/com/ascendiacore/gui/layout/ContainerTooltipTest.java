package com.ascendiacore.gui.layout;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.ascendiacore.gui.theme.Themes;
import com.ascendiacore.gui.widget.panel.Panel;
import com.ascendiacore.gui.widget.panel.model.FillKind;

import net.minecraft.network.chat.Component;

class ContainerTooltipTest {
    private static final int PANEL_SIZE = 100;
    private static final int CHILD_SIZE = 10;
    private static final int ON_CHILD = 5;
    private static final int PAST_CHILD = 50;
    private static final String PANEL_TEXT = "Panel";
    private static final String CHILD_TEXT = "Child";

    @Test
    void panelShowsItsTooltipWhereNoChildHasOne() {
        Panel panel = panel();
        panel.add(new Box(CHILD_SIZE, CHILD_SIZE));
        assertEquals(PANEL_TEXT, panel.tooltipAt(ON_CHILD, ON_CHILD).getString());
    }

    @Test
    void childWithATooltipCoversThePanelsOnlyOverItself() {
        Panel panel = panel();
        panel.add(childWithTooltip());
        assertEquals("", panel.tooltipAt(ON_CHILD, ON_CHILD).getString());
        assertEquals(PANEL_TEXT, panel.tooltipAt(PAST_CHILD, PAST_CHILD).getString());
    }

    @Test
    void childInsideAPlainRowCoversThePanels() {
        Panel panel = panel();
        Row row = new Row();
        row.setSize(CHILD_SIZE, CHILD_SIZE);
        row.add(childWithTooltip());
        panel.add(row);
        assertEquals("", panel.tooltipAt(ON_CHILD, ON_CHILD).getString());
    }

    @Test
    void plainRowsOwnTooltipCoversNothing() {
        Panel panel = panel();
        Row row = new Row();
        row.setSize(CHILD_SIZE, CHILD_SIZE);
        row.setTooltip(Themes.VANILLA.tooltip(), Component.literal(CHILD_TEXT));
        panel.add(row);
        assertEquals(PANEL_TEXT, panel.tooltipAt(ON_CHILD, ON_CHILD).getString());
    }

    @Test
    void hiddenChildCoversNothing() {
        Panel panel = panel();
        Box child = childWithTooltip();
        child.setVisible(false);
        panel.add(child);
        assertEquals(PANEL_TEXT, panel.tooltipAt(ON_CHILD, ON_CHILD).getString());
    }

    private static Panel panel() {
        Panel panel = new Panel(Themes.BOOK.panel(), FillKind.PARCHMENT);
        panel.setSize(PANEL_SIZE, PANEL_SIZE);
        panel.setTooltip(Themes.BOOK.tooltip(), Component.literal(PANEL_TEXT));
        return panel;
    }

    private static Box childWithTooltip() {
        Box child = new Box(CHILD_SIZE, CHILD_SIZE);
        child.setTooltip(Themes.BOOK.tooltip(), Component.literal(CHILD_TEXT));
        return child;
    }
}

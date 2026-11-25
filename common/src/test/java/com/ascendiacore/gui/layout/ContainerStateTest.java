package com.ascendiacore.gui.layout;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.ascendiacore.gui.base.input.PointerInput;
import com.ascendiacore.gui.base.input.model.Modifiers;
import com.ascendiacore.gui.base.input.model.PointerButton;
import com.ascendiacore.gui.base.property.Value;
import com.ascendiacore.gui.theme.TestThemes;
import com.ascendiacore.gui.widget.panel.Panel;
import com.ascendiacore.gui.widget.panel.model.FillKind;

class ContainerStateTest {
    private static final PointerInput PRESS_INSIDE = new PointerInput(5, 5, PointerButton.LEFT, Modifiers.NONE);

    @Test
    void hiddenContainerHidesEverythingInsideIt() {
        Column outer = new Column();
        Row inner = new Row();
        Box box = new Box(10, 10);
        inner.add(box);
        outer.add(inner);
        Value<Boolean> shown = Value.of(true);
        outer.setVisible(shown);
        assertTrue(box.isVisible());
        shown.set(false);
        assertFalse(inner.isVisible());
        assertFalse(box.isVisible());
        assertTrue(box.isVisibleByOwnRule());
    }

    @Test
    void disabledContainerDisablesEverythingInsideIt() {
        Column outer = new Column();
        Box box = new Box(10, 10);
        outer.add(box);
        outer.setEnabled(false);
        assertFalse(box.isEnabled());
        assertTrue(box.isEnabledByOwnRule());
        assertFalse(box.tryPressPointer(PRESS_INSIDE));
    }

    @Test
    void containerTakesNoPressAndItsChildDoes() {
        Column column = new Column();
        column.setSize(20, 20);
        Box box = new Box(10, 10);
        column.add(box);
        assertFalse(column.tryPressPointer(PRESS_INSIDE));
        assertTrue(box.tryPressPointer(PRESS_INSIDE));
    }

    @Test
    void paintingContainerIsMountedBeforeItsContentAndAPlainOneIsNot() {
        Column root = new Column();
        Panel panel = new Panel(TestThemes.BOOK.panel(), FillKind.PARCHMENT);
        Row row = new Row();
        Box first = new Box(0, 0);
        Box second = new Box(0, 0);
        row.add(second);
        panel.add(first);
        panel.add(row);
        root.add(panel);
        assertEquals(List.of(panel, first, second), root.getMountedComponents());
    }

    @Test
    void paintingRootIsMountedItself() {
        Panel panel = new Panel(TestThemes.BOOK.panel(), FillKind.PARCHMENT);
        Box box = new Box(0, 0);
        panel.add(box);
        assertEquals(List.of(panel, box), panel.getMountedComponents());
    }
}

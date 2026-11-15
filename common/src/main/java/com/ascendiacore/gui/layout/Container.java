package com.ascendiacore.gui.layout;

import java.util.ArrayList;
import java.util.List;

import com.ascendiacore.gui.base.component.GuiComponent;
import com.ascendiacore.gui.base.draw.Canvas;
import com.ascendiacore.gui.base.draw.TextMetrics;
import com.ascendiacore.gui.base.form.Validated;
import com.ascendiacore.gui.base.geometry.Size;
import com.ascendiacore.gui.layout.model.Align;
import com.ascendiacore.gui.layout.model.Axis;
import com.ascendiacore.gui.layout.model.Length;

public abstract class Container extends GuiComponent {
    private record Slot(GuiComponent component, Length length) {
    }

    private final Axis axis;
    private final List<Slot> slots = new ArrayList<>();
    private int gap;
    private int padding;
    private Align align = Align.STRETCH;

    protected Container(Axis axis) {
        this.axis = axis;
    }

    public void add(GuiComponent child) {
        add(child, Length.natural());
    }

    public void add(GuiComponent child, Length length) {
        adopt(child);
        slots.add(new Slot(child, length));
    }

    public void setGap(int gap) {
        this.gap = gap;
    }

    public void setPadding(int padding) {
        this.padding = padding;
    }

    public void setAlign(Align align) {
        this.align = align;
    }

    public boolean paints() {
        return false;
    }

    @Override
    public boolean interactive() {
        return false;
    }

    public List<GuiComponent> mountedComponents() {
        List<GuiComponent> mounted = new ArrayList<>();
        if (paints()) {
            mounted.add(this);
        }
        for (Slot slot : slots) {
            if (slot.component() instanceof Container container) {
                mounted.addAll(container.mountedComponents());
            } else {
                mounted.add(slot.component());
            }
        }
        return mounted;
    }

    public boolean fieldsValid() {
        return fieldsValid(this);
    }

    public void layout(TextMetrics metrics) {
        List<Slot> shown = shownSlots();
        List<Size> naturals = shown.stream().map(slot -> slot.component().naturalSize(metrics)).toList();
        int[] lengths = Distribution.sizes(axis.along(width(), height()) - 2 * padding, gap, items(shown, naturals));
        int offset = padding;
        for (int index = 0; index < shown.size(); index++) {
            GuiComponent child = shown.get(index).component();
            place(child, offset, lengths[index], naturals.get(index));
            offset += lengths[index] + gap;
            if (child instanceof Container container) {
                container.layout(metrics);
            }
        }
    }

    @Override
    public Size naturalSize(TextMetrics metrics) {
        List<Slot> shown = shownSlots();
        int along = gap * Math.max(shown.size() - 1, 0) + 2 * padding;
        int across = 0;
        for (Slot slot : shown) {
            Size natural = slot.component().naturalSize(metrics);
            along += Distribution.preferredSize(item(slot, natural));
            across = Math.max(across, axis.across(natural.width(), natural.height()));
        }
        across += 2 * padding;
        return new Size(axis.horizontal(along, across), axis.vertical(along, across));
    }

    @Override
    protected void drawContent(Canvas canvas) {
    }

    private void place(GuiComponent child, int offset, int length, Size natural) {
        int room = axis.across(width(), height()) - 2 * padding;
        int breadth = align == Align.STRETCH ? room : axis.across(natural.width(), natural.height());
        int shift = padding + switch (align) {
            case START, STRETCH -> 0;
            case CENTER -> (room - breadth) / 2;
            case END -> room - breadth;
        };
        child.setPosition(x() + axis.horizontal(offset, shift), y() + axis.vertical(offset, shift));
        child.setSize(axis.horizontal(length, breadth), axis.vertical(length, breadth));
    }

    private List<Slot> shownSlots() {
        return slots.stream().filter(slot -> slot.component().visibleByOwnRule()).toList();
    }

    private List<Distribution.Item> items(List<Slot> shown, List<Size> naturals) {
        List<Distribution.Item> items = new ArrayList<>(shown.size());
        for (int index = 0; index < shown.size(); index++) {
            items.add(item(shown.get(index), naturals.get(index)));
        }
        return items;
    }

    private Distribution.Item item(Slot slot, Size natural) {
        return new Distribution.Item(slot.length(), axis.along(natural.width(), natural.height()));
    }

    private static boolean fieldsValid(GuiComponent component) {
        if (component instanceof Container container) {
            return !counted(container) || container.slots.stream().allMatch(slot -> fieldsValid(slot.component()));
        }
        if (component instanceof Validated field) {
            return !counted(component) || field.valid();
        }
        return true;
    }

    private static boolean counted(GuiComponent component) {
        return component.countedWhenInactive() || (component.visibleByOwnRule() && component.enabledByOwnRule());
    }
}

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

import net.minecraft.network.chat.Component;

public abstract class Container extends GuiComponent {
    private record Slot(GuiComponent component, Length length) {
    }

    private final Axis axis;
    private final List<Slot> slots = new ArrayList<>();
    private int gapPixels;
    private int paddingPixels;
    private Align align = Align.STRETCH;

    protected Container(Axis axis) {
        this.axis = axis;
    }

    protected void add(GuiComponent child) {
        add(child, Length.natural());
    }

    protected void add(GuiComponent child, Length length) {
        adopt(child);
        slots.add(new Slot(child, length));
    }

    public void setGapPixels(int gapPixels) {
        this.gapPixels = gapPixels;
    }

    public void setPaddingPixels(int paddingPixels) {
        this.paddingPixels = paddingPixels;
    }

    public void setAlign(Align align) {
        this.align = align;
    }

    public boolean isMountingItself() {
        return false;
    }

    @Override
    public boolean isInteractive() {
        return false;
    }

    @Override
    public boolean isTakingPointerAt(double pointX, double pointY) {
        return super.isTakingPointerAt(pointX, pointY) && !isDescendantTakingPointerAt(pointX, pointY);
    }

    public List<GuiComponent> getMountedComponents() {
        List<GuiComponent> mounted = new ArrayList<>();
        if (isMountingItself()) {
            mounted.add(this);
        }
        for (Slot slot : slots) {
            if (slot.component() instanceof Container container) {
                mounted.addAll(container.getMountedComponents());
            } else {
                mounted.add(slot.component());
            }
        }
        return mounted;
    }

    public boolean areFieldsValid() {
        return areFieldsValid(this);
    }

    public void layout(TextMetrics metrics) {
        List<Slot> shown = getShownSlots();
        List<Size> naturals = shown.stream().map(slot -> slot.component().naturalSize(metrics)).toList();
        int[] lengthsPixels = Distribution.sizes(axis.along(getWidthPixels(), getHeightPixels()) - 2 * paddingPixels, gapPixels, toItems(shown, naturals));
        int offsetPixels = paddingPixels;
        for (int index = 0; index < shown.size(); index++) {
            GuiComponent child = shown.get(index).component();
            place(child, offsetPixels, lengthsPixels[index], naturals.get(index));
            offsetPixels += lengthsPixels[index] + gapPixels;
            if (child instanceof Container container) {
                container.layout(metrics);
            }
        }
    }

    @Override
    public Size naturalSize(TextMetrics metrics) {
        List<Slot> shown = getShownSlots();
        int alongPixels = gapPixels * Math.max(shown.size() - 1, 0) + 2 * paddingPixels;
        int acrossPixels = 0;
        for (Slot slot : shown) {
            Size natural = slot.component().naturalSize(metrics);
            alongPixels += Distribution.preferredSize(toItem(slot, natural));
            acrossPixels = Math.max(acrossPixels, axis.across(natural.widthPixels(), natural.heightPixels()));
        }
        acrossPixels += 2 * paddingPixels;
        return new Size(axis.horizontal(alongPixels, acrossPixels), axis.vertical(alongPixels, acrossPixels));
    }

    @Override
    public Component getTooltipAt(double pointX, double pointY) {
        return isChildShowingTooltipAt(pointX, pointY) ? Component.empty() : getTooltip();
    }

    @Override
    protected void drawContent(Canvas canvas) {
    }

    private boolean isDescendantTakingPointerAt(double pointX, double pointY) {
        for (Slot slot : slots) {
            GuiComponent child = slot.component();
            if (child.isTakingPointerAt(pointX, pointY)
                    || (child instanceof Container container && container.isDescendantTakingPointerAt(pointX, pointY))) {
                return true;
            }
        }
        return false;
    }

    private boolean isChildShowingTooltipAt(double pointX, double pointY) {
        for (Slot slot : slots) {
            GuiComponent child = slot.component();
            if (child.isVisible() && child.isContaining(pointX, pointY) && isShowingTooltipAt(child, pointX, pointY)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isShowingTooltipAt(GuiComponent component, double pointX, double pointY) {
        if (component instanceof Container container) {
            return container.isChildShowingTooltipAt(pointX, pointY)
                    || (container.isMountingItself() && !container.getTooltip().getString().isEmpty());
        }
        return !component.getTooltipAt(pointX, pointY).getString().isEmpty();
    }

    private void place(GuiComponent child, int offsetPixels, int lengthPixels, Size natural) {
        int roomPixels = axis.across(getWidthPixels(), getHeightPixels()) - 2 * paddingPixels;
        int breadthPixels = align == Align.STRETCH ? roomPixels : axis.across(natural.widthPixels(), natural.heightPixels());
        int shiftPixels = paddingPixels + switch (align) {
            case START, STRETCH -> 0;
            case CENTER -> (roomPixels - breadthPixels) / 2;
            case END -> roomPixels - breadthPixels;
        };
        child.setPosition(getX() + axis.horizontal(offsetPixels, shiftPixels), getY() + axis.vertical(offsetPixels, shiftPixels));
        child.setSize(axis.horizontal(lengthPixels, breadthPixels), axis.vertical(lengthPixels, breadthPixels));
    }

    private List<Slot> getShownSlots() {
        return slots.stream().filter(slot -> slot.component().isVisibleByOwnRule()).toList();
    }

    private List<Distribution.Item> toItems(List<Slot> shown, List<Size> naturals) {
        List<Distribution.Item> items = new ArrayList<>(shown.size());
        for (int index = 0; index < shown.size(); index++) {
            items.add(toItem(shown.get(index), naturals.get(index)));
        }
        return items;
    }

    private Distribution.Item toItem(Slot slot, Size natural) {
        return new Distribution.Item(slot.length(), axis.along(natural.widthPixels(), natural.heightPixels()));
    }

    private static boolean areFieldsValid(GuiComponent component) {
        if (component instanceof Container container) {
            return !isCounted(container) || container.slots.stream().allMatch(slot -> areFieldsValid(slot.component()));
        }
        if (component instanceof Validated field) {
            return !isCounted(component) || field.isValid();
        }
        return true;
    }

    private static boolean isCounted(GuiComponent component) {
        return component.isCountedWhenInactive() || (component.isVisibleByOwnRule() && component.isEnabledByOwnRule());
    }
}

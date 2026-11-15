package com.ascendiacore.gui.widget.list;

import java.util.List;
import java.util.function.IntConsumer;

import com.ascendiacore.gui.base.component.GuiComponent;
import com.ascendiacore.gui.base.draw.Canvas;
import com.ascendiacore.gui.base.draw.Icon;
import com.ascendiacore.gui.base.draw.TextMetrics;
import com.ascendiacore.gui.base.geometry.Size;
import com.ascendiacore.gui.base.input.PointerInput;
import com.ascendiacore.gui.base.input.WheelInput;
import com.ascendiacore.gui.widget.list.model.ListRow;
import com.ascendiacore.gui.widget.list.model.ListStyle;
import com.ascendiacore.gui.widget.scrollbar.Scroll;

import net.minecraft.network.chat.Component;

final class ListRows extends GuiComponent {
    static final int ROW_HEIGHT = 22;
    static final int NONE = -1;
    private static final int ROW_PADDING = 3;
    private static final int ICON_GAP = 3;
    private static final int LINE_HEIGHT = 9;

    private final ListStyle style;
    private final int labelColor;
    private final List<ListRow> rows;
    private final Scroll scroll;
    private final IntConsumer onSelect;
    private int selected;

    ListRows(ListStyle style, int labelColor, List<ListRow> rows, Scroll scroll, IntConsumer onSelect) {
        this.style = style;
        this.labelColor = labelColor;
        this.rows = List.copyOf(rows);
        this.scroll = scroll;
        this.onSelect = onSelect;
        this.selected = this.rows.isEmpty() ? NONE : 0;
        scroll.setContent(this.rows.size() * ROW_HEIGHT);
    }

    int selected() {
        return selected;
    }

    int rowAt(double pointX, double pointY) {
        if (!contains(pointX, pointY)) {
            return NONE;
        }
        int row = (int) ((pointY - y() + scroll.offset()) / ROW_HEIGHT);
        return row < rows.size() ? row : NONE;
    }

    @Override
    public Component narration() {
        return selected == NONE ? Component.empty() : rows.get(selected).label();
    }

    @Override
    public void setSize(int width, int height) {
        super.setSize(width, height);
        scroll.setView(height);
    }

    @Override
    public Size naturalSize(TextMetrics metrics) {
        int widest = rows.stream().mapToInt(row -> metrics.textWidth(row.label())).max().orElse(0);
        return new Size(2 * ROW_PADDING + Icon.SIZE + ICON_GAP + widest, rows.size() * ROW_HEIGHT);
    }

    @Override
    protected void drawContent(Canvas canvas) {
        int hoveredRow = hovered() ? rowAt(pointerX(), pointerY()) : NONE;
        canvas.pushClip(x(), y(), width(), height());
        for (int row = scroll.offset() / ROW_HEIGHT; row < rows.size(); row++) {
            int top = y() + row * ROW_HEIGHT - scroll.offset();
            if (top >= y() + height()) {
                break;
            }
            drawRow(canvas, row, top, row == hoveredRow);
        }
        canvas.popClip();
    }

    @Override
    protected void onPress(PointerInput input) {
        int row = rowAt(input.x(), input.y());
        if (row != NONE) {
            selected = row;
            onSelect.accept(row);
        }
    }

    @Override
    protected boolean onWheel(WheelInput input) {
        scroll.wheel(input.amount());
        return true;
    }

    private void drawRow(Canvas canvas, int row, int top, boolean hoveredRow) {
        boolean chosen = row == selected;
        if (chosen || hoveredRow) {
            canvas.sprite(chosen ? style.rowSelected() : style.rowHover(), x(), top, width(), ROW_HEIGHT);
        }
        ListRow entry = rows.get(row);
        int iconLeft = x() + ROW_PADDING;
        entry.icon().draw(canvas, iconLeft, top + (ROW_HEIGHT - Icon.SIZE) / 2);
        canvas.text(entry.label(), iconLeft + Icon.SIZE + ICON_GAP, top + (ROW_HEIGHT - LINE_HEIGHT) / 2 + 1,
                chosen ? style.selectedLabelColor() : labelColor, style.labelShadow());
    }
}

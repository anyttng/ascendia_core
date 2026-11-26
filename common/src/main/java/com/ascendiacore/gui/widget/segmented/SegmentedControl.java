package com.ascendiacore.gui.widget.segmented;

import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.IntStream;

import org.jspecify.annotations.Nullable;

import com.ascendiacore.gui.base.component.GuiComponent;
import com.ascendiacore.gui.base.component.model.TooltipStyle;
import com.ascendiacore.gui.base.draw.Canvas;
import com.ascendiacore.gui.base.draw.Marquee;
import com.ascendiacore.gui.base.draw.TextMetrics;
import com.ascendiacore.gui.base.geometry.Size;
import com.ascendiacore.gui.base.hint.ValueHint;
import com.ascendiacore.gui.base.input.KeyInput;
import com.ascendiacore.gui.base.input.PointerInput;
import com.ascendiacore.gui.base.property.Property;
import com.ascendiacore.gui.base.property.Value;
import com.ascendiacore.gui.widget.button.model.ButtonStyle;

import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public final class SegmentedControl<T> extends GuiComponent {
    private static final int HEIGHT_PIXELS = 20;
    private static final int LINE_HEIGHT_PIXELS = 9;
    private static final int SIDE_PADDING_PIXELS = 8;
    private static final int LABEL_MARGIN_PIXELS = 5;
    private static final int FRAME_COLUMN_PIXELS = 1;
    private static final int OVERDRAW_PIXELS = HEIGHT_PIXELS / 2;
    private static final int NONE = -1;
    private static final int RANK_CHOSEN = 1;
    private static final int RANK_HOVERED = 2;

    private final ButtonStyle style;
    private final Component name;
    private final Property<List<T>> values;
    private final Value<T> chosen;
    private final Function<T, Component> valueName;
    private Consumer<T> onChange = picked -> {
    };
    private @Nullable Function<T, Component> segmentHint;

    public SegmentedControl(ButtonStyle style, Component name, Property<List<T>> values, Value<T> chosen,
            Function<T, Component> valueName) {
        this.style = style;
        this.name = name;
        this.values = values;
        this.chosen = chosen;
        this.valueName = valueName;
    }

    public void setOnChange(Consumer<T> onChange) {
        this.onChange = onChange;
    }

    public void setHint(TooltipStyle style, String captionKey, Function<T, String> valueKey) {
        setTooltip(style, Component.empty());
        segmentHint = value -> ValueHint.ofOneValue(style, captionKey, valueName.apply(value),
                ValueHint.text(captionKey, valueKey.apply(value))).build();
    }

    @Override
    public Component getTooltipAt(double pointX, double pointY) {
        Function<T, Component> hint = segmentHint;
        if (hint == null) {
            return super.getTooltipAt(pointX, pointY);
        }
        if (!isContaining(pointX, pointY)) {
            return hint.apply(chosen.get());
        }
        List<T> list = values.get();
        int index = getSegmentAt(pointX, list.size());
        return index == NONE ? Component.empty() : hint.apply(list.get(index));
    }

    @Override
    public Component getNarration() {
        return CommonComponents.optionNameValue(name, valueName.apply(chosen.get()));
    }

    @Override
    public Size naturalSize(TextMetrics metrics) {
        List<T> list = values.get();
        int widestPixels = list.stream()
                .mapToInt(value -> metrics.textWidthPixels(valueName.apply(value)))
                .max()
                .orElse(0);
        return new Size(list.size() * (widestPixels + 2 * SIDE_PADDING_PIXELS), HEIGHT_PIXELS);
    }

    @Override
    protected void drawContent(Canvas canvas) {
        List<T> list = values.get();
        int chosenIndex = list.indexOf(chosen.get());
        int hoveredIndex = isHovered() && isEnabled() ? getSegmentAt(getPointerX(), list.size()) : NONE;
        IntStream.range(0, list.size()).boxed()
                .sorted(Comparator.comparingInt(index -> getDrawRank(index, chosenIndex, hoveredIndex)))
                .forEach(index -> drawSegment(canvas, list, index, index == chosenIndex, index == hoveredIndex));
    }

    @Override
    protected void onPress(PointerInput input) {
        List<T> list = values.get();
        int index = getSegmentAt(input.x(), list.size());
        if (index != NONE) {
            pick(list, index);
        }
    }

    @Override
    protected boolean tryHandleKey(KeyInput input) {
        int delta = switch (input.key()) {
            case LEFT -> -1;
            case RIGHT -> 1;
            default -> 0;
        };
        if (delta == 0) {
            return false;
        }
        List<T> list = values.get();
        if (!list.isEmpty()) {
            pick(list, Math.clamp(list.indexOf(chosen.get()) + delta, 0, list.size() - 1));
        }
        return true;
    }

    private void pick(List<T> list, int index) {
        T picked = list.get(index);
        if (picked.equals(chosen.get())) {
            return;
        }
        playClick();
        chosen.set(picked);
        onChange.accept(picked);
    }

    private int getSegmentAt(double pointX, int count) {
        for (int index = 0; index < count; index++) {
            if (pointX >= getSegmentLeft(index, count) && pointX < getSegmentLeft(index + 1, count)) {
                return index;
            }
        }
        return NONE;
    }

    private int getSegmentLeft(int index, int count) {
        return getX() + index * getWidthPixels() / count;
    }

    private static int getDrawRank(int index, int chosenIndex, int hoveredIndex) {
        return (index == chosenIndex ? RANK_CHOSEN : 0) + (index == hoveredIndex ? RANK_HOVERED : 0);
    }

    private void drawSegment(Canvas canvas, List<T> list, int index, boolean isChosen, boolean isHovered) {
        int count = list.size();
        boolean isFirst = index == 0;
        boolean isLast = index == count - 1;
        int left = getSegmentLeft(index, count);
        int right = isLast ? getSegmentLeft(count, count) : getSegmentLeft(index + 1, count) + FRAME_COLUMN_PIXELS;
        String sprite = getSprite(isChosen, isHovered);

        int bodyLeft = isFirst ? left : left - OVERDRAW_PIXELS;
        int bodyRight = isLast ? right : right + OVERDRAW_PIXELS;
        canvas.pushClip(left, getY(), right - left, getHeightPixels());
        canvas.sprite(sprite, bodyLeft, getY(), bodyRight - bodyLeft, getHeightPixels());
        canvas.popClip();
        if (!isFirst) {
            drawFrameColumn(canvas, sprite, left, left, right);
        }
        if (!isLast) {
            drawFrameColumn(canvas, sprite, right - FRAME_COLUMN_PIXELS, left, right);
        }
        drawLabel(canvas, valueName.apply(list.get(index)), left, right, isChosen);
    }

    private void drawFrameColumn(Canvas canvas, String sprite, int columnX, int left, int right) {
        canvas.pushClip(columnX, getY(), FRAME_COLUMN_PIXELS, getHeightPixels());
        canvas.sprite(sprite, left, getY(), right - left, getHeightPixels());
        canvas.popClip();
    }

    private void drawLabel(Canvas canvas, Component text, int left, int right, boolean isChosen) {
        int textWidthPixels = canvas.textWidthPixels(text);
        int roomPixels = right - left - 2 * LABEL_MARGIN_PIXELS;
        int textY = getY() + (getHeightPixels() - LINE_HEIGHT_PIXELS) / 2 + 1;
        int color = getLabelColor(isChosen);
        if (textWidthPixels <= roomPixels) {
            canvas.text(text, left + (right - left - textWidthPixels) / 2, textY, color, style.hasLabelShadow());
            return;
        }
        int textLeft = left + LABEL_MARGIN_PIXELS;
        canvas.pushClip(textLeft, getY(), roomPixels, getHeightPixels());
        canvas.text(text, textLeft - Marquee.offsetPixels(textWidthPixels, roomPixels, canvas.getFrameTimeMillis()), textY,
                color, style.hasLabelShadow());
        canvas.popClip();
    }

    private int getLabelColor(boolean isChosen) {
        if (!isEnabled()) {
            return isChosen ? style.labelColor() : style.disabledLabelColor();
        }
        return isChosen ? style.selectedLabelColor() : style.labelColor();
    }

    private String getSprite(boolean isChosen, boolean isHovered) {
        if (!isEnabled()) {
            return style.disabled();
        }
        if (isChosen) {
            return isHovered || isFocused() ? style.selectedHover() : style.selected();
        }
        return isHovered ? style.hover() : style.normal();
    }
}

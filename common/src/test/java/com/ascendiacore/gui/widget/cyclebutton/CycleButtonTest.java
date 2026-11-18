package com.ascendiacore.gui.widget.cyclebutton;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import com.ascendiacore.gui.base.component.model.AlertColors;
import com.ascendiacore.gui.base.component.model.LevelColors;
import com.ascendiacore.gui.base.component.model.TooltipStyle;
import com.ascendiacore.gui.base.draw.RecordingCanvas;
import com.ascendiacore.gui.base.hint.Hint;
import com.ascendiacore.gui.base.hint.model.HintValue;
import com.ascendiacore.gui.base.input.KeyInput;
import com.ascendiacore.gui.base.input.PointerInput;
import com.ascendiacore.gui.base.input.model.Key;
import com.ascendiacore.gui.base.input.model.Modifiers;
import com.ascendiacore.gui.base.input.model.PointerButton;
import com.ascendiacore.gui.base.property.Property;
import com.ascendiacore.gui.base.property.Value;
import com.ascendiacore.gui.widget.button.Button;
import com.ascendiacore.gui.widget.button.model.ButtonStyle;

import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

class CycleButtonTest {
    private enum Shape {
        NORMAL,
        TOROIDAL,
        CYLINDER
    }

    private static final ButtonStyle STYLE = new ButtonStyle("test:normal", "test:hover", "test:disabled",
            "test:selected", "test:selected_hover", 0xFF111111, 0xFF222222, 0xFF333333, false);
    private static final Component NAME = Component.literal("Shape");
    private static final TooltipStyle TOOLTIP = new TooltipStyle(null, 0xFFFFFFFF, 0xFFFFFF55, 0xFFAAAAAA,
            new AlertColors(0xFFFFAA00, 0xFFFF5555, 0xFFA0A0FF), new LevelColors(0xFF55FF55, 0xFFFFFF55, 0xFFFF5555),
            0xFF55FF55, 0xFF5A5A5A);
    private static final String CAPTION_KEY = "gui.example.shape";
    private static final String PREVIOUS_VALUE_KEY = "gui.ascendia_core.cycle_button.previous_value";
    private static final String WARNING = "Most structures do not fit.";
    private static final Modifiers SHIFT = new Modifiers(true, false, false);
    private static final int LEFT = 10;
    private static final int TOP = 20;
    private static final int WIDTH = 150;
    private static final int HEIGHT = 20;
    private static final int CHAR_WIDTH = 6;

    private final List<Shape> reported = new ArrayList<>();

    @Test
    void clickStepsForwardAndWrapsAround() {
        Value<Shape> chosen = Value.of(Shape.NORMAL);
        CycleButton<Shape> cycle = cycle(chosen);
        List<Shape> seen = new ArrayList<>();
        for (int press = 0; press < Shape.values().length; press++) {
            click(cycle, Modifiers.NONE);
            seen.add(chosen.get());
        }
        assertEquals(List.of(Shape.TOROIDAL, Shape.CYLINDER, Shape.NORMAL), seen);
    }

    @Test
    void shiftClickStepsBack() {
        Value<Shape> chosen = Value.of(Shape.NORMAL);
        click(cycle(chosen), SHIFT);
        assertEquals(Shape.CYLINDER, chosen.get());
    }

    @Test
    void enterStepsForwardAndShiftEnterBack() {
        Value<Shape> chosen = Value.of(Shape.TOROIDAL);
        CycleButton<Shape> cycle = cycle(chosen);
        button(cycle).keyPressed(new KeyInput(Key.ENTER, Modifiers.NONE));
        assertEquals(Shape.CYLINDER, chosen.get());
        button(cycle).keyPressed(new KeyInput(Key.ENTER, SHIFT));
        assertEquals(Shape.TOROIDAL, chosen.get());
    }

    @Test
    void choiceOutsideTheValuesStepsToTheFirstOrTheLast() {
        Value<Shape> chosen = Value.of(Shape.NORMAL);
        CycleButton<Shape> cycle = cycle(chosen, Property.of(List.of(Shape.TOROIDAL, Shape.CYLINDER)));
        click(cycle, Modifiers.NONE);
        assertEquals(Shape.TOROIDAL, chosen.get());
        chosen.set(Shape.NORMAL);
        click(cycle, SHIFT);
        assertEquals(Shape.CYLINDER, chosen.get());
    }

    @Test
    void valuesFollowTheirRule() {
        Value<Shape> chosen = Value.of(Shape.NORMAL);
        Value<List<Shape>> values = Value.of(List.of(Shape.values()));
        CycleButton<Shape> cycle = cycle(chosen, values);
        values.set(List.of(Shape.NORMAL, Shape.CYLINDER));
        click(cycle, Modifiers.NONE);
        assertEquals(Shape.CYLINDER, chosen.get());
    }

    @Test
    void noValuesLeaveTheChoiceAndReportNothing() {
        Value<Shape> chosen = Value.of(Shape.NORMAL);
        click(cycle(chosen, Property.of(List.of())), Modifiers.NONE);
        assertEquals(Shape.NORMAL, chosen.get());
        assertEquals(List.of(), reported);
    }

    @Test
    void eachStepReportsThePickedValue() {
        CycleButton<Shape> cycle = cycle(Value.of(Shape.NORMAL));
        click(cycle, Modifiers.NONE);
        click(cycle, SHIFT);
        assertEquals(List.of(Shape.TOROIDAL, Shape.NORMAL), reported);
    }

    @Test
    void labelNamesTheOptionAndTheValue() {
        CycleButton<Shape> cycle = cycle(Value.of(Shape.TOROIDAL));
        assertEquals(CommonComponents.optionNameValue(NAME, valueName(Shape.TOROIDAL)), button(cycle).narration());
    }

    @Test
    void valueOnlyLabelShowsTheValueAlone() {
        CycleButton<Shape> cycle = cycle(Value.of(Shape.TOROIDAL));
        cycle.showValueOnly();
        assertEquals(valueName(Shape.TOROIDAL), button(cycle).narration());
    }

    @Test
    void labelFollowsAChoiceMadeElsewhere() {
        Value<Shape> chosen = Value.of(Shape.NORMAL);
        CycleButton<Shape> cycle = cycle(chosen);
        chosen.set(Shape.CYLINDER);
        assertEquals(CommonComponents.optionNameValue(NAME, valueName(Shape.CYLINDER)), button(cycle).narration());
    }

    @Test
    void hintListsEveryValueUnderTheNameTheButtonShows() {
        CycleButton<Shape> cycle = cycle(Value.of(Shape.NORMAL));
        cycle.setHint(TOOLTIP, CAPTION_KEY, CycleButtonTest::valueKey);
        assertEquals(hint(Shape.NORMAL, Shape.values()).build(), button(cycle).tooltip());
        assertEquals(TOOLTIP, button(cycle).tooltipStyle());
    }

    @Test
    void hintFollowsTheValuesRule() {
        Value<List<Shape>> values = Value.of(List.of(Shape.values()));
        CycleButton<Shape> cycle = cycle(Value.of(Shape.NORMAL), values);
        cycle.setHint(TOOLTIP, CAPTION_KEY, CycleButtonTest::valueKey);
        values.set(List.of(Shape.NORMAL, Shape.CYLINDER));
        assertEquals(hint(Shape.NORMAL, Shape.NORMAL, Shape.CYLINDER).build(), button(cycle).tooltip());
    }

    @Test
    void hintMarksTheChosenValueAsCurrent() {
        Value<Shape> chosen = Value.of(Shape.NORMAL);
        CycleButton<Shape> cycle = cycle(chosen);
        cycle.setHint(TOOLTIP, CAPTION_KEY, CycleButtonTest::valueKey);
        chosen.set(Shape.TOROIDAL);
        assertEquals(hint(Shape.TOROIDAL, Shape.values()).build(), button(cycle).tooltip());
    }

    @Test
    void hintOfASingleValueNamesNoKey() {
        CycleButton<Shape> cycle = cycle(Value.of(Shape.NORMAL), Property.of(List.of(Shape.NORMAL)));
        cycle.setHint(TOOLTIP, CAPTION_KEY, CycleButtonTest::valueKey);
        assertEquals(Hint.of(TOOLTIP).values(hintValues(Shape.NORMAL, Shape.NORMAL)).build(), button(cycle).tooltip());
    }

    @Test
    void hintTakesTheOtherKindsItsAuthorAdds() {
        CycleButton<Shape> cycle = cycle(Value.of(Shape.NORMAL));
        cycle.setHint(TOOLTIP, CAPTION_KEY, CycleButtonTest::valueKey, hint -> hint.warning(Component.literal(WARNING)));
        assertEquals(hint(Shape.NORMAL, Shape.values()).warning(Component.literal(WARNING)).build(), button(cycle).tooltip());
    }

    private static Hint hint(Shape current, Shape... shapes) {
        return Hint.of(TOOLTIP).values(hintValues(current, shapes)).keyHint(Component.translatable(PREVIOUS_VALUE_KEY));
    }

    private static List<HintValue> hintValues(Shape current, Shape... shapes) {
        return Arrays.stream(shapes).map(shape -> new HintValue(valueName(shape),
                Component.translatable(CAPTION_KEY + ".hint." + valueKey(shape)), shape == current)).toList();
    }

    private static String valueKey(Shape shape) {
        return shape.name().toLowerCase(Locale.ROOT);
    }

    private CycleButton<Shape> cycle(Value<Shape> chosen) {
        return cycle(chosen, Property.of(List.of(Shape.values())));
    }

    private CycleButton<Shape> cycle(Value<Shape> chosen, Property<List<Shape>> values) {
        CycleButton<Shape> cycle = new CycleButton<>(STYLE, NAME, values, chosen, CycleButtonTest::valueName);
        cycle.setOnChange(reported::add);
        RecordingCanvas canvas = new RecordingCanvas();
        canvas.setCharWidth(CHAR_WIDTH);
        cycle.setPosition(LEFT, TOP);
        cycle.setSize(WIDTH, HEIGHT);
        cycle.layout(canvas);
        return cycle;
    }

    private static void click(CycleButton<Shape> cycle, Modifiers modifiers) {
        button(cycle).pointerPressed(new PointerInput(LEFT + 1, TOP + 1, PointerButton.LEFT, modifiers));
    }

    private static Button button(CycleButton<Shape> cycle) {
        return (Button) cycle.mountedComponents().getFirst();
    }

    private static Component valueName(Shape shape) {
        return Component.literal(shape.name());
    }
}

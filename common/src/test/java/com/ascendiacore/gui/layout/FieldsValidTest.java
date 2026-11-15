package com.ascendiacore.gui.layout;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.ascendiacore.gui.base.component.GuiComponent;
import com.ascendiacore.gui.base.draw.Canvas;
import com.ascendiacore.gui.base.form.Validated;
import com.ascendiacore.gui.base.property.Property;
import com.ascendiacore.gui.base.property.Value;
import com.ascendiacore.gui.widget.button.Button;
import com.ascendiacore.gui.widget.button.model.ButtonSprites;

import net.minecraft.network.chat.Component;

class FieldsValidTest {
    @Test
    void validWhenEveryFieldIs() {
        Column form = new Column();
        form.add(new Box(0, 0));
        form.add(new Box(0, 0));
        assertTrue(form.fieldsValid());
    }

    @Test
    void aButtonOfTheFormCanBeEnabledByTheFormsOwnValidity() {
        Column form = new Column();
        Value<Boolean> valid = Value.of(true);
        Button done = Button.text(ButtonSprites.GAME, Component.literal("Done"), () -> {
        });
        done.setEnabled(form::fieldsValid);
        form.add(new RuleField(valid));
        form.add(done);
        assertTrue(done.enabled());
        valid.set(false);
        assertFalse(done.enabled());
    }

    @Test
    void oneInvalidFieldAnywhereInTheTreeIsEnough() {
        Column form = new Column();
        Row inner = new Row();
        inner.add(Box.invalid());
        form.add(new Box(0, 0));
        form.add(inner);
        assertFalse(form.fieldsValid());
    }

    @Test
    void hiddenInvalidFieldIsNotCounted() {
        Column form = new Column();
        Box field = Box.invalid();
        field.setVisible(false);
        form.add(field);
        assertTrue(form.fieldsValid());
    }

    @Test
    void disabledInvalidFieldIsNotCounted() {
        Column form = new Column();
        Box field = Box.invalid();
        field.setEnabled(false);
        form.add(field);
        assertTrue(form.fieldsValid());
    }

    @Test
    void fieldInsideAHiddenContainerIsNotCounted() {
        Column form = new Column();
        Column section = new Column();
        section.setVisible(false);
        section.add(Box.invalid());
        form.add(section);
        assertTrue(form.fieldsValid());
    }

    @Test
    void fieldInsideAHiddenContainerThatStillCountsIsCounted() {
        Column form = new Column();
        Column page = offScreenPage();
        page.add(Box.invalid());
        form.add(page);
        assertFalse(form.fieldsValid());
    }

    @Test
    void fieldHiddenByItsOwnRuleStaysOutEvenOnAPageThatStillCounts() {
        Column form = new Column();
        Column page = offScreenPage();
        Box field = Box.invalid();
        field.setVisible(false);
        page.add(field);
        form.add(page);
        assertTrue(form.fieldsValid());
    }

    private static final class RuleField extends GuiComponent implements Validated {
        private final Property<Boolean> valid;

        private RuleField(Property<Boolean> valid) {
            this.valid = valid;
        }

        @Override
        public boolean valid() {
            return valid.get();
        }

        @Override
        protected void drawContent(Canvas canvas) {
        }
    }

    private static Column offScreenPage() {
        Column page = new Column();
        page.setVisible(false);
        page.setCountedWhenInactive(true);
        return page;
    }
}

package com.ascendiacore.gui.theme;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.ascendiacore.gui.theme.definition.ThemeResolver;
import com.ascendiacore.gui.theme.definition.model.ButtonSection;
import com.ascendiacore.gui.theme.definition.model.ColorRoles;
import com.ascendiacore.gui.theme.definition.model.FillOverride;
import com.ascendiacore.gui.theme.definition.model.FillsOverride;
import com.ascendiacore.gui.theme.definition.model.ListSection;
import com.ascendiacore.gui.theme.definition.model.PanelSection;
import com.ascendiacore.gui.theme.definition.model.TextFieldSection;
import com.ascendiacore.gui.theme.definition.model.ThemeCodecs;
import com.ascendiacore.gui.theme.definition.model.ThemeDefinition;
import com.ascendiacore.gui.theme.definition.model.TooltipSection;
import com.ascendiacore.gui.widget.panel.model.FillKind;
import com.ascendiacore.gui.widget.link.model.LinkColors;
import com.ascendiacore.gui.widget.panel.model.FillStyle;

class ThemeContrastTest {
    private static final double TEXT_RATIO = 4.5;
    private static final double MUTED_RATIO = 3.0;
    private static final double LUMINANCE_OFFSET = 0.05;
    private static final double RED_WEIGHT = 0.2126;
    private static final double GREEN_WEIGHT = 0.7152;
    private static final double BLUE_WEIGHT = 0.0722;
    private static final double LINEAR_LIMIT = 0.03928;
    private static final double LINEAR_DIVISOR = 12.92;
    private static final double GAMMA_OFFSET = 0.055;
    private static final double GAMMA_DIVISOR = 1.055;
    private static final double GAMMA = 2.4;
    private static final int CHANNEL_MAX = 0xFF;
    private static final int RED_SHIFT = 16;
    private static final int GREEN_SHIFT = 8;
    private static final int RGB_MASK = 0xFFFFFF;

    @Test
    void everyAuthoredTextColorReadsOnItsFill() {
        List<String> ids = TestThemes.listOwnIds();
        assertFalse(ids.isEmpty());
        List<String> failures = new ArrayList<>();
        for (String id : ids) {
            new ThemeReading(id, TestThemes.readDefinition(id), failures).measure();
        }
        assertEquals(List.of(), failures);
    }

    @Test
    void blackOnWhiteIsTheFullRatioAndAColorOnItselfIsOne() {
        assertEquals(21.0, contrastRatio(0xFF000000, 0xFFFFFFFF), 0.001);
        assertEquals(1.0, contrastRatio(0xFF8B8B8B, 0xFF8B8B8B), 0.001);
    }

    private static double contrastRatio(int ink, int fill) {
        double inkLuminance = relativeLuminance(ink);
        double fillLuminance = relativeLuminance(fill);
        return (Math.max(inkLuminance, fillLuminance) + LUMINANCE_OFFSET)
                / (Math.min(inkLuminance, fillLuminance) + LUMINANCE_OFFSET);
    }

    private static double relativeLuminance(int color) {
        return RED_WEIGHT * linearChannel(color >> RED_SHIFT)
                + GREEN_WEIGHT * linearChannel(color >> GREEN_SHIFT)
                + BLUE_WEIGHT * linearChannel(color);
    }

    private static double linearChannel(int shifted) {
        double channel = (shifted & CHANNEL_MAX) / (double) CHANNEL_MAX;
        return channel <= LINEAR_LIMIT ? channel / LINEAR_DIVISOR : Math.pow((channel + GAMMA_OFFSET) / GAMMA_DIVISOR, GAMMA);
    }

    private static final class ThemeReading {
        private final String id;
        private final ThemeDefinition definition;
        private final ColorRoles roles;
        private final Theme theme;
        private final List<String> failures;

        private ThemeReading(String id, ThemeDefinition definition, List<String> failures) {
            this.id = id;
            this.definition = definition;
            this.roles = definition.roles();
            this.theme = ThemeResolver.resolve(id, definition);
            this.failures = failures;
        }

        private void measure() {
            measureRoles();
            measureButton(definition.button());
            measurePanel(definition.panel());
            measureFills("inset", definition.inset().fills(), Optional.empty());
            measureLink("link.on_light", FillKind.PARCHMENT);
            measureLink("link.on_mid", FillKind.PAGE);
            measureLink("link.on_dark", FillKind.DARK);
            measureList(definition.list(), definition.button().surface());
            measureTooltip(definition.tooltip());
            measureTextField(definition.textField());
        }

        private void measureRoles() {
            measureSet("roles.light", roles.light());
            measureSet("roles.mid", roles.mid());
            measureSet("roles.dark", roles.dark());
            int dark = roles.dark().color();
            measureText("roles.accent", roles.accent(), dark);
            measureText("roles.accent_hover", roles.accentHover(), dark);
            measureText("roles.success", roles.success(), dark);
            measureText("roles.warning", roles.warning(), dark);
            measureText("roles.danger", roles.danger(), dark);
            measureText("roles.requirement", roles.requirement(), dark);
            measureText("roles.derived", roles.derived(), dark);
        }

        private void measureSet(String name, FillStyle set) {
            measureText(name + ".text", set.ink(), set.color());
            measureMuted(name + ".muted", set.mutedInk(), set.color());
            measureText(name + ".error", set.errorInk(), set.color());
        }

        private void measureButton(ButtonSection section) {
            int fill = roles.getSurface(section.surface()).color();
            section.label().ifPresent(ink -> measureText("button.label", ink, fill));
            section.disabledLabel().ifPresent(ink -> measureMuted("button.disabled_label", ink, fill));
            section.selectedLabel().ifPresent(ink -> measureText("button.selected_label", ink, roles.dark().color()));
        }

        private void measurePanel(PanelSection section) {
            Optional<FillStyle> spriteSurface = section.surface().map(roles::getSurface);
            measureFills("panel", section.fills(), spriteSurface);
        }

        private void measureFills(String name, FillsOverride fills, Optional<FillStyle> spriteSurface) {
            measureOverriddenSet(name + "." + ThemeCodecs.LIGHT_KEY, fills.light(), roles.light(), spriteSurface);
            measureOverriddenSet(name + "." + ThemeCodecs.MID_KEY, fills.mid(), roles.mid(), spriteSurface);
            measureOverriddenSet(name + "." + ThemeCodecs.DARK_KEY, fills.dark(), roles.dark(), spriteSurface);
        }

        private void measureOverriddenSet(String name, FillOverride override, FillStyle role,
                Optional<FillStyle> spriteSurface) {
            if (override.equals(FillOverride.NONE)) {
                return;
            }
            FillStyle set = override.applyTo(spriteSurface.orElse(role));
            if (set.isDrawn()) {
                measureSet(name, set);
            }
        }

        private void measureLink(String name, FillKind surface) {
            LinkColors colors = theme.link().getColors(surface);
            int fill = roles.getSurface(surface).color();
            measureText(name + ".normal", colors.normal(), fill);
            measureText(name + ".hover", colors.hover(), fill);
            measureMuted(name + ".disabled", colors.disabled(), fill);
        }

        private void measureList(ListSection section, FillKind buttonSurface) {
            measureTextOverride("list.framed_text", section.framedText(), section.surface());
            measureTextOverride("list.framed_selected_text", section.framedSelectedText(), section.selectedSurface());
            measureTextOverride("list.button_text", section.buttonText(), buttonSurface);
            measureTextOverride("list.button_selected_text", section.buttonSelectedText(), section.selectedSurface());
        }

        private void measureTextOverride(String name, FillOverride override, FillKind surface) {
            int fill = roles.getSurface(surface).color();
            override.text().ifPresent(ink -> measureText(name + ".text", ink, fill));
            override.muted().ifPresent(ink -> measureMuted(name + ".muted", ink, fill));
            override.error().ifPresent(ink -> measureText(name + ".error", ink, fill));
        }

        private void measureTooltip(TooltipSection section) {
            int fill = roles.getSurface(section.surface()).color();
            section.text().ifPresent(ink -> measureText("tooltip.text", ink, fill));
            section.value().ifPresent(ink -> measureText("tooltip.value", ink, fill));
            section.note().ifPresent(ink -> measureMuted("tooltip.note", ink, fill));
        }

        private void measureTextField(TextFieldSection section) {
            int fill = roles.getSurface(section.surface()).color();
            section.text().ifPresent(ink -> measureText("text_field.text", ink, fill));
            section.disabledText().ifPresent(ink -> measureMuted("text_field.disabled_text", ink, fill));
            section.placeholder().ifPresent(ink -> measureMuted("text_field.placeholder", ink, fill));
        }

        private void measureText(String name, int ink, int fill) {
            measure(name, ink, fill, TEXT_RATIO);
        }

        private void measureMuted(String name, int ink, int fill) {
            measure(name, ink, fill, MUTED_RATIO);
        }

        private void measure(String name, int ink, int fill, double requiredRatio) {
            double ratio = contrastRatio(ink, fill);
            if (ratio < requiredRatio) {
                failures.add(String.format(Locale.ROOT, "%s %s #%06X on #%06X: %.2f, needs %.1f", id, name,
                        ink & RGB_MASK, fill & RGB_MASK, ratio, requiredRatio));
            }
        }
    }
}

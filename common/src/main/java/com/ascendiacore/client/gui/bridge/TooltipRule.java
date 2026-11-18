package com.ascendiacore.client.gui.bridge;

import java.util.Optional;

import com.ascendiacore.gui.base.hint.Hint;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

final class TooltipRule {
    private TooltipRule() {
    }

    static Component stretched(Minecraft minecraft, Component text) {
        if (!hasRule(text)) {
            return text;
        }
        Font font = minecraft.font;
        int width = Tooltip.splitTooltip(minecraft, text).stream().mapToInt(font::width).max().orElse(0);
        MutableComponent stretched = Component.empty();
        text.visit((style, segment) -> {
            String shown = isRule(style, segment) ? rule(font, style, width) : segment;
            stretched.append(Component.literal(shown).withStyle(style));
            return Optional.empty();
        }, Style.EMPTY);
        return stretched;
    }

    private static boolean hasRule(Component text) {
        return text.visit((style, segment) -> isRule(style, segment) ? Optional.of(segment) : Optional.empty(), Style.EMPTY)
                .isPresent();
    }

    private static boolean isRule(Style style, String segment) {
        return style.isStrikethrough() && segment.equals(Hint.RULE);
    }

    private static String rule(Font font, Style style, int width) {
        int step = font.width(Component.literal(Hint.RULE).withStyle(style));
        return step <= 0 ? Hint.RULE : Hint.RULE.repeat(Math.max(1, Math.ceilDiv(width, step)));
    }
}

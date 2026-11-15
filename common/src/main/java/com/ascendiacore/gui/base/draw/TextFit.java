package com.ascendiacore.gui.base.draw;

import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public final class TextFit {
    private TextFit() {
    }

    public static Component fit(Component text, int room, TextMetrics metrics) {
        if (metrics.textWidth(text) <= room) {
            return text;
        }
        String plain = text.getString();
        int fitting = 0;
        int above = plain.codePointCount(0, plain.length()) + 1;
        while (above - fitting > 1) {
            int middle = (fitting + above) >>> 1;
            if (metrics.textWidth(cut(text, plain, middle)) <= room) {
                fitting = middle;
            } else {
                above = middle;
            }
        }
        Component shortest = cut(text, plain, fitting);
        return metrics.textWidth(shortest) <= room ? shortest : Component.empty();
    }

    private static Component cut(Component text, String plain, int codePoints) {
        String prefix = plain.substring(0, plain.offsetByCodePoints(0, codePoints));
        return Component.literal(prefix).withStyle(text.getStyle()).append(CommonComponents.ELLIPSIS);
    }
}

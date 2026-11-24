package com.ascendiacore.gui.base.draw;

import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public final class TextFit {
    private TextFit() {
    }

    public static Component fit(Component text, int roomPixels, TextMetrics metrics) {
        if (metrics.textWidthPixels(text) <= roomPixels) {
            return text;
        }
        String plain = text.getString();
        int fittingCodePoints = 0;
        int tooManyCodePoints = plain.codePointCount(0, plain.length()) + 1;
        while (tooManyCodePoints - fittingCodePoints > 1) {
            int middleCodePoints = (fittingCodePoints + tooManyCodePoints) >>> 1;
            if (metrics.textWidthPixels(cut(text, plain, middleCodePoints)) <= roomPixels) {
                fittingCodePoints = middleCodePoints;
            } else {
                tooManyCodePoints = middleCodePoints;
            }
        }
        Component shortest = cut(text, plain, fittingCodePoints);
        return metrics.textWidthPixels(shortest) <= roomPixels ? shortest : Component.empty();
    }

    private static Component cut(Component text, String plain, int codePoints) {
        String prefix = plain.substring(0, plain.offsetByCodePoints(0, codePoints));
        return Component.literal(prefix).withStyle(text.getStyle()).append(CommonComponents.ELLIPSIS);
    }
}

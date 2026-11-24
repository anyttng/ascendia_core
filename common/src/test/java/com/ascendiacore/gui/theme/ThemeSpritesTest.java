package com.ascendiacore.gui.theme;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.lang.reflect.RecordComponent;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.ascendiacore.gui.base.component.model.TooltipStyle;

class ThemeSpritesTest {
    private static final String OWN_NAMESPACE = "ascendia_core:";
    private static final String SPRITE_ROOT = "/assets/ascendia_core/textures/gui/sprites/";
    private static final String TOOLTIP_FOLDER = "tooltip/";
    private static final String TOOLTIP_BACKGROUND_SUFFIX = "_background";
    private static final String TOOLTIP_FRAME_SUFFIX = "_frame";
    private static final String PNG = ".png";
    private static final String MCMETA = ".png.mcmeta";
    private static final List<Theme> THEMES = List.of(Themes.VANILLA, Themes.BOOK);

    @Test
    void everyOwnSpriteAThemeNamesShipsWithItsMcmeta() {
        List<String> missing = new ArrayList<>();
        for (Theme theme : THEMES) {
            List<String> sprites = new ArrayList<>();
            collectSprites(theme, sprites);
            assertFalse(sprites.isEmpty(), theme.name());
            for (String sprite : sprites) {
                String path = SPRITE_ROOT + sprite.substring(OWN_NAMESPACE.length());
                if (ThemeSpritesTest.class.getResource(path + PNG) == null
                        || ThemeSpritesTest.class.getResource(path + MCMETA) == null) {
                    missing.add(theme.name() + " " + sprite);
                }
            }
        }
        assertEquals(List.of(), missing);
    }

    private static void collectSprites(Record style, List<String> sprites) {
        if (style instanceof TooltipStyle tooltip) {
            String id = tooltip.sprites();
            if (id != null && id.startsWith(OWN_NAMESPACE)) {
                String path = id.substring(OWN_NAMESPACE.length());
                sprites.add(OWN_NAMESPACE + TOOLTIP_FOLDER + path + TOOLTIP_BACKGROUND_SUFFIX);
                sprites.add(OWN_NAMESPACE + TOOLTIP_FOLDER + path + TOOLTIP_FRAME_SUFFIX);
            }
            return;
        }
        for (RecordComponent component : style.getClass().getRecordComponents()) {
            Object value = read(style, component);
            if (value instanceof Record nested) {
                collectSprites(nested, sprites);
            } else if (value instanceof String text && text.startsWith(OWN_NAMESPACE)) {
                sprites.add(text);
            }
        }
    }

    private static Object read(Record style, RecordComponent component) {
        try {
            return component.getAccessor().invoke(style);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(component.getName(), e);
        }
    }
}

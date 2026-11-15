package com.ascendiacore.gui.theme;

import com.ascendiacore.gui.widget.button.model.ButtonStyle;

public final class Themes {
    public static final Theme VANILLA = new Theme("vanilla", new ButtonStyle(
            "minecraft:widget/button",
            "minecraft:widget/button_highlighted",
            "minecraft:widget/button_disabled",
            0xFFFFFFFF,
            0xFFA0A0A0,
            true));

    public static final Theme BOOK = new Theme("book", new ButtonStyle(
            "ascendia_core:theme/book/button",
            "ascendia_core:theme/book/button_hover",
            "ascendia_core:theme/book/button_disabled",
            0xFF4C1A0B,
            0xFFA89A85,
            false));

    private Themes() {
    }
}

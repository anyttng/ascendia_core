package com.ascendiacore.gui.theme;

import com.ascendiacore.gui.widget.button.model.ButtonStyle;
import com.ascendiacore.gui.widget.panel.model.FillStyle;
import com.ascendiacore.gui.widget.panel.model.Fills;
import com.ascendiacore.gui.widget.panel.model.FrameStyle;
import com.ascendiacore.gui.widget.panel.model.InsetStyle;
import com.ascendiacore.gui.widget.panel.model.PanelStyle;

public final class Themes {
    private static final int WHITE = 0xFFFFFFFF;
    private static final String VANILLA_SPRITES = "ascendia_core:theme/vanilla/";
    private static final String BOOK_SPRITES = "ascendia_core:theme/book/";

    public static final Theme VANILLA = new Theme(
            "vanilla",
            new ButtonStyle(
                    "minecraft:widget/button",
                    "minecraft:widget/button_highlighted",
                    "minecraft:widget/button_disabled",
                    WHITE,
                    0xFFA0A0A0,
                    true),
            new PanelStyle(new FrameStyle("minecraft:popup/background", 6), spriteOwnFill(WHITE)),
            new InsetStyle(
                    new FrameStyle(VANILLA_SPRITES + "inset_light", 3),
                    new FrameStyle(VANILLA_SPRITES + "inset_dark", 4),
                    new FrameStyle(VANILLA_SPRITES + "inset_rim", 4),
                    new Fills(
                            new FillStyle(0xFFC6C6C6, 0xFF3F3F3F),
                            new FillStyle(0xFF8B8B8B, WHITE),
                            new FillStyle(0xFF373737, WHITE))));

    public static final Theme BOOK = new Theme(
            "book",
            new ButtonStyle(
                    BOOK_SPRITES + "button",
                    BOOK_SPRITES + "button_hover",
                    BOOK_SPRITES + "button_disabled",
                    0xFF4C1A0B,
                    0xFFA89A85,
                    false),
            new PanelStyle(new FrameStyle(BOOK_SPRITES + "panel_cover", 8), bookFills()),
            new InsetStyle(
                    new FrameStyle(BOOK_SPRITES + "inset_light", 3),
                    new FrameStyle(BOOK_SPRITES + "inset_dark", 4),
                    new FrameStyle(BOOK_SPRITES + "inset_rim", 4),
                    bookFills()));

    private Themes() {
    }

    private static Fills bookFills() {
        return new Fills(
                new FillStyle(0xFFFBF3DF, 0xFF4C1A0B),
                new FillStyle(0xFFF6E6BD, 0xFF534E47),
                new FillStyle(0xFF534E47, 0xFFFBF3DF));
    }

    private static Fills spriteOwnFill(int ink) {
        FillStyle none = new FillStyle(FillStyle.NO_FILL, ink);
        return new Fills(none, none, none);
    }
}

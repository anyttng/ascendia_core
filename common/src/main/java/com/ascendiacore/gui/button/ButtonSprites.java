package com.ascendiacore.gui.button;

public record ButtonSprites(String normal, String hover, String disabled) {
    public static final ButtonSprites GAME = new ButtonSprites(
            "minecraft:widget/button", "minecraft:widget/button_highlighted", "minecraft:widget/button_disabled");
}

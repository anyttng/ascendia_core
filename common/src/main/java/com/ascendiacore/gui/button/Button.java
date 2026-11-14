package com.ascendiacore.gui.button;

import org.jspecify.annotations.Nullable;

import com.ascendiacore.gui.component.GuiComponent;
import com.ascendiacore.gui.draw.Canvas;
import com.ascendiacore.gui.icon.Icon;
import com.ascendiacore.gui.input.Key;
import com.ascendiacore.gui.input.KeyInput;
import com.ascendiacore.gui.input.PointerInput;

import net.minecraft.network.chat.Component;

public final class Button extends GuiComponent {
    private static final int LINE_HEIGHT = 9;
    private static final int ICON_GAP = 4;
    private static final int TEXT_COLOR = 0xFFFFFFFF;
    private static final int DISABLED_TEXT_COLOR = 0xFFA0A0A0;

    private final ButtonSprites sprites;
    private final Component label;
    private final boolean labelDrawn;
    private final @Nullable Icon icon;
    private final Runnable action;

    private Button(ButtonSprites sprites, Component label, boolean labelDrawn, @Nullable Icon icon, Runnable action) {
        this.sprites = sprites;
        this.label = label;
        this.labelDrawn = labelDrawn;
        this.icon = icon;
        this.action = action;
    }

    public static Button text(ButtonSprites sprites, Component label, Runnable action) {
        return new Button(sprites, label, true, null, action);
    }

    public static Button icon(ButtonSprites sprites, Icon icon, Component name, Runnable action) {
        return new Button(sprites, name, false, icon, action);
    }

    public static Button iconAndText(ButtonSprites sprites, Icon icon, Component label, Runnable action) {
        return new Button(sprites, label, true, icon, action);
    }

    @Override
    public Component narration() {
        return label;
    }

    @Override
    protected void drawContent(Canvas canvas) {
        canvas.sprite(sprite(), x(), y(), width(), height());
        int textWidth = labelDrawn ? canvas.textWidth(label) : 0;
        int left = x() + (width() - contentWidth(textWidth)) / 2;
        if (icon != null) {
            icon.draw(canvas, left, y() + (height() - Icon.SIZE) / 2);
            left += Icon.SIZE + ICON_GAP;
        }
        if (labelDrawn) {
            canvas.text(label, left, y() + (height() - LINE_HEIGHT) / 2 + 1, enabled() ? TEXT_COLOR : DISABLED_TEXT_COLOR, true);
        }
    }

    @Override
    protected void onPress(PointerInput input) {
        activate();
    }

    @Override
    protected boolean onKey(KeyInput input) {
        if (input.key() != Key.ENTER && input.key() != Key.NUMPAD_ENTER && input.key() != Key.SPACE) {
            return false;
        }
        activate();
        return true;
    }

    private void activate() {
        playClick();
        action.run();
    }

    private String sprite() {
        if (!enabled()) {
            return sprites.disabled();
        }
        return hovered() || focused() ? sprites.hover() : sprites.normal();
    }

    private int contentWidth(int textWidth) {
        if (icon == null) {
            return textWidth;
        }
        return labelDrawn ? Icon.SIZE + ICON_GAP + textWidth : Icon.SIZE;
    }
}

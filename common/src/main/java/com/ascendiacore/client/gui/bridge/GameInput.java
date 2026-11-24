package com.ascendiacore.client.gui.bridge;

import com.mojang.blaze3d.platform.InputConstants;

import com.ascendiacore.gui.base.input.KeyInput;
import com.ascendiacore.gui.base.input.PointerInput;
import com.ascendiacore.gui.base.input.TextInput;
import com.ascendiacore.gui.base.input.WheelInput;
import com.ascendiacore.gui.base.input.model.Key;
import com.ascendiacore.gui.base.input.model.Modifiers;
import com.ascendiacore.gui.base.input.model.PointerButton;

import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;

public final class GameInput {
    private GameInput() {
    }

    public static PointerInput toPointerInput(MouseButtonEvent event) {
        return new PointerInput(event.x(), event.y(), toPointerButton(event.button()), toModifiers(event));
    }

    public static KeyInput toKeyInput(KeyEvent event) {
        return new KeyInput(toKey(event.key()), toModifiers(event));
    }

    public static TextInput toTextInput(CharacterEvent event) {
        return new TextInput(event.codepoint());
    }

    public static WheelInput toWheelInput(double x, double y, double scrollY) {
        return new WheelInput(x, y, scrollY);
    }

    private static PointerButton toPointerButton(int button) {
        return switch (button) {
            case InputConstants.MOUSE_BUTTON_LEFT -> PointerButton.LEFT;
            case InputConstants.MOUSE_BUTTON_RIGHT -> PointerButton.RIGHT;
            case InputConstants.MOUSE_BUTTON_MIDDLE -> PointerButton.MIDDLE;
            default -> PointerButton.OTHER;
        };
    }

    private static Key toKey(int key) {
        return switch (key) {
            case InputConstants.KEY_RETURN -> Key.ENTER;
            case InputConstants.KEY_NUMPADENTER -> Key.NUMPAD_ENTER;
            case InputConstants.KEY_SPACE -> Key.SPACE;
            case InputConstants.KEY_ESCAPE -> Key.ESCAPE;
            case InputConstants.KEY_TAB -> Key.TAB;
            case InputConstants.KEY_BACKSPACE -> Key.BACKSPACE;
            case InputConstants.KEY_DELETE -> Key.DELETE;
            case InputConstants.KEY_HOME -> Key.HOME;
            case InputConstants.KEY_END -> Key.END;
            case InputConstants.KEY_LEFT -> Key.LEFT;
            case InputConstants.KEY_RIGHT -> Key.RIGHT;
            case InputConstants.KEY_UP -> Key.UP;
            case InputConstants.KEY_DOWN -> Key.DOWN;
            default -> Key.OTHER;
        };
    }

    private static Modifiers toModifiers(InputWithModifiers input) {
        return new Modifiers(input.hasShiftDown(), input.hasControlDown(), input.hasAltDown());
    }
}

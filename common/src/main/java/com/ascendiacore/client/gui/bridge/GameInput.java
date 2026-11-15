package com.ascendiacore.client.gui.bridge;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.platform.InputConstants;

import com.ascendiacore.gui.base.input.KeyInput;
import com.ascendiacore.gui.base.input.PointerInput;
import com.ascendiacore.gui.base.input.TextInput;
import com.ascendiacore.gui.base.input.model.Key;
import com.ascendiacore.gui.base.input.model.Modifiers;
import com.ascendiacore.gui.base.input.model.PointerButton;

import net.minecraft.client.gui.screens.Screen;

public final class GameInput {
    private GameInput() {
    }

    public static PointerInput pointer(double x, double y, int button, int modifiers) {
        return new PointerInput(x, y, button(button), modifiers(modifiers));
    }

    public static KeyInput key(int key, int modifiers) {
        return new KeyInput(keyOf(key), modifiers(modifiers));
    }

    public static TextInput text(char character) {
        return new TextInput(character);
    }

    public static int heldModifiers() {
        return (Screen.hasShiftDown() ? GLFW.GLFW_MOD_SHIFT : 0)
                | (Screen.hasControlDown() ? GLFW.GLFW_MOD_CONTROL : 0)
                | (Screen.hasAltDown() ? GLFW.GLFW_MOD_ALT : 0);
    }

    private static PointerButton button(int button) {
        return switch (button) {
            case InputConstants.MOUSE_BUTTON_LEFT -> PointerButton.LEFT;
            case InputConstants.MOUSE_BUTTON_RIGHT -> PointerButton.RIGHT;
            case InputConstants.MOUSE_BUTTON_MIDDLE -> PointerButton.MIDDLE;
            default -> PointerButton.OTHER;
        };
    }

    private static Key keyOf(int key) {
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

    private static Modifiers modifiers(int modifiers) {
        return new Modifiers((modifiers & GLFW.GLFW_MOD_SHIFT) != 0, (modifiers & GLFW.GLFW_MOD_CONTROL) != 0,
                (modifiers & GLFW.GLFW_MOD_ALT) != 0);
    }
}

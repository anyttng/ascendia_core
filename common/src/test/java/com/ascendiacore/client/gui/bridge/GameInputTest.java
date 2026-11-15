package com.ascendiacore.client.gui.bridge;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.platform.InputConstants;

import com.ascendiacore.gui.base.input.KeyInput;
import com.ascendiacore.gui.base.input.PointerInput;
import com.ascendiacore.gui.base.input.TextInput;
import com.ascendiacore.gui.base.input.model.Key;
import com.ascendiacore.gui.base.input.model.Modifiers;
import com.ascendiacore.gui.base.input.model.PointerButton;

class GameInputTest {
    private static final double X = 12.5;
    private static final double Y = 40.25;
    private static final int NO_MODIFIERS = 0;
    private static final char CHARACTER = 'Ж';
    private static final Map<Integer, Key> KEYS = Map.ofEntries(
            Map.entry(InputConstants.KEY_RETURN, Key.ENTER),
            Map.entry(InputConstants.KEY_NUMPADENTER, Key.NUMPAD_ENTER),
            Map.entry(InputConstants.KEY_SPACE, Key.SPACE),
            Map.entry(InputConstants.KEY_ESCAPE, Key.ESCAPE),
            Map.entry(InputConstants.KEY_TAB, Key.TAB),
            Map.entry(InputConstants.KEY_BACKSPACE, Key.BACKSPACE),
            Map.entry(InputConstants.KEY_DELETE, Key.DELETE),
            Map.entry(InputConstants.KEY_HOME, Key.HOME),
            Map.entry(InputConstants.KEY_END, Key.END),
            Map.entry(InputConstants.KEY_LEFT, Key.LEFT),
            Map.entry(InputConstants.KEY_RIGHT, Key.RIGHT),
            Map.entry(InputConstants.KEY_UP, Key.UP),
            Map.entry(InputConstants.KEY_DOWN, Key.DOWN),
            Map.entry(InputConstants.KEY_A, Key.OTHER));

    @Test
    void pointerKeepsThePosition() {
        PointerInput input = GameInput.pointer(X, Y, InputConstants.MOUSE_BUTTON_LEFT, NO_MODIFIERS);
        assertEquals(new PointerInput(X, Y, PointerButton.LEFT, Modifiers.NONE), input);
    }

    @Test
    void gameButtonsMapToOurs() {
        assertEquals(PointerButton.LEFT, button(InputConstants.MOUSE_BUTTON_LEFT));
        assertEquals(PointerButton.RIGHT, button(InputConstants.MOUSE_BUTTON_RIGHT));
        assertEquals(PointerButton.MIDDLE, button(InputConstants.MOUSE_BUTTON_MIDDLE));
        assertEquals(PointerButton.OTHER, button(GLFW.GLFW_MOUSE_BUTTON_4));
    }

    @Test
    void everyGameKeyMapsToItsOwn() {
        assertEquals(Key.values().length, KEYS.size());
        KEYS.forEach((gameKey, key) -> assertEquals(key, key(gameKey, NO_MODIFIERS).key(), key.name()));
    }

    @Test
    void eachModifierIsReadOnItsOwn() {
        assertEquals(new Modifiers(true, false, false), key(InputConstants.KEY_TAB, GLFW.GLFW_MOD_SHIFT).modifiers());
        assertEquals(new Modifiers(false, true, false), key(InputConstants.KEY_TAB, GLFW.GLFW_MOD_CONTROL).modifiers());
        assertEquals(new Modifiers(false, false, true), key(InputConstants.KEY_TAB, GLFW.GLFW_MOD_ALT).modifiers());
        assertEquals(new Modifiers(true, false, true),
                GameInput.pointer(X, Y, InputConstants.MOUSE_BUTTON_LEFT, GLFW.GLFW_MOD_SHIFT | GLFW.GLFW_MOD_ALT)
                        .modifiers());
    }

    @Test
    void textKeepsTheCharacter() {
        assertEquals(new TextInput(CHARACTER), GameInput.text(CHARACTER));
    }

    private static PointerButton button(int gameButton) {
        return GameInput.pointer(X, Y, gameButton, NO_MODIFIERS).button();
    }

    private static KeyInput key(int gameKey, int modifiers) {
        return GameInput.key(gameKey, modifiers);
    }
}

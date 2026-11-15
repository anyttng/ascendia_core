package com.ascendiacore.gui.base.input;

import com.ascendiacore.gui.base.input.model.Key;
import com.ascendiacore.gui.base.input.model.Modifiers;

public record KeyInput(Key key, Modifiers modifiers) {
    public boolean isSelection() {
        return key == Key.ENTER || key == Key.NUMPAD_ENTER || key == Key.SPACE;
    }
}

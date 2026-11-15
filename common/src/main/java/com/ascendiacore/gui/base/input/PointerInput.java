package com.ascendiacore.gui.base.input;

import com.ascendiacore.gui.base.input.model.Modifiers;
import com.ascendiacore.gui.base.input.model.PointerButton;

public record PointerInput(double x, double y, PointerButton button, Modifiers modifiers) {
}

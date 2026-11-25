package com.ascendiacore.gui.theme.definition.model;

import java.io.Reader;

import net.minecraft.server.packs.resources.IoSupplier;

public record ThemeSource(String packId, IoSupplier<Reader> reader) {
}

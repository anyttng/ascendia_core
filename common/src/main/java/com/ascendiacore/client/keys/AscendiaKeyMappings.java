package com.ascendiacore.client.keys;

import com.ascendiacore.AscendiaCore;

import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;

public final class AscendiaKeyMappings {
    public static final KeyMapping.Category CATEGORY =
            new KeyMapping.Category(Identifier.fromNamespaceAndPath(AscendiaCore.MODID, "main"));

    private AscendiaKeyMappings() {
    }
}

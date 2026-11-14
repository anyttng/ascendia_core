package com.ascendia.core.client;

import com.ascendia.core.AscendiaCore;

import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;

public final class AscendiaKeyMappings {
    public static final KeyMapping.Category CATEGORY =
            new KeyMapping.Category(Identifier.fromNamespaceAndPath(AscendiaCore.MODID, "main"));

    private AscendiaKeyMappings() {
    }
}

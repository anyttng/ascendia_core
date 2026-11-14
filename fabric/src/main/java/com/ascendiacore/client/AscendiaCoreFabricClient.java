package com.ascendiacore.client;

import com.ascendiacore.client.keys.AscendiaKeyMappings;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.KeyMapping;

public class AscendiaCoreFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        KeyMapping.Category.register(AscendiaKeyMappings.CATEGORY.id());
    }
}

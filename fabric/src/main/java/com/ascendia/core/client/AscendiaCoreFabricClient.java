package com.ascendia.core.client;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.KeyMapping;

public class AscendiaCoreFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        KeyMapping.Category.register(AscendiaKeyMappings.CATEGORY.id());
    }
}

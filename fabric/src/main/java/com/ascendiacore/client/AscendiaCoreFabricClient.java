package com.ascendiacore.client;

import com.ascendiacore.client.gui.theme.ThemeReloadListener;
import com.ascendiacore.client.keys.AscendiaKeyMappings;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.client.KeyMapping;
import net.minecraft.server.packs.PackType;

public class AscendiaCoreFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        KeyMapping.Category.register(AscendiaKeyMappings.CATEGORY.id());
        ResourceLoader.get(PackType.CLIENT_RESOURCES).registerReloadListener(ThemeReloadListener.ID, new ThemeReloadListener());
    }
}

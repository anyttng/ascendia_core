package com.ascendia.core;

import com.ascendia.core.platform.FabricPlatform;
import com.ascendia.core.platform.Platforms;

import net.fabricmc.api.ModInitializer;

public class AscendiaCoreFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        AscendiaCore.LOGGER.info("Ascendia Core initializing");
        Platforms.set(new FabricPlatform());
    }
}

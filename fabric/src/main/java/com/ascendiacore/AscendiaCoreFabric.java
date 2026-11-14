package com.ascendiacore;

import com.ascendiacore.platform.FabricPlatform;
import com.ascendiacore.platform.Platforms;

import net.fabricmc.api.ModInitializer;

public class AscendiaCoreFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        AscendiaCore.LOGGER.info("Ascendia Core initializing");
        Platforms.set(new FabricPlatform());
    }
}

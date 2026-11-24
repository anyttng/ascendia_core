package com.ascendiacore.platform;

import com.ascendiacore.AscendiaCore;

import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;

public final class FabricPlatform implements Platform {
    private static final String LOADER_MOD_ID = "fabricloader";

    @Override
    public boolean isClient() {
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
    }

    @Override
    public String getModVersion() {
        return getVersion(AscendiaCore.MODID);
    }

    @Override
    public String getLoaderName() {
        return "fabric";
    }

    @Override
    public String getLoaderVersion() {
        return getVersion(LOADER_MOD_ID);
    }

    private static String getVersion(String modId) {
        return FabricLoader.getInstance().getModContainer(modId).orElseThrow()
                .getMetadata().getVersion().getFriendlyString();
    }
}

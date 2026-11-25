package com.ascendiacore.platform;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForgeVersion;

public final class NeoForgePlatform implements Platform {
    private final ModContainer modContainer;

    public NeoForgePlatform(ModContainer modContainer) {
        this.modContainer = modContainer;
    }

    @Override
    public boolean isClient() {
        return FMLEnvironment.getDist() == Dist.CLIENT;
    }

    @Override
    public String getModVersion() {
        return modContainer.getModInfo().getVersion().toString();
    }

    @Override
    public String getLoaderName() {
        return "neoforge";
    }

    @Override
    public String getLoaderVersion() {
        return NeoForgeVersion.getVersion();
    }
}

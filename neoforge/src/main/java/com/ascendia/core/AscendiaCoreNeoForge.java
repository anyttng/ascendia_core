package com.ascendia.core;

import com.ascendia.core.platform.NeoForgePlatform;
import com.ascendia.core.platform.Platforms;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(AscendiaCore.MODID)
public class AscendiaCoreNeoForge {
    public AscendiaCoreNeoForge(IEventBus modEventBus, ModContainer modContainer) {
        AscendiaCore.LOGGER.info("Ascendia Core initializing");
        Platforms.set(new NeoForgePlatform(modContainer));
    }
}

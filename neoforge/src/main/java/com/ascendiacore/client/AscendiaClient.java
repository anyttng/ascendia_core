package com.ascendiacore.client;

import com.ascendiacore.AscendiaCore;
import com.ascendiacore.client.keys.AscendiaKeyMappings;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;

@EventBusSubscriber(modid = AscendiaCore.MODID, value = Dist.CLIENT)
public final class AscendiaClient {

    @SubscribeEvent
    static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        event.registerCategory(AscendiaKeyMappings.CATEGORY);
    }

    private AscendiaClient() {
    }
}

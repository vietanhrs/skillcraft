package com.example.villageextras;

import com.example.villageextras.event.VillageGenerationHandler;
import com.example.villageextras.init.ModFeatures;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig.Type;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

@Mod(VillageExtras.MOD_ID)
public class VillageExtras {

    public static final String MOD_ID = "villageextras";

    public VillageExtras(IEventBus modBus, ModContainer modContainer) {
        ModFeatures.FEATURES.register(modBus);

        modContainer.registerConfig(Type.COMMON, VillageExtrasConfig.COMMON_SPEC);

        NeoForge.EVENT_BUS.addListener(VillageGenerationHandler::onChunkLoad);
        NeoForge.EVENT_BUS.addListener(VillageGenerationHandler::onServerTick);
        NeoForge.EVENT_BUS.addListener(VillageGenerationHandler::onServerStopped);
    }
}

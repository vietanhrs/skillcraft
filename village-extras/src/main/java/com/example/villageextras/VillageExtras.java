package com.example.villageextras;

import com.example.villageextras.event.VillageGenerationHandler;
import com.example.villageextras.init.ModFeatures;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(VillageExtras.MOD_ID)
public class VillageExtras {

    public static final String MOD_ID = "villageextras";

    public VillageExtras(FMLJavaModLoadingContext context) {
        var bus = context.getModBusGroup();

        ModFeatures.FEATURES.register(bus);

        context.registerConfig(Type.COMMON, VillageExtrasConfig.COMMON_SPEC);

        ChunkEvent.Load.BUS.addListener(VillageGenerationHandler::onChunkLoad);
        TickEvent.ServerTickEvent.Post.BUS.addListener(VillageGenerationHandler::onServerTick);
    }
}

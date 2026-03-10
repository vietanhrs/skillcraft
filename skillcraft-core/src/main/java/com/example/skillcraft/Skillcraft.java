package com.example.skillcraft;

import com.example.skillcraft.client.ClientSetup;
import com.example.skillcraft.init.ModEntityTypes;
import com.example.skillcraft.init.ModFeatures;
import com.example.skillcraft.init.ModItems;
import com.example.skillcraft.mana.ManaEvents;
import com.example.skillcraft.mana.ManaNetwork;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(Skillcraft.MOD_ID)
public class Skillcraft {

    public static final String MOD_ID = "skillcraft";

    public Skillcraft(FMLJavaModLoadingContext context) {
        var bus = context.getModBusGroup();

        ModFeatures.FEATURES.register(bus);
        ModItems.ITEMS.register(bus);
        ModEntityTypes.ENTITY_TYPES.register(bus);

        ManaNetwork.register();

        context.registerConfig(Type.COMMON, SkillcraftConfig.COMMON_SPEC);

        // Register attributes for custom entities
        EntityAttributeCreationEvent.BUS.addListener(e ->
                e.put(ModEntityTypes.SECRET_MERCHANT.get(), net.minecraft.world.entity.Mob.createMobAttributes().build()));

        // Register game events on their dedicated buses
        PlayerEvent.PlayerLoggedInEvent.BUS.addListener(ManaEvents::onPlayerLoggedIn);
        PlayerEvent.PlayerRespawnEvent.BUS.addListener(ManaEvents::onPlayerRespawn);
        PlayerEvent.PlayerChangedDimensionEvent.BUS.addListener(ManaEvents::onPlayerChangedDimension);

        // Register client-only BUS listeners immediately so they are set up before
        // SelfDestructing events (EntityRenderersEvent, AddGuiOverlayLayersEvent) fire.
        if (FMLEnvironment.dist == Dist.CLIENT) {
            ClientSetup.init();
        }
    }
}

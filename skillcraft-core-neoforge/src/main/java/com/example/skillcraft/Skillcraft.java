package com.example.skillcraft;

import com.example.skillcraft.client.ClientSetup;
import com.example.skillcraft.init.ModEntityTypes;
import com.example.skillcraft.init.ModFeatures;
import com.example.skillcraft.init.ModItems;
import com.example.skillcraft.mana.ManaEvents;
import com.example.skillcraft.mana.ManaNetwork;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig.Type;
import net.neoforged.neoforge.event.AnvilUpdateEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.common.NeoForge;

@Mod(Skillcraft.MOD_ID)
public class Skillcraft {

    public static final String MOD_ID = "skillcraft";

    public Skillcraft(IEventBus modBus, ModContainer modContainer) {
        ModFeatures.FEATURES.register(modBus);
        ModItems.ITEMS.register(modBus);
        ModEntityTypes.ENTITY_TYPES.register(modBus);

        ManaNetwork.register(modBus);

        modContainer.registerConfig(Type.COMMON, SkillcraftConfig.COMMON_SPEC);

        modBus.addListener((EntityAttributeCreationEvent e) -> e.put(ModEntityTypes.SECRET_MERCHANT.get(),
                net.minecraft.world.entity.Mob.createMobAttributes().build()));

        NeoForge.EVENT_BUS.addListener(ManaEvents::onPlayerLoggedIn);
        NeoForge.EVENT_BUS.addListener(ManaEvents::onPlayerRespawn);
        NeoForge.EVENT_BUS.addListener(ManaEvents::onPlayerChangedDimension);
        NeoForge.EVENT_BUS.addListener(ManaEvents::onPlayerTick);
        NeoForge.EVENT_BUS.addListener(ManaEvents::onAnvilUpdate);

        ClientSetup.init(modBus);
    }
}

package com.example.specialforces;

import com.example.specialforces.client.ClientSetup;
import com.example.specialforces.client.SFKeyBindings;
import com.example.specialforces.event.SFEvents;
import com.example.specialforces.init.SFDataComponents;
import com.example.specialforces.init.SFEntityTypes;
import com.example.specialforces.init.SFFeatures;
import com.example.specialforces.init.SFItems;
import com.example.specialforces.init.SFSounds;
import com.example.specialforces.item.SniperRifle;
import com.example.specialforces.network.SFNetwork;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@Mod(SpecialForces.MOD_ID)
public class SpecialForces {

    public static final String MOD_ID = "specialforces";

    public SpecialForces(IEventBus modBus) {
        SFDataComponents.COMPONENTS.register(modBus);
        SFItems.ITEMS.register(modBus);
        SFSounds.SOUNDS.register(modBus);
        SFFeatures.FEATURES.register(modBus);
        SFEntityTypes.ENTITY_TYPES.register(modBus);
        SFNetwork.register(modBus);

        modBus.addListener(ClientSetup::registerOverlays);
        modBus.addListener(ClientSetup::registerRenderers);
        modBus.addListener((RegisterKeyMappingsEvent e) -> e.register(SFKeyBindings.RELOAD_KEY));
        modBus.addListener((FMLClientSetupEvent e) -> ClientSetup.init());

        NeoForge.EVENT_BUS.addListener(SFEvents::onPlayerTick);
        NeoForge.EVENT_BUS.addListener((PlayerEvent.PlayerLoggedOutEvent e) -> {
            SniperRifle.SERVER_ZOOM.remove(e.getEntity().getUUID());
            SFEvents.clearPlayerState(e.getEntity().getUUID());
        });
        NeoForge.EVENT_BUS.addListener((ServerStoppedEvent e) -> {
            SniperRifle.SERVER_ZOOM.clear();
            SFEvents.clearAllState();
        });
    }
}

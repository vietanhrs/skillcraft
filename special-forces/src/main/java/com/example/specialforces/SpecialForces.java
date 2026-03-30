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
import net.minecraftforge.client.event.AddGuiOverlayLayersEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(SpecialForces.MOD_ID)
public class SpecialForces {

    public static final String MOD_ID = "specialforces";

    public SpecialForces(FMLJavaModLoadingContext context) {
        var bus = context.getModBusGroup();
        SFDataComponents.COMPONENTS.register(bus);
        SFItems.ITEMS.register(bus);
        SFSounds.SOUNDS.register(bus);
        SFFeatures.FEATURES.register(bus);
        SFEntityTypes.ENTITY_TYPES.register(bus);
        SFNetwork.register();

        // AddGuiOverlayLayersEvent is SelfDestructing: it fires once during init and
        // destroys its bus immediately after. Must register via getBus(bus) here in the
        // constructor — registering inside FMLClientSetupEvent is too late.
        // Lambda defers ClientSetup class loading until invocation (client only).
        AddGuiOverlayLayersEvent.getBus(bus).addListener(e -> ClientSetup.registerOverlays(e));

        EntityRenderersEvent.RegisterRenderers.getBus(bus).addListener(e -> ClientSetup.registerRenderers(e));

        // Register reload key binding
        RegisterKeyMappingsEvent.getBus(bus).addListener(e -> e.register(SFKeyBindings.RELOAD_KEY));

        // Persistent-bus events (FOV, tick, input) are safe to register any time.
        FMLClientSetupEvent.getBus(bus).addListener(e -> ClientSetup.init());

        // Tick down reload timers on held guns
        TickEvent.PlayerTickEvent.Post.BUS.addListener(SFEvents::onPlayerTick);

        // Clean up server-side zoom state to prevent memory leaks
        PlayerEvent.PlayerLoggedOutEvent.BUS.addListener(e ->
                SniperRifle.SERVER_ZOOM.remove(e.getEntity().getUUID()));
        ServerStoppedEvent.BUS.addListener(e -> SniperRifle.SERVER_ZOOM.clear());
    }
}

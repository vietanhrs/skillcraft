package com.example.skillcraft.client;

import com.example.skillcraft.init.ModEntityTypes;
import com.example.skillcraft.mana.ManaHud;
import net.minecraft.client.renderer.entity.WanderingTraderRenderer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;

/**
 * Client-only event handlers, registered from the NeoForge mod bus.
 * This class is never loaded on a dedicated server.
 */
@OnlyIn(Dist.CLIENT)
public class ClientSetup {

    public static void init(IEventBus modBus) {
        modBus.addListener(ClientSetup::onRegisterRenderers);
        modBus.addListener(ManaHud::register);
    }

    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // Reuse the vanilla WanderingTrader model/skin for the SecretMerchant
        event.registerEntityRenderer(ModEntityTypes.SECRET_MERCHANT.get(), WanderingTraderRenderer::new);
    }
}

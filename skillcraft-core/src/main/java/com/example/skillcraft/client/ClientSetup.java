package com.example.skillcraft.client;

import com.example.skillcraft.init.ModEntityTypes;
import com.example.skillcraft.mana.ManaHud;
import net.minecraft.client.renderer.entity.WanderingTraderRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.AddGuiOverlayLayersEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;

/**
 * Client-only event handlers, registered manually in the mod constructor.
 * This class is never loaded on a dedicated server.
 */
@OnlyIn(Dist.CLIENT)
public class ClientSetup {

    /**
     * Entry point called via DistExecutor.safeRunWhenOn — must be a static void
     * method.
     */
    public static void init() {
        EntityRenderersEvent.RegisterRenderers.BUS.addListener(ClientSetup::onRegisterRenderers);
        AddGuiOverlayLayersEvent.BUS.addListener(ManaHud::register);
    }

    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // Reuse the vanilla WanderingTrader model/skin for the SecretMerchant
        event.registerEntityRenderer(ModEntityTypes.SECRET_MERCHANT.get(), WanderingTraderRenderer::new);
    }
}

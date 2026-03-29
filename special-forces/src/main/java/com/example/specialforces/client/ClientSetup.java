package com.example.specialforces.client;

import com.example.specialforces.init.SFItems;
import com.example.specialforces.item.SniperRifle;
import com.example.specialforces.network.SFNetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraftforge.client.event.AddGuiOverlayLayersEvent;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.gui.overlay.ForgeLayeredDraw;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.event.TickEvent;

@OnlyIn(Dist.CLIENT)
public class ClientSetup {

    /** Called from AddGuiOverlayLayersEvent.getBus(bus) in the mod constructor (SelfDestructing). */
    public static void registerOverlays(AddGuiOverlayLayersEvent event) {
        // Hide vanilla crosshair while scoped in
        event.getLayeredDraw().addConditionTo(
                ForgeLayeredDraw.PRE_SLEEP_STACK,
                ForgeLayeredDraw.CROSSHAIR,
                () -> ScopeState.zoomLevel == 0);

        ScopeOverlay.register(event);
        NvOverlay.register(event);
    }

    /** Called from EntityRenderersEvent.RegisterRenderers.getBus(bus) in the mod constructor. */
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(com.example.specialforces.init.SFEntityTypes.GLOW_STICK.get(),
                context -> new ThrownItemRenderer<>(context, 1.0F, true));
    }

    /** Called from FMLClientSetupEvent for persistent-bus events. */
    public static void init() {
        ViewportEvent.ComputeFov.BUS.addListener(ClientSetup::onComputeFov);
        TickEvent.ClientTickEvent.Post.BUS.addListener(ClientSetup::onClientTick);
        InputEvent.InteractionKeyMappingTriggered.BUS.addListener(ClientSetup::onInteractionKey);
    }

    /**
     * Returns true to cancel the event (suppress vanilla melee attack) when the player
     * is holding a sniper rifle and presses the attack key.
     */
    private static boolean onInteractionKey(InputEvent.InteractionKeyMappingTriggered event) {
        if (!event.isAttack() || event.getHand() != InteractionHand.MAIN_HAND) return false;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return false;
        if (!(mc.player.getMainHandItem().getItem() instanceof SniperRifle)) return false;

        event.setSwingHand(false); // suppress vanilla swing animation
        ScopeState.preShotZoom = ScopeState.zoomLevel; // save zoom to restore after cooldown
        ScopeState.zoomLevel = 0; // immediately reset view to normal when shot fires
        SFNetwork.sendShoot();
        return true; // cancel vanilla melee attack
    }

    private static void onComputeFov(ViewportEvent.ComputeFov event) {
        event.setFOV(event.getFOV() * ScopeState.FOV_MULTIPLIER[ScopeState.zoomLevel]);
    }

    private static void onClientTick(TickEvent.ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        ItemStack stack = mc.player.getMainHandItem();
        boolean holdingSniper = stack.getItem() == SFItems.SNIPER.get();

        boolean onCooldown = holdingSniper && mc.player.getCooldowns().isOnCooldown(stack);

        // Detect cooldown expiry: was on cooldown last tick, now it's done
        if (ScopeState.wasOnCooldown && !onCooldown && holdingSniper) {
            // Restore zoom to what it was before shooting
            ScopeState.zoomLevel = ScopeState.preShotZoom;
        }

        ScopeState.wasOnCooldown = onCooldown;

        // If player switches away from sniper, clear zoom
        if (!holdingSniper && ScopeState.zoomLevel != 0) {
            ScopeState.zoomLevel = 0;
            ScopeState.preShotZoom = 0;
        }
    }
}

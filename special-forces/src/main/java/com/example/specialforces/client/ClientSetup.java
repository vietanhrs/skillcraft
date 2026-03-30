package com.example.specialforces.client;

import com.example.specialforces.init.SFDataComponents;
import com.example.specialforces.init.SFItems;
import com.example.specialforces.item.M4A1Rifle;
import com.example.specialforces.item.SniperRifle;
import com.example.specialforces.network.SFNetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
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

    private static int arFireCooldown = 0;

    /** Called from AddGuiOverlayLayersEvent.getBus(bus) in the mod constructor (SelfDestructing). */
    public static void registerOverlays(AddGuiOverlayLayersEvent event) {
        // Replace vanilla crosshair with CS-style crosshair
        event.getLayeredDraw().addConditionTo(
                ForgeLayeredDraw.PRE_SLEEP_STACK,
                ForgeLayeredDraw.CROSSHAIR,
                () -> false); // always hide vanilla crosshair

        CrosshairOverlay.register(event);
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

    private static boolean onInteractionKey(InputEvent.InteractionKeyMappingTriggered event) {
        if (!event.isAttack() || event.getHand() != InteractionHand.MAIN_HAND) return false;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return false;
        ItemStack held = mc.player.getMainHandItem();

        if (held.getItem() instanceof SniperRifle) {
            event.setSwingHand(false);
            if (held.getOrDefault(SFDataComponents.RELOAD_TICKS.get(), 0) > 0) return true;
            int ammo = held.getOrDefault(SFDataComponents.MAGAZINE_AMMO.get(), 0);
            if (ammo <= 0) return true;

            int zoomAtShot = ScopeState.zoomLevel;
            ScopeState.preShotZoom = ScopeState.zoomLevel;
            ScopeState.zoomLevel = 0;

            int targetId = performClientRaycast(mc, 500.0);
            SFNetwork.sendSniperShoot(targetId, zoomAtShot);
            return true;
        }

        if (held.getItem() instanceof M4A1Rifle) {
            event.setSwingHand(false);
            return true; // suppress vanilla attack; auto-fire is handled in tick
        }

        return false;
    }

    private static void onComputeFov(ViewportEvent.ComputeFov event) {
        event.setFOV(event.getFOV() * ScopeState.FOV_MULTIPLIER[ScopeState.zoomLevel]);
    }

    private static void onClientTick(TickEvent.ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        ItemStack stack = mc.player.getMainHandItem();
        boolean holdingSniper = stack.getItem() instanceof SniperRifle;
        boolean holdingAR = stack.getItem() instanceof M4A1Rifle;

        // --- Sniper cooldown / zoom restore ---
        boolean onCooldown = holdingSniper && mc.player.getCooldowns().isOnCooldown(stack);
        if (ScopeState.wasOnCooldown && !onCooldown && holdingSniper) {
            ScopeState.zoomLevel = ScopeState.preShotZoom;
        }
        ScopeState.wasOnCooldown = onCooldown;
        if (!holdingSniper && ScopeState.zoomLevel != 0) {
            ScopeState.zoomLevel = 0;
            ScopeState.preShotZoom = 0;
        }

        // --- M4A1 auto-fire ---
        if (arFireCooldown > 0) arFireCooldown--;

        if (holdingAR && mc.options.keyAttack.isDown()
                && arFireCooldown <= 0 && mc.screen == null) {
            int reloadTicks = stack.getOrDefault(SFDataComponents.RELOAD_TICKS.get(), 0);
            int ammo = stack.getOrDefault(SFDataComponents.MAGAZINE_AMMO.get(), 0);
            if (reloadTicks <= 0 && ammo > 0) {
                int targetId = performClientRaycast(mc, M4A1Rifle.MAX_RANGE);
                SFNetwork.sendARShoot(targetId);
                applyRecoil(mc.player);
                arFireCooldown = M4A1Rifle.FIRE_RATE;
            }
        }

        // --- Reload key ---
        if (SFKeyBindings.RELOAD_KEY.consumeClick()) {
            if (holdingSniper || holdingAR) {
                SFNetwork.sendReload();
            }
        }
    }

    /** Perform a client-side raycast and return the hit entity ID, or -1 for miss. */
    private static int performClientRaycast(Minecraft mc, double range) {
        Vec3 eyePos = mc.player.getEyePosition(1.0F);
        Vec3 lookVec = mc.player.getViewVector(1.0F);
        Vec3 endPos = eyePos.add(lookVec.scale(range));
        AABB searchArea = mc.player.getBoundingBox()
                .expandTowards(lookVec.scale(range)).inflate(1.0);
        EntityHitResult hit = ProjectileUtil.getEntityHitResult(
                mc.level, mc.player, eyePos, endPos, searchArea,
                e -> !e.isSpectator() && e.isPickable(), 0.3F);
        return hit != null ? hit.getEntity().getId() : -1;
    }

    /** Apply camera recoil for the M4A1 — muzzle kicks upward (toward player). */
    private static void applyRecoil(LocalPlayer player) {
        // Negative xRot = look up = gun pushed back toward player
        float verticalKick = -1.5f;
        float horizontalKick = (player.getRandom().nextFloat() - 0.5f) * 0.4f;
        player.setXRot(player.getXRot() + verticalKick);
        player.setYRot(player.getYRot() + horizontalKick);
    }
}

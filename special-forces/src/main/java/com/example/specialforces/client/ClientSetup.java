package com.example.specialforces.client;

import com.example.specialforces.init.SFDataComponents;
import com.example.specialforces.init.SFItems;
import com.example.specialforces.item.M4A1Rifle;
import com.example.specialforces.item.SniperRifle;
import com.example.specialforces.network.SFNetwork;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
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
import net.minecraftforge.client.event.RenderHandEvent;
import net.minecraftforge.client.gui.overlay.ForgeLayeredDraw;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.event.TickEvent;

@OnlyIn(Dist.CLIENT)
public class ClientSetup {

    private static int arFireCooldown = 0;
    /** Visual gun-model kick intensity (0.0 = none, 1.0 = full). Decays each frame. */
    private static float recoilAmount = 0f;
    /**
     * Camera-only pitch offset for vertical recoil (degrees, negative = look up).
     * Applied via ComputeCameraAngles so it does NOT affect hand rendering.
     * Decays slowly when not firing.
     */
    private static float cameraRecoilPitch = 0f;

    public static void registerOverlays(AddGuiOverlayLayersEvent event) {
        event.getLayeredDraw().addConditionTo(
                ForgeLayeredDraw.PRE_SLEEP_STACK,
                ForgeLayeredDraw.CROSSHAIR,
                () -> false);

        CrosshairOverlay.register(event);
        ScopeOverlay.register(event);
        NvOverlay.register(event);
    }

    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(com.example.specialforces.init.SFEntityTypes.GLOW_STICK.get(),
                context -> new ThrownItemRenderer<>(context, 1.0F, true));
    }

    public static void init() {
        ViewportEvent.ComputeFov.BUS.addListener(ClientSetup::onComputeFov);
        ViewportEvent.ComputeCameraAngles.BUS.addListener(ClientSetup::onComputeCameraAngles);
        TickEvent.ClientTickEvent.Post.BUS.addListener(ClientSetup::onClientTick);
        InputEvent.InteractionKeyMappingTriggered.BUS.addListener(ClientSetup::onInteractionKey);
        RenderHandEvent.BUS.addListener(ClientSetup::onRenderHand);
    }

    private static boolean onInteractionKey(InputEvent.InteractionKeyMappingTriggered event) {
        if (!event.isAttack() || event.getHand() != InteractionHand.MAIN_HAND)
            return false;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null)
            return false;
        ItemStack held = mc.player.getMainHandItem();

        if (held.getItem() instanceof SniperRifle) {
            event.setSwingHand(false);
            if (held.getOrDefault(SFDataComponents.RELOAD_TICKS.get(), 0) > 0)
                return true;
            int ammo = held.getOrDefault(SFDataComponents.MAGAZINE_AMMO.get(), 0);
            if (ammo <= 0)
                return true;

            int zoomAtShot = ScopeState.zoomLevel;
            ScopeState.preShotZoom = ScopeState.zoomLevel;
            ScopeState.zoomLevel = 0;

            int targetId = performClientRaycast(mc, 500.0);
            SFNetwork.sendSniperShoot(targetId, zoomAtShot);
            return true;
        }

        if (held.getItem() instanceof M4A1Rifle) {
            event.setSwingHand(false);
            return true;
        }

        return false;
    }

    private static void onComputeFov(ViewportEvent.ComputeFov event) {
        event.setFOV(event.getFOV() * ScopeState.FOV_MULTIPLIER[ScopeState.zoomLevel]);
    }

    /** Apply accumulated recoil pitch to the camera WITHOUT changing player.xRot. */
    private static void onComputeCameraAngles(ViewportEvent.ComputeCameraAngles event) {
        if (cameraRecoilPitch != 0f) {
            event.setPitch(event.getPitch() + cameraRecoilPitch);
        }
    }

    private static void onClientTick(TickEvent.ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null)
            return;

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
        if (arFireCooldown > 0)
            arFireCooldown--;

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

        // Decay camera recoil when not firing
        if (!holdingAR || !mc.options.keyAttack.isDown()) {
            cameraRecoilPitch *= 0.85f;
            if (Math.abs(cameraRecoilPitch) < 0.1f) cameraRecoilPitch = 0f;
        }

        // --- Reload key ---
        if (SFKeyBindings.RELOAD_KEY.consumeClick()) {
            if (holdingSniper || holdingAR) {
                SFNetwork.sendReload();
            }
        }
    }

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

    /** Apply recoil — visual gun kick + camera pitch offset (no xRot change). */
    private static void applyRecoil(LocalPlayer player) {
        // Accumulate camera pitch recoil (applied in ComputeCameraAngles, not xRot)
        cameraRecoilPitch -= 1.0f;
        // Horizontal jitter on actual yaw
        float newYaw = player.getYRot() + (player.getRandom().nextFloat() - 0.5f) * 0.3f;
        player.setYRot(newYaw);
        player.yRotO = newYaw;
        recoilAmount = 1.0f;
    }

    /** Visual gun kick: push backward + tilt muzzle up. */
    private static boolean onRenderHand(RenderHandEvent event) {
        if (event.getHand() != InteractionHand.MAIN_HAND) return false;
        if (!(event.getItemStack().getItem() instanceof M4A1Rifle)) return false;
        if (recoilAmount <= 0.01f) return false;

        PoseStack ps = event.getPoseStack();
        ps.translate(0.0, 0.05 * recoilAmount, 0.15 * recoilAmount);
        ps.mulPose(Axis.XP.rotationDegrees(12.0f * recoilAmount));

        recoilAmount *= 0.5f;
        if (recoilAmount < 0.05f) recoilAmount = 0f;

        return false;
    }
}

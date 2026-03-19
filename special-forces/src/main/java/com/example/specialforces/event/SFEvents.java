package com.example.specialforces.event;

import com.example.specialforces.init.SFItems;
import com.example.specialforces.init.SFSounds;
import com.example.specialforces.item.SniperRifle;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class SFEvents {

    private static final float NO_SCOPE_SPREAD = 0.20f;
    private static final float SCOPED_SPREAD = 0.005f;
    private static final float SNIPER_DAMAGE = 50.0f;

    /** Called from SniperShootPacket handler on the server thread. */
    public static void handleSniperShot(ServerPlayer player) {
        ItemStack stack = player.getMainHandItem();
        if (!(stack.getItem() instanceof SniperRifle)) return;
        if (player.getCooldowns().isOnCooldown(stack)) return;

        // Consume a bullet
        boolean hasBullet = false;
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack slot = player.getInventory().getItem(i);
            if (slot.getItem() == SFItems.BULLET.get()) {
                slot.shrink(1);
                hasBullet = true;
                break;
            }
        }
        if (!hasBullet) return;

        int zoom = SniperRifle.getZoom(player);
        double range = 500.0;

        HitResult hit = ProjectileUtil.getHitResultOnViewVector(
                player,
                e -> !e.isSpectator() && e.isPickable() && e != player,
                range);

        if (hit.getType() == HitResult.Type.ENTITY) {
            Entity target = ((EntityHitResult) hit).getEntity();
            // Apply spread as miss chance: ~20% no-scope, ~0.5% scoped
            float spread = zoom == 0 ? NO_SCOPE_SPREAD : SCOPED_SPREAD;
            if (player.getRandom().nextFloat() >= spread) {
                target.hurt(player.damageSources().playerAttack(player), SNIPER_DAMAGE);
            }
        }

        player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                SFSounds.SNIPER_FIRE.get(), SoundSource.PLAYERS, 1.0f, 1.0f);

        SniperRifle.setZoom(player, 0);
        player.getCooldowns().addCooldown(stack, SniperRifle.COOLDOWN_TICKS);
    }
}

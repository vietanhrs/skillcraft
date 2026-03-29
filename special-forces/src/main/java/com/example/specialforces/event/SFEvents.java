package com.example.specialforces.event;

import com.example.specialforces.init.SFItems;
import com.example.specialforces.init.SFSounds;
import com.example.specialforces.item.SniperRifle;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;

public class SFEvents {

    private static final float NO_SCOPE_SPREAD = 0.20f;
    private static final float SCOPED_SPREAD = 0.005f;
    private static final float SNIPER_DAMAGE = 50.0f;
    private static final double MAX_RANGE_SQ = 500.0 * 500.0;

    /**
     * Called from SniperShootPacket handler on the server thread.
     * The client performs the raycast and sends the hit entity ID and zoom level.
     */
    public static void handleSniperShot(ServerPlayer player, int targetEntityId, int zoom) {
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

        // Clamp zoom to valid range to prevent exploits
        zoom = Math.max(0, Math.min(2, zoom));

        if (targetEntityId >= 0) {
            Entity target = player.level().getEntity(targetEntityId);
            if (target != null && target.isAlive()
                    && target.distanceToSqr(player) <= MAX_RANGE_SQ) {
                float spread = zoom == 0 ? NO_SCOPE_SPREAD : SCOPED_SPREAD;
                if (player.getRandom().nextFloat() >= spread) {
                    target.hurt(player.damageSources().playerAttack(player), SNIPER_DAMAGE);
                }
            }
        }

        player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                SFSounds.SNIPER_FIRE.get(), SoundSource.PLAYERS, 1.0f, 1.0f);

        SniperRifle.setZoom(player, 0);
        player.getCooldowns().addCooldown(stack, SniperRifle.COOLDOWN_TICKS);
    }
}

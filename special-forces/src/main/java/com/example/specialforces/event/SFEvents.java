package com.example.specialforces.event;

import com.example.specialforces.init.SFDataComponents;
import com.example.specialforces.init.SFItems;
import com.example.specialforces.init.SFSounds;
import com.example.specialforces.item.M4A1Rifle;
import com.example.specialforces.item.SniperRifle;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomModelData;
import java.util.List;
import net.minecraftforge.event.TickEvent;

public class SFEvents {

    private static final float SNIPER_NO_SCOPE_SPREAD = 0.20f;
    private static final float SNIPER_SCOPED_SPREAD = 0.005f;
    private static final float SNIPER_DAMAGE = 50.0f;
    private static final double SNIPER_MAX_RANGE_SQ = 500.0 * 500.0;

    /** Called from SniperShootPacket handler on the server thread. */
    public static void handleSniperShot(ServerPlayer player, int targetEntityId, int zoom) {
        ItemStack stack = player.getMainHandItem();
        if (!(stack.getItem() instanceof SniperRifle)) return;
        if (player.getCooldowns().isOnCooldown(stack)) return;
        if (stack.getOrDefault(SFDataComponents.RELOAD_TICKS.get(), 0) > 0) return;

        int ammo = stack.getOrDefault(SFDataComponents.MAGAZINE_AMMO.get(), 0);
        if (ammo <= 0) return;
        stack.set(SFDataComponents.MAGAZINE_AMMO.get(), ammo - 1);

        zoom = Math.max(0, Math.min(2, zoom));

        if (targetEntityId >= 0) {
            Entity target = player.level().getEntity(targetEntityId);
            if (target != null && target.isAlive()
                    && target.distanceToSqr(player) <= SNIPER_MAX_RANGE_SQ) {
                float spread = zoom == 0 ? SNIPER_NO_SCOPE_SPREAD : SNIPER_SCOPED_SPREAD;
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

    /** Called from ARShootPacket handler on the server thread. */
    public static void handleARShot(ServerPlayer player, int targetEntityId) {
        ItemStack stack = player.getMainHandItem();
        if (!(stack.getItem() instanceof M4A1Rifle)) return;
        if (player.getCooldowns().isOnCooldown(stack)) return;
        if (stack.getOrDefault(SFDataComponents.RELOAD_TICKS.get(), 0) > 0) return;

        int ammo = stack.getOrDefault(SFDataComponents.MAGAZINE_AMMO.get(), 0);
        if (ammo <= 0) return;
        stack.set(SFDataComponents.MAGAZINE_AMMO.get(), ammo - 1);

        if (targetEntityId >= 0) {
            Entity target = player.level().getEntity(targetEntityId);
            double maxRangeSq = M4A1Rifle.MAX_RANGE * M4A1Rifle.MAX_RANGE;
            if (target != null && target.isAlive()
                    && target.distanceToSqr(player) <= maxRangeSq) {
                if (player.getRandom().nextFloat() >= M4A1Rifle.SPREAD) {
                    target.hurt(player.damageSources().playerAttack(player), M4A1Rifle.DAMAGE);
                }
            }
        }

        player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                SFSounds.AR_FIRE.get(), SoundSource.PLAYERS, 0.8f, 1.0f);

        player.getCooldowns().addCooldown(stack, M4A1Rifle.FIRE_RATE);
    }

    /** Called from ReloadGunPacket handler on the server thread. */
    public static void handleReload(ServerPlayer player) {
        ItemStack stack = player.getMainHandItem();
        int maxMag;
        Item bulletType;
        int reloadTime;

        if (stack.getItem() instanceof SniperRifle) {
            maxMag = SniperRifle.MAX_MAGAZINE;
            bulletType = SFItems.SNIPER_BULLET.get();
            reloadTime = SniperRifle.RELOAD_TIME;
        } else if (stack.getItem() instanceof M4A1Rifle) {
            maxMag = M4A1Rifle.MAX_MAGAZINE;
            bulletType = SFItems.AR_BULLET.get();
            reloadTime = M4A1Rifle.RELOAD_TIME;
        } else return;

        int currentAmmo = stack.getOrDefault(SFDataComponents.MAGAZINE_AMMO.get(), 0);
        if (currentAmmo >= maxMag) return;
        if (stack.getOrDefault(SFDataComponents.RELOAD_TICKS.get(), 0) > 0) return;

        int needed = maxMag - currentAmmo;
        int consumed = consumeBullets(player, bulletType, needed);
        if (consumed <= 0) return;

        // Add ammo immediately, but prevent firing until reload timer expires
        stack.set(SFDataComponents.MAGAZINE_AMMO.get(), currentAmmo + consumed);
        stack.set(SFDataComponents.RELOAD_TICKS.get(), reloadTime);
        // Set custom model data to signal reload animation
        stack.set(DataComponents.CUSTOM_MODEL_DATA,
                new CustomModelData(List.of(), List.of(), List.of(), List.of(1)));
    }

    /** Tick down reload timer on held guns. Called every server player tick. */
    public static void onPlayerTick(TickEvent.PlayerTickEvent.Post event) {
        if (event.player().level().isClientSide()) return;
        Player player = event.player();
        ItemStack stack = player.getMainHandItem();
        int ticks = stack.getOrDefault(SFDataComponents.RELOAD_TICKS.get(), 0);
        if (ticks > 0) {
            ticks--;
            stack.set(SFDataComponents.RELOAD_TICKS.get(), ticks);
            if (ticks == 0) {
                // Clear reload animation
                stack.set(DataComponents.CUSTOM_MODEL_DATA, CustomModelData.EMPTY);
            }
        }
    }

    /** Consume up to {@code max} bullets of the given type from player inventory. */
    private static int consumeBullets(Player player, Item bulletType, int max) {
        int consumed = 0;
        for (int i = 0; i < player.getInventory().getContainerSize() && consumed < max; i++) {
            ItemStack slot = player.getInventory().getItem(i);
            if (slot.getItem() == bulletType) {
                int take = Math.min(slot.getCount(), max - consumed);
                slot.shrink(take);
                consumed += take;
            }
        }
        return consumed;
    }
}

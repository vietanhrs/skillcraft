package com.example.skillcraft.mana;

import com.example.skillcraft.item.LightningBook;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;

/**
 * Forge-bus listeners that keep client mana in sync with the server whenever
 * the player's session state changes (login, respawn, dimension change).
 */
public class ManaEvents {

    /**
     * Passive mana regeneration rate at level 1, in mana per second.
     * Scales as {@code REGEN_BASE * level^REGEN_EXPONENT}, reaching
     * approximately 6.59/s at level 30.
     */
    private static final double REGEN_BASE = 1.1;
    /** Exponent calibrated so that level 30 yields ~6.59/s. */
    private static final double REGEN_EXPONENT = Math.log(6.59 / REGEN_BASE) / Math.log(30); // ≈ 0.5263

    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ManaNetwork.syncMana(player);
        }
    }

    public static void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ManaNetwork.syncMana(player);
        }
    }

    public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            ManaNetwork.syncMana(player);
        }
    }

    /**
     * Passive mana regeneration: accumulates fractional mana each server tick
     * and flushes whole-number gains to the player's mana pool.
     */
    public static void onPlayerTick(TickEvent.PlayerTickEvent.Post event) {
        if (!(event.player() instanceof ServerPlayer player)) return;
        if (!ManaHelper.hasManaBar(player)) return;

        int current = ManaHelper.getMana(player);
        int max = ManaHelper.getMaxMana(player);
        if (current >= max) return;

        int level = player.experienceLevel;
        if (level <= 0) return;

        double regenPerTick = (REGEN_BASE * Math.pow(level, REGEN_EXPONENT) + ManaHelper.getRegenBonus(player)) / 20.0;

        float acc = player.getPersistentData().getFloatOr(ManaHelper.KEY_REGEN_ACC, 0f);
        acc += (float) regenPerTick;

        if (acc >= 1f) {
            int toAdd = (int) acc;
            acc -= toAdd;
            ManaHelper.setMana(player, current + toAdd);
            ManaNetwork.syncMana(player);
        }

        player.getPersistentData().putFloat(ManaHelper.KEY_REGEN_ACC, acc);
    }

    /**
     * Anvil recipe: two Lightning Books of the same level combine into one at level+1.
     * Level 1 + Level 1 → Level 2 (2 strikes), Level 2 + Level 2 → Level 3 (4 strikes).
     */
    public static void onAnvilUpdate(AnvilUpdateEvent event) {
        ItemStack left = event.getLeft();
        ItemStack right = event.getRight();

        if (!(left.getItem() instanceof LightningBook) || !(right.getItem() instanceof LightningBook)) return;

        int leftLevel = LightningBook.getLevel(left);
        int rightLevel = LightningBook.getLevel(right);
        if (leftLevel != rightLevel || leftLevel >= 3) return;

        int newLevel = leftLevel + 1;
        event.setOutput(LightningBook.ofLevel(left.getItem(), newLevel));
        event.setCost(newLevel * 5L);
        event.setMaterialCost(1);
    }
}

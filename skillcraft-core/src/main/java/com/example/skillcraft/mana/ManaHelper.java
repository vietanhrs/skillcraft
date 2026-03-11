package com.example.skillcraft.mana;

import net.minecraft.world.entity.player.Player;

/**
 * Server-side mana storage backed by the player's persistent NBT data.
 * Persistent data survives death (Forge preserves it across respawns).
 */
public class ManaHelper {

    public static final int DEFAULT_MAX_MANA = 100;
    public static final int LIGHTNING_COST = 30;

    private static final String KEY_HAS_MANA = "skillcraft_has_mana";
    private static final String KEY_MANA = "skillcraft_mana";
    private static final String KEY_MAX_MANA = "skillcraft_max_mana";
    static final String KEY_REGEN_ACC = "skillcraft_regen_acc";
    static final String KEY_REGEN_BONUS = "skillcraft_regen_bonus";

    public static boolean hasManaBar(Player player) {
        return player.getPersistentData().getBooleanOr(KEY_HAS_MANA, false);
    }

    /** Grant a fresh mana bar (idempotent — safe to call on re-drink). */
    public static void giveManaBar(Player player) {
        player.getPersistentData().putBoolean(KEY_HAS_MANA, true);
        if (!player.getPersistentData().contains(KEY_MAX_MANA)) {
            player.getPersistentData().putInt(KEY_MAX_MANA, DEFAULT_MAX_MANA);
        }
        // Refill mana to full whenever the potion is drunk
        player.getPersistentData().putInt(KEY_MANA, getMaxMana(player));
    }

    public static int getMana(Player player) {
        return player.getPersistentData().getIntOr(KEY_MANA, 0);
    }

    public static int getMaxMana(Player player) {
        int v = player.getPersistentData().getIntOr(KEY_MAX_MANA, 0);
        return v > 0 ? v : DEFAULT_MAX_MANA;
    }

    public static void setMana(Player player, int value) {
        int clamped = Math.max(0, Math.min(value, getMaxMana(player)));
        player.getPersistentData().putInt(KEY_MANA, clamped);
    }

    /** Returns permanent mana regen bonus (mana/s) accumulated from extra potions. */
    public static float getRegenBonus(Player player) {
        return player.getPersistentData().getFloatOr(KEY_REGEN_BONUS, 0f);
    }

    /** Adds {@code amount} mana/s to the player's permanent regen bonus. */
    public static void addRegenBonus(Player player, float amount) {
        player.getPersistentData().putFloat(KEY_REGEN_BONUS,
                getRegenBonus(player) + amount);
    }

    /** Increases the player's max mana without changing current mana. */
    public static void increaseMaxMana(Player player, int amount) {
        player.getPersistentData().putInt(KEY_MAX_MANA, getMaxMana(player) + amount);
    }

    /**
     * Attempts to drain {@code amount} mana from the player.
     * 
     * @return true if the player had enough mana and it was deducted.
     */
    public static boolean drainMana(Player player, int amount) {
        if (!hasManaBar(player))
            return false;
        int current = getMana(player);
        if (current < amount)
            return false;
        setMana(player, current - amount);
        return true;
    }
}

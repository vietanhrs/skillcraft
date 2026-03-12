package com.example.specialforces.item;

import com.example.specialforces.client.ScopeState;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SniperRifle extends Item {

    public static final int COOLDOWN_TICKS = 40;
    public static final Map<UUID, Integer> SERVER_ZOOM = new HashMap<>();

    public SniperRifle(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (hand != InteractionHand.MAIN_HAND) return InteractionResult.PASS;
        ItemStack stack = player.getItemInHand(hand);
        if (player.getCooldowns().isOnCooldown(stack)) return InteractionResult.FAIL;
        if (level.isClientSide()) {
            // Use ScopeState.zoomLevel as the source of truth on client.
            // SERVER_ZOOM is only reset by handleSniperShot (server-side), so it
            // diverges after a shot and would compute the wrong next level here.
            int next = (ScopeState.zoomLevel + 1) % 3;
            ScopeState.zoomLevel = next;
        } else {
            int next = (getZoom(player) + 1) % 3;
            setZoom(player, next);
        }
        return InteractionResult.SUCCESS;
    }

    public static int getZoom(Player player) { return SERVER_ZOOM.getOrDefault(player.getUUID(), 0); }
    public static void setZoom(Player player, int lvl) { SERVER_ZOOM.put(player.getUUID(), lvl); }
}

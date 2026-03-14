package com.example.skillcraft.item;

import com.example.skillcraft.mana.ManaHelper;
import com.example.skillcraft.mana.ManaNetwork;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

/**
 * A drinkable item that grants the player a permanent mana bar (or refills it).
 * After drinking, the empty glass bottle is returned — just like vanilla potions.
 */
public class ManaPotion extends Item {

    public ManaPotion(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        player.startUsingItem(hand);
        return InteractionResult.CONSUME;
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int remainingUseDuration) {
        // Mirror vanilla Consumable logic: play drink sound every 4 ticks after
        // the first ~22% of the use duration has elapsed.
        int total = getUseDuration(stack, entity);
        int elapsed = total - remainingUseDuration;
        if (elapsed > (int)(total * 0.21875f) && elapsed % 4 == 0) {
            float pitch = entity.getRandom().nextFloat() * 0.1f + 0.9f;
            level.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                    SoundEvents.GENERIC_DRINK, SoundSource.PLAYERS, 0.5f, pitch);
        }
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (entity instanceof ServerPlayer player) {
            if (ManaHelper.hasManaBar(player)) {
                ManaHelper.increaseMaxMana(player, 50);
                ManaHelper.addRegenBonus(player, 1f);
            }
            ManaHelper.giveManaBar(player);
            ManaNetwork.syncMana(player);
        }

        if (entity instanceof Player player && !player.getAbilities().instabuild) {
            stack.shrink(1);
        }

        // Return the glass bottle (vanilla potion behaviour)
        if (entity instanceof Player player && !player.getAbilities().instabuild) {
            ItemStack bottle = new ItemStack(Items.GLASS_BOTTLE);
            if (stack.isEmpty()) return bottle;
            if (!player.getInventory().add(bottle)) {
                player.drop(bottle, false);
            }
        }
        return stack;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 32;
    }

    @Override
    public ItemUseAnimation getUseAnimation(ItemStack stack) {
        return ItemUseAnimation.DRINK;
    }
}

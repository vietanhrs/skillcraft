package com.example.specialforces.item;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class NightGoggles extends Item {

    public NightGoggles(Properties properties) {
        super(properties);
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, EquipmentSlot slot) {
        if (!(entity instanceof Player player)) return;
        if (slot != EquipmentSlot.HEAD) return;
        player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 300, 0, false, false));
    }
}

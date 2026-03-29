package com.example.specialforces.item;

import com.example.specialforces.entity.GlowStickEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class GlowStick extends Item {

    public static final int DEFAULT_COLOR = 0xCCFF00; // yellow-green

    public GlowStick(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.SNOWBALL_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F);

        if (!level.isClientSide()) {
            int color = getColor(stack);
            GlowStickEntity entity = new GlowStickEntity(level,
                    player.getX(), player.getEyeY() - 0.1, player.getZ(), color);
            entity.setOwner(player);
            entity.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F);
            level.addFreshEntity(entity);
        }

        player.awardStat(Stats.ITEM_USED.get(this));
        stack.consume(1, player);

        return InteractionResult.SUCCESS;
    }

    public static int getColor(ItemStack stack) {
        if (stack.has(net.minecraft.core.component.DataComponents.DYED_COLOR)) {
            return stack.get(net.minecraft.core.component.DataComponents.DYED_COLOR).rgb();
        }
        return DEFAULT_COLOR;
    }
}

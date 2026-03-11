package com.example.skillcraft.item;

import com.example.skillcraft.mana.ManaHelper;
import com.example.skillcraft.mana.ManaNetwork;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

/**
 * Right-click to fire a targeted lightning strike at any entity within 20 blocks.
 *
 * <p>Requirements:
 * <ul>
 *   <li>The player must have a mana bar (obtained from a Mana Potion).</li>
 *   <li>The player must have at least {@link ManaHelper#LIGHTNING_COST} mana.</li>
 *   <li>An entity must be in the player's line of sight within 20 blocks.</li>
 * </ul>
 */
public class LightningBook extends Item {

    private static final double MAX_RANGE = 20.0;
    private static final String NBT_LEVEL = "skillcraft_book_level";

    public LightningBook(Properties properties) {
        super(properties);
    }

    /** Returns the level of this book (1–3). Defaults to 1 if no data is stored. */
    public static int getLevel(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        if (data == null) return 1;
        int level = data.copyTag().getIntOr(NBT_LEVEL, 0);
        return (level >= 1 && level <= 3) ? level : 1;
    }

    /** Creates a new Lightning Book stack at the given level. */
    public static ItemStack ofLevel(Item item, int level) {
        ItemStack stack = new ItemStack(item);
        if (level > 1) {
            CompoundTag tag = new CompoundTag();
            tag.putInt(NBT_LEVEL, level);
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        }
        return stack;
    }

    @Override
    public Component getName(ItemStack stack) {
        int level = getLevel(stack);
        if (level <= 1) return super.getName(stack);
        String suffix = level == 2 ? " II" : " III";
        return Component.translatable(getDescriptionId()).append(suffix);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }

        if (!ManaHelper.hasManaBar(player)) {
            player.displayClientMessage(
                    Component.translatable("item.skillcraft.lightning_book.no_mana"), true);
            return InteractionResult.FAIL;
        }

        if (ManaHelper.getMana(player) < ManaHelper.LIGHTNING_COST) {
            player.displayClientMessage(
                    Component.translatable("item.skillcraft.lightning_book.not_enough_mana"), true);
            return InteractionResult.FAIL;
        }

        Entity target = findTarget(level, player);
        if (target == null) {
            player.displayClientMessage(
                    Component.translatable("item.skillcraft.lightning_book.no_target"), true);
            return InteractionResult.FAIL;
        }

        // Drain mana and strike
        ManaHelper.drainMana(player, ManaHelper.LIGHTNING_COST);
        if (player instanceof ServerPlayer sp) {
            ManaNetwork.syncMana(sp);
        }

        int strikes = 1 << (getLevel(player.getItemInHand(hand)) - 1); // 1, 2, or 4
        for (int i = 0; i < strikes; i++) {
            LightningBolt bolt = new LightningBolt(EntityType.LIGHTNING_BOLT, level);
            bolt.setPos(target.getX(), target.getY(), target.getZ());
            level.addFreshEntity(bolt);
        }

        return InteractionResult.SUCCESS;
    }

    /**
     * Ray-casts from the player's eye position in their look direction and
     * returns the closest entity within {@link #MAX_RANGE} blocks, or null.
     */
    private static Entity findTarget(Level level, Player player) {
        Vec3 from   = player.getEyePosition();
        Vec3 look   = player.getLookAngle();
        Vec3 to     = from.add(look.scale(MAX_RANGE));
        AABB search = player.getBoundingBox()
                            .expandTowards(look.scale(MAX_RANGE))
                            .inflate(1.0);

        double closestDist = Double.MAX_VALUE;
        Entity closest     = null;

        for (Entity e : level.getEntitiesOfClass(Entity.class, search,
                entity -> entity != player && entity.isPickable())) {
            AABB hitBox = e.getBoundingBox().inflate(e.getPickRadius());
            Optional<Vec3> hit = hitBox.clip(from, to);
            if (hit.isPresent()) {
                double d = from.distanceTo(hit.get());
                if (d < closestDist) {
                    closestDist = d;
                    closest = e;
                }
            }
        }
        return closest;
    }
}

package com.example.enchantmentshrine.world.gen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class EnchantmentShrineFeature extends Feature<NoneFeatureConfiguration> {

    public EnchantmentShrineFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource random = context.random();

        // origin is the first air block above the surface (from WORLD_SURFACE_WG heightmap).
        // Verify there is solid ground below; skip generation over water / void.
        if (!level.getBlockState(origin.below()).isSolid()) {
            return false;
        }

        buildShrine(level, random, origin);
        return true;
    }

    // -------------------------------------------------------------------------
    // Structure layout (all offsets are relative to `origin`)
    //
    //  origin  = enchanting table position (at surface air level)
    //  y = -1  = stone brick floor
    //  y =  0  = enchanting table + bookshelf ring (12 bookshelves)
    //  y =  1  = 3 upper bookshelves  →  15 total (max enchanting power)
    //  y = 0-3 = corner pillars
    //  y =  4  = lanterns atop pillars
    //  chest at (0, 0, 3) facing north, outside the bookshelf ring
    // -------------------------------------------------------------------------
    protected void buildShrine(WorldGenLevel level, RandomSource random, BlockPos origin) {

        BlockState stoneBricks = Blocks.STONE_BRICKS.defaultBlockState();
        BlockState bookshelf   = Blocks.BOOKSHELF.defaultBlockState();

        // ---- FLOOR (7×7) ----
        for (int x = -3; x <= 3; x++) {
            for (int z = -3; z <= 3; z++) {
                setBlock(level, origin.offset(x, -1, z), stoneBricks);
                // Fill downward so the shrine doesn't float over caves / ravines
                for (int y = -2; y >= -6; y--) {
                    BlockPos below = origin.offset(x, y, z);
                    if (level.isEmptyBlock(below)) {
                        setBlock(level, below, Blocks.COBBLESTONE.defaultBlockState());
                    } else {
                        break;
                    }
                }
            }
        }

        // ---- CORNER PILLARS (height 4, lantern on top) ----
        int[][] corners = {{-3, -3}, {3, -3}, {-3, 3}, {3, 3}};
        for (int[] c : corners) {
            for (int y = 0; y <= 3; y++) {
                setBlock(level, origin.offset(c[0], y, c[1]), stoneBricks);
            }
            setBlock(level, origin.offset(c[0], 4, c[1]),
                    Blocks.LANTERN.defaultBlockState().setValue(LanternBlock.HANGING, false));
        }

        // ---- ENCHANTING TABLE ----
        setBlock(level, origin, Blocks.ENCHANTING_TABLE.defaultBlockState());

        // ---- BOOKSHELVES — bottom ring (12, same y as table) ----
        // North side (z = -2, x ∈ [-1, 1])
        for (int x = -1; x <= 1; x++) setBlock(level, origin.offset(x,  0, -2), bookshelf);
        // South side (z =  2, x ∈ [-1, 1])
        for (int x = -1; x <= 1; x++) setBlock(level, origin.offset(x,  0,  2), bookshelf);
        // West side  (x = -2, z ∈ [-1, 1])
        for (int z = -1; z <= 1; z++) setBlock(level, origin.offset(-2, 0,  z), bookshelf);
        // East side  (x =  2, z ∈ [-1, 1])
        for (int z = -1; z <= 1; z++) setBlock(level, origin.offset( 2, 0,  z), bookshelf);

        // ---- BOOKSHELVES — upper row (+y=1, 3 more = 15 total) ----
        setBlock(level, origin.offset(-2, 1,  0), bookshelf);
        setBlock(level, origin.offset( 2, 1,  0), bookshelf);
        setBlock(level, origin.offset( 0, 1, -2), bookshelf);

        // ---- CHEST (just south of the bookshelf ring, facing the table) ----
        BlockPos chestPos = origin.offset(0, 0, 3);
        setBlock(level, chestPos,
                Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, Direction.NORTH));
        fillChest(level, chestPos);
    }

    protected void fillChest(WorldGenLevel level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof ChestBlockEntity chest) {
            // Bottles o' Enchanting (XP bottles)
            chest.setItem(0, new ItemStack(Items.EXPERIENCE_BOTTLE, 64));
            chest.setItem(1, new ItemStack(Items.EXPERIENCE_BOTTLE, 64));
            chest.setItem(2, new ItemStack(Items.EXPERIENCE_BOTTLE, 32));
            // Lapis lazuli (needed for enchanting)
            chest.setItem(3, new ItemStack(Items.LAPIS_LAZULI, 64));
            chest.setItem(4, new ItemStack(Items.LAPIS_LAZULI, 64));
            chest.setItem(5, new ItemStack(Items.LAPIS_LAZULI, 64));
            // Books (for anvil + enchanted book combos)
            chest.setItem(6, new ItemStack(Items.BOOK, 64));
            chest.setItem(7, new ItemStack(Items.BOOK, 64));
            chest.setItem(8, new ItemStack(Items.BOOK, 32));
            // Full iron tool set
            chest.setItem(9,  new ItemStack(Items.IRON_SWORD));
            chest.setItem(10, new ItemStack(Items.IRON_PICKAXE));
            chest.setItem(11, new ItemStack(Items.IRON_AXE));
            chest.setItem(12, new ItemStack(Items.IRON_SHOVEL));
            chest.setItem(13, new ItemStack(Items.IRON_HOE));
            // Full iron armor set
            chest.setItem(14, new ItemStack(Items.IRON_HELMET));
            chest.setItem(15, new ItemStack(Items.IRON_CHESTPLATE));
            chest.setItem(16, new ItemStack(Items.IRON_LEGGINGS));
            chest.setItem(17, new ItemStack(Items.IRON_BOOTS));
        }
    }
}

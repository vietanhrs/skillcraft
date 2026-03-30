package com.example.specialforces.world.gen.feature;

import com.example.specialforces.init.SFItems;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class SpawnChestFeature extends Feature<NoneFeatureConfiguration> {

    private static final int CHEST_X = 8;
    private static final int CHEST_Z = 8;
    private static final int TARGET_CHUNK_X = CHEST_X >> 4;
    private static final int TARGET_CHUNK_Z = CHEST_Z >> 4;

    public SpawnChestFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        int chunkX = context.origin().getX() >> 4;
        int chunkZ = context.origin().getZ() >> 4;

        if (chunkX != TARGET_CHUNK_X || chunkZ != TARGET_CHUNK_Z) return false;

        int y = level.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, CHEST_X, CHEST_Z);
        BlockPos pos = new BlockPos(CHEST_X, y, CHEST_Z);

        if (level.getBlockState(pos).is(Blocks.CHEST)) return false;

        level.setBlock(pos, Blocks.CHEST.defaultBlockState(), 3);

        if (level.getBlockEntity(pos) instanceof ChestBlockEntity chest) {
            chest.setItem(0, new ItemStack(SFItems.SNIPER.get(), 1));
            chest.setItem(1, new ItemStack(SFItems.SNIPER_BULLET.get(), 64));
            chest.setItem(2, new ItemStack(SFItems.NIGHT_GOGGLES.get(), 1));
            chest.setItem(3, new ItemStack(SFItems.GLOW_STICK.get(), 64));
            chest.setItem(4, new ItemStack(SFItems.M4A1.get(), 1));
            chest.setItem(5, new ItemStack(SFItems.AR_BULLET.get(), 64));
            chest.setItem(6, new ItemStack(SFItems.AR_BULLET.get(), 64));
            chest.setItem(7, new ItemStack(SFItems.AR_BULLET.get(), 64));
        }

        return true;
    }
}

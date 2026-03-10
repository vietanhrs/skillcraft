package com.example.skillcraft.world.gen.feature;

import com.example.skillcraft.SkillcraftConfig;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/**
 * Generates a Mana Castle near the world origin whenever
 * {@code debugSpawnNearby = true} in the config.
 */
public class ManaCastleDebugFeature extends ManaCastleFeature {

    private static final int SPAWN_CHUNK_RADIUS = 2;

    public ManaCastleDebugFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> ctx) {
        if (!SkillcraftConfig.DEBUG_SPAWN_NEARBY.get()) return false;

        BlockPos origin = ctx.origin();
        int chunkX = origin.getX() >> 4;
        int chunkZ = origin.getZ() >> 4;

        if (Math.abs(chunkX) > SPAWN_CHUNK_RADIUS || Math.abs(chunkZ) > SPAWN_CHUNK_RADIUS) {
            return false;
        }

        WorldGenLevel level = ctx.level();
        if (!level.getBlockState(origin.below()).isFaceSturdy(level, origin.below(), Direction.UP)) {
            return false;
        }

        buildCastle(level, ctx.random(), origin);
        return true;
    }
}

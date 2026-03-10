package com.example.enchantmentshrine.world.gen.feature;

import com.example.enchantmentshrine.ShrineConfig;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/**
 * A zero-rarity-filter copy of the enchantment shrine feature that only builds
 * when {@code debugSpawnNearby} is {@code true} in the common config AND the
 * generation attempt lands within two chunks of the world origin (chunk 0,0).
 *
 * <p>This is registered alongside the normal shrine placed-feature but its
 * placed-feature JSON uses {@code minecraft:count} instead of
 * {@code minecraft:rarity_filter}, so it is always attempted. The {@link #place}
 * method short-circuits immediately when the flag is off, meaning the runtime
 * cost in normal gameplay is a single boolean check per chunk.
 */
public class EnchantmentShrineDebugFeature extends EnchantmentShrineFeature {

    /** Radius in chunks around (0, 0) that qualifies as "near spawn". */
    private static final int SPAWN_CHUNK_RADIUS = 2;

    public EnchantmentShrineDebugFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        if (!ShrineConfig.DEBUG_SPAWN_NEARBY.get()) return false;

        BlockPos origin = context.origin();
        int chunkX = origin.getX() >> 4;
        int chunkZ = origin.getZ() >> 4;

        // Only build inside the spawn-chunk radius.
        if (Math.abs(chunkX) > SPAWN_CHUNK_RADIUS || Math.abs(chunkZ) > SPAWN_CHUNK_RADIUS) {
            return false;
        }

        WorldGenLevel level = context.level();
        if (!level.getBlockState(origin.below()).isFaceSturdy(level, origin.below(), Direction.UP)) return false;

        buildShrine(level, context.random(), origin);
        return true;
    }
}

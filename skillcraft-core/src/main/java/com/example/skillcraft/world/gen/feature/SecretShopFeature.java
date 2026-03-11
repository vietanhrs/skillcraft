package com.example.skillcraft.world.gen.feature;

import com.example.skillcraft.entity.SecretMerchant;
import com.example.skillcraft.init.ModEntityTypes;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/**
 * Generates a small open-air market tent with a stationary
 * {@link SecretMerchant}.
 *
 * <p>
 * Layout (all offsets relative to {@code origin}):
 * 
 * <pre>
 *  y = -1  : 5×5 oak-plank floor
 *  y = 0–2 : oak-fence posts at each of the four corners (x=±2, z=±2)
 *  y = 3   : oak-fence frame connecting the four posts (perimeter ring)
 *  y = 4   : white-wool roof (5×5)
 *  merchant entity spawned at origin centre
 * </pre>
 */
public class SecretShopFeature extends Feature<NoneFeatureConfiguration> {

    public SecretShopFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> ctx) {
        WorldGenLevel level = ctx.level();
        BlockPos origin = ctx.origin();

        if (!level.getBlockState(origin.below()).isFaceSturdy(level, origin.below(), Direction.UP)) {
            return false;
        }

        buildShop(level, ctx.random(), origin);
        return true;
    }

    // -------------------------------------------------------------------------

    protected void buildShop(WorldGenLevel level, RandomSource random, BlockPos origin) {
        BlockState planks = Blocks.OAK_PLANKS.defaultBlockState();
        BlockState fence = Blocks.OAK_FENCE.defaultBlockState();
        BlockState wool = Blocks.WHITE_WOOL.defaultBlockState();

        // ---- FLOOR (5×5) ----
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                setBlock(level, origin.offset(x, -1, z), planks);
            }
        }

        // ---- CORNER FENCE POSTS (y=0..2) ----
        int[] cs = { -2, 2 };
        for (int cx : cs) {
            for (int cz : cs) {
                for (int y = 0; y <= 2; y++) {
                    setBlock(level, origin.offset(cx, y, cz), fence);
                }
            }
        }

        // ---- FENCE FRAME at y=3 (perimeter ring connecting posts) ----
        for (int x = -2; x <= 2; x++) {
            setBlock(level, origin.offset(x, 3, -2), fence);
            setBlock(level, origin.offset(x, 3, 2), fence);
        }
        for (int z = -1; z <= 1; z++) {
            setBlock(level, origin.offset(-2, 3, z), fence);
            setBlock(level, origin.offset(2, 3, z), fence);
        }

        // ---- WOOL ROOF (5×5 at y=4) ----
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                setBlock(level, origin.offset(x, 4, z), wool);
            }
        }

        // ---- MERCHANT ENTITY ----
        spawnMerchant(level, origin);
    }

    protected void spawnMerchant(WorldGenLevel level, BlockPos origin) {
        if (!(level instanceof ServerLevelAccessor sla))
            return;

        SecretMerchant merchant = ModEntityTypes.SECRET_MERCHANT.get().create(sla.getLevel(),
                EntitySpawnReason.COMMAND);
        if (merchant == null)
            return;

        merchant.setPos(
                origin.getX() + 0.5,
                origin.getY(),
                origin.getZ() + 0.5);
        merchant.setPersistenceRequired();
        sla.getLevel().addFreshEntity(merchant);
    }
}

package com.example.skillcraft.world.gen.feature;

import com.example.skillcraft.init.ModItems;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.item.ItemStack;

/**
 * Generates a dark, rook-shaped Mana Castle at the surface.
 *
 * <p>Layout (all y offsets relative to {@code origin}, the first surface-air block):
 * <pre>
 *  y = -1 : 9×9 deepslate-brick floor
 *  y = 0..13 : 2-block-thick deepslate-brick walls (hollow 5×5 interior)
 *               south face has a 1×2 entrance at x=0
 *  y = 0..13 : ladder on north interior wall (x=0, z=-2) — player climbs to top
 *  y = 14   : top floor (5×5 interior + outer ring continuation)
 *              a chest at the centre contains the Mana Potion and gold ingots
 *  y = 14   : battlements base (outer ring continues)
 *  y = 15   : merlons — deepslate bricks at alternating outer-perimeter positions
 * </pre>
 */
public class ManaCastleFeature extends Feature<NoneFeatureConfiguration> {

    public ManaCastleFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> ctx) {
        WorldGenLevel level  = ctx.level();
        BlockPos      origin = ctx.origin();

        if (!level.getBlockState(origin.below()).isFaceSturdy(level, origin.below(), Direction.UP)) {
            return false;
        }

        buildCastle(level, ctx.random(), origin);
        return true;
    }

    // -------------------------------------------------------------------------

    protected void buildCastle(WorldGenLevel level, RandomSource random, BlockPos origin) {
        BlockState deepslate = Blocks.DEEPSLATE_BRICKS.defaultBlockState();
        BlockState ladder    = Blocks.LADDER.defaultBlockState()
                                    .setValue(LadderBlock.FACING, Direction.SOUTH);

        // ---- FLOOR (9×9) + downward fill ----
        for (int x = -4; x <= 4; x++) {
            for (int z = -4; z <= 4; z++) {
                setBlock(level, origin.offset(x, -1, z), deepslate);
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

        // ---- WALLS y=0..13 (9×9 shell, hollow 5×5 interior) ----
        for (int y = 0; y <= 13; y++) {
            for (int x = -4; x <= 4; x++) {
                for (int z = -4; z <= 4; z++) {
                    if (isWall(x, z)) {
                        setBlock(level, origin.offset(x, y, z), deepslate);
                    }
                }
            }
        }

        // ---- ENTRANCE: clear south-face blocks at x=0, z=3 and z=4, y=0 and y=1 ----
        for (int y = 0; y <= 1; y++) {
            setBlock(level, origin.offset(0, y, 3), Blocks.AIR.defaultBlockState());
            setBlock(level, origin.offset(0, y, 4), Blocks.AIR.defaultBlockState());
        }

        // ---- LADDER on north interior wall (x=0, z=-2), y=0..13 ----
        for (int y = 0; y <= 13; y++) {
            setBlock(level, origin.offset(0, y, -2), ladder);
        }

        // ---- TOP FLOOR (y=14): 5×5 interior + outer ring ----
        for (int x = -4; x <= 4; x++) {
            for (int z = -4; z <= 4; z++) {
                if (x == 0 && z == 0) continue; // reserved for chest
                setBlock(level, origin.offset(x, 14, z), deepslate);
            }
        }

        // ---- CHEST at centre of the top floor ----
        BlockPos chestPos = origin.offset(0, 14, 0);
        setBlock(level, chestPos,
                Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, Direction.NORTH));
        fillChest(level, chestPos);

        // ---- MERLONS (y=15): alternating blocks on outer perimeter ----
        for (int x = -4; x <= 4; x++) {
            for (int z = -4; z <= 4; z++) {
                boolean outerPerimeter = (x == -4 || x == 4 || z == -4 || z == 4);
                if (outerPerimeter && (x + z) % 2 == 0) {
                    setBlock(level, origin.offset(x, 15, z), deepslate);
                }
            }
        }

        // ---- INTERIOR TORCHES for lighting ----
        setBlock(level, origin.offset( 0, 5,  2),
                Blocks.WALL_TORCH.defaultBlockState()
                        .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH));
        setBlock(level, origin.offset(-2, 5,  0),
                Blocks.WALL_TORCH.defaultBlockState()
                        .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST));
        setBlock(level, origin.offset( 2, 5,  0),
                Blocks.WALL_TORCH.defaultBlockState()
                        .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.WEST));
    }

    protected void fillChest(WorldGenLevel level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof ChestBlockEntity chest) {
            // Mana Potion — the main reward
            chest.setItem(0, new ItemStack(ModItems.MANA_POTION.get(), 1));
            // Gold ingots: enough to buy the Lightning Book (63 total)
            chest.setItem(1, new ItemStack(net.minecraft.world.item.Items.GOLD_INGOT, 32));
            chest.setItem(2, new ItemStack(net.minecraft.world.item.Items.GOLD_INGOT, 31));
        }
    }

    // ---- helpers ----

    /** True if (x,z) falls inside the 2-thick wall ring of the 9×9 tower. */
    private static boolean isWall(int x, int z) {
        return (Math.abs(x) >= 3 || Math.abs(z) >= 3)
                && Math.abs(x) <= 4 && Math.abs(z) <= 4;
    }
}

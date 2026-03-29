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
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.item.ItemStack;

/**
 * Generates a dark, rook-shaped Mana Castle at the surface.
 *
 * <p>Layout (y offsets relative to {@code origin}):
 * <pre>
 *  y = -1      : 11×11 cobbled-deepslate foundation
 *  y = 0       : 11×11 base course (cobbled deepslate)
 *  y = 1       : stair taper on outer ring, 9×9 wall start
 *  y = 2..13   : 9×9 walls with material bands + polished corner pillars
 *  y = 0..6    : corner buttresses (polished deepslate arms)
 *  y = 0..2    : 3-wide arched entrance on south face
 *  y = 6..8    : lower iron-bar windows on N/E/W faces
 *  y = 0..8    : ladder on north interior wall to reach platform
 *  y = 8       : interior 5×5 platform (chest access)
 *  y = 9       : chest at centre of platform
 *  y = 11..12  : upper iron-bar windows on N/E/W faces
 *  y = 14      : 9×9 roof
 *  y = 15..16  : battlements with wall blocks and merlons
 * </pre>
 */
public class ManaCastleFeature extends Feature<NoneFeatureConfiguration> {

    public ManaCastleFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> ctx) {
        WorldGenLevel level = ctx.level();
        BlockPos origin = ctx.origin();

        if (!level.getBlockState(origin.below()).isFaceSturdy(level, origin.below(), Direction.UP)) {
            return false;
        }

        buildCastle(level, ctx.random(), origin);
        return true;
    }

    // -------------------------------------------------------------------------

    protected void buildCastle(WorldGenLevel level, RandomSource random, BlockPos origin) {
        // -- Block palette --
        BlockState deepBrick = Blocks.DEEPSLATE_BRICKS.defaultBlockState();
        BlockState deepTile = Blocks.DEEPSLATE_TILES.defaultBlockState();
        BlockState polished = Blocks.POLISHED_DEEPSLATE.defaultBlockState();
        BlockState cobbledDeep = Blocks.COBBLED_DEEPSLATE.defaultBlockState();
        BlockState air = Blocks.AIR.defaultBlockState();
        BlockState ladder = Blocks.LADDER.defaultBlockState()
                .setValue(LadderBlock.FACING, Direction.SOUTH);

        // ---- 1. FOUNDATION FILL (below 11×11) ----
        for (int x = -5; x <= 5; x++) {
            for (int z = -5; z <= 5; z++) {
                setBlock(level, origin.offset(x, -1, z), cobbledDeep);
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

        // ---- 2. BASE COURSE (y=0): 11×11 cobbled deepslate ----
        for (int x = -5; x <= 5; x++) {
            for (int z = -5; z <= 5; z++) {
                setBlock(level, origin.offset(x, 0, z), cobbledDeep);
            }
        }

        // ---- 3. BASE TAPER (y=1): stairs on outer ring, 9×9 inner ----
        for (int x = -5; x <= 5; x++) {
            for (int z = -5; z <= 5; z++) {
                if (Math.abs(x) == 5 || Math.abs(z) == 5) {
                    // Outer ring: stairs facing outward
                    Direction facing = getOutwardStairFacing(x, z);
                    if (facing != null) {
                        setBlock(level, origin.offset(x, 1, z),
                                Blocks.DEEPSLATE_BRICK_STAIRS.defaultBlockState()
                                        .setValue(StairBlock.FACING, facing)
                                        .setValue(StairBlock.HALF, Half.BOTTOM));
                    } else {
                        // Corners of taper ring: full block
                        setBlock(level, origin.offset(x, 1, z), cobbledDeep);
                    }
                } else if (Math.abs(x) <= 4 && Math.abs(z) <= 4) {
                    if (isWall(x, z)) {
                        setBlock(level, origin.offset(x, 1, z), cobbledDeep);
                    }
                }
            }
        }

        // ---- 4. WALLS (y=2..13): 9×9 shell with material bands ----
        for (int y = 2; y <= 13; y++) {
            BlockState wallMat = getWallMaterial(y, deepBrick, deepTile);
            for (int x = -4; x <= 4; x++) {
                for (int z = -4; z <= 4; z++) {
                    if (isWall(x, z)) {
                        BlockState mat = isCornerPillar(x, z) ? polished : wallMat;
                        setBlock(level, origin.offset(x, y, z), mat);
                    }
                }
            }
        }

        // ---- 5. CORNER BUTTRESSES (y=0..6) ----
        int[][] cornerSigns = {{-1, -1}, {-1, 1}, {1, -1}, {1, 1}};
        for (int[] c : cornerSigns) {
            int sx = c[0], sz = c[1];
            int cx = 4 * sx, cz = 4 * sz;
            // Side arms
            for (int y = 0; y <= 5; y++) {
                setBlock(level, origin.offset(cx + sx, y, cz), polished);
                setBlock(level, origin.offset(cx, y, cz + sz), polished);
            }
            // Diagonal block (bottom only)
            for (int y = 0; y <= 2; y++) {
                setBlock(level, origin.offset(cx + sx, y, cz + sz), cobbledDeep);
            }
            // Cap buttress arms with stairs at y=6
            setBlock(level, origin.offset(cx + sx, 6, cz),
                    Blocks.DEEPSLATE_BRICK_STAIRS.defaultBlockState()
                            .setValue(StairBlock.FACING, sx > 0 ? Direction.WEST : Direction.EAST)
                            .setValue(StairBlock.HALF, Half.TOP));
            setBlock(level, origin.offset(cx, 6, cz + sz),
                    Blocks.DEEPSLATE_BRICK_STAIRS.defaultBlockState()
                            .setValue(StairBlock.FACING, sz > 0 ? Direction.NORTH : Direction.SOUTH)
                            .setValue(StairBlock.HALF, Half.TOP));
        }

        // ---- 6. ENTRANCE: 3-wide, 3-tall arch on south face ----
        for (int ex = -1; ex <= 1; ex++) {
            for (int ey = 0; ey <= 2; ey++) {
                setBlock(level, origin.offset(ex, ey, 3), air);
                setBlock(level, origin.offset(ex, ey, 4), air);
            }
        }
        // Arch columns (polished deepslate)
        for (int ey = 0; ey <= 2; ey++) {
            setBlock(level, origin.offset(-2, ey, 4), polished);
            setBlock(level, origin.offset(2, ey, 4), polished);
        }
        // Arch top: stairs + keystone
        setBlock(level, origin.offset(-1, 3, 4),
                Blocks.DEEPSLATE_BRICK_STAIRS.defaultBlockState()
                        .setValue(StairBlock.FACING, Direction.EAST)
                        .setValue(StairBlock.HALF, Half.TOP));
        setBlock(level, origin.offset(1, 3, 4),
                Blocks.DEEPSLATE_BRICK_STAIRS.defaultBlockState()
                        .setValue(StairBlock.FACING, Direction.WEST)
                        .setValue(StairBlock.HALF, Half.TOP));
        setBlock(level, origin.offset(0, 3, 4), polished);

        // ---- 7. WINDOWS ----
        // Lower windows (y=6..8) on N, E, W faces
        placeWindows(level, origin, 6, 8, new int[]{-2, 2});
        // Upper windows (y=11..12) on N, E, W faces — single center window
        placeWindows(level, origin, 11, 12, new int[]{0});

        // ---- 8. CLEAR INTERIOR (y=0..7) ----
        for (int y = 0; y <= 7; y++) {
            for (int x = -2; x <= 2; x++) {
                for (int z = -2; z <= 2; z++) {
                    setBlock(level, origin.offset(x, y, z), air);
                }
            }
        }

        // ---- 9. LADDER (y=0..8) on north interior wall ----
        for (int y = 0; y <= 8; y++) {
            setBlock(level, origin.offset(0, y, -2), ladder);
        }

        // ---- 10. INTERIOR PLATFORM (y=8): 5×5 deepslate bricks ----
        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                if (x == 0 && z == -2) continue; // ladder position
                setBlock(level, origin.offset(x, 8, z), deepBrick);
            }
        }

        // ---- 11. CLEAR ABOVE PLATFORM (y=9..13) ----
        for (int y = 9; y <= 13; y++) {
            for (int x = -2; x <= 2; x++) {
                for (int z = -2; z <= 2; z++) {
                    if (y == 9 && x == 0 && z == 0) continue; // chest
                    setBlock(level, origin.offset(x, y, z), air);
                }
            }
        }

        // ---- 12. CHEST at centre of platform ----
        BlockPos chestPos = origin.offset(0, 9, 0);
        setBlock(level, chestPos,
                Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, Direction.SOUTH));
        fillChest(level, chestPos);

        // ---- 13. TOP FLOOR (y=14): full 9×9 roof ----
        for (int x = -4; x <= 4; x++) {
            for (int z = -4; z <= 4; z++) {
                setBlock(level, origin.offset(x, 14, z), deepBrick);
            }
        }

        // ---- 14. BATTLEMENTS (y=15..16) ----
        for (int x = -4; x <= 4; x++) {
            for (int z = -4; z <= 4; z++) {
                boolean outer = (Math.abs(x) == 4 || Math.abs(z) == 4);
                if (!outer) continue;
                // y=15: continuous wall-block parapet
                setBlock(level, origin.offset(x, 15, z),
                        Blocks.DEEPSLATE_BRICK_WALL.defaultBlockState());
                // y=16: merlons at corners, midpoints, and alternating positions
                boolean isCorner = (Math.abs(x) == 4 && Math.abs(z) == 4);
                boolean isMid = (x == 0 && Math.abs(z) == 4) || (z == 0 && Math.abs(x) == 4);
                if (isCorner || isMid || (Math.abs(x) + Math.abs(z)) % 2 == 0) {
                    BlockState merlonMat = isCorner ? polished : deepBrick;
                    setBlock(level, origin.offset(x, 16, z), merlonMat);
                }
            }
        }

        // ---- 15. LIGHTING ----
        // Interior: lantern on floor below platform
        setBlock(level, origin.offset(0, 0, 0),
                Blocks.LANTERN.defaultBlockState());
        // Interior: hanging lantern from platform ceiling
        setBlock(level, origin.offset(0, 7, 0),
                Blocks.LANTERN.defaultBlockState()
                        .setValue(LanternBlock.HANGING, true));
        // Interior: hanging lantern from roof above platform
        setBlock(level, origin.offset(0, 13, 0),
                Blocks.LANTERN.defaultBlockState()
                        .setValue(LanternBlock.HANGING, true));
        // Wall torches at mid-height
        setBlock(level, origin.offset(2, 4, 0),
                Blocks.WALL_TORCH.defaultBlockState()
                        .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.WEST));
        setBlock(level, origin.offset(-2, 4, 0),
                Blocks.WALL_TORCH.defaultBlockState()
                        .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.EAST));
        // Exterior: soul torches on top of entrance arch columns
        setBlock(level, origin.offset(-2, 3, 4),
                Blocks.SOUL_WALL_TORCH.defaultBlockState()
                        .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH));
        setBlock(level, origin.offset(2, 3, 4),
                Blocks.SOUL_WALL_TORCH.defaultBlockState()
                        .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH));
    }

    protected void fillChest(WorldGenLevel level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof ChestBlockEntity chest) {
            chest.setItem(0, new ItemStack(ModItems.MANA_POTION.get(), 1));
            chest.setItem(1, new ItemStack(net.minecraft.world.item.Items.GOLD_INGOT, 32));
            chest.setItem(2, new ItemStack(net.minecraft.world.item.Items.GOLD_INGOT, 31));
        }
    }

    // ---- helpers ----

    /** Places iron-bar windows on north, east, and west faces. */
    private void placeWindows(WorldGenLevel level, BlockPos origin,
                              int yMin, int yMax, int[] offsets) {
        BlockState bars = Blocks.IRON_BARS.defaultBlockState();
        BlockState air = Blocks.AIR.defaultBlockState();
        // North face (z=-4 outer, z=-3 inner)
        for (int wx : offsets) {
            for (int wy = yMin; wy <= yMax; wy++) {
                setBlock(level, origin.offset(wx, wy, -3), air);
                setBlock(level, origin.offset(wx, wy, -4), bars);
            }
        }
        // East face (x=4 outer, x=3 inner)
        for (int wz : offsets) {
            for (int wy = yMin; wy <= yMax; wy++) {
                setBlock(level, origin.offset(3, wy, wz), air);
                setBlock(level, origin.offset(4, wy, wz), bars);
            }
        }
        // West face (x=-4 outer, x=-3 inner)
        for (int wz : offsets) {
            for (int wy = yMin; wy <= yMax; wy++) {
                setBlock(level, origin.offset(-3, wy, wz), air);
                setBlock(level, origin.offset(-4, wy, wz), bars);
            }
        }
    }

    /** True if (x,z) falls inside the 2-thick wall ring of the 9×9 tower. */
    private static boolean isWall(int x, int z) {
        return (Math.abs(x) >= 3 || Math.abs(z) >= 3)
                && Math.abs(x) <= 4 && Math.abs(z) <= 4;
    }

    /** True if (x,z) is one of the 2×2 corner pillar positions. */
    private static boolean isCornerPillar(int x, int z) {
        return Math.abs(x) >= 3 && Math.abs(z) >= 3;
    }

    /** Returns the wall material for a given Y level (banded pattern). */
    private static BlockState getWallMaterial(int y, BlockState brick, BlockState tile) {
        if (y == 4 || y == 5 || y == 10 || y == 11) return tile;
        return brick;
    }

    /** Returns the outward-facing direction for a stair on the taper ring, or null for corners. */
    private static Direction getOutwardStairFacing(int x, int z) {
        boolean onX = Math.abs(x) == 5;
        boolean onZ = Math.abs(z) == 5;
        if (onX && onZ) return null; // corner — use full block
        if (onZ) return z > 0 ? Direction.NORTH : Direction.SOUTH;
        return x > 0 ? Direction.WEST : Direction.EAST;
    }
}

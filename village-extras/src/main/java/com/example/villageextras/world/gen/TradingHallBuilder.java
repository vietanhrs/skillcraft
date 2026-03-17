package com.example.villageextras.world.gen;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.villager.VillagerData;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.entity.npc.villager.VillagerType;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.GrindstoneBlock;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.state.properties.SlabType;

/**
 * Builds a trading hall with villagers locked into individual cells.
 *
 * <p>
 * Each cell is a 1×1 space. The villager is trapped by:
 * <ul>
 * <li>Job block in front at y=0 (between cell and aisle) — doubles as wall</li>
 * <li>Glass pane at y=1 above job block — player can see and trade through
 * it</li>
 * <li>Stone slab at y=2 above villager — prevents jumping</li>
 * <li>Separator columns on the sides (odd z) — prevents sideways movement</li>
 * <li>Outer wall on the back — prevents backward movement</li>
 * </ul>
 *
 * <pre>
 *  Width  : 9 blocks  (x = -4 … +4)
 *    x = -4      : left outer wall
 *    x = -3      : left villager cells
 *    x = -2      : left job blocks + glass (cell front wall)
 *    x = -1..+1  : 3-wide player aisle
 *    x = +2      : right job blocks + glass (cell front wall)
 *    x = +3      : right villager cells
 *    x = +4      : right outer wall
 * </pre>
 */
public class TradingHallBuilder {

    private static final int HALL_LENGTH = 28; // z = 0 .. 27
    private static final int HALL_HALF_WIDTH = 4; // x = -4 .. +4

    /**
     * Returns true only if every chunk the hall will touch is already loaded.
     * Call this before {@link #build} to avoid silently dropping blocks in
     * unloaded chunks.
     */
    public static boolean allChunksLoaded(LevelAccessor level, BlockPos origin) {
        int cx1 = (origin.getX() - HALL_HALF_WIDTH) >> 4;
        int cx2 = (origin.getX() + HALL_HALF_WIDTH) >> 4;
        int cz1 = origin.getZ() >> 4;
        int cz2 = (origin.getZ() + HALL_LENGTH - 1) >> 4;
        for (int cx = cx1; cx <= cx2; cx++) {
            for (int cz = cz1; cz <= cz2; cz++) {
                if (!level.hasChunk(cx, cz))
                    return false;
            }
        }
        return true;
    }

    public static void build(LevelAccessor level, RandomSource random, BlockPos origin) {
        buildStructure(level, origin);
        spawnVillagers(level, origin);
    }

    // =========================================================================
    // Structure
    // =========================================================================

    private static void buildStructure(LevelAccessor level, BlockPos origin) {
        BlockState planks = Blocks.DARK_OAK_PLANKS.defaultBlockState();
        BlockState log = Blocks.DARK_OAK_LOG.defaultBlockState();
        BlockState lantern = Blocks.LANTERN.defaultBlockState();
        BlockState air = Blocks.AIR.defaultBlockState();
        BlockState glass = Blocks.GLASS_PANE.defaultBlockState();
        BlockState slab = Blocks.STONE_SLAB.defaultBlockState()
                .setValue(SlabBlock.TYPE, SlabType.TOP);

        // ---- FLOOR (y=-1) + downward fill ----
        for (int x = -HALL_HALF_WIDTH; x <= HALL_HALF_WIDTH; x++) {
            for (int z = 0; z < HALL_LENGTH; z++) {
                setBlock(level, origin.offset(x, -1, z), planks);
                for (int dy = -2; dy >= -6; dy--) {
                    BlockPos below = origin.offset(x, dy, z);
                    if (level.isEmptyBlock(below))
                        setBlock(level, below, Blocks.COBBLESTONE.defaultBlockState());
                    else
                        break;
                }
            }
        }

        // ---- CEILING (y=3): glass over aisle for light, planks over cells ----
        for (int x = -HALL_HALF_WIDTH; x <= HALL_HALF_WIDTH; x++) {
            for (int z = 0; z < HALL_LENGTH; z++) {
                BlockState ceilBlock = (x >= -1 && x <= 1) ? Blocks.GLASS.defaultBlockState() : planks;
                setBlock(level, origin.offset(x, 3, z), ceilBlock);
            }
        }

        // ---- OUTER WALLS (x=-3 and x=+3) ----
        for (int z = 0; z < HALL_LENGTH; z++) {
            for (int y = 0; y <= 2; y++) {
                setBlock(level, origin.offset(-HALL_HALF_WIDTH, y, z), planks);
                setBlock(level, origin.offset(+HALL_HALF_WIDTH, y, z), planks);
            }
        }

        // ---- END WALL (z=27) ----
        for (int x = -HALL_HALF_WIDTH; x <= HALL_HALF_WIDTH; x++) {
            for (int y = 0; y <= 2; y++) {
                setBlock(level, origin.offset(x, y, HALL_LENGTH - 1), planks);
            }
        }

        // ---- ENTRANCE (z=0) with 3-wide 2-tall opening matching aisle ----
        for (int x = -HALL_HALF_WIDTH; x <= HALL_HALF_WIDTH; x++) {
            for (int y = 0; y <= 2; y++) {
                if (y <= 1 && x >= -1 && x <= 1) {
                    setBlock(level, origin.offset(x, y, 0), air);
                } else {
                    setBlock(level, origin.offset(x, y, 0), planks);
                }
            }
        }

        // ---- AISLE: clear the 3-wide centre corridor ----
        for (int z = 1; z < HALL_LENGTH - 1; z++) {
            for (int y = 0; y <= 2; y++) {
                for (int x = -1; x <= 1; x++) {
                    setBlock(level, origin.offset(x, y, z), air);
                }
            }
        }

        // ---- CELL SEPARATOR COLUMNS at x=-3, x=+3 on odd z ----
        for (int z = 1; z < HALL_LENGTH - 1; z += 2) {
            for (int y = 0; y <= 2; y++) {
                setBlock(level, origin.offset(-3, y, z), log);
                setBlock(level, origin.offset(+3, y, z), log);
            }
        }

        // ---- CELL FRONT WALLS (x=-2 and x=+2) on odd z (columns) ----
        for (int z = 1; z < HALL_LENGTH - 1; z += 2) {
            for (int y = 0; y <= 2; y++) {
                setBlock(level, origin.offset(-2, y, z), log);
                setBlock(level, origin.offset(+2, y, z), log);
            }
        }

        // ---- Cell containment: air inside cell, job block + glass front ----
        // Left cells at x=-3, even z
        for (int z = 2; z < HALL_LENGTH - 1; z += 2) {
            setBlock(level, origin.offset(-3, 0, z), air); // cell interior
            setBlock(level, origin.offset(-3, 1, z), air); // cell interior
            setBlock(level, origin.offset(-3, 2, z), slab); // ceiling slab — prevent jumping
            // Front wall at x=-2: job block at y=0 placed by placeJobBlocks(), glass at y=1
            setBlock(level, origin.offset(-2, 1, z), glass);
            setBlock(level, origin.offset(-2, 2, z), planks);
        }

        // Right cells at x=+3, even z
        for (int z = 2; z < HALL_LENGTH - 1; z += 2) {
            setBlock(level, origin.offset(+3, 0, z), air);
            setBlock(level, origin.offset(+3, 1, z), air);
            setBlock(level, origin.offset(+3, 2, z), slab);
            setBlock(level, origin.offset(+2, 1, z), glass);
            setBlock(level, origin.offset(+2, 2, z), planks);
        }

        // ---- JOB BLOCKS at cell front (x=-1 and x=+1), y=0 ----
        placeJobBlocks(level, origin);

        // ---- LANTERNS ----
        for (int z = 2; z < HALL_LENGTH - 1; z += 8) {
            setBlock(level, origin.offset(0, 2, z), lantern);
        }
    }

    private static void placeJobBlocks(LevelAccessor level, BlockPos origin) {
        // LEFT SIDE job blocks at x=-2, y=0 (between villager cell and aisle)

        // Armorer @ z=2 — Blast Furnace
        setBlock(level, origin.offset(-2, 0, 2),
                Blocks.BLAST_FURNACE.defaultBlockState()
                        .setValue(HorizontalDirectionalBlock.FACING, Direction.EAST));

        // Toolsmith @ z=4 — Smithing Table
        setBlock(level, origin.offset(-2, 0, 4), Blocks.SMITHING_TABLE.defaultBlockState());

        // Weaponsmith @ z=6 — Grindstone
        setBlock(level, origin.offset(-2, 0, 6),
                Blocks.GRINDSTONE.defaultBlockState()
                        .setValue(GrindstoneBlock.FACE, AttachFace.FLOOR)
                        .setValue(GrindstoneBlock.FACING, Direction.EAST));

        // Fletchers @ z=8..26 — Fletching Table
        for (int z = 8; z <= 26; z += 2) {
            setBlock(level, origin.offset(-2, 0, z), Blocks.FLETCHING_TABLE.defaultBlockState());
        }

        // RIGHT SIDE job blocks at x=+2, y=0
        // Librarians @ z=2..20 — Lectern
        for (int z = 2; z <= 20; z += 2) {
            setBlock(level, origin.offset(+2, 0, z),
                    Blocks.LECTERN.defaultBlockState()
                            .setValue(LecternBlock.FACING, Direction.WEST));
        }
        // Fill remaining right cells (z=22..26) with fletching tables
        for (int z = 22; z <= 26; z += 2) {
            setBlock(level, origin.offset(+2, 0, z), Blocks.FLETCHING_TABLE.defaultBlockState());
        }
    }

    // =========================================================================
    // Villager entities
    // =========================================================================

    private static void spawnVillagers(LevelAccessor level, BlockPos origin) {
        if (!(level instanceof ServerLevelAccessor sla))
            return;

        // LEFT SIDE — villagers at x=-3
        spawnVillager(sla, origin.offset(-3, 0, 2), VillagerProfession.ARMORER);
        spawnVillager(sla, origin.offset(-3, 0, 4), VillagerProfession.TOOLSMITH);
        spawnVillager(sla, origin.offset(-3, 0, 6), VillagerProfession.WEAPONSMITH);
        for (int z = 8; z <= 26; z += 2) {
            spawnVillager(sla, origin.offset(-3, 0, z), VillagerProfession.FLETCHER);
        }

        // RIGHT SIDE — villagers at x=+3
        for (int z = 2; z <= 20; z += 2) {
            spawnVillager(sla, origin.offset(+3, 0, z), VillagerProfession.LIBRARIAN);
        }
        for (int z = 22; z <= 26; z += 2) {
            spawnVillager(sla, origin.offset(+3, 0, z), VillagerProfession.FLETCHER);
        }
    }

    private static void spawnVillager(ServerLevelAccessor sla, BlockPos pos,
            ResourceKey<VillagerProfession> professionKey) {
        Villager villager = EntityType.VILLAGER.create(sla.getLevel(), EntitySpawnReason.COMMAND);
        if (villager == null)
            return;

        villager.setPos(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5);
        villager.setPersistenceRequired();
        // villager.setNoAi(true); // prevent all movement/pathfinding

        Holder<VillagerType> plainsType = sla.getLevel().registryAccess()
                .lookupOrThrow(Registries.VILLAGER_TYPE)
                .getOrThrow(VillagerType.PLAINS);
        Holder<VillagerProfession> profession = sla.getLevel().registryAccess()
                .lookupOrThrow(Registries.VILLAGER_PROFESSION)
                .getOrThrow(professionKey);

        villager.setVillagerData(new VillagerData(plainsType, profession, 1));
        sla.getLevel().addFreshEntity(villager);
    }

    // =========================================================================

    private static void setBlock(LevelAccessor level, BlockPos pos, BlockState state) {
        level.setBlock(pos, state, 3);
    }
}

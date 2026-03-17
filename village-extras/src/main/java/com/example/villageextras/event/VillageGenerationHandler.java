package com.example.villageextras.event;

import com.example.villageextras.data.VillageExtrasSavedData;
import com.example.villageextras.world.gen.IronFarmBuilder;
import com.example.villageextras.world.gen.TradingHallBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraftforge.event.level.ChunkEvent;

public class VillageGenerationHandler {

    /** Tag that matches all vanilla village structures. */
    private static final TagKey<Structure> VILLAGE_TAG =
            TagKey.create(Registries.STRUCTURE, Identifier.withDefaultNamespace("village"));

    /**
     * Fires every time a chunk loads. We only act on freshly generated chunks
     * on the server side that are inside a village structure.
     */
    public static void onChunkLoad(ChunkEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel level)) return;
        if (!(event.getChunk() instanceof LevelChunk chunk)) return;
        if (!event.isNewChunk()) return;  // only process newly generated chunks

        ChunkPos chunkPos = chunk.getPos();
        BlockPos center = chunkPos.getMiddleBlockPosition(level.getSeaLevel());

        // Check whether this chunk contains a piece of any village structure.
        StructureStart start = level.structureManager()
                .getStructureWithPieceAt(center, VILLAGE_TAG);

        if (!start.isValid()) return;

        // Use the structure start's chunk position as the unique key.
        long startChunkLong = start.getChunkPos().toLong();

        VillageExtrasSavedData data = VillageExtrasSavedData.get(level);
        if (data.isProcessed(startChunkLong)) return;

        if (!generateExtras(level, start.getChunkPos())) return;
        data.markProcessed(startChunkLong);
    }

    // -------------------------------------------------------------------------

    /** Returns true if all structures were placed successfully. */
    private static boolean generateExtras(ServerLevel level, ChunkPos villageChunk) {
        int cx = villageChunk.getMiddleBlockX();
        int cz = villageChunk.getMiddleBlockZ();

        // Trading hall — 32 blocks east of the village centre
        int hallX = cx + 32;
        int hallY = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, hallX, cz);
        BlockPos hallOrigin = new BlockPos(hallX, hallY, cz);
        if (!TradingHallBuilder.allChunksLoaded(level, hallOrigin)) return false;
        TradingHallBuilder.build(level, level.getRandom(), hallOrigin);

        // Iron farm — temporarily disabled
        // int farmX = cx - 32;
        // int farmY = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, farmX, cz);
        // IronFarmBuilder.build(level, level.getRandom(), new BlockPos(farmX, farmY, cz));

        return true;
    }
}

package com.example.villageextras.event;

import com.example.villageextras.data.VillageExtrasSavedData;
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
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.ChunkEvent;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

public class VillageGenerationHandler {

    /** Tag that matches all vanilla village structures. */
    private static final TagKey<Structure> VILLAGE_TAG =
            TagKey.create(Registries.STRUCTURE, Identifier.withDefaultNamespace("village"));

    /**
     * Thread-safe queue of new-chunk positions to inspect on the next server
     * tick. ChunkEvent.Load may fire from chunk worker threads in Forge 61, so
     * we only collect positions here and do all heavy work on the server thread.
     */
    private static final ConcurrentLinkedQueue<ChunkPos> LOADED_CHUNKS = new ConcurrentLinkedQueue<>();

    /**
     * In-memory set of village start-chunk longs that were detected but whose
     * hall chunks were not yet loaded. Retried every server tick.
     * Not persisted — VillageExtrasSavedData prevents duplicates across restarts.
     */
    private static final Set<Long> PENDING = new HashSet<>();

    /**
     * Clears static state between server sessions (e.g. singleplayer world switches).
     */
    public static void onServerStopped(net.minecraftforge.event.server.ServerStoppedEvent event) {
        LOADED_CHUNKS.clear();
        PENDING.clear();
    }

    /**
     * Collect newly generated chunk positions from the (possibly off-thread)
     * ChunkEvent.Load. No heavy work here — just enqueue the position.
     * Only queues overworld chunks since villages only generate there.
     */
    public static void onChunkLoad(ChunkEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel serverLevel)) return;
        if (serverLevel.dimension() != net.minecraft.world.level.Level.OVERWORLD) return;
        if (!(event.getChunk() instanceof LevelChunk)) return;
        if (!event.isNewChunk()) return;
        LOADED_CHUNKS.add(event.getChunk().getPos());
    }

    /**
     * Runs on the server thread every tick. Drains the chunk queue, detects
     * villages, and attempts to build pending trading halls.
     */
    public static void onServerTick(TickEvent.ServerTickEvent.Post event) {
        // Drain queued new-chunk positions and detect villages.
        ChunkPos pos;
        while ((pos = LOADED_CHUNKS.poll()) != null) {
            ServerLevel level = event.server().overworld();
            if (level == null) continue;

            VillageExtrasSavedData data = VillageExtrasSavedData.get(level);
            BlockPos center = pos.getMiddleBlockPosition(level.getSeaLevel());

            StructureStart start = level.structureManager()
                    .getStructureWithPieceAt(center, VILLAGE_TAG);
            if (!start.isValid()) continue;

            long key = start.getChunkPos().toLong();
            if (!data.isProcessed(key)) {
                PENDING.add(key);
            }
        }

        // Attempt to build any pending villages whose hall chunks are loaded.
        if (PENDING.isEmpty()) return;

        ServerLevel level = event.server().overworld();
        if (level == null) return;

        VillageExtrasSavedData data = VillageExtrasSavedData.get(level);
        Iterator<Long> it = PENDING.iterator();
        while (it.hasNext()) {
            long key = it.next();
            if (data.isProcessed(key)) {
                it.remove();
                continue;
            }
            if (generateExtras(level, new ChunkPos(key))) {
                data.markProcessed(key);
                it.remove();
            }
        }
    }

    // -------------------------------------------------------------------------

    /** Returns true if the trading hall was placed successfully. */
    private static boolean generateExtras(ServerLevel level, ChunkPos villageChunk) {
        int cx = villageChunk.getMiddleBlockX();
        int cz = villageChunk.getMiddleBlockZ();

        // Trading hall — 32 blocks east of the village centre
        int hallX = cx + 32;
        // Guard: getHeight internally calls getChunk, which blocks if the chunk
        // isn't loaded (deadlocks during shutdown). Check first.
        if (!level.hasChunk(hallX >> 4, cz >> 4)) return false;
        int hallY = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, hallX, cz);
        BlockPos hallOrigin = new BlockPos(hallX, hallY, cz);
        if (!TradingHallBuilder.allChunksLoaded(level, hallOrigin)) return false;
        TradingHallBuilder.build(level, level.getRandom(), hallOrigin);

        return true;
    }
}

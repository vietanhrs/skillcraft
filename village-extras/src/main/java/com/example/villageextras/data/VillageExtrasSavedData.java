package com.example.villageextras.data;

import com.mojang.serialization.Codec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Persists the set of village start-chunk positions (as packed longs) that
 * have already had a trading hall + iron farm generated next to them, so we
 * never generate duplicates across server restarts.
 */
public class VillageExtrasSavedData extends SavedData {

    private static final String DATA_NAME = "village_extras_processed";

    private static final Codec<VillageExtrasSavedData> CODEC =
            Codec.LONG.listOf().fieldOf("processed").codec().xmap(
                    list -> {
                        VillageExtrasSavedData data = new VillageExtrasSavedData();
                        data.processedVillages.addAll(list);
                        return data;
                    },
                    data -> new ArrayList<>(data.processedVillages)
            );

    public static final SavedDataType<VillageExtrasSavedData> TYPE = new SavedDataType<>(
            DATA_NAME,
            VillageExtrasSavedData::new,
            CODEC,
            null
    );

    private final Set<Long> processedVillages = new HashSet<>();

    private VillageExtrasSavedData() {}

    // -------------------------------------------------------------------------

    public static VillageExtrasSavedData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(TYPE);
    }

    public boolean isProcessed(long packedChunkPos) {
        return processedVillages.contains(packedChunkPos);
    }

    public void markProcessed(long packedChunkPos) {
        processedVillages.add(packedChunkPos);
        setDirty();
    }
}

package com.example.enchantmentshrine;

import net.minecraftforge.common.ForgeConfigSpec;

public class ShrineConfig {

    public static final ForgeConfigSpec COMMON_SPEC;

    /**
     * When true the mod forces one shrine to generate near the world spawn point
     * every time the overworld loads. Intended for testing only — leave false in
     * normal gameplay.
     */
    public static final ForgeConfigSpec.BooleanValue DEBUG_SPAWN_NEARBY;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        DEBUG_SPAWN_NEARBY = builder
                .comment("Debug: forces a shrine to generate ~16 blocks from the world spawn on every level load.",
                         "Set to true only for in-game testing. Default: false.")
                .define("debugSpawnNearby", false);
        COMMON_SPEC = builder.build();
    }
}

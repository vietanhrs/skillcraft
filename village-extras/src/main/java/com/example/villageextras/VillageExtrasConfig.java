package com.example.villageextras;

import net.minecraftforge.common.ForgeConfigSpec;

public class VillageExtrasConfig {

    public static final ForgeConfigSpec COMMON_SPEC;

    /**
     * When true, forces a Trading Hall and an Iron Farm to generate within two
     * chunks of the world spawn on every level load. For testing only.
     */
    public static final ForgeConfigSpec.BooleanValue DEBUG_SPAWN_NEARBY;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        DEBUG_SPAWN_NEARBY = builder
                .comment("Debug: forces a Trading Hall and Iron Farm to generate near world spawn.",
                        "Set to true only for in-game testing. Default: false.")
                .define("debugSpawnNearby", false);
        COMMON_SPEC = builder.build();
    }
}

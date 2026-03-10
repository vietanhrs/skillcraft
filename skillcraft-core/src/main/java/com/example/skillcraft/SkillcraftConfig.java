package com.example.skillcraft;

import net.minecraftforge.common.ForgeConfigSpec;

public class SkillcraftConfig {

    public static final ForgeConfigSpec COMMON_SPEC;

    /**
     * When true, forces a Mana Castle and a Secret Shop to generate within two
     * chunks of the world spawn on every level load. For testing only.
     */
    public static final ForgeConfigSpec.BooleanValue DEBUG_SPAWN_NEARBY;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        DEBUG_SPAWN_NEARBY = builder
                .comment("Debug: forces a Mana Castle and a Secret Shop to generate near world spawn.",
                         "Set to true only for in-game testing. Default: false.")
                .define("debugSpawnNearby", false);
        COMMON_SPEC = builder.build();
    }
}

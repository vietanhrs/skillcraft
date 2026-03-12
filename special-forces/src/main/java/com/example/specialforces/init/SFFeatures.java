package com.example.specialforces.init;

import com.example.specialforces.SpecialForces;
import com.example.specialforces.world.gen.feature.SpawnChestFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SFFeatures {

    public static final DeferredRegister<Feature<?>> FEATURES =
            DeferredRegister.create(ForgeRegistries.FEATURES, SpecialForces.MOD_ID);

    public static final RegistryObject<Feature<NoneFeatureConfiguration>> SPAWN_CHEST =
            FEATURES.register("spawn_chest",
                    () -> new SpawnChestFeature(NoneFeatureConfiguration.CODEC));
}

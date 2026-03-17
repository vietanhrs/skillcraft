package com.example.villageextras.init;

import com.example.villageextras.VillageExtras;
import com.example.villageextras.world.gen.feature.IronFarmDebugFeature;
import com.example.villageextras.world.gen.feature.TradingHallDebugFeature;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModFeatures {

    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Registries.FEATURE,
            VillageExtras.MOD_ID);

    public static final RegistryObject<Feature<NoneFeatureConfiguration>> TRADING_HALL_DEBUG = FEATURES.register(
            "trading_hall_debug",
            () -> new TradingHallDebugFeature(NoneFeatureConfiguration.CODEC));

    public static final RegistryObject<Feature<NoneFeatureConfiguration>> IRON_FARM_DEBUG = FEATURES.register(
            "iron_farm_debug",
            () -> new IronFarmDebugFeature(NoneFeatureConfiguration.CODEC));
}

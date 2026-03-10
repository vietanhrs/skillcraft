package com.example.skillcraft.init;

import com.example.skillcraft.Skillcraft;
import com.example.skillcraft.world.gen.feature.ManaCastleDebugFeature;
import com.example.skillcraft.world.gen.feature.ManaCastleFeature;
import com.example.skillcraft.world.gen.feature.SecretShopDebugFeature;
import com.example.skillcraft.world.gen.feature.SecretShopFeature;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModFeatures {

    public static final DeferredRegister<Feature<?>> FEATURES =
            DeferredRegister.create(Registries.FEATURE, Skillcraft.MOD_ID);

    public static final RegistryObject<Feature<NoneFeatureConfiguration>> MANA_CASTLE =
            FEATURES.register("mana_castle",
                    () -> new ManaCastleFeature(NoneFeatureConfiguration.CODEC));

    public static final RegistryObject<Feature<NoneFeatureConfiguration>> MANA_CASTLE_DEBUG =
            FEATURES.register("mana_castle_debug",
                    () -> new ManaCastleDebugFeature(NoneFeatureConfiguration.CODEC));

    public static final RegistryObject<Feature<NoneFeatureConfiguration>> SECRET_SHOP =
            FEATURES.register("secret_shop",
                    () -> new SecretShopFeature(NoneFeatureConfiguration.CODEC));

    public static final RegistryObject<Feature<NoneFeatureConfiguration>> SECRET_SHOP_DEBUG =
            FEATURES.register("secret_shop_debug",
                    () -> new SecretShopDebugFeature(NoneFeatureConfiguration.CODEC));
}

package com.example.enchantmentshrine.init;

import com.example.enchantmentshrine.EnchantmentShrine;
import com.example.enchantmentshrine.world.gen.feature.EnchantmentShrineDebugFeature;
import com.example.enchantmentshrine.world.gen.feature.EnchantmentShrineFeature;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class ModFeatures {

    public static final DeferredRegister<Feature<?>> FEATURES =
            DeferredRegister.create(Registries.FEATURE, EnchantmentShrine.MOD_ID);

    public static final RegistryObject<Feature<NoneFeatureConfiguration>> ENCHANTMENT_SHRINE =
            FEATURES.register("enchantment_shrine",
                    () -> new EnchantmentShrineFeature(NoneFeatureConfiguration.CODEC));

    /** Used only when {@code debugSpawnNearby = true} in the config. */
    public static final RegistryObject<Feature<NoneFeatureConfiguration>> ENCHANTMENT_SHRINE_DEBUG =
            FEATURES.register("enchantment_shrine_debug",
                    () -> new EnchantmentShrineDebugFeature(NoneFeatureConfiguration.CODEC));
}

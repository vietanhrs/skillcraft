package com.example.enchantmentshrine;

import com.example.enchantmentshrine.init.ModFeatures;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig.Type;

@Mod(EnchantmentShrine.MOD_ID)
public class EnchantmentShrine {

    public static final String MOD_ID = "enchantmentshrine";

    public EnchantmentShrine(IEventBus modBus, ModContainer modContainer) {
        ModFeatures.FEATURES.register(modBus);
        modContainer.registerConfig(Type.COMMON, ShrineConfig.COMMON_SPEC);
    }
}

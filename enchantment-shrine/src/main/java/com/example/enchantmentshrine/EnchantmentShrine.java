package com.example.enchantmentshrine;

import com.example.enchantmentshrine.init.ModFeatures;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(EnchantmentShrine.MOD_ID)
public class EnchantmentShrine {

    public static final String MOD_ID = "enchantmentshrine";

    public EnchantmentShrine(FMLJavaModLoadingContext context) {
        ModFeatures.FEATURES.register(context.getModBusGroup());
        context.registerConfig(Type.COMMON, ShrineConfig.COMMON_SPEC);
    }
}

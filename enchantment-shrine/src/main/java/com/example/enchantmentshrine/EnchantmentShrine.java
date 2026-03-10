package com.example.enchantmentshrine;

import com.example.enchantmentshrine.init.ModFeatures;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(EnchantmentShrine.MOD_ID)
public class EnchantmentShrine {

    public static final String MOD_ID = "enchantmentshrine";

    public EnchantmentShrine() {
        BusGroup modBus = FMLJavaModLoadingContext.get().getModBusGroup();
        ModFeatures.FEATURES.register(modBus);
    }
}

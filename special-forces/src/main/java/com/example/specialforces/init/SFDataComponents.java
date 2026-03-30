package com.example.specialforces.init;

import com.example.specialforces.SpecialForces;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class SFDataComponents {

    @SuppressWarnings("unchecked")
    public static final DeferredRegister<DataComponentType<?>> COMPONENTS =
            DeferredRegister.create(
                    (net.minecraft.resources.ResourceKey) BuiltInRegistries.DATA_COMPONENT_TYPE.key(),
                    SpecialForces.MOD_ID);

    public static final RegistryObject<DataComponentType<Integer>> MAGAZINE_AMMO =
            COMPONENTS.register("magazine_ammo", () ->
                    DataComponentType.<Integer>builder()
                            .persistent(Codec.INT)
                            .networkSynchronized(ByteBufCodecs.VAR_INT)
                            .build());

    public static final RegistryObject<DataComponentType<Integer>> RELOAD_TICKS =
            COMPONENTS.register("reload_ticks", () ->
                    DataComponentType.<Integer>builder()
                            .persistent(Codec.INT)
                            .networkSynchronized(ByteBufCodecs.VAR_INT)
                            .build());
}

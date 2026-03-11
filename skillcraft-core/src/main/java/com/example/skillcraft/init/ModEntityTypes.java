package com.example.skillcraft.init;

import com.example.skillcraft.Skillcraft;
import com.example.skillcraft.entity.SecretMerchant;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntityTypes {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister
            .create(ForgeRegistries.ENTITY_TYPES, Skillcraft.MOD_ID);

    public static final RegistryObject<EntityType<SecretMerchant>> SECRET_MERCHANT = ENTITY_TYPES
            .register("secret_merchant",
                    () -> EntityType.Builder.<SecretMerchant>of(SecretMerchant::new, MobCategory.CREATURE)
                            .sized(0.6f, 1.95f)
                            .clientTrackingRange(10)
                            .build(ResourceKey.create(
                                    Registries.ENTITY_TYPE,
                                    Identifier.fromNamespaceAndPath(Skillcraft.MOD_ID, "secret_merchant"))));
}

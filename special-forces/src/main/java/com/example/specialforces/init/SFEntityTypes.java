package com.example.specialforces.init;

import com.example.specialforces.SpecialForces;
import com.example.specialforces.entity.GlowStickEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SFEntityTypes {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, SpecialForces.MOD_ID);

    public static final RegistryObject<EntityType<GlowStickEntity>> GLOW_STICK =
            ENTITY_TYPES.register("glow_stick",
                    () -> EntityType.Builder.<GlowStickEntity>of(GlowStickEntity::new, MobCategory.MISC)
                            .sized(0.25F, 0.25F)
                            .clientTrackingRange(4)
                            .updateInterval(10)
                            .build(ResourceKey.create(Registries.ENTITY_TYPE,
                                    Identifier.fromNamespaceAndPath(SpecialForces.MOD_ID, "glow_stick"))));
}

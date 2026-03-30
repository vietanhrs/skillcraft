package com.example.specialforces.init;

import com.example.specialforces.SpecialForces;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SFSounds {

    public static final DeferredRegister<SoundEvent> SOUNDS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, SpecialForces.MOD_ID);

    public static final RegistryObject<SoundEvent> SNIPER_FIRE = SOUNDS.register("sniper_fire",
            () -> SoundEvent.createVariableRangeEvent(
                    Identifier.fromNamespaceAndPath(SpecialForces.MOD_ID, "sniper_fire")));

    public static final RegistryObject<SoundEvent> AR_FIRE = SOUNDS.register("ar_fire",
            () -> SoundEvent.createVariableRangeEvent(
                    Identifier.fromNamespaceAndPath(SpecialForces.MOD_ID, "ar_fire")));
}

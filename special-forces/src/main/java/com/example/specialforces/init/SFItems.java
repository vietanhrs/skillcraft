package com.example.specialforces.init;

import com.example.specialforces.SpecialForces;
import com.example.specialforces.item.ARBullet;
import com.example.specialforces.item.GlowStick;
import com.example.specialforces.item.M4A1Rifle;
import com.example.specialforces.item.NightGoggles;
import com.example.specialforces.item.SniperBullet;
import com.example.specialforces.item.SniperRifle;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SFItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, SpecialForces.MOD_ID);

    public static final RegistryObject<Item> SNIPER = ITEMS.register("sniper",
            () -> new SniperRifle(new Item.Properties()
                    .setId(ITEMS.key("sniper"))
                    .stacksTo(1)
                    .durability(100)
                    .component(SFDataComponents.MAGAZINE_AMMO.get(), 0)
                    .component(SFDataComponents.RELOAD_TICKS.get(), 0)));

    public static final RegistryObject<Item> M4A1 = ITEMS.register("m4a1",
            () -> new M4A1Rifle(new Item.Properties()
                    .setId(ITEMS.key("m4a1"))
                    .stacksTo(1)
                    .durability(200)
                    .component(SFDataComponents.MAGAZINE_AMMO.get(), 0)
                    .component(SFDataComponents.RELOAD_TICKS.get(), 0)));

    public static final RegistryObject<Item> SNIPER_BULLET = ITEMS.register("sniper_bullet",
            () -> new SniperBullet(new Item.Properties()
                    .setId(ITEMS.key("sniper_bullet"))
                    .stacksTo(64)));

    public static final RegistryObject<Item> AR_BULLET = ITEMS.register("ar_bullet",
            () -> new ARBullet(new Item.Properties()
                    .setId(ITEMS.key("ar_bullet"))
                    .stacksTo(64)));

    public static final RegistryObject<Item> GLOW_STICK = ITEMS.register("glow_stick",
            () -> new GlowStick(new Item.Properties()
                    .setId(ITEMS.key("glow_stick"))
                    .stacksTo(64)));

    public static final RegistryObject<Item> NIGHT_GOGGLES = ITEMS.register("night_goggles",
            () -> new NightGoggles(new Item.Properties()
                    .setId(ITEMS.key("night_goggles"))
                    .stacksTo(1)
                    .component(net.minecraft.core.component.DataComponents.EQUIPPABLE,
                            Equippable.builder(EquipmentSlot.HEAD).build())));
}

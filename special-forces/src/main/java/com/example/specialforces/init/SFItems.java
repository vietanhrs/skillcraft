package com.example.specialforces.init;

import com.example.specialforces.SpecialForces;
import com.example.specialforces.item.Bullet;
import com.example.specialforces.item.GlowStick;
import com.example.specialforces.item.NightGoggles;
import com.example.specialforces.item.SniperRifle;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.ItemAttributeModifiers;
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
                    .durability(100)));

    public static final RegistryObject<Item> BULLET = ITEMS.register("bullet",
            () -> new Bullet(new Item.Properties()
                    .setId(ITEMS.key("bullet"))
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

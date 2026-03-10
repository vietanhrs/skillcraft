package com.example.skillcraft.init;

import com.example.skillcraft.Skillcraft;
import com.example.skillcraft.item.LightningBook;
import com.example.skillcraft.item.ManaPotion;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, Skillcraft.MOD_ID);

    public static final RegistryObject<Item> MANA_POTION =
            ITEMS.register("mana_potion",
                    () -> new ManaPotion(new Item.Properties().stacksTo(16).setId(ITEMS.key("mana_potion"))));

    public static final RegistryObject<Item> LIGHTNING_BOOK =
            ITEMS.register("lightning_book",
                    () -> new LightningBook(new Item.Properties().stacksTo(1).setId(ITEMS.key("lightning_book"))));
}

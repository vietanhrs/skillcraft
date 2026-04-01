package com.example.specialforces.item;

import net.minecraft.world.item.Item;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import java.util.function.Consumer;

public class M4A1Rifle extends Item {

    public static final int MAX_MAGAZINE = 30;
    public static final int RELOAD_TIME = 50;  // 2.5 seconds
    public static final int FIRE_RATE = 2;     // ticks between shots
    public static final float DAMAGE = 8.0f;
    public static final double MAX_RANGE = 200.0;
    public static final float SPREAD = 0.06f;

    public M4A1Rifle(Properties properties) {
        super(properties);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(com.example.specialforces.client.M4A1ClientExtension.INSTANCE);
    }
}

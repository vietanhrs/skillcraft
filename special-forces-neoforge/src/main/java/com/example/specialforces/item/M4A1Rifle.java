package com.example.specialforces.item;

import net.minecraft.world.item.Item;

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
}

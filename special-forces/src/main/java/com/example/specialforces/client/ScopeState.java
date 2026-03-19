package com.example.specialforces.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Client-only state for the sniper scope.
 * <p>
 * zoomLevel: 0 = no scope, 1 = x2, 2 = x4
 * preShotZoom: zoom level before the last shot (restored after cooldown ends)
 * wasOnCooldown: used to detect the moment the cooldown expires
 */
@OnlyIn(Dist.CLIENT)
public class ScopeState {

    public static int zoomLevel = 0;
    public static int preShotZoom = 0;
    public static boolean wasOnCooldown = false;

    /** FOV multipliers indexed by zoomLevel. */
    public static final float[] FOV_MULTIPLIER = { 1.0f, 0.5f, 0.25f }; // 0=normal, 1=x2, 2=x4

    private ScopeState() {}
}

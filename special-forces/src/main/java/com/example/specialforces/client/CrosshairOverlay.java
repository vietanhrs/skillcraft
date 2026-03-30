package com.example.specialforces.client;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.Identifier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.AddGuiOverlayLayersEvent;

/**
 * CS-style crosshair: 1px center dot + 4 short dashes (up/down/left/right).
 * Hidden while scoped in.
 */
@OnlyIn(Dist.CLIENT)
public class CrosshairOverlay {

    // Cyan color with full opacity
    private static final int COLOR = 0xFF00FFFF;
    // Gap between center dot and dash start
    private static final int GAP = 3;
    // Length of each dash
    private static final int DASH_LEN = 4;

    public static void register(AddGuiOverlayLayersEvent event) {
        event.getLayeredDraw().add(
                Identifier.fromNamespaceAndPath("specialforces", "crosshair"),
                CrosshairOverlay::render);
    }

    private static void render(GuiGraphics graphics, DeltaTracker delta) {
        if (ScopeState.zoomLevel != 0) return; // scope overlay handles its own crosshair

        Minecraft mc = Minecraft.getInstance();
        if (mc.options.hideGui || mc.player == null) return;

        int cx = graphics.guiWidth() / 2;
        int cy = graphics.guiHeight() / 2;

        // Center dot (1x1)
        graphics.fill(cx, cy, cx + 1, cy + 1, COLOR);

        // Top dash
        graphics.fill(cx, cy - GAP - DASH_LEN, cx + 1, cy - GAP, COLOR);
        // Bottom dash
        graphics.fill(cx, cy + GAP + 1, cx + 1, cy + GAP + 1 + DASH_LEN, COLOR);
        // Left dash
        graphics.fill(cx - GAP - DASH_LEN, cy, cx - GAP, cy + 1, COLOR);
        // Right dash
        graphics.fill(cx + GAP + 1, cy, cx + GAP + 1 + DASH_LEN, cy + 1, COLOR);
    }
}

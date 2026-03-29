package com.example.specialforces.client;

import com.example.specialforces.init.SFItems;
import com.example.specialforces.item.SniperRifle;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.Identifier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.AddGuiOverlayLayersEvent;

@OnlyIn(Dist.CLIENT)
public class ScopeOverlay {

    public static void register(AddGuiOverlayLayersEvent event) {
        event.getLayeredDraw().add(
                Identifier.fromNamespaceAndPath("specialforces", "scope"),
                ScopeOverlay::render);
    }

    private static void render(GuiGraphics graphics, DeltaTracker delta) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.options.hideGui) return;
        if (!(mc.player.getMainHandItem().getItem() instanceof SniperRifle)) return;
        if (ScopeState.zoomLevel == 0) return;

        int w = mc.getWindow().getGuiScaledWidth();
        int h = mc.getWindow().getGuiScaledHeight();
        int cx = w / 2;
        int cy = h / 2;

        // Circle radius: largest that fits on screen with a margin
        int r = Math.min(w, h) / 2 - 10;

        // Fill black outside the circle, row by row
        for (int y = 0; y < h; y++) {
            int dy = y - cy;
            if (Math.abs(dy) >= r) {
                graphics.fill(0, y, w, y + 1, 0xFF000000);
            } else {
                int dx = (int) Math.sqrt((double) (r * r - dy * dy));
                graphics.fill(0, y, cx - dx, y + 1, 0xFF000000);
                graphics.fill(cx + dx, y, w, y + 1, 0xFF000000);
            }
        }

        // Thin circle border (2 px ring just outside the transparent area)
        for (int y = cy - r - 2; y <= cy + r + 2; y++) {
            int dy = y - cy;
            if (Math.abs(dy) > r + 2) continue;
            int outerDx = (int) Math.sqrt(Math.max(0.0, (r + 2.0) * (r + 2.0) - dy * dy));
            int innerDx = (int) Math.sqrt(Math.max(0.0, (r - 0.0) * (r - 0.0) - dy * dy));
            if (outerDx > innerDx) {
                graphics.fill(cx - outerDx, y, cx - innerDx, y + 1, 0xFF1A1A1A);
                graphics.fill(cx + innerDx, y, cx + outerDx, y + 1, 0xFF1A1A1A);
            }
        }

        // Crosshair: full-diameter lines, 1 px thick
        int lineColor = 0xCC000000;
        // Horizontal (1 px tall, full diameter)
        graphics.fill(cx - r, cy, cx + r, cy + 1, lineColor);
        // Vertical (1 px wide, full diameter)
        graphics.fill(cx, cy - r, cx + 1, cy + r, lineColor);

        // Small center dot (1x1)
        graphics.fill(cx, cy, cx + 1, cy + 1, 0xFFFF0000);

        // Zoom level indicator (bottom of scope circle)
        String zoomLabel = ScopeState.zoomLevel == 1 ? "x2" : "x4";
        graphics.drawString(mc.font, zoomLabel, cx - mc.font.width(zoomLabel) / 2,
                cy + r - mc.font.lineHeight - 4, 0xFFCCCCCC, true);
    }
}

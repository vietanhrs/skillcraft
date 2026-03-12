package com.example.specialforces.client;

import com.example.specialforces.init.SFItems;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.AddGuiOverlayLayersEvent;

@OnlyIn(Dist.CLIENT)
public class NvOverlay {

    public static void register(AddGuiOverlayLayersEvent event) {
        event.getLayeredDraw().add(
                Identifier.fromNamespaceAndPath("specialforces", "night_vision"),
                NvOverlay::render);
    }

    private static void render(GuiGraphics graphics, DeltaTracker delta) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.options.hideGui) return;

        // Only render when wearing the goggles
        if (mc.player.getItemBySlot(EquipmentSlot.HEAD).getItem() != SFItems.NIGHT_GOGGLES.get()) return;

        int w = mc.getWindow().getGuiScaledWidth();
        int h = mc.getWindow().getGuiScaledHeight();

        // Semi-transparent green tint to simulate night-vision phosphor
        graphics.fill(0, 0, w, h, 0x2000CC44);

        // Thin vignette darkening at edges to mimic goggle tubes
        int vigSize = Math.min(w, h) / 6;
        // Top/bottom gradient approximation
        for (int i = 0; i < vigSize; i++) {
            int alpha = (int) (120 * (1f - (float) i / vigSize));
            int color = (alpha << 24); // black vignette
            graphics.fill(0, i, w, i + 1, color);
            graphics.fill(0, h - 1 - i, w, h - i, color);
        }
        // Left/right vignette
        for (int i = 0; i < vigSize; i++) {
            int alpha = (int) (120 * (1f - (float) i / vigSize));
            int color = (alpha << 24);
            graphics.fill(i, 0, i + 1, h, color);
            graphics.fill(w - 1 - i, 0, w - i, h, color);
        }
    }
}

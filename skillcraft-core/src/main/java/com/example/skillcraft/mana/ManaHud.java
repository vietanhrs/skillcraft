package com.example.skillcraft.mana;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.Identifier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.AddGuiOverlayLayersEvent;

/**
 * Renders a mana bar in the bottom-left corner of the HUD when the player
 * has an active mana bar (obtained by drinking a Mana Potion).
 *
 * <p>Layout: a dark background rectangle filled proportionally in blue,
 * with a "Mana: X/Y" text label above it.
 */
@OnlyIn(Dist.CLIENT)
public class ManaHud {

    private static final int BAR_WIDTH  = 82;
    private static final int BAR_HEIGHT = 8;
    private static final int MARGIN     = 5;

    public static void register(AddGuiOverlayLayersEvent event) {
        event.getLayeredDraw().add(
                Identifier.fromNamespaceAndPath("skillcraft", "mana_bar"),
                ManaHud::render
        );
    }

    private static void render(GuiGraphics graphics, DeltaTracker delta) {
        if (!ClientManaData.hasMana) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.options.hideGui) return;

        int screenH   = mc.getWindow().getGuiScaledHeight();
        int x         = MARGIN;
        int barY      = screenH - MARGIN - BAR_HEIGHT;
        int textY     = barY - mc.font.lineHeight - 2;

        // Background
        graphics.fill(x - 1, barY - 1, x + BAR_WIDTH + 1, barY + BAR_HEIGHT + 1, 0xA0000000);

        // Fill proportional to current mana
        float ratio   = (float) ClientManaData.mana / Math.max(1, ClientManaData.maxMana);
        int fillWidth = (int) (BAR_WIDTH * ratio);
        if (fillWidth > 0) {
            graphics.fill(x, barY, x + fillWidth, barY + BAR_HEIGHT, 0xFF3355FF);
        }

        // Label
        String label = "Mana: " + ClientManaData.mana + " / " + ClientManaData.maxMana;
        graphics.drawString(mc.font, label, x, textY, 0xFF99AAFF, true);
    }
}

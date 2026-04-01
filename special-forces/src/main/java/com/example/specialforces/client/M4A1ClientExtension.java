package com.example.specialforces.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

/**
 * Overrides the vanilla first-person hand transform for the M4A1,
 * suppressing the equip bob that triggers when ItemStack components change.
 */
@OnlyIn(Dist.CLIENT)
public class M4A1ClientExtension implements IClientItemExtensions {

    public static final M4A1ClientExtension INSTANCE = new M4A1ClientExtension();

    @Override
    public boolean applyForgeHandTransform(PoseStack poseStack, LocalPlayer player,
                                           HumanoidArm arm, ItemStack itemInHand,
                                           float equipProgress, float swingProgress,
                                           float partialTick) {
        // Apply a stable hand position, ignoring equipProgress entirely.
        // This prevents the equip bob (item dip) when ammo components change.
        int side = arm == HumanoidArm.RIGHT ? 1 : -1;
        // Standard first-person item positioning (matches vanilla at equipProgress=0)
        poseStack.translate(side * 0.56f, -0.52f, -0.72f);

        // Apply swing animation if attacking (but not the equip bob)
        if (swingProgress > 0f) {
            float swing = (float) Math.sin(swingProgress * Math.PI);
            float swing2 = (float) Math.sin(Math.sqrt(swingProgress) * Math.PI);
            poseStack.translate(side * (-0.014f * swing2), 0.01f * swing, -0.04f * swing2);
        }

        return true; // skip vanilla hand transform
    }
}

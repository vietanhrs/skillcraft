package com.example.specialforces.client;

import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.glfw.GLFW;

@OnlyIn(Dist.CLIENT)
public class SFKeyBindings {

    public static final KeyMapping.Category CATEGORY =
            new KeyMapping.Category(Identifier.fromNamespaceAndPath("specialforces", "keys"));

    public static final KeyMapping RELOAD_KEY = new KeyMapping(
            "key.specialforces.reload", GLFW.GLFW_KEY_R, CATEGORY);
}

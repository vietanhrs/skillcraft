package com.example.specialforces.network;

import com.example.specialforces.event.SFEvents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.network.CustomPayloadEvent;

public record ReloadGunPacket() {

    public static final StreamCodec<RegistryFriendlyByteBuf, ReloadGunPacket> STREAM_CODEC =
            StreamCodec.of((buf, pkt) -> {}, buf -> new ReloadGunPacket());

    public static void handle(ReloadGunPacket packet, CustomPayloadEvent.Context ctx) {
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            if (player != null) SFEvents.handleReload(player);
        });
        ctx.setPacketHandled(true);
    }
}

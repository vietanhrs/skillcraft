package com.example.specialforces.network;

import com.example.specialforces.event.SFEvents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.network.CustomPayloadEvent;

public record SniperShootPacket() {

    public static final StreamCodec<RegistryFriendlyByteBuf, SniperShootPacket> STREAM_CODEC =
            StreamCodec.of((buf, pkt) -> {}, buf -> new SniperShootPacket());

    public static void handle(SniperShootPacket packet, CustomPayloadEvent.Context ctx) {
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            if (player != null) SFEvents.handleSniperShot(player);
        });
        ctx.setPacketHandled(true);
    }
}

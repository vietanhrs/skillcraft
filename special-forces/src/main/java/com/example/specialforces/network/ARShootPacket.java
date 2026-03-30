package com.example.specialforces.network;

import com.example.specialforces.event.SFEvents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.network.CustomPayloadEvent;

public record ARShootPacket(int targetEntityId) {

    public static final StreamCodec<RegistryFriendlyByteBuf, ARShootPacket> STREAM_CODEC =
            StreamCodec.of(
                    (buf, pkt) -> buf.writeInt(pkt.targetEntityId),
                    buf -> new ARShootPacket(buf.readInt()));

    public static void handle(ARShootPacket packet, CustomPayloadEvent.Context ctx) {
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            if (player != null) SFEvents.handleARShot(player, packet.targetEntityId);
        });
        ctx.setPacketHandled(true);
    }
}

package com.example.specialforces.network;

import com.example.specialforces.event.SFEvents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.network.CustomPayloadEvent;

/**
 * Serverbound packet sent when the player fires the sniper rifle.
 * Carries the entity ID the client-side raycast hit (-1 for a miss)
 * so hit detection matches what the player actually aimed at.
 */
public record SniperShootPacket(int targetEntityId, int zoom) {

    public static final StreamCodec<RegistryFriendlyByteBuf, SniperShootPacket> STREAM_CODEC =
            StreamCodec.of(
                    (buf, pkt) -> { buf.writeInt(pkt.targetEntityId); buf.writeByte(pkt.zoom); },
                    buf -> new SniperShootPacket(buf.readInt(), buf.readByte()));

    public static void handle(SniperShootPacket packet, CustomPayloadEvent.Context ctx) {
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            if (player != null) SFEvents.handleSniperShot(player, packet.targetEntityId, packet.zoom);
        });
        ctx.setPacketHandled(true);
    }
}

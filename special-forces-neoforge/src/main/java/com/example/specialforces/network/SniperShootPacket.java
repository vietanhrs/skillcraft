package com.example.specialforces.network;

import com.example.specialforces.event.SFEvents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Serverbound packet sent when the player fires the sniper rifle.
 * Carries the entity ID the client-side raycast hit (-1 for a miss)
 * so hit detection matches what the player actually aimed at.
 */
public record SniperShootPacket(int targetEntityId, int zoom) implements CustomPacketPayload {

    public static final Type<SniperShootPacket> TYPE = new Type<>(SFNetwork.id("sniper_shoot"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SniperShootPacket> STREAM_CODEC =
            StreamCodec.of(
                    (buf, pkt) -> { buf.writeInt(pkt.targetEntityId); buf.writeByte(pkt.zoom); },
                    buf -> new SniperShootPacket(buf.readInt(), buf.readByte()));

    public static void handle(SniperShootPacket packet, IPayloadContext ctx) {
        if (ctx.player() instanceof ServerPlayer player) {
            SFEvents.handleSniperShot(player, packet.targetEntityId, packet.zoom);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

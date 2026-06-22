package com.example.specialforces.network;

import com.example.specialforces.event.SFEvents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ARShootPacket(int targetEntityId) implements CustomPacketPayload {

    public static final Type<ARShootPacket> TYPE = new Type<>(SFNetwork.id("ar_shoot"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ARShootPacket> STREAM_CODEC =
            StreamCodec.of(
                    (buf, pkt) -> buf.writeInt(pkt.targetEntityId),
                    buf -> new ARShootPacket(buf.readInt()));

    public static void handle(ARShootPacket packet, IPayloadContext ctx) {
        if (ctx.player() instanceof ServerPlayer player) {
            SFEvents.handleARShot(player, packet.targetEntityId);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

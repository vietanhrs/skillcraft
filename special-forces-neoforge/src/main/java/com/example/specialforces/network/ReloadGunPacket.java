package com.example.specialforces.network;

import com.example.specialforces.event.SFEvents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ReloadGunPacket() implements CustomPacketPayload {

    public static final Type<ReloadGunPacket> TYPE = new Type<>(SFNetwork.id("reload_gun"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ReloadGunPacket> STREAM_CODEC =
            StreamCodec.of((buf, pkt) -> {}, buf -> new ReloadGunPacket());

    public static void handle(ReloadGunPacket packet, IPayloadContext ctx) {
        if (ctx.player() instanceof ServerPlayer player) {
            SFEvents.handleReload(player);
        }
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

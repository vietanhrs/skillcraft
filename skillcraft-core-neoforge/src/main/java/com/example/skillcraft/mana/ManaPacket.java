package com.example.skillcraft.mana;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Server -> client packet that synchronises the local player's mana values.
 * Sent on login, respawn, dimension change, and whenever mana changes.
 */
public record ManaPacket(boolean hasMana, int mana, int maxMana) implements CustomPacketPayload {

    public static final Type<ManaPacket> TYPE = new Type<>(ManaNetwork.id("mana"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ManaPacket> STREAM_CODEC =
            StreamCodec.of((buf, packet) -> packet.encode(buf), ManaPacket::decode);

    public void encode(FriendlyByteBuf buf) {
        buf.writeBoolean(hasMana);
        buf.writeInt(mana);
        buf.writeInt(maxMana);
    }

    public static ManaPacket decode(FriendlyByteBuf buf) {
        return new ManaPacket(buf.readBoolean(), buf.readInt(), buf.readInt());
    }

    public static void handle(ManaPacket packet, IPayloadContext ctx) {
        ClientManaData.update(packet.hasMana(), packet.mana(), packet.maxMana());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

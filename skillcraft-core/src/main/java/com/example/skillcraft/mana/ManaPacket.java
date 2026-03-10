package com.example.skillcraft.mana;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent;

/**
 * Server → client packet that synchronises the local player's mana values.
 * Sent on login, respawn, dimension change, and whenever mana changes.
 */
public record ManaPacket(boolean hasMana, int mana, int maxMana) {

    public void encode(FriendlyByteBuf buf) {
        buf.writeBoolean(hasMana);
        buf.writeInt(mana);
        buf.writeInt(maxMana);
    }

    public static ManaPacket decode(FriendlyByteBuf buf) {
        return new ManaPacket(buf.readBoolean(), buf.readInt(), buf.readInt());
    }

    public static void handle(ManaPacket packet, CustomPayloadEvent.Context ctx) {
        ctx.enqueueWork(() ->
                ClientManaData.update(packet.hasMana(), packet.mana(), packet.maxMana()));
        ctx.setPacketHandled(true);
    }
}

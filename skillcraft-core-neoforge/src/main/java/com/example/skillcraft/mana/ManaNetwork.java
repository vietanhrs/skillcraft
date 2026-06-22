package com.example.skillcraft.mana;

import com.example.skillcraft.Skillcraft;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public class ManaNetwork {

    public static void register(IEventBus modBus) {
        modBus.addListener(ManaNetwork::registerPayloads);
    }

    private static void registerPayloads(RegisterPayloadHandlersEvent event) {
        event.registrar("1")
                .playToClient(ManaPacket.TYPE, ManaPacket.STREAM_CODEC, ManaPacket::handle);
    }

    /** Push the current server-side mana state to the given player's client. */
    public static void syncMana(ServerPlayer player) {
        PacketDistributor.sendToPlayer(
                player,
                new ManaPacket(
                        ManaHelper.hasManaBar(player),
                        ManaHelper.getMana(player),
                        ManaHelper.getMaxMana(player)));
    }

    static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(Skillcraft.MOD_ID, path);
    }
}

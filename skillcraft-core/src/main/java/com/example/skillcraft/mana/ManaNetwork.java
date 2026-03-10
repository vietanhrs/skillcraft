package com.example.skillcraft.mana;

import com.example.skillcraft.Skillcraft;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.SimpleChannel;

public class ManaNetwork {

    public static final SimpleChannel CHANNEL = ChannelBuilder
            .named(Identifier.fromNamespaceAndPath(Skillcraft.MOD_ID, "main"))
            .networkProtocolVersion(1)
            .acceptedVersions((status, version) -> true)
            .simpleChannel();

    public static void register() {
        CHANNEL.messageBuilder(ManaPacket.class, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(ManaPacket::encode)
                .decoder(ManaPacket::decode)
                .consumerMainThread(ManaPacket::handle)
                .add();
    }

    /** Push the current server-side mana state to the given player's client. */
    public static void syncMana(ServerPlayer player) {
        CHANNEL.send(
                new ManaPacket(
                        ManaHelper.hasManaBar(player),
                        ManaHelper.getMana(player),
                        ManaHelper.getMaxMana(player)
                ),
                PacketDistributor.PLAYER.with(player)
        );
    }
}

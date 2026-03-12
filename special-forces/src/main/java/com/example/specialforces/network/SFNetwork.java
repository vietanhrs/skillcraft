package com.example.specialforces.network;

import com.example.specialforces.SpecialForces;
import net.minecraft.resources.Identifier;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.SimpleChannel;

public class SFNetwork {

    public static final SimpleChannel CHANNEL = ChannelBuilder
            .named(Identifier.fromNamespaceAndPath(SpecialForces.MOD_ID, "main"))
            .networkProtocolVersion(1)
            .acceptedVersions((status, version) -> true)
            .simpleChannel();

    public static void register() {
        CHANNEL.play()
                .serverbound()
                .add(SniperShootPacket.class, SniperShootPacket.STREAM_CODEC, SniperShootPacket::handle);
    }

    public static void sendShoot() {
        CHANNEL.send(new SniperShootPacket(), PacketDistributor.SERVER.noArg());
    }
}

package com.example.specialforces.network;

import com.example.specialforces.SpecialForces;
import net.minecraft.resources.Identifier;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.SimpleChannel;

public class SFNetwork {

    public static final SimpleChannel CHANNEL = ChannelBuilder
            .named(Identifier.fromNamespaceAndPath(SpecialForces.MOD_ID, "main"))
            .networkProtocolVersion(2)
            .acceptedVersions((status, version) -> true)
            .simpleChannel();

    public static void register() {
        CHANNEL.play()
                .serverbound()
                .add(SniperShootPacket.class, SniperShootPacket.STREAM_CODEC, SniperShootPacket::handle)
                .add(ARShootPacket.class, ARShootPacket.STREAM_CODEC, ARShootPacket::handle)
                .add(ReloadGunPacket.class, ReloadGunPacket.STREAM_CODEC, ReloadGunPacket::handle);
    }

    public static void sendSniperShoot(int targetEntityId, int zoom) {
        CHANNEL.send(new SniperShootPacket(targetEntityId, zoom), PacketDistributor.SERVER.noArg());
    }

    public static void sendARShoot(int targetEntityId) {
        CHANNEL.send(new ARShootPacket(targetEntityId), PacketDistributor.SERVER.noArg());
    }

    public static void sendReload() {
        CHANNEL.send(new ReloadGunPacket(), PacketDistributor.SERVER.noArg());
    }
}

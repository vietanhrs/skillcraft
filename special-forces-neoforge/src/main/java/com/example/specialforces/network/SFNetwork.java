package com.example.specialforces.network;

import com.example.specialforces.SpecialForces;
import net.minecraft.resources.Identifier;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;

public class SFNetwork {

    public static void register(IEventBus modBus) {
        modBus.addListener(SFNetwork::registerPayloads);
    }

    private static void registerPayloads(RegisterPayloadHandlersEvent event) {
        event.registrar("2")
                .playToServer(SniperShootPacket.TYPE, SniperShootPacket.STREAM_CODEC, SniperShootPacket::handle)
                .playToServer(ARShootPacket.TYPE, ARShootPacket.STREAM_CODEC, ARShootPacket::handle)
                .playToServer(ReloadGunPacket.TYPE, ReloadGunPacket.STREAM_CODEC, ReloadGunPacket::handle);
    }

    public static void sendSniperShoot(int targetEntityId, int zoom) {
        ClientPacketDistributor.sendToServer(new SniperShootPacket(targetEntityId, zoom));
    }

    public static void sendARShoot(int targetEntityId) {
        ClientPacketDistributor.sendToServer(new ARShootPacket(targetEntityId));
    }

    public static void sendReload() {
        ClientPacketDistributor.sendToServer(new ReloadGunPacket());
    }

    static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(SpecialForces.MOD_ID, path);
    }
}

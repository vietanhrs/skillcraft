package com.example.specialforces;

import com.example.specialforces.client.ClientSetup;
import com.example.specialforces.init.SFFeatures;
import com.example.specialforces.init.SFItems;
import com.example.specialforces.network.SFNetwork;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(SpecialForces.MOD_ID)
public class SpecialForces {

    public static final String MOD_ID = "specialforces";

    public SpecialForces(FMLJavaModLoadingContext context) {
        var bus = context.getModBusGroup();
        SFItems.ITEMS.register(bus);
        SFFeatures.FEATURES.register(bus);
        SFNetwork.register();

        // Client-only: register scope overlay, NV overlay, FOV event, input handler
        FMLClientSetupEvent.getBus(bus).addListener(e -> ClientSetup.init());
    }
}

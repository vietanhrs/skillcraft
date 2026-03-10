package com.example.skillcraft.mana;

/**
 * Client-side cache of the local player's mana values, kept in sync via
 * {@link ManaPacket}.  Never access this class from server-side code.
 */
public class ClientManaData {

    public static boolean hasMana = false;
    public static int mana        = 0;
    public static int maxMana     = ManaHelper.DEFAULT_MAX_MANA;

    public static void update(boolean hasMana, int mana, int maxMana) {
        ClientManaData.hasMana = hasMana;
        ClientManaData.mana    = mana;
        ClientManaData.maxMana = maxMana;
    }
}

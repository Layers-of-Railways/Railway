package com.railwayteam.railways.compat.tracks.mods;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.compat.Mods;
import com.railwayteam.railways.compat.tracks.GenericTrackCompat;

public class BiomesOPlentyTrackCompat extends GenericTrackCompat {
    BiomesOPlentyTrackCompat() {
        super(Mods.BIOMESOPLENTY);
    }

    private static boolean registered = false;
    public static void register() {
        if (registered) {
            Railways.LOGGER.error("Duplicate registration of Biomes O' Plenty track compat");
            return;
        }
        registered = true;
        Railways.LOGGER.info("Registering tracks for Biomes O' Plenty");
        new BiomesOPlentyTrackCompat().register(
            "dead",
            "fir",
            "hellbark",
            "jacaranda",
            "magic",
            "mahogany",
            "palm",
            "redwood",
            "umbran",
            "willow"
        );
    }
}

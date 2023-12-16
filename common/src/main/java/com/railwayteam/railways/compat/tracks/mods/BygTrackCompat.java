package com.railwayteam.railways.compat.tracks.mods;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.compat.Mods;
import com.railwayteam.railways.compat.tracks.GenericTrackCompat;

public class BygTrackCompat extends GenericTrackCompat {
    BygTrackCompat() {
        super(Mods.BYG);
    }

    private static boolean registered = false;
    public static void register() {
        if (registered) {
            Railways.LOGGER.error("Duplicate registration of BYG track compat");
            return;
        }
        registered = true;
        Railways.LOGGER.info("Registering tracks for Oh The Biomes You'll Go");
        new BygTrackCompat().register(
            "aspen",
            "baobab",
            "blue_enchanted",
            "bulbis",
            "cherry",
            "cika",
            "cypress",
            "ebony",
            "embur",
            "ether",
            "fir",
            "green_enchanted",
            "holly",
            "imparius",
            "jacaranda",
            "lament",
            "mahogany",
            "maple",
            "nightshade",
            "palm",
            "pine",
            "rainbow_eucalyptus",
            "redwood",
            "skyris",
            "sythian",
            "white_mangrove",
            "willow",
            "witch_hazel",
            "zelkova"
        );
    }
}

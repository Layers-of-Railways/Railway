package com.railwayteam.railways.compat.tracks.mods;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.compat.Mods;
import com.railwayteam.railways.compat.tracks.GenericTrackCompat;

public class BlueSkiesTrackCompat extends GenericTrackCompat {
    BlueSkiesTrackCompat() {
        super(Mods.BLUE_SKIES);
    }

    private static boolean registered = false;
    public static void register() {
        if (registered) {
            Railways.LOGGER.error("Duplicate registration of Blue Skies track compat");
            return;
        }
        registered = true;
        Railways.LOGGER.info("Registering tracks for Blue Skies");
        new BlueSkiesTrackCompat().register(
            "bluebright",
            "dusk",
            "frostbright",
            "lunar",
            "maple",
            "starlit"
        );
    }
}

package com.railwayteam.railways.compat.tracks.mods;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.compat.Mods;
import com.railwayteam.railways.compat.tracks.GenericTrackCompat;

public class QuarkTrackCompat extends GenericTrackCompat {
    QuarkTrackCompat() {
        super(Mods.QUARK);
    }

    private static boolean registered = false;
    public static void register() {
        if (registered) {
            Railways.LOGGER.error("Duplicate registration of Quark track compat");
            return;
        }
        registered = true;
        Railways.LOGGER.info("Registering tracks for Quark");
        new QuarkTrackCompat().register(
                "blossom",
                "ancient",
                "azalea"
        );
    }
}

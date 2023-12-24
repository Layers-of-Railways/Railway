package com.railwayteam.railways.compat.tracks.mods;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.compat.Mods;
import com.railwayteam.railways.compat.tracks.GenericTrackCompat;

public class HexCastingTrackCompat extends GenericTrackCompat {
    HexCastingTrackCompat() {
        super(Mods.HEXCASTING);
    }

    private static boolean registered = false;
    public static void register() {
        if (registered) {
            Railways.LOGGER.error("Duplicate registration of Hex Casting track compat");
            return;
        }
        registered = true;
        Railways.LOGGER.info("Registering tracks for Hex Casting");
        new HexCastingTrackCompat().register(
            "edified"
        );
    }
}

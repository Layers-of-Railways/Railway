package com.railwayteam.railways.compat.tracks.mods;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.compat.Mods;
import com.railwayteam.railways.compat.tracks.GenericTrackCompat;

public class DreamsAndDesiresTrackCompat extends GenericTrackCompat {
    DreamsAndDesiresTrackCompat() {
        super("create_dd");
    }

    @Override
    protected boolean registerTracksAnyway() {
        return super.registerTracksAnyway() || Mods.DREAMSANDDESIRES.isLoaded;
    }

    private static boolean registered = false;
    public static void register() {
        if (registered) {
            Railways.LOGGER.error("Duplicate registration of Dreams and Desires compat");
            return;
        }
        registered = true;
        Railways.LOGGER.info("Registering tracks for Dreams and Desires");
        new DreamsAndDesiresTrackCompat().register(
                "rose",
                "rubber",
                "smoked",
                "spirit"
        );
    }
}

package com.railwayteam.railways.compat.tracks.mods;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.compat.Mods;
import com.railwayteam.railways.compat.tracks.GenericTrackCompat;

public class NaturesSpiritTrackCompat extends GenericTrackCompat {
    NaturesSpiritTrackCompat() {
        super("natures_spirit");
    }

    @Override
    protected boolean registerTracksAnyway() {
        return super.registerTracksAnyway() || Mods.NATURES_SPIRIT.isLoaded;
    }

    private static boolean registered = false;
    public static void register() {
        if (registered) {
            Railways.LOGGER.error("Duplicate registration of Nature's Spirit track compat");
            return;
        }
        registered = true;
        Railways.LOGGER.info("Registering tracks for Nature's Spirit");
        new NaturesSpiritTrackCompat().register(
            "palo_verde",
                "ghaf",
                "joshua",
                "olive",
                "cypress",
                "maple",
                "aspen",
                "willow",
                "fir",
                "wisteria",
                "sugi",
                "redwood"
        );
    }
}

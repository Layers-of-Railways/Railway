package com.railwayteam.railways.compat.tracks.mods;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.compat.Mods;
import com.railwayteam.railways.compat.tracks.GenericTrackCompat;
import net.minecraft.resources.ResourceLocation;

public class HexCastingTrackCompat extends GenericTrackCompat {
    HexCastingTrackCompat() {
        super("hexcasting");
    }

    @Override
    protected boolean registerTracksAnyway() {
        return super.registerTracksAnyway() || Mods.HEXCASTING.isLoaded;
    }

    @Override
    protected ResourceLocation getSlabLocation(String name) {
        return asResource("akashic_slab");
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

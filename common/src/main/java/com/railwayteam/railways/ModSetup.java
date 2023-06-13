package com.railwayteam.railways;

import com.railwayteam.railways.compat.Mods;
import com.railwayteam.railways.compat.tracks.mods.BlueSkiesTrackCompat;
import com.railwayteam.railways.compat.tracks.mods.BygTrackCompat;
import com.railwayteam.railways.compat.tracks.mods.HexCastingTrackCompat;
import com.railwayteam.railways.compat.tracks.mods.TwilightForestTrackCompat;
import com.railwayteam.railways.registry.*;

public class ModSetup {
  public static void register() {
    CRTrackMaterials.register();
    CRBogeyStyles.register();
    CRItems.register();
    CRBlockEntities.register();
    CRBlocks.register();
    CRContainerTypes.register();
    CREntities.register();
    CRSounds.register();
    CRTags.register();
    CREdgePointTypes.register();
    CRSchedule.register();
    CRDataFixers.register();
    CRExtraDisplays.register();

    Railways.registrate().creativeModeTab(() -> CRItems.compatTracksCreativeTab, "Create Steam 'n Rails: Compat Tracks");
    Mods.HEXCASTING.executeIfInstalled(() -> () -> HexCastingTrackCompat.register());
    BygTrackCompat.register();
    BlueSkiesTrackCompat.register();
    TwilightForestTrackCompat.register();
  }
}

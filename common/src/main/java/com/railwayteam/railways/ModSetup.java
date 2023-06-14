package com.railwayteam.railways;

import com.railwayteam.railways.compat.tracks.TrackCompatUtils;
import com.railwayteam.railways.compat.tracks.mods.*;
import com.railwayteam.railways.registry.*;
import com.railwayteam.railways.util.Utils;

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

    for(String compatMod : TrackCompatUtils.TRACK_COMPAT_MODS) {
      if(Utils.isModLoaded(compatMod, compatMod)) {
        Railways.registrate().creativeModeTab(() -> CRItems.compatTracksCreativeTab, "Create Steam 'n Rails: Compat Tracks");
        break;
      }
    }
    HexCastingTrackCompat.register();
    BygTrackCompat.register();
    BlueSkiesTrackCompat.register();
    TwilightForestTrackCompat.register();
    BiomesOPlentyTrackCompat.register();
  }
}

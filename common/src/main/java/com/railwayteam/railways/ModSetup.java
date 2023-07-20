package com.railwayteam.railways;

import com.railwayteam.railways.compat.tracks.TrackCompatUtils;
import com.railwayteam.railways.compat.tracks.mods.*;
import com.railwayteam.railways.registry.*;

public class ModSetup {
  public static void register() {
    Railways.registrate().useCreativeTab(CRCreativeModeTabs.getBaseTabKey());
    CRTrackMaterials.register();
    CRBogeyStyles.register();
    CRCreativeModeTabs.register();
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
    CRExtraRegistration.register();

    // Compat
    if (TrackCompatUtils.anyLoaded())
      Railways.registrate().useCreativeTab(CRCreativeModeTabs.getCompatTracksTabKey());
    HexCastingTrackCompat.register();
    BygTrackCompat.register();
    BlueSkiesTrackCompat.register();
    TwilightForestTrackCompat.register();
    BiomesOPlentyTrackCompat.register();
  }
}

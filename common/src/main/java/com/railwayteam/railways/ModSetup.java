package com.railwayteam.railways;

import com.railwayteam.railways.compat.tracks.TrackCompatUtils;
import com.railwayteam.railways.compat.tracks.mods.*;
import com.railwayteam.railways.registry.*;
import dev.architectury.injectables.annotations.ExpectPlatform;

public class ModSetup {

  @ExpectPlatform
  private static void useBaseTab() {
    throw new AssertionError();
  }

  @ExpectPlatform
  private static void useCompatTab() {
    throw new AssertionError();
  }

  public static void register() {
    useBaseTab();
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
      useCompatTab(); // fixme use track tab
    HexCastingTrackCompat.register();
    BygTrackCompat.register();
    BlueSkiesTrackCompat.register();
    TwilightForestTrackCompat.register();
    BiomesOPlentyTrackCompat.register();
  }
}

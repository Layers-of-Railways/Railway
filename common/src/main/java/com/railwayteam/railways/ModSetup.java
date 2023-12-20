package com.railwayteam.railways;

import com.railwayteam.railways.compat.tracks.mods.*;
import com.railwayteam.railways.content.custom_tracks.casing.CasingCollisionUtils;
import com.railwayteam.railways.registry.*;

public class ModSetup {
  public static void register() {
    CRTrackMaterials.register();
    CRBogeyStyles.register();
    CRItems.register();
    CRSpriteShifts.register();
    CRBlockEntities.register();
    CRBlocks.register();
    CRPalettes.register();
    CRContainerTypes.register();
    CREntities.register();
    CRSounds.register();
    CRTags.register();
    CREdgePointTypes.register();
    CRSchedule.register();
    CRDataFixers.register();
    CRExtraRegistration.register();
    CasingCollisionUtils.register();

    // Compat
    HexCastingTrackCompat.register();
    BygTrackCompat.register();
    BlueSkiesTrackCompat.register();
    TwilightForestTrackCompat.register();
    BiomesOPlentyTrackCompat.register();
    DreamsAndDesiresTrackCompat.register();
    QuarkTrackCompat.register();
  }
}

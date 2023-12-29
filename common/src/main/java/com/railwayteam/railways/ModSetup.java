package com.railwayteam.railways;

import com.railwayteam.railways.compat.tracks.mods.*;
import com.railwayteam.railways.content.custom_tracks.casing.CasingCollisionUtils;
import com.railwayteam.railways.registry.*;
import dev.architectury.injectables.annotations.ExpectPlatform;

public class ModSetup {

  @ExpectPlatform
  public static void useBaseTab() {
    throw new AssertionError();
  }

  @ExpectPlatform
  public static void useTracksTab() {
    throw new AssertionError();
  }

  @ExpectPlatform
  public static void usePalettesTab() {
    throw new AssertionError();
  }

  public static void register() {
    useBaseTab();
    CRTrackMaterials.register();
    CRBogeyStyles.register();
    CRCreativeModeTabs.register();
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
    CRInteractionBehaviours.register();

    // Compat
    useTracksTab();
    HexCastingTrackCompat.register();
    BygTrackCompat.register();
    BlueSkiesTrackCompat.register();
    TwilightForestTrackCompat.register();
    BiomesOPlentyTrackCompat.register();
    NaturesSpiritTrackCompat.register();
    DreamsAndDesiresTrackCompat.register();
    QuarkTrackCompat.register();
    TFCTrackCompat.register();
  }
}

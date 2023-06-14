package com.railwayteam.railways;

import com.railwayteam.railways.compat.Mods;
import com.railwayteam.railways.compat.tracks.mods.*;
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


    if (Mods.HEXCASTING.isLoaded || Mods.BYG.isLoaded || Mods.BLUE_SKIES.isLoaded || Mods.TWILIGHTFOREST.isLoaded || Mods.BIOMESOPLENTY.isLoaded)
      Railways.registrate().creativeModeTab(() -> CRItems.compatTracksCreativeTab, "Create Steam 'n Rails: Compat Tracks");

    if (Mods.HEXCASTING.isLoaded)
      HexCastingTrackCompat.register();
    if (Mods.BYG.isLoaded)
      BygTrackCompat.register();
    if (Mods.BLUE_SKIES.isLoaded)
      BlueSkiesTrackCompat.register();
    if (Mods.TWILIGHTFOREST.isLoaded)
      TwilightForestTrackCompat.register();
    if (Mods.BIOMESOPLENTY.isLoaded)
      BiomesOPlentyTrackCompat.register();
  }
}

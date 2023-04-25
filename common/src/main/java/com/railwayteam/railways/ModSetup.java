package com.railwayteam.railways;

import com.railwayteam.railways.registry.*;

public class ModSetup {
  public static void register() {
    CRTrackMaterials.register();
    CRItems.register();
    CRBlockEntities.register();
    CRBlocks.register();
    CRContainerTypes.register();
    CREntities.register();
    CRSounds.register();
    CRTags.register();
    CREdgePointTypes.register();
    CRSchedule.register();
  }
}


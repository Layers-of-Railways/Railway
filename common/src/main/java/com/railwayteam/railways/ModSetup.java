package com.railwayteam.railways;

import com.railwayteam.railways.registry.*;

public class ModSetup {
  public void init() {
  }

  public static void register() {
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


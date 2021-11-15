package com.railwayteam.railways;

import com.railwayteam.railways.registry.CRBlocks;
import com.railwayteam.railways.registry.CREntities;
import com.railwayteam.railways.registry.CRItems;
import com.railwayteam.railways.registry.CRTiles;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.repack.registrate.Registrate;

public class ModSetup {
    public void init() {
    }

  public static void register (Registrate reg) {
      CRItems.register(reg);
      CRBlocks.register(reg);
      CRTiles.register(reg);
      CREntities.register(reg);
  }
}


package com.railwayteam.railways;

import com.railwayteam.railways.content.coupling.CustomTrackOverlayRendering;
import com.railwayteam.railways.registry.CRBlockPartials;
import com.railwayteam.railways.registry.CREdgePointTypes;
import com.railwayteam.railways.registry.CRPonderIndex;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

public class RailwaysClient {
  public static void clientSetup(FMLClientSetupEvent event) {
  }

  public static void clientRegister() {
    CRPonderIndex.register();
    CRBlockPartials.init();
    CustomTrackOverlayRendering.register(CREdgePointTypes.COUPLER, CRBlockPartials.COUPLER_BOTH);
  }
}

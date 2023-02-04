package com.railwayteam.railways;

import com.railwayteam.railways.compat.Mods;
import com.railwayteam.railways.compat.journeymap.RailwayMapPlugin;
import com.railwayteam.railways.content.coupling.CustomTrackOverlayRendering;
import com.railwayteam.railways.registry.CRBlockPartials;
import com.railwayteam.railways.registry.CREdgePointTypes;
import com.railwayteam.railways.registry.CRPonderIndex;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import static com.railwayteam.railways.registry.CRBlockPartials.registerCustomCap;

public class RailwaysClient {
  public static void clientSetup(FMLClientSetupEvent event) {
  }

  public static void clientCtor() {
    CRPonderIndex.register();
    CRBlockPartials.init();
    CustomTrackOverlayRendering.register(CREdgePointTypes.COUPLER, CRBlockPartials.COUPLER_BOTH);
    Mods.JOURNEYMAP.executeIfInstalled(() -> RailwayMapPlugin::load);
    registerCustomCap("Slimeist", "slimeist");
    registerCustomCap("bosbesballon", "bosbesballon");
    registerCustomCap("SpottyTheTurtle", "turtle");
  }
}

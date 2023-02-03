package com.railwayteam.railways;

import com.railwayteam.railways.registry.*;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class ModSetup {
  public void init() {
  }

  public static void register() {
    CRItems.register();
    CRBlockEntities.register();
    CRBlocks.register();
    CRContainerTypes.register();
    CREntities.register();
    CRSounds.register(FMLJavaModLoadingContext.get().getModEventBus());
    CRPackets.registerPackets();
    CRTags.register();
    CREdgePointTypes.register();
    CRSchedule.register();
  }
}


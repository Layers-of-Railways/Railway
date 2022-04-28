package com.railwayteam.railways;

import com.railwayteam.railways.registry.CRBlocks;
import com.railwayteam.railways.registry.CREntities;
import com.railwayteam.railways.registry.CRItems;
import com.railwayteam.railways.registry.CRBlockEntities;
import com.railwayteam.railways.util.packet.PacketSender;
import com.simibubi.create.repack.registrate.Registrate;

public class ModSetup {
  public void init() {
  }

  public static void register (Registrate reg) {
    CRItems.register(reg);
    CRBlocks.register(reg);
    CRBlockEntities.register(reg);
    CREntities.register(reg);
    PacketSender.register();
  }
}


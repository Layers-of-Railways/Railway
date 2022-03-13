package com.railwayteam.railways.registry;

import com.railwayteam.railways.content.Boiler.BoilerBlockEntity;
import com.railwayteam.railways.content.Firebox.FireboxBlockEntity;
import com.simibubi.create.repack.registrate.Registrate;
import com.simibubi.create.repack.registrate.util.entry.BlockEntityEntry;

public class CRBlockEntities {
  public static BlockEntityEntry<FireboxBlockEntity> FIREBOX_BE;
  public static BlockEntityEntry<BoilerBlockEntity>  BOILER_BE;

  public static void register(Registrate reg) {
    FIREBOX_BE = reg.blockEntity("firebox", FireboxBlockEntity::new)
    .validBlock(CRBlocks.BLOCK_FIREBOX)
    .register();

    BOILER_BE = reg.blockEntity("boiler", BoilerBlockEntity::new)
    .validBlock(CRBlocks.BLOCK_BOILER)
    .register();
  }
}

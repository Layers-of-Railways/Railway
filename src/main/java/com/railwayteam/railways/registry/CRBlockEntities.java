package com.railwayteam.railways.registry;

import com.railwayteam.railways.content.Boiler.BoilerBlockEntity;
import com.railwayteam.railways.content.Tender.TenderBlockEntity;
import com.simibubi.create.repack.registrate.Registrate;
import com.simibubi.create.repack.registrate.util.entry.BlockEntityEntry;

public class CRBlockEntities {
  public static BlockEntityEntry<TenderBlockEntity> TENDER_BE;
  public static BlockEntityEntry<BoilerBlockEntity> BOILER_BE;

  public static void register (Registrate reg) {
    TENDER_BE = reg.blockEntity("tender", TenderBlockEntity::new)
    .validBlock(CRBlocks.BLOCK_TENDER)
    .register();

    BOILER_BE = reg.blockEntity ("boiler", BoilerBlockEntity::new)
    .validBlock(CRBlocks.BLOCK_BOILER)
    .register();
  }
}

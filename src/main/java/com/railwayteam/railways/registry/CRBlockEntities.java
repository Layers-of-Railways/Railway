package com.railwayteam.railways.registry;

import com.railwayteam.railways.content.tender.TenderBlockEntity;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

public class CRBlockEntities {
  public static BlockEntityEntry<TenderBlockEntity> TENDER_BE;

  public static void register (Registrate reg) {
    /*
    TENDER_BE = reg.blockEntity("tender", TenderBlockEntity::new)
    .validBlock(CRBlocks.BLOCK_TENDER)
    .register();

    BOILER_BE = reg.blockEntity ("boiler", BoilerBlockEntity::new)
    .validBlock(CRBlocks.BLOCK_BOILER)
    .register();
     */
  }
}

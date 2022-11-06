package com.railwayteam.railways.registry;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.content.semaphore.SemaphoreBlockEntity;
import com.railwayteam.railways.content.semaphore.SemaphoreRenderer;
import com.railwayteam.railways.content.tender.TenderBlockEntity;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.entry.BlockEntityEntry;

public class CRBlockEntities {
  private static final CreateRegistrate REGISTRATE = Railways.registrate();

  public static final BlockEntityEntry<TenderBlockEntity> TENDER_BE = null;
  public static final BlockEntityEntry<SemaphoreBlockEntity> SEMAPHORE = REGISTRATE.tileEntity("semaphore", SemaphoreBlockEntity::new)
          .validBlocks(CRBlocks.SEMAPHORE)
          .renderer(() -> SemaphoreRenderer::new)
          .register();


  @SuppressWarnings("EmptyMethod")
  public static void register() {}
}

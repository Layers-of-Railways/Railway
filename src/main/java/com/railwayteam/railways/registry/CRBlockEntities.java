package com.railwayteam.railways.registry;

import com.railwayteam.railways.content.Firebox.FireboxBlockEntity;
import com.simibubi.create.repack.registrate.Registrate;
import com.simibubi.create.repack.registrate.util.entry.BlockEntityEntry;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class CRBlockEntities {
  public static BlockEntityEntry<BlockEntity> FIREBOX_BE;

  public static void register(Registrate reg) {
    FIREBOX_BE = reg.blockEntity("firebox", FireboxBlockEntity::new)
    .validBlock(CRBlocks.BLOCK_FIREBOX)
    .register();
  }

  public static class Type {
    public static BlockEntityType<FireboxBlockEntity> FIREBOX = BlockEntityType.Builder.of(FireboxBlockEntity::new, CRBlocks.BLOCK_FIREBOX.get()).build(null);
  }
}

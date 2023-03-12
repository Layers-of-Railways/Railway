package com.railwayteam.railways.mixin;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.track_api.TrackMaterial;
import com.railwayteam.railways.registry.CRBlocks;
import com.simibubi.create.AllTileEntities;
import com.tterrag.registrate.builders.BlockEntityBuilder;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.Slice;

@Mixin(value = AllTileEntities.class, remap = false)
public class MixinAllTileEntities {
  @Redirect(method = "<clinit>",
      at = @At(value = "INVOKE", target = "Lcom/tterrag/registrate/builders/BlockEntityBuilder;validBlocks([Lcom/tterrag/registrate/util/nullness/NonNullSupplier;)Lcom/tterrag/registrate/builders/BlockEntityBuilder;"),
      slice = @Slice(
          from = @At(value = "CONSTANT", args = "stringValue=toolbox"),
          to = @At(value = "CONSTANT", args = "stringValue=fake_track")
      )
  )
  private static BlockEntityBuilder<?, ?> addLegalTracks(BlockEntityBuilder<?, ?> instance, NonNullSupplier<? extends Block>[] blocks) { //TODO track api
    //Railways.LOGGER.info("TEST POS 1");
    for (BlockEntry<? extends Block> blockEntry : TrackMaterial.allBlocks()) {
      if (blockEntry != null) {
        instance.validBlock(blockEntry);
      }
    }
    return instance.validBlocks(blocks);
  }
}

package com.railwayteam.railways.registry;

import com.railwayteam.railways.content.tender.TenderBlock;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.util.entry.BlockEntry;

public class CRBlocks {
  public static BlockEntry<TenderBlock> BLOCK_TENDER;

  public static void register(Registrate reg) {
/*
    BLOCK_TENDER = reg.block("tender", TenderBlock::new)
    .blockstate((ctx,prov)->
      prov.getVariantBuilder(ctx.get()).forAllStates(state -> {
        ResourceLocation loc = prov.modLoc("block/tender/" + state.getValue(TenderBlock.SHAPE).getSerializedName());
        return ConfiguredModel.builder().modelFile(prov.models().getExistingFile(loc))
        .rotationY(switch(state.getValue(TenderBlock.FACING)) {
          case SOUTH -> 180;
          case EAST  ->  90;
          case WEST  -> -90;
          default    ->   0;
        })
        .build();
      }))
    .item()
      .model((ctx,prov)-> prov.getExistingFile(prov.modLoc("tender")))
      .build()
    .lang("Tender")
    .addLayer(()-> RenderType::cutoutMipped)
    .register();*/
  }
}

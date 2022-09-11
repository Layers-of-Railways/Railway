package com.railwayteam.railways.registry;

import com.railwayteam.railways.content.Boiler.BoilerBlock;
import com.railwayteam.railways.content.HydraulicPistonBlock;
import com.railwayteam.railways.content.Steamcart.SteamCartBlock;
import com.railwayteam.railways.content.Tender.TenderBlock;;
import com.tterrag.registrate.Registrate;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraftforge.client.model.generators.ConfiguredModel;

public class CRBlocks {
  public static BlockEntry<TenderBlock> BLOCK_TENDER;
  public static BlockEntry<BoilerBlock>          BLOCK_BOILER;
  public static BlockEntry<HydraulicPistonBlock> BLOCK_HYDRAULIC_PISTON;
  public static BlockEntry<SteamCartBlock>       BLOCK_STEAMCART;

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
    .register();

    BLOCK_BOILER = reg.block("boiler", BoilerBlock::new)
    .blockstate((ctx,prov)-> {
      String loc = "block/" + ctx.getName() + "/" + ctx.getName();
      prov.horizontalBlock(ctx.get(),
        prov.modLoc(loc + "_side"),  // side
        prov.modLoc(loc + "_unlit"), // front
        prov.modLoc(loc + "_side")); // top
    })
    .onRegister(CreateRegistrate.connectedTextures(()-> new FacingBlockCTBehaviour(CTSpriteShifts.BOILER)))
    .simpleItem()
    .lang("Boiler")
    .register();

    BLOCK_HYDRAULIC_PISTON = reg.block("hydraulic_piston", HydraulicPistonBlock::new)
    .blockstate((ctx,prov)-> prov.simpleBlock(ctx.get(), prov.models().getExistingFile(prov.modLoc("block/"+ ctx.getName()))))
    .simpleItem()
    .lang("Hydraulic Piston")
    .register();
//*/
    // this is used to render the steam cart entity, it isn't meant to be a placeable block
    BLOCK_STEAMCART = reg.block("steamcart", SteamCartBlock::new)
    .blockstate((ctx,prov)-> prov.models().getExistingFile(prov.modLoc("entity/steamcart")))
    .blockstate((ctx,prov)-> prov.getVariantBuilder(ctx.get()).forAllStates((state)-> ConfiguredModel.builder()
      .modelFile(prov.models().getExistingFile(prov.modLoc("entity/steamcart_" + (state.getValue(SteamCartBlock.POWERED) ? "on" : "off"))))
      .rotationY((int)state.getValue(SteamCartBlock.FACING).toYRot())
      .build()
    ))
    .register();
  }
}

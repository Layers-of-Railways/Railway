package com.railwayteam.railways.registry;

import com.railwayteam.railways.Railways;
import com.railwayteam.railways.base.CTSpriteShifts;
import com.railwayteam.railways.base.ConnectedBlockCTBehavior;
import com.railwayteam.railways.content.*;
import com.railwayteam.railways.content.Boiler.BoilerBlock;
import com.railwayteam.railways.content.Firebox.FireboxBlock;
import com.railwayteam.railways.content.Steamcart.SteamCartBlock;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.repack.registrate.Registrate;
import com.simibubi.create.repack.registrate.util.entry.BlockEntry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.generators.ConfiguredModel;

public class CRBlocks {
  public static BlockEntry<FireboxBlock>         BLOCK_FIREBOX;
  public static BlockEntry<BoilerBlock>          BLOCK_BOILER;
  public static BlockEntry<HydraulicPistonBlock> BLOCK_HYDRAULIC_PISTON;
  public static BlockEntry<SteamCartBlock>       BLOCK_STEAMCART;

  public static void register(Registrate reg) {
    BLOCK_FIREBOX = reg.block("firebox", FireboxBlock::new)
    .blockstate((ctx,prov)->
    //  prov.simpleBlock(ctx.get(), prov.models().getExistingFile(prov.modLoc("block/"+ ctx.getName())));
      prov.getVariantBuilder(ctx.get()).forAllStates(state -> {
        ResourceLocation loc = prov.modLoc("block/firebox/" + state.getValue(FireboxBlock.SHAPE).getSerializedName());
        return ConfiguredModel.builder().modelFile(prov.models().getExistingFile(loc))
        .rotationY(switch(state.getValue(FireboxBlock.FACING)) {
          case SOUTH -> 180;
          case EAST  ->  90;
          case WEST  -> -90;
          default    ->   0;
        })
        //.rotationY(90) // determine rotation for model somehow, so we don't need a million state enum values
        .build();
      }))
    .item()
      .model((ctx,prov)-> prov.getExistingFile(prov.modLoc("firebox")))
      .build()
    .lang("Firebox")
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
    .onRegister(CreateRegistrate.connectedTextures(()-> new ConnectedBlockCTBehavior(CTSpriteShifts.BOILER)))
    .simpleItem()
    .lang("Boiler")
    .register();

    BLOCK_HYDRAULIC_PISTON = reg.block("hydraulic_piston", HydraulicPistonBlock::new)
    .blockstate((ctx,prov)-> prov.simpleBlock(ctx.get(), prov.models().getExistingFile(prov.modLoc("block/"+ ctx.getName()))))
    .simpleItem()
    .lang("Hydraulic Piston")
    .register();

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

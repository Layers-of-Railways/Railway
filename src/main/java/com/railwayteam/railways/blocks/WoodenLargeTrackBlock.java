package com.railwayteam.railways.blocks;

import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import net.minecraftforge.client.model.generators.ModelFile;

public class WoodenLargeTrackBlock extends LargeTrackBlock {
  public static final String name = "wooden_large_track";

  public WoodenLargeTrackBlock(Properties properties) {
    super(properties);
  }

  // Overloaded from AbstractLargeTrackBlock
  public static ModelFile partialModel(DataGenContext<?, ?> ctx, RegistrateBlockstateProvider prov, String... suffix) {
    StringBuilder loc = new StringBuilder("block/wide_gauge/wooden/" + ctx.getName().replace("wooden_",""));
    for (String suf : suffix) loc.append("_" + suf);
    return prov.models().getExistingFile(prov.modLoc(loc.toString()));
  }
}
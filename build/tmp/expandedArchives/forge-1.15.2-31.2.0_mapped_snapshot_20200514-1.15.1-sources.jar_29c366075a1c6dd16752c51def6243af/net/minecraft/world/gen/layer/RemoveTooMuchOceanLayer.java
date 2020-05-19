package net.minecraft.world.gen.layer;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.ICastleTransformer;

public enum RemoveTooMuchOceanLayer implements ICastleTransformer {
   INSTANCE;

   public int apply(INoiseRandom context, int north, int west, int south, int east, int center) {
      return LayerUtil.isShallowOcean(center) && LayerUtil.isShallowOcean(north) && LayerUtil.isShallowOcean(west) && LayerUtil.isShallowOcean(east) && LayerUtil.isShallowOcean(south) && context.random(2) == 0 ? 1 : center;
   }
}
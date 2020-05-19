package net.minecraft.world.gen.layer;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.IC0Transformer;

public enum StartRiverLayer implements IC0Transformer {
   INSTANCE;

   public int apply(INoiseRandom context, int value) {
      return LayerUtil.isShallowOcean(value) ? value : context.random(299999) + 2;
   }
}
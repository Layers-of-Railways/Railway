package net.minecraft.world.gen.layer;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.IC0Transformer;
import net.minecraft.world.gen.layer.traits.ICastleTransformer;

public class EdgeLayer {
   public static enum CoolWarm implements ICastleTransformer {
      INSTANCE;

      public int apply(INoiseRandom context, int north, int west, int south, int east, int center) {
         return center != 1 || north != 3 && west != 3 && east != 3 && south != 3 && north != 4 && west != 4 && east != 4 && south != 4 ? center : 2;
      }
   }

   public static enum HeatIce implements ICastleTransformer {
      INSTANCE;

      public int apply(INoiseRandom context, int north, int west, int south, int east, int center) {
         return center != 4 || north != 1 && west != 1 && east != 1 && south != 1 && north != 2 && west != 2 && east != 2 && south != 2 ? center : 3;
      }
   }

   public static enum Special implements IC0Transformer {
      INSTANCE;

      public int apply(INoiseRandom context, int value) {
         if (!LayerUtil.isShallowOcean(value) && context.random(13) == 0) {
            value |= 1 + context.random(15) << 8 & 3840;
         }

         return value;
      }
   }
}
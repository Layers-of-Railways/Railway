package net.minecraft.world.gen.layer;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.layer.traits.ICastleTransformer;

public enum DeepOceanLayer implements ICastleTransformer {
   INSTANCE;

   public int apply(INoiseRandom context, int north, int west, int south, int east, int center) {
      if (LayerUtil.isShallowOcean(center)) {
         int i = 0;
         if (LayerUtil.isShallowOcean(north)) {
            ++i;
         }

         if (LayerUtil.isShallowOcean(west)) {
            ++i;
         }

         if (LayerUtil.isShallowOcean(east)) {
            ++i;
         }

         if (LayerUtil.isShallowOcean(south)) {
            ++i;
         }

         if (i > 3) {
            if (center == LayerUtil.WARM_OCEAN) {
               return LayerUtil.DEEP_WARM_OCEAN;
            }

            if (center == LayerUtil.LUKEWARM_OCEAN) {
               return LayerUtil.DEEP_LUKEWARM_OCEAN;
            }

            if (center == LayerUtil.OCEAN) {
               return LayerUtil.DEEP_OCEAN;
            }

            if (center == LayerUtil.COLD_OCEAN) {
               return LayerUtil.DEEP_COLD_OCEAN;
            }

            if (center == LayerUtil.FROZEN_OCEAN) {
               return LayerUtil.DEEP_FROZEN_OCEAN;
            }

            return LayerUtil.DEEP_OCEAN;
         }
      }

      return center;
   }
}
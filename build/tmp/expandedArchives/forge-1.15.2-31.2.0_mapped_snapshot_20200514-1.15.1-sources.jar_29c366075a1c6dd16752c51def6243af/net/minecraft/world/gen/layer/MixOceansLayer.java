package net.minecraft.world.gen.layer;

import net.minecraft.world.gen.INoiseRandom;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.layer.traits.IAreaTransformer2;
import net.minecraft.world.gen.layer.traits.IDimOffset0Transformer;

public enum MixOceansLayer implements IAreaTransformer2, IDimOffset0Transformer {
   INSTANCE;

   public int apply(INoiseRandom p_215723_1_, IArea p_215723_2_, IArea p_215723_3_, int p_215723_4_, int p_215723_5_) {
      int i = p_215723_2_.getValue(this.func_215721_a(p_215723_4_), this.func_215722_b(p_215723_5_));
      int j = p_215723_3_.getValue(this.func_215721_a(p_215723_4_), this.func_215722_b(p_215723_5_));
      if (!LayerUtil.isOcean(i)) {
         return i;
      } else {
         int k = 8;
         int l = 4;

         for(int i1 = -8; i1 <= 8; i1 += 4) {
            for(int j1 = -8; j1 <= 8; j1 += 4) {
               int k1 = p_215723_2_.getValue(this.func_215721_a(p_215723_4_ + i1), this.func_215722_b(p_215723_5_ + j1));
               if (!LayerUtil.isOcean(k1)) {
                  if (j == LayerUtil.WARM_OCEAN) {
                     return LayerUtil.LUKEWARM_OCEAN;
                  }

                  if (j == LayerUtil.FROZEN_OCEAN) {
                     return LayerUtil.COLD_OCEAN;
                  }
               }
            }
         }

         if (i == LayerUtil.DEEP_OCEAN) {
            if (j == LayerUtil.LUKEWARM_OCEAN) {
               return LayerUtil.DEEP_LUKEWARM_OCEAN;
            }

            if (j == LayerUtil.OCEAN) {
               return LayerUtil.DEEP_OCEAN;
            }

            if (j == LayerUtil.COLD_OCEAN) {
               return LayerUtil.DEEP_COLD_OCEAN;
            }

            if (j == LayerUtil.FROZEN_OCEAN) {
               return LayerUtil.DEEP_FROZEN_OCEAN;
            }
         }

         return j;
      }
   }
}
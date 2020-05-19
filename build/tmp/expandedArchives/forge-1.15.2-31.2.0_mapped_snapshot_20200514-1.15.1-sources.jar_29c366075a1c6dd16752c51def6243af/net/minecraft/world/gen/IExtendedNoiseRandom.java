package net.minecraft.world.gen;

import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.layer.traits.IPixelTransformer;

public interface IExtendedNoiseRandom<R extends IArea> extends INoiseRandom {
   void setPosition(long x, long z);

   R func_212861_a_(IPixelTransformer p_212861_1_);

   default R func_212859_a_(IPixelTransformer p_212859_1_, R p_212859_2_) {
      return (R)this.func_212861_a_(p_212859_1_);
   }

   default R makeArea(IPixelTransformer p_212860_1_, R p_212860_2_, R p_212860_3_) {
      return (R)this.func_212861_a_(p_212860_1_);
   }

   default int func_215715_a(int p_215715_1_, int p_215715_2_) {
      return this.random(2) == 0 ? p_215715_1_ : p_215715_2_;
   }

   default int func_215714_a(int p_215714_1_, int p_215714_2_, int p_215714_3_, int p_215714_4_) {
      int i = this.random(4);
      if (i == 0) {
         return p_215714_1_;
      } else if (i == 1) {
         return p_215714_2_;
      } else {
         return i == 2 ? p_215714_3_ : p_215714_4_;
      }
   }
}
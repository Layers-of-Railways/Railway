package net.minecraft.world.gen.layer;

import net.minecraft.world.gen.IExtendedNoiseRandom;
import net.minecraft.world.gen.area.IArea;
import net.minecraft.world.gen.layer.traits.IAreaTransformer1;

public enum ZoomLayer implements IAreaTransformer1 {
   NORMAL,
   FUZZY {
      protected int func_202715_a(IExtendedNoiseRandom<?> p_202715_1_, int p_202715_2_, int p_202715_3_, int p_202715_4_, int p_202715_5_) {
         return p_202715_1_.func_215714_a(p_202715_2_, p_202715_3_, p_202715_4_, p_202715_5_);
      }
   };

   private ZoomLayer() {
   }

   public int func_215721_a(int p_215721_1_) {
      return p_215721_1_ >> 1;
   }

   public int func_215722_b(int p_215722_1_) {
      return p_215722_1_ >> 1;
   }

   public int func_215728_a(IExtendedNoiseRandom<?> p_215728_1_, IArea p_215728_2_, int p_215728_3_, int p_215728_4_) {
      int i = p_215728_2_.getValue(this.func_215721_a(p_215728_3_), this.func_215722_b(p_215728_4_));
      p_215728_1_.setPosition((long)(p_215728_3_ >> 1 << 1), (long)(p_215728_4_ >> 1 << 1));
      int j = p_215728_3_ & 1;
      int k = p_215728_4_ & 1;
      if (j == 0 && k == 0) {
         return i;
      } else {
         int l = p_215728_2_.getValue(this.func_215721_a(p_215728_3_), this.func_215722_b(p_215728_4_ + 1));
         int i1 = p_215728_1_.func_215715_a(i, l);
         if (j == 0 && k == 1) {
            return i1;
         } else {
            int j1 = p_215728_2_.getValue(this.func_215721_a(p_215728_3_ + 1), this.func_215722_b(p_215728_4_));
            int k1 = p_215728_1_.func_215715_a(i, j1);
            if (j == 1 && k == 0) {
               return k1;
            } else {
               int l1 = p_215728_2_.getValue(this.func_215721_a(p_215728_3_ + 1), this.func_215722_b(p_215728_4_ + 1));
               return this.func_202715_a(p_215728_1_, i, j1, l, l1);
            }
         }
      }
   }

   protected int func_202715_a(IExtendedNoiseRandom<?> p_202715_1_, int p_202715_2_, int p_202715_3_, int p_202715_4_, int p_202715_5_) {
      if (p_202715_3_ == p_202715_4_ && p_202715_4_ == p_202715_5_) {
         return p_202715_3_;
      } else if (p_202715_2_ == p_202715_3_ && p_202715_2_ == p_202715_4_) {
         return p_202715_2_;
      } else if (p_202715_2_ == p_202715_3_ && p_202715_2_ == p_202715_5_) {
         return p_202715_2_;
      } else if (p_202715_2_ == p_202715_4_ && p_202715_2_ == p_202715_5_) {
         return p_202715_2_;
      } else if (p_202715_2_ == p_202715_3_ && p_202715_4_ != p_202715_5_) {
         return p_202715_2_;
      } else if (p_202715_2_ == p_202715_4_ && p_202715_3_ != p_202715_5_) {
         return p_202715_2_;
      } else if (p_202715_2_ == p_202715_5_ && p_202715_3_ != p_202715_4_) {
         return p_202715_2_;
      } else if (p_202715_3_ == p_202715_4_ && p_202715_2_ != p_202715_5_) {
         return p_202715_3_;
      } else if (p_202715_3_ == p_202715_5_ && p_202715_2_ != p_202715_4_) {
         return p_202715_3_;
      } else {
         return p_202715_4_ == p_202715_5_ && p_202715_2_ != p_202715_3_ ? p_202715_4_ : p_202715_1_.func_215714_a(p_202715_2_, p_202715_3_, p_202715_4_, p_202715_5_);
      }
   }
}
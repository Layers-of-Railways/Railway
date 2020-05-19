package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.gen.IWorldGenerationReader;

public class MegaPineTree extends HugeTreesFeature<HugeTreeFeatureConfig> {
   public MegaPineTree(Function<Dynamic<?>, ? extends HugeTreeFeatureConfig> p_i225809_1_) {
      super(p_i225809_1_);
   }

   /**
    * Called when placing the tree feature.
    */
   public boolean place(IWorldGenerationReader generationReader, Random rand, BlockPos positionIn, Set<BlockPos> p_225557_4_, Set<BlockPos> p_225557_5_, MutableBoundingBox boundingBoxIn, HugeTreeFeatureConfig configIn) {
      int i = this.func_227256_a_(rand, configIn);
      if (!this.hasRoom(generationReader, positionIn, i, configIn)) {
         return false;
      } else {
         this.func_227253_a_(generationReader, rand, positionIn.getX(), positionIn.getZ(), positionIn.getY() + i, 0, p_225557_5_, boundingBoxIn, configIn);
         this.func_227254_a_(generationReader, rand, positionIn, i, p_225557_4_, boundingBoxIn, configIn);
         return true;
      }
   }

   private void func_227253_a_(IWorldGenerationReader p_227253_1_, Random p_227253_2_, int p_227253_3_, int p_227253_4_, int p_227253_5_, int p_227253_6_, Set<BlockPos> p_227253_7_, MutableBoundingBox p_227253_8_, HugeTreeFeatureConfig p_227253_9_) {
      int i = p_227253_2_.nextInt(5) + p_227253_9_.crownHeight;
      int j = 0;

      for(int k = p_227253_5_ - i; k <= p_227253_5_; ++k) {
         int l = p_227253_5_ - k;
         int i1 = p_227253_6_ + MathHelper.floor((float)l / (float)i * 3.5F);
         this.func_227255_a_(p_227253_1_, p_227253_2_, new BlockPos(p_227253_3_, k, p_227253_4_), i1 + (l > 0 && i1 == j && (k & 1) == 0 ? 1 : 0), p_227253_7_, p_227253_8_, p_227253_9_);
         j = i1;
      }

   }
}
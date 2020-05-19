package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.HugeMushroomBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class BigRedMushroomFeature extends AbstractBigMushroomFeature {
   public BigRedMushroomFeature(Function<Dynamic<?>, ? extends BigMushroomFeatureConfig> p_i49863_1_) {
      super(p_i49863_1_);
   }

   protected void func_225564_a_(IWorld p_225564_1_, Random p_225564_2_, BlockPos p_225564_3_, int p_225564_4_, BlockPos.Mutable p_225564_5_, BigMushroomFeatureConfig p_225564_6_) {
      for(int i = p_225564_4_ - 3; i <= p_225564_4_; ++i) {
         int j = i < p_225564_4_ ? p_225564_6_.field_227274_c_ : p_225564_6_.field_227274_c_ - 1;
         int k = p_225564_6_.field_227274_c_ - 2;

         for(int l = -j; l <= j; ++l) {
            for(int i1 = -j; i1 <= j; ++i1) {
               boolean flag = l == -j;
               boolean flag1 = l == j;
               boolean flag2 = i1 == -j;
               boolean flag3 = i1 == j;
               boolean flag4 = flag || flag1;
               boolean flag5 = flag2 || flag3;
               if (i >= p_225564_4_ || flag4 != flag5) {
                  p_225564_5_.setPos(p_225564_3_).move(l, i, i1);
                  if (p_225564_1_.getBlockState(p_225564_5_).canBeReplacedByLeaves(p_225564_1_, p_225564_5_)) {
                     this.setBlockState(p_225564_1_, p_225564_5_, p_225564_6_.field_227272_a_.getBlockState(p_225564_2_, p_225564_3_).with(HugeMushroomBlock.UP, Boolean.valueOf(i >= p_225564_4_ - 1)).with(HugeMushroomBlock.WEST, Boolean.valueOf(l < -k)).with(HugeMushroomBlock.EAST, Boolean.valueOf(l > k)).with(HugeMushroomBlock.NORTH, Boolean.valueOf(i1 < -k)).with(HugeMushroomBlock.SOUTH, Boolean.valueOf(i1 > k)));
                  }
               }
            }
         }
      }

   }

   protected int func_225563_a_(int p_225563_1_, int p_225563_2_, int p_225563_3_, int p_225563_4_) {
      int i = 0;
      if (p_225563_4_ < p_225563_2_ && p_225563_4_ >= p_225563_2_ - 3) {
         i = p_225563_3_;
      } else if (p_225563_4_ == p_225563_2_) {
         i = p_225563_3_;
      }

      return i;
   }
}
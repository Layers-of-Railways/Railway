package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.HugeMushroomBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class BigBrownMushroomFeature extends AbstractBigMushroomFeature {
   public BigBrownMushroomFeature(Function<Dynamic<?>, ? extends BigMushroomFeatureConfig> p_i49864_1_) {
      super(p_i49864_1_);
   }

   protected void func_225564_a_(IWorld p_225564_1_, Random p_225564_2_, BlockPos p_225564_3_, int p_225564_4_, BlockPos.Mutable p_225564_5_, BigMushroomFeatureConfig p_225564_6_) {
      int i = p_225564_6_.field_227274_c_;

      for(int j = -i; j <= i; ++j) {
         for(int k = -i; k <= i; ++k) {
            boolean flag = j == -i;
            boolean flag1 = j == i;
            boolean flag2 = k == -i;
            boolean flag3 = k == i;
            boolean flag4 = flag || flag1;
            boolean flag5 = flag2 || flag3;
            if (!flag4 || !flag5) {
               p_225564_5_.setPos(p_225564_3_).move(j, p_225564_4_, k);
               if (p_225564_1_.getBlockState(p_225564_5_).canBeReplacedByLeaves(p_225564_1_, p_225564_5_)) {
                  boolean flag6 = flag || flag5 && j == 1 - i;
                  boolean flag7 = flag1 || flag5 && j == i - 1;
                  boolean flag8 = flag2 || flag4 && k == 1 - i;
                  boolean flag9 = flag3 || flag4 && k == i - 1;
                  this.setBlockState(p_225564_1_, p_225564_5_, p_225564_6_.field_227272_a_.getBlockState(p_225564_2_, p_225564_3_).with(HugeMushroomBlock.WEST, Boolean.valueOf(flag6)).with(HugeMushroomBlock.EAST, Boolean.valueOf(flag7)).with(HugeMushroomBlock.NORTH, Boolean.valueOf(flag8)).with(HugeMushroomBlock.SOUTH, Boolean.valueOf(flag9)));
               }
            }
         }
      }

   }

   protected int func_225563_a_(int p_225563_1_, int p_225563_2_, int p_225563_3_, int p_225563_4_) {
      return p_225563_4_ <= 3 ? 0 : p_225563_3_;
   }
}
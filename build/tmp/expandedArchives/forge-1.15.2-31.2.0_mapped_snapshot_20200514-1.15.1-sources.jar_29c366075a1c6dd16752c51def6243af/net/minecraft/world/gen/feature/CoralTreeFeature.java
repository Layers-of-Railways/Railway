package net.minecraft.world.gen.feature;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class CoralTreeFeature extends CoralFeature {
   public CoralTreeFeature(Function<Dynamic<?>, ? extends NoFeatureConfig> p_i49896_1_) {
      super(p_i49896_1_);
   }

   protected boolean func_204623_a(IWorld p_204623_1_, Random p_204623_2_, BlockPos p_204623_3_, BlockState p_204623_4_) {
      BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable(p_204623_3_);
      int i = p_204623_2_.nextInt(3) + 1;

      for(int j = 0; j < i; ++j) {
         if (!this.func_204624_b(p_204623_1_, p_204623_2_, blockpos$mutable, p_204623_4_)) {
            return true;
         }

         blockpos$mutable.move(Direction.UP);
      }

      BlockPos blockpos = blockpos$mutable.toImmutable();
      int k = p_204623_2_.nextInt(3) + 2;
      List<Direction> list = Lists.newArrayList(Direction.Plane.HORIZONTAL);
      Collections.shuffle(list, p_204623_2_);

      for(Direction direction : list.subList(0, k)) {
         blockpos$mutable.setPos(blockpos);
         blockpos$mutable.move(direction);
         int l = p_204623_2_.nextInt(5) + 2;
         int i1 = 0;

         for(int j1 = 0; j1 < l && this.func_204624_b(p_204623_1_, p_204623_2_, blockpos$mutable, p_204623_4_); ++j1) {
            ++i1;
            blockpos$mutable.move(Direction.UP);
            if (j1 == 0 || i1 >= 2 && p_204623_2_.nextFloat() < 0.25F) {
               blockpos$mutable.move(direction);
               i1 = 0;
            }
         }
      }

      return true;
   }
}
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

public class CoralClawFeature extends CoralFeature {
   public CoralClawFeature(Function<Dynamic<?>, ? extends NoFeatureConfig> p_i49899_1_) {
      super(p_i49899_1_);
   }

   protected boolean func_204623_a(IWorld p_204623_1_, Random p_204623_2_, BlockPos p_204623_3_, BlockState p_204623_4_) {
      if (!this.func_204624_b(p_204623_1_, p_204623_2_, p_204623_3_, p_204623_4_)) {
         return false;
      } else {
         Direction direction = Direction.Plane.HORIZONTAL.random(p_204623_2_);
         int i = p_204623_2_.nextInt(2) + 2;
         List<Direction> list = Lists.newArrayList(direction, direction.rotateY(), direction.rotateYCCW());
         Collections.shuffle(list, p_204623_2_);

         for(Direction direction1 : list.subList(0, i)) {
            BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable(p_204623_3_);
            int j = p_204623_2_.nextInt(2) + 1;
            blockpos$mutable.move(direction1);
            int k;
            Direction direction2;
            if (direction1 == direction) {
               direction2 = direction;
               k = p_204623_2_.nextInt(3) + 2;
            } else {
               blockpos$mutable.move(Direction.UP);
               Direction[] adirection = new Direction[]{direction1, Direction.UP};
               direction2 = adirection[p_204623_2_.nextInt(adirection.length)];
               k = p_204623_2_.nextInt(3) + 3;
            }

            for(int l = 0; l < j && this.func_204624_b(p_204623_1_, p_204623_2_, blockpos$mutable, p_204623_4_); ++l) {
               blockpos$mutable.move(direction2);
            }

            blockpos$mutable.move(direction2.getOpposite());
            blockpos$mutable.move(Direction.UP);

            for(int i1 = 0; i1 < k; ++i1) {
               blockpos$mutable.move(direction);
               if (!this.func_204624_b(p_204623_1_, p_204623_2_, blockpos$mutable, p_204623_4_)) {
                  break;
               }

               if (p_204623_2_.nextFloat() < 0.25F) {
                  blockpos$mutable.move(Direction.UP);
               }
            }
         }

         return true;
      }
   }
}
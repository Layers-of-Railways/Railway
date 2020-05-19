package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class CoralMushroomFeature extends CoralFeature {
   public CoralMushroomFeature(Function<Dynamic<?>, ? extends NoFeatureConfig> p_i49897_1_) {
      super(p_i49897_1_);
   }

   protected boolean func_204623_a(IWorld p_204623_1_, Random p_204623_2_, BlockPos p_204623_3_, BlockState p_204623_4_) {
      int i = p_204623_2_.nextInt(3) + 3;
      int j = p_204623_2_.nextInt(3) + 3;
      int k = p_204623_2_.nextInt(3) + 3;
      int l = p_204623_2_.nextInt(3) + 1;
      BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable(p_204623_3_);

      for(int i1 = 0; i1 <= j; ++i1) {
         for(int j1 = 0; j1 <= i; ++j1) {
            for(int k1 = 0; k1 <= k; ++k1) {
               blockpos$mutable.setPos(i1 + p_204623_3_.getX(), j1 + p_204623_3_.getY(), k1 + p_204623_3_.getZ());
               blockpos$mutable.move(Direction.DOWN, l);
               if ((i1 != 0 && i1 != j || j1 != 0 && j1 != i) && (k1 != 0 && k1 != k || j1 != 0 && j1 != i) && (i1 != 0 && i1 != j || k1 != 0 && k1 != k) && (i1 == 0 || i1 == j || j1 == 0 || j1 == i || k1 == 0 || k1 == k) && !(p_204623_2_.nextFloat() < 0.1F) && !this.func_204624_b(p_204623_1_, p_204623_2_, blockpos$mutable, p_204623_4_)) {
                  ;
               }
            }
         }
      }

      return true;
   }
}
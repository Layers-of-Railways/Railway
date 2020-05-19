package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.IWorldGenerationReader;

public abstract class AbstractSmallTreeFeature<T extends TreeFeatureConfig> extends AbstractTreeFeature<T> {
   public AbstractSmallTreeFeature(Function<Dynamic<?>, ? extends T> p_i225796_1_) {
      super(p_i225796_1_);
   }

   protected void func_227213_a_(IWorldGenerationReader generationReader, Random rand, int p_227213_3_, BlockPos p_227213_4_, int p_227213_5_, Set<BlockPos> p_227213_6_, MutableBoundingBox p_227213_7_, TreeFeatureConfig p_227213_8_) {
      for(int i = 0; i < p_227213_3_ - p_227213_5_; ++i) {
         this.func_227216_a_(generationReader, rand, p_227213_4_.up(i), p_227213_6_, p_227213_7_, p_227213_8_);
      }

   }

   public Optional<BlockPos> func_227212_a_(IWorldGenerationReader p_227212_1_, int p_227212_2_, int p_227212_3_, int p_227212_4_, BlockPos p_227212_5_, TreeFeatureConfig treeFeatureConfigIn) {
      BlockPos blockpos;
      if (!treeFeatureConfigIn.forcePlacement) {
         int i = p_227212_1_.getHeight(Heightmap.Type.OCEAN_FLOOR, p_227212_5_).getY();
         int j = p_227212_1_.getHeight(Heightmap.Type.WORLD_SURFACE, p_227212_5_).getY();
         blockpos = new BlockPos(p_227212_5_.getX(), i, p_227212_5_.getZ());
         if (j - i > treeFeatureConfigIn.maxWaterDepth) {
            return Optional.empty();
         }
      } else {
         blockpos = p_227212_5_;
      }

      if (blockpos.getY() >= 1 && blockpos.getY() + p_227212_2_ + 1 <= p_227212_1_.getMaxHeight()) {
         for(int i1 = 0; i1 <= p_227212_2_ + 1; ++i1) {
            int j1 = treeFeatureConfigIn.foliagePlacer.func_225570_a_(p_227212_3_, p_227212_2_, p_227212_4_, i1);
            BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();

            for(int k = -j1; k <= j1; ++k) {
               int l = -j1;

               while(l <= j1) {
                  if (i1 + blockpos.getY() >= 0 && i1 + blockpos.getY() < p_227212_1_.getMaxHeight()) {
                     blockpos$mutable.setPos(k + blockpos.getX(), i1 + blockpos.getY(), l + blockpos.getZ());
                     if (canBeReplacedByLogs(p_227212_1_, blockpos$mutable) && (treeFeatureConfigIn.ignoreVines || !isVine(p_227212_1_, blockpos$mutable))) {
                        ++l;
                        continue;
                     }

                     return Optional.empty();
                  }

                  return Optional.empty();
               }
            }
         }

         return isSoilOrFarm(p_227212_1_, blockpos.down(), treeFeatureConfigIn.getSapling()) && blockpos.getY() < p_227212_1_.getMaxHeight() - p_227212_2_ - 1 ? Optional.of(blockpos) : Optional.empty();
      } else {
         return Optional.empty();
      }
   }
}
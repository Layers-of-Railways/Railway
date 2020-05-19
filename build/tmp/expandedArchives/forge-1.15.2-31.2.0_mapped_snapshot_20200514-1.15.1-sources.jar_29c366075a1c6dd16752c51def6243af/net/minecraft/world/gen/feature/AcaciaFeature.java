package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.gen.IWorldGenerationReader;

public class AcaciaFeature extends AbstractSmallTreeFeature<TreeFeatureConfig> {
   public AcaciaFeature(Function<Dynamic<?>, ? extends TreeFeatureConfig> p_i225798_1_) {
      super(p_i225798_1_);
   }

   /**
    * Called when placing the tree feature.
    */
   public boolean place(IWorldGenerationReader generationReader, Random rand, BlockPos positionIn, Set<BlockPos> p_225557_4_, Set<BlockPos> p_225557_5_, MutableBoundingBox boundingBoxIn, TreeFeatureConfig configIn) {
      int i = configIn.baseHeight + rand.nextInt(configIn.heightRandA + 1) + rand.nextInt(configIn.heightRandB + 1);
      int j = configIn.trunkHeight >= 0 ? configIn.trunkHeight + rand.nextInt(configIn.trunkHeightRandom + 1) : i - (configIn.foliageHeight + rand.nextInt(configIn.foliageHeightRandom + 1));
      int k = configIn.foliagePlacer.func_225573_a_(rand, j, i, configIn);
      Optional<BlockPos> optional = this.func_227212_a_(generationReader, i, j, k, positionIn, configIn);
      if (!optional.isPresent()) {
         return false;
      } else {
         BlockPos blockpos = optional.get();
         this.setDirtAt(generationReader, blockpos.down(), blockpos);
         Direction direction = Direction.Plane.HORIZONTAL.random(rand);
         int l = i - rand.nextInt(4) - 1;
         int i1 = 3 - rand.nextInt(3);
         BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
         int j1 = blockpos.getX();
         int k1 = blockpos.getZ();
         int l1 = 0;

         for(int i2 = 0; i2 < i; ++i2) {
            int j2 = blockpos.getY() + i2;
            if (i2 >= l && i1 > 0) {
               j1 += direction.getXOffset();
               k1 += direction.getZOffset();
               --i1;
            }

            if (this.func_227216_a_(generationReader, rand, blockpos$mutable.setPos(j1, j2, k1), p_225557_4_, boundingBoxIn, configIn)) {
               l1 = j2;
            }
         }

         BlockPos blockpos1 = new BlockPos(j1, l1, k1);
         configIn.foliagePlacer.func_225571_a_(generationReader, rand, configIn, i, j, k + 1, blockpos1, p_225557_5_);
         j1 = blockpos.getX();
         k1 = blockpos.getZ();
         Direction direction1 = Direction.Plane.HORIZONTAL.random(rand);
         if (direction1 != direction) {
            int j3 = l - rand.nextInt(2) - 1;
            int k2 = 1 + rand.nextInt(3);
            l1 = 0;

            for(int l2 = j3; l2 < i && k2 > 0; --k2) {
               if (l2 >= 1) {
                  int i3 = blockpos.getY() + l2;
                  j1 += direction1.getXOffset();
                  k1 += direction1.getZOffset();
                  if (this.func_227216_a_(generationReader, rand, blockpos$mutable.setPos(j1, i3, k1), p_225557_4_, boundingBoxIn, configIn)) {
                     l1 = i3;
                  }
               }

               ++l2;
            }

            if (l1 > 0) {
               BlockPos blockpos2 = new BlockPos(j1, l1, k1);
               configIn.foliagePlacer.func_225571_a_(generationReader, rand, configIn, i, j, k, blockpos2, p_225557_5_);
            }
         }

         return true;
      }
   }
}
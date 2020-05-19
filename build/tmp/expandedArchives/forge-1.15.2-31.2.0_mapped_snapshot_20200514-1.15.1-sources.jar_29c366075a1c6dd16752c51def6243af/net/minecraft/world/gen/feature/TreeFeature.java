package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.gen.IWorldGenerationReader;

public class TreeFeature extends AbstractSmallTreeFeature<TreeFeatureConfig> {
   public TreeFeature(Function<Dynamic<?>, ? extends TreeFeatureConfig> p_i225820_1_) {
      super(p_i225820_1_);
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
         configIn.foliagePlacer.func_225571_a_(generationReader, rand, configIn, i, j, k, blockpos, p_225557_5_);
         this.func_227213_a_(generationReader, rand, i, blockpos, configIn.trunkTopOffset + rand.nextInt(configIn.trunkTopOffsetRandom + 1), p_225557_4_, boundingBoxIn, configIn);
         return true;
      }
   }
}
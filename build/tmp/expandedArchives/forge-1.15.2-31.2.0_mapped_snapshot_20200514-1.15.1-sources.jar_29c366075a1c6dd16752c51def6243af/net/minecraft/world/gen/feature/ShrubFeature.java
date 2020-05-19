package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.IWorldGenerationReader;

public class ShrubFeature extends AbstractTreeFeature<BaseTreeFeatureConfig> {
   public ShrubFeature(Function<Dynamic<?>, ? extends BaseTreeFeatureConfig> p_i225805_1_) {
      super(p_i225805_1_);
   }

   /**
    * Called when placing the tree feature.
    */
   public boolean place(IWorldGenerationReader generationReader, Random rand, BlockPos positionIn, Set<BlockPos> p_225557_4_, Set<BlockPos> p_225557_5_, MutableBoundingBox boundingBoxIn, BaseTreeFeatureConfig configIn) {
      positionIn = generationReader.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, positionIn).down();
      if (isSoil(generationReader, positionIn, configIn.getSapling())) {
         positionIn = positionIn.up();
         this.func_227216_a_(generationReader, rand, positionIn, p_225557_4_, boundingBoxIn, configIn);

         for(int i = 0; i <= 2; ++i) {
            int j = 2 - i;

            for(int k = -j; k <= j; ++k) {
               for(int l = -j; l <= j; ++l) {
                  if (Math.abs(k) != j || Math.abs(l) != j || rand.nextInt(2) != 0) {
                     this.func_227219_b_(generationReader, rand, new BlockPos(k + positionIn.getX(), i + positionIn.getY(), l + positionIn.getZ()), p_225557_5_, boundingBoxIn, configIn);
                  }
               }
            }
         }
      }

      return true;
   }
}
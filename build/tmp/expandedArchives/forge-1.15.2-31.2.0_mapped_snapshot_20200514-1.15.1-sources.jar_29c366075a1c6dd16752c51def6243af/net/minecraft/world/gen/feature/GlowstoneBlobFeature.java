package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.Blocks;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;

public class GlowstoneBlobFeature extends Feature<NoFeatureConfig> {
   public GlowstoneBlobFeature(Function<Dynamic<?>, ? extends NoFeatureConfig> p_i49871_1_) {
      super(p_i49871_1_);
   }

   public boolean place(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generator, Random rand, BlockPos pos, NoFeatureConfig config) {
      if (!worldIn.isAirBlock(pos)) {
         return false;
      } else if (worldIn.getBlockState(pos.up()).getBlock() != Blocks.NETHERRACK) {
         return false;
      } else {
         worldIn.setBlockState(pos, Blocks.GLOWSTONE.getDefaultState(), 2);

         for(int i = 0; i < 1500; ++i) {
            BlockPos blockpos = pos.add(rand.nextInt(8) - rand.nextInt(8), -rand.nextInt(12), rand.nextInt(8) - rand.nextInt(8));
            if (worldIn.getBlockState(blockpos).isAir(worldIn, blockpos)) {
               int j = 0;

               for(Direction direction : Direction.values()) {
                  if (worldIn.getBlockState(blockpos.offset(direction)).getBlock() == Blocks.GLOWSTONE) {
                     ++j;
                  }

                  if (j > 1) {
                     break;
                  }
               }

               if (j == 1) {
                  worldIn.setBlockState(blockpos, Blocks.GLOWSTONE.getDefaultState(), 2);
               }
            }
         }

         return true;
      }
   }
}
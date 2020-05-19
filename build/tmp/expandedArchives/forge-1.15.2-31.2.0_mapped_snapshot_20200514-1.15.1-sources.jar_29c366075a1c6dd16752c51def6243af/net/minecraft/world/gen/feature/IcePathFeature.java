package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;

public class IcePathFeature extends Feature<FeatureRadiusConfig> {
   private final Block block = Blocks.PACKED_ICE;

   public IcePathFeature(Function<Dynamic<?>, ? extends FeatureRadiusConfig> p_i49861_1_) {
      super(p_i49861_1_);
   }

   public boolean place(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generator, Random rand, BlockPos pos, FeatureRadiusConfig config) {
      while(worldIn.isAirBlock(pos) && pos.getY() > 2) {
         pos = pos.down();
      }

      if (worldIn.getBlockState(pos).getBlock() != Blocks.SNOW_BLOCK) {
         return false;
      } else {
         int i = rand.nextInt(config.radius) + 2;
         int j = 1;

         for(int k = pos.getX() - i; k <= pos.getX() + i; ++k) {
            for(int l = pos.getZ() - i; l <= pos.getZ() + i; ++l) {
               int i1 = k - pos.getX();
               int j1 = l - pos.getZ();
               if (i1 * i1 + j1 * j1 <= i * i) {
                  for(int k1 = pos.getY() - 1; k1 <= pos.getY() + 1; ++k1) {
                     BlockPos blockpos = new BlockPos(k, k1, l);
                     Block block = worldIn.getBlockState(blockpos).getBlock();
                     if (isDirt(block) || block == Blocks.SNOW_BLOCK || block == Blocks.ICE) {
                        worldIn.setBlockState(blockpos, this.block.getDefaultState(), 2);
                     }
                  }
               }
            }
         }

         return true;
      }
   }
}
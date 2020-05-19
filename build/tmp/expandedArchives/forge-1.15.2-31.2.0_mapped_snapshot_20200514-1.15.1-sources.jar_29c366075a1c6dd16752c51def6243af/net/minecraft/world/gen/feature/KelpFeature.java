package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.KelpTopBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.Heightmap;

public class KelpFeature extends Feature<NoFeatureConfig> {
   public KelpFeature(Function<Dynamic<?>, ? extends NoFeatureConfig> p_i51487_1_) {
      super(p_i51487_1_);
   }

   public boolean place(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generator, Random rand, BlockPos pos, NoFeatureConfig config) {
      int i = 0;
      int j = worldIn.getHeight(Heightmap.Type.OCEAN_FLOOR, pos.getX(), pos.getZ());
      BlockPos blockpos = new BlockPos(pos.getX(), j, pos.getZ());
      if (worldIn.getBlockState(blockpos).getBlock() == Blocks.WATER) {
         BlockState blockstate = Blocks.KELP.getDefaultState();
         BlockState blockstate1 = Blocks.KELP_PLANT.getDefaultState();
         int k = 1 + rand.nextInt(10);

         for(int l = 0; l <= k; ++l) {
            if (worldIn.getBlockState(blockpos).getBlock() == Blocks.WATER && worldIn.getBlockState(blockpos.up()).getBlock() == Blocks.WATER && blockstate1.isValidPosition(worldIn, blockpos)) {
               if (l == k) {
                  worldIn.setBlockState(blockpos, blockstate.with(KelpTopBlock.AGE, Integer.valueOf(rand.nextInt(4) + 20)), 2);
                  ++i;
               } else {
                  worldIn.setBlockState(blockpos, blockstate1, 2);
               }
            } else if (l > 0) {
               BlockPos blockpos1 = blockpos.down();
               if (blockstate.isValidPosition(worldIn, blockpos1) && worldIn.getBlockState(blockpos1.down()).getBlock() != Blocks.KELP) {
                  worldIn.setBlockState(blockpos1, blockstate.with(KelpTopBlock.AGE, Integer.valueOf(rand.nextInt(4) + 20)), 2);
                  ++i;
               }
               break;
            }

            blockpos = blockpos.up();
         }
      }

      return i > 0;
   }
}
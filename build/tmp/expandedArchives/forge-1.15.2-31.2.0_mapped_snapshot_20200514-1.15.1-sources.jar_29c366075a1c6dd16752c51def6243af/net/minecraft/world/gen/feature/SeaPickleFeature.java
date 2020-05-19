package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SeaPickleBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.placement.CountConfig;

public class SeaPickleFeature extends Feature<CountConfig> {
   public SeaPickleFeature(Function<Dynamic<?>, ? extends CountConfig> p_i51442_1_) {
      super(p_i51442_1_);
   }

   public boolean place(IWorld worldIn, ChunkGenerator<?> generator, Random rand, BlockPos pos, CountConfig config) {
      int i = 0;

      for(int j = 0; j < config.count; ++j) {
         int k = rand.nextInt(8) - rand.nextInt(8);
         int l = rand.nextInt(8) - rand.nextInt(8);
         int i1 = worldIn.getHeight(Heightmap.Type.OCEAN_FLOOR, pos.getX() + k, pos.getZ() + l);
         BlockPos blockpos = new BlockPos(pos.getX() + k, i1, pos.getZ() + l);
         BlockState blockstate = Blocks.SEA_PICKLE.getDefaultState().with(SeaPickleBlock.PICKLES, Integer.valueOf(rand.nextInt(4) + 1));
         if (worldIn.getBlockState(blockpos).getBlock() == Blocks.WATER && blockstate.isValidPosition(worldIn, blockpos)) {
            worldIn.setBlockState(blockpos, blockstate, 2);
            ++i;
         }
      }

      return i > 0;
   }
}
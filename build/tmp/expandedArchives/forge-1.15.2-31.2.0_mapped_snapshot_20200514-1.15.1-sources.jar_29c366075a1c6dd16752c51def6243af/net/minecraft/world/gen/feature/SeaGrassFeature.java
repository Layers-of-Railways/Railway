package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.TallSeaGrassBlock;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.Heightmap;

public class SeaGrassFeature extends Feature<SeaGrassConfig> {
   public SeaGrassFeature(Function<Dynamic<?>, ? extends SeaGrassConfig> p_i51441_1_) {
      super(p_i51441_1_);
   }

   public boolean place(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generator, Random rand, BlockPos pos, SeaGrassConfig config) {
      int i = 0;

      for(int j = 0; j < config.count; ++j) {
         int k = rand.nextInt(8) - rand.nextInt(8);
         int l = rand.nextInt(8) - rand.nextInt(8);
         int i1 = worldIn.getHeight(Heightmap.Type.OCEAN_FLOOR, pos.getX() + k, pos.getZ() + l);
         BlockPos blockpos = new BlockPos(pos.getX() + k, i1, pos.getZ() + l);
         if (worldIn.getBlockState(blockpos).getBlock() == Blocks.WATER) {
            boolean flag = rand.nextDouble() < config.tallProbability;
            BlockState blockstate = flag ? Blocks.TALL_SEAGRASS.getDefaultState() : Blocks.SEAGRASS.getDefaultState();
            if (blockstate.isValidPosition(worldIn, blockpos)) {
               if (flag) {
                  BlockState blockstate1 = blockstate.with(TallSeaGrassBlock.field_208065_c, DoubleBlockHalf.UPPER);
                  BlockPos blockpos1 = blockpos.up();
                  if (worldIn.getBlockState(blockpos1).getBlock() == Blocks.WATER) {
                     worldIn.setBlockState(blockpos, blockstate, 2);
                     worldIn.setBlockState(blockpos1, blockstate1, 2);
                  }
               } else {
                  worldIn.setBlockState(blockpos, blockstate, 2);
               }

               ++i;
            }
         }
      }

      return i > 0;
   }
}
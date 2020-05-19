package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;

public class SpringFeature extends Feature<LiquidsConfig> {
   public SpringFeature(Function<Dynamic<?>, ? extends LiquidsConfig> p_i51430_1_) {
      super(p_i51430_1_);
   }

   public boolean place(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generator, Random rand, BlockPos pos, LiquidsConfig config) {
      if (!config.acceptedBlocks.contains(worldIn.getBlockState(pos.up()).getBlock())) {
         return false;
      } else if (config.needsBlockBelow && !config.acceptedBlocks.contains(worldIn.getBlockState(pos.down()).getBlock())) {
         return false;
      } else {
         BlockState blockstate = worldIn.getBlockState(pos);
         if (!blockstate.isAir(worldIn, pos) && !config.acceptedBlocks.contains(blockstate.getBlock())) {
            return false;
         } else {
            int i = 0;
            int j = 0;
            if (config.acceptedBlocks.contains(worldIn.getBlockState(pos.west()).getBlock())) {
               ++j;
            }

            if (config.acceptedBlocks.contains(worldIn.getBlockState(pos.east()).getBlock())) {
               ++j;
            }

            if (config.acceptedBlocks.contains(worldIn.getBlockState(pos.north()).getBlock())) {
               ++j;
            }

            if (config.acceptedBlocks.contains(worldIn.getBlockState(pos.south()).getBlock())) {
               ++j;
            }

            if (config.acceptedBlocks.contains(worldIn.getBlockState(pos.down()).getBlock())) {
               ++j;
            }

            int k = 0;
            if (worldIn.isAirBlock(pos.west())) {
               ++k;
            }

            if (worldIn.isAirBlock(pos.east())) {
               ++k;
            }

            if (worldIn.isAirBlock(pos.north())) {
               ++k;
            }

            if (worldIn.isAirBlock(pos.south())) {
               ++k;
            }

            if (worldIn.isAirBlock(pos.down())) {
               ++k;
            }

            if (j == config.rockAmount && k == config.holeAmount) {
               worldIn.setBlockState(pos, config.state.getBlockState(), 2);
               worldIn.getPendingFluidTicks().scheduleTick(pos, config.state.getFluid(), 0);
               ++i;
            }

            return i > 0;
         }
      }
   }
}
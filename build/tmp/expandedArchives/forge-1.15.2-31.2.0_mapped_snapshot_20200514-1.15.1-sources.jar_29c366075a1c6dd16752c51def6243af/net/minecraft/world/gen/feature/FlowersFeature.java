package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;

public abstract class FlowersFeature<U extends IFeatureConfig> extends Feature<U> {
   public FlowersFeature(Function<Dynamic<?>, ? extends U> p_i49876_1_) {
      super(p_i49876_1_);
   }

   public boolean place(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generator, Random rand, BlockPos pos, U config) {
      BlockState blockstate = this.getFlowerToPlace(rand, pos, config);
      int i = 0;

      for(int j = 0; j < this.func_225560_a_(config); ++j) {
         BlockPos blockpos = this.getNearbyPos(rand, pos, config);
         if (worldIn.isAirBlock(blockpos) && blockpos.getY() < worldIn.getMaxHeight() - 1 && blockstate.isValidPosition(worldIn, blockpos) && this.func_225559_a_(worldIn, blockpos, config)) {
            worldIn.setBlockState(blockpos, blockstate, 2);
            ++i;
         }
      }

      return i > 0;
   }

   public abstract boolean func_225559_a_(IWorld p_225559_1_, BlockPos p_225559_2_, U p_225559_3_);

   public abstract int func_225560_a_(U p_225560_1_);

   public abstract BlockPos getNearbyPos(Random p_225561_1_, BlockPos p_225561_2_, U p_225561_3_);

   public abstract BlockState getFlowerToPlace(Random p_225562_1_, BlockPos p_225562_2_, U p_225562_3_);
}
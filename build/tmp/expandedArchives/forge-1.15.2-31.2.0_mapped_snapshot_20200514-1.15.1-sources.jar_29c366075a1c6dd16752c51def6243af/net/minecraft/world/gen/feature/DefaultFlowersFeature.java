package net.minecraft.world.gen.feature;

import com.mojang.datafixers.Dynamic;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class DefaultFlowersFeature extends FlowersFeature<BlockClusterFeatureConfig> {
   public DefaultFlowersFeature(Function<Dynamic<?>, ? extends BlockClusterFeatureConfig> p_i49889_1_) {
      super(p_i49889_1_);
   }

   public boolean func_225559_a_(IWorld p_225559_1_, BlockPos p_225559_2_, BlockClusterFeatureConfig p_225559_3_) {
      return !p_225559_3_.blacklist.contains(p_225559_1_.getBlockState(p_225559_2_));
   }

   public int func_225560_a_(BlockClusterFeatureConfig p_225560_1_) {
      return p_225560_1_.tryCount;
   }

   public BlockPos getNearbyPos(Random p_225561_1_, BlockPos p_225561_2_, BlockClusterFeatureConfig p_225561_3_) {
      return p_225561_2_.add(p_225561_1_.nextInt(p_225561_3_.xSpread) - p_225561_1_.nextInt(p_225561_3_.xSpread), p_225561_1_.nextInt(p_225561_3_.ySpread) - p_225561_1_.nextInt(p_225561_3_.ySpread), p_225561_1_.nextInt(p_225561_3_.zSpread) - p_225561_1_.nextInt(p_225561_3_.zSpread));
   }

   public BlockState getFlowerToPlace(Random p_225562_1_, BlockPos p_225562_2_, BlockClusterFeatureConfig p_225562_3_) {
      return p_225562_3_.stateProvider.getBlockState(p_225562_1_, p_225562_2_);
   }
}
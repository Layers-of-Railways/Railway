package net.minecraft.block;

import net.minecraft.entity.EntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class AbstractGlassBlock extends BreakableBlock {
   protected AbstractGlassBlock(Block.Properties p_i49999_1_) {
      super(p_i49999_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public float getAmbientOcclusionLightValue(BlockState state, IBlockReader worldIn, BlockPos pos) {
      return 1.0F;
   }

   public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos) {
      return true;
   }

   public boolean causesSuffocation(BlockState state, IBlockReader worldIn, BlockPos pos) {
      return false;
   }

   public boolean isNormalCube(BlockState state, IBlockReader worldIn, BlockPos pos) {
      return false;
   }

   public boolean canEntitySpawn(BlockState state, IBlockReader worldIn, BlockPos pos, EntityType<?> type) {
      return false;
   }
}
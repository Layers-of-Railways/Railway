package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class LilyPadBlock extends BushBlock {
   protected static final VoxelShape LILY_PAD_AABB = Block.makeCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 1.5D, 15.0D);

   protected LilyPadBlock(Block.Properties builder) {
      super(builder);
   }

   public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
      super.onEntityCollision(state, worldIn, pos, entityIn);
      if (worldIn instanceof ServerWorld && entityIn instanceof BoatEntity) {
         worldIn.destroyBlock(new BlockPos(pos), true, entityIn);
      }

   }

   public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
      return LILY_PAD_AABB;
   }

   protected boolean isValidGround(BlockState state, IBlockReader worldIn, BlockPos pos) {
      IFluidState ifluidstate = worldIn.getFluidState(pos);
      return ifluidstate.getFluid() == Fluids.WATER || state.getMaterial() == Material.ICE;
   }
}
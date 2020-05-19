package net.minecraft.block;

import java.util.Random;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.server.ServerWorld;

public class KelpBlock extends Block implements ILiquidContainer {
   private final KelpTopBlock top;

   protected KelpBlock(KelpTopBlock kelpTopBlockIn, Block.Properties properties) {
      super(properties);
      this.top = kelpTopBlockIn;
   }

   public IFluidState getFluidState(BlockState state) {
      return Fluids.WATER.getStillFluidState(false);
   }

   public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
      if (!state.isValidPosition(worldIn, pos)) {
         worldIn.destroyBlock(pos, true);
      }

      super.tick(state, worldIn, pos, rand);
   }

   /**
    * Update the provided state given the provided neighbor facing and neighbor state, returning a new state.
    * For example, fences make their connections to the passed in state if possible, and wet concrete powder immediately
    * returns its solidified counterpart.
    * Note that this method should ideally consider only the specific face passed in.
    */
   public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
      if (facing == Direction.DOWN && !stateIn.isValidPosition(worldIn, currentPos)) {
         worldIn.getPendingBlockTicks().scheduleTick(currentPos, this, 1);
      }

      if (facing == Direction.UP) {
         Block block = facingState.getBlock();
         if (block != this && block != this.top) {
            return this.top.randomAge(worldIn);
         }
      }

      worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
      return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
   }

   public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
      BlockPos blockpos = pos.down();
      BlockState blockstate = worldIn.getBlockState(blockpos);
      Block block = blockstate.getBlock();
      return block != Blocks.MAGMA_BLOCK && (block == this || blockstate.isSolidSide(worldIn, blockpos, Direction.UP));
   }

   public ItemStack getItem(IBlockReader worldIn, BlockPos pos, BlockState state) {
      return new ItemStack(Blocks.KELP);
   }

   public boolean canContainFluid(IBlockReader worldIn, BlockPos pos, BlockState state, Fluid fluidIn) {
      return false;
   }

   public boolean receiveFluid(IWorld worldIn, BlockPos pos, BlockState state, IFluidState fluidStateIn) {
      return false;
   }
}
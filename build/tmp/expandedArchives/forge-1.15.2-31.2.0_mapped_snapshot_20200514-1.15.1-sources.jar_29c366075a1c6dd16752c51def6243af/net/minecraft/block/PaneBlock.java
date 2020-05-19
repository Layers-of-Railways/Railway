package net.minecraft.block;

import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class PaneBlock extends FourWayBlock {
   protected PaneBlock(Block.Properties builder) {
      super(1.0F, 1.0F, 16.0F, 16.0F, 16.0F, builder);
      this.setDefaultState(this.stateContainer.getBaseState().with(NORTH, Boolean.valueOf(false)).with(EAST, Boolean.valueOf(false)).with(SOUTH, Boolean.valueOf(false)).with(WEST, Boolean.valueOf(false)).with(WATERLOGGED, Boolean.valueOf(false)));
   }

   public BlockState getStateForPlacement(BlockItemUseContext context) {
      IBlockReader iblockreader = context.getWorld();
      BlockPos blockpos = context.getPos();
      IFluidState ifluidstate = context.getWorld().getFluidState(context.getPos());
      BlockPos blockpos1 = blockpos.north();
      BlockPos blockpos2 = blockpos.south();
      BlockPos blockpos3 = blockpos.west();
      BlockPos blockpos4 = blockpos.east();
      BlockState blockstate = iblockreader.getBlockState(blockpos1);
      BlockState blockstate1 = iblockreader.getBlockState(blockpos2);
      BlockState blockstate2 = iblockreader.getBlockState(blockpos3);
      BlockState blockstate3 = iblockreader.getBlockState(blockpos4);
      return this.getDefaultState().with(NORTH, Boolean.valueOf(this.canAttachTo(blockstate, blockstate.isSolidSide(iblockreader, blockpos1, Direction.SOUTH)))).with(SOUTH, Boolean.valueOf(this.canAttachTo(blockstate1, blockstate1.isSolidSide(iblockreader, blockpos2, Direction.NORTH)))).with(WEST, Boolean.valueOf(this.canAttachTo(blockstate2, blockstate2.isSolidSide(iblockreader, blockpos3, Direction.EAST)))).with(EAST, Boolean.valueOf(this.canAttachTo(blockstate3, blockstate3.isSolidSide(iblockreader, blockpos4, Direction.WEST)))).with(WATERLOGGED, Boolean.valueOf(ifluidstate.getFluid() == Fluids.WATER));
   }

   /**
    * Update the provided state given the provided neighbor facing and neighbor state, returning a new state.
    * For example, fences make their connections to the passed in state if possible, and wet concrete powder immediately
    * returns its solidified counterpart.
    * Note that this method should ideally consider only the specific face passed in.
    */
   public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
      if (stateIn.get(WATERLOGGED)) {
         worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
      }

      return facing.getAxis().isHorizontal() ? stateIn.with(FACING_TO_PROPERTY_MAP.get(facing), Boolean.valueOf(this.canAttachTo(facingState, facingState.isSolidSide(worldIn, facingPos, facing.getOpposite())))) : super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isSideInvisible(BlockState state, BlockState adjacentBlockState, Direction side) {
      if (adjacentBlockState.getBlock() == this) {
         if (!side.getAxis().isHorizontal()) {
            return true;
         }

         if (state.get(FACING_TO_PROPERTY_MAP.get(side)) && adjacentBlockState.get(FACING_TO_PROPERTY_MAP.get(side.getOpposite()))) {
            return true;
         }
      }

      return super.isSideInvisible(state, adjacentBlockState, side);
   }

   public final boolean canAttachTo(BlockState p_220112_1_, boolean p_220112_2_) {
      Block block = p_220112_1_.getBlock();
      return !cannotAttach(block) && p_220112_2_ || block instanceof PaneBlock;
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
      builder.add(NORTH, EAST, WEST, SOUTH, WATERLOGGED);
   }
}
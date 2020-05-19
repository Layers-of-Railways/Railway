package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;

public class AbstractCoralPlantBlock extends Block implements IWaterLoggable {
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   private static final VoxelShape field_212559_a = Block.makeCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 4.0D, 14.0D);

   protected AbstractCoralPlantBlock(Block.Properties p_i49810_1_) {
      super(p_i49810_1_);
      this.setDefaultState(this.stateContainer.getBaseState().with(WATERLOGGED, Boolean.valueOf(true)));
   }

   protected void updateIfDry(BlockState state, IWorld worldIn, BlockPos pos) {
      if (!isInWater(state, worldIn, pos)) {
         worldIn.getPendingBlockTicks().scheduleTick(pos, this, 60 + worldIn.getRandom().nextInt(40));
      }

   }

   protected static boolean isInWater(BlockState state, IBlockReader worldIn, BlockPos pos) {
      if (state.get(WATERLOGGED)) {
         return true;
      } else {
         for(Direction direction : Direction.values()) {
            if (worldIn.getFluidState(pos.offset(direction)).isTagged(FluidTags.WATER)) {
               return true;
            }
         }

         return false;
      }
   }

   @Nullable
   public BlockState getStateForPlacement(BlockItemUseContext context) {
      IFluidState ifluidstate = context.getWorld().getFluidState(context.getPos());
      return this.getDefaultState().with(WATERLOGGED, Boolean.valueOf(ifluidstate.isTagged(FluidTags.WATER) && ifluidstate.getLevel() == 8));
   }

   public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
      return field_212559_a;
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

      return facing == Direction.DOWN && !this.isValidPosition(stateIn, worldIn, currentPos) ? Blocks.AIR.getDefaultState() : super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
   }

   public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
      BlockPos blockpos = pos.down();
      return worldIn.getBlockState(blockpos).isSolidSide(worldIn, blockpos, Direction.UP);
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
      builder.add(WATERLOGGED);
   }

   public IFluidState getFluidState(BlockState state) {
      return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
   }
}
package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;

public class LadderBlock extends Block implements IWaterLoggable {
   public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
   public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
   protected static final VoxelShape LADDER_EAST_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 3.0D, 16.0D, 16.0D);
   protected static final VoxelShape LADDER_WEST_AABB = Block.makeCuboidShape(13.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
   protected static final VoxelShape LADDER_SOUTH_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 3.0D);
   protected static final VoxelShape LADDER_NORTH_AABB = Block.makeCuboidShape(0.0D, 0.0D, 13.0D, 16.0D, 16.0D, 16.0D);

   protected LadderBlock(Block.Properties builder) {
      super(builder);
      this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH).with(WATERLOGGED, Boolean.valueOf(false)));
   }

   public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
      switch((Direction)state.get(FACING)) {
      case NORTH:
         return LADDER_NORTH_AABB;
      case SOUTH:
         return LADDER_SOUTH_AABB;
      case WEST:
         return LADDER_WEST_AABB;
      case EAST:
      default:
         return LADDER_EAST_AABB;
      }
   }

   private boolean canAttachTo(IBlockReader p_196471_1_, BlockPos p_196471_2_, Direction p_196471_3_) {
      BlockState blockstate = p_196471_1_.getBlockState(p_196471_2_);
      return !blockstate.canProvidePower() && blockstate.isSolidSide(p_196471_1_, p_196471_2_, p_196471_3_);
   }

   public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
      Direction direction = state.get(FACING);
      return this.canAttachTo(worldIn, pos.offset(direction.getOpposite()), direction);
   }

   /**
    * Update the provided state given the provided neighbor facing and neighbor state, returning a new state.
    * For example, fences make their connections to the passed in state if possible, and wet concrete powder immediately
    * returns its solidified counterpart.
    * Note that this method should ideally consider only the specific face passed in.
    */
   public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
      if (facing.getOpposite() == stateIn.get(FACING) && !stateIn.isValidPosition(worldIn, currentPos)) {
         return Blocks.AIR.getDefaultState();
      } else {
         if (stateIn.get(WATERLOGGED)) {
            worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
         }

         return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
      }
   }

   @Nullable
   public BlockState getStateForPlacement(BlockItemUseContext context) {
      if (!context.replacingClickedOnBlock()) {
         BlockState blockstate = context.getWorld().getBlockState(context.getPos().offset(context.getFace().getOpposite()));
         if (blockstate.getBlock() == this && blockstate.get(FACING) == context.getFace()) {
            return null;
         }
      }

      BlockState blockstate1 = this.getDefaultState();
      IWorldReader iworldreader = context.getWorld();
      BlockPos blockpos = context.getPos();
      IFluidState ifluidstate = context.getWorld().getFluidState(context.getPos());

      for(Direction direction : context.getNearestLookingDirections()) {
         if (direction.getAxis().isHorizontal()) {
            blockstate1 = blockstate1.with(FACING, direction.getOpposite());
            if (blockstate1.isValidPosition(iworldreader, blockpos)) {
               return blockstate1.with(WATERLOGGED, Boolean.valueOf(ifluidstate.getFluid() == Fluids.WATER));
            }
         }
      }

      return null;
   }

   @Override
   public boolean isLadder(BlockState state, net.minecraft.world.IWorldReader world, BlockPos pos, net.minecraft.entity.LivingEntity entity) {
      return true;
   }

   /**
    * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed
    * blockstate.
    * @deprecated call via {@link IBlockState#withRotation(Rotation)} whenever possible. Implementing/overriding is
    * fine.
    */
   public BlockState rotate(BlockState state, Rotation rot) {
      return state.with(FACING, rot.rotate(state.get(FACING)));
   }

   /**
    * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed
    * blockstate.
    * @deprecated call via {@link IBlockState#withMirror(Mirror)} whenever possible. Implementing/overriding is fine.
    */
   public BlockState mirror(BlockState state, Mirror mirrorIn) {
      return state.rotate(mirrorIn.toRotation(state.get(FACING)));
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
      builder.add(FACING, WATERLOGGED);
   }

   public IFluidState getFluidState(BlockState state) {
      return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
   }
}
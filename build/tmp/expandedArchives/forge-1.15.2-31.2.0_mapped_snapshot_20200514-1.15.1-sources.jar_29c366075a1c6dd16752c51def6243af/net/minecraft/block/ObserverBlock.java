package net.minecraft.block;

import java.util.Random;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class ObserverBlock extends DirectionalBlock {
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

   public ObserverBlock(Block.Properties properties) {
      super(properties);
      this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.SOUTH).with(POWERED, Boolean.valueOf(false)));
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
      builder.add(FACING, POWERED);
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

   public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
      if (state.get(POWERED)) {
         worldIn.setBlockState(pos, state.with(POWERED, Boolean.valueOf(false)), 2);
      } else {
         worldIn.setBlockState(pos, state.with(POWERED, Boolean.valueOf(true)), 2);
         worldIn.getPendingBlockTicks().scheduleTick(pos, this, 2);
      }

      this.updateNeighborsInFront(worldIn, pos, state);
   }

   /**
    * Update the provided state given the provided neighbor facing and neighbor state, returning a new state.
    * For example, fences make their connections to the passed in state if possible, and wet concrete powder immediately
    * returns its solidified counterpart.
    * Note that this method should ideally consider only the specific face passed in.
    */
   public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
      if (stateIn.get(FACING) == facing && !stateIn.get(POWERED)) {
         this.startSignal(worldIn, currentPos);
      }

      return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
   }

   private void startSignal(IWorld worldIn, BlockPos pos) {
      if (!worldIn.isRemote() && !worldIn.getPendingBlockTicks().isTickScheduled(pos, this)) {
         worldIn.getPendingBlockTicks().scheduleTick(pos, this, 2);
      }

   }

   protected void updateNeighborsInFront(World worldIn, BlockPos pos, BlockState state) {
      Direction direction = state.get(FACING);
      BlockPos blockpos = pos.offset(direction.getOpposite());
      worldIn.neighborChanged(blockpos, this, pos);
      worldIn.notifyNeighborsOfStateExcept(blockpos, this, direction);
   }

   /**
    * Can this block provide power. Only wire currently seems to have this change based on its state.
    * @deprecated call via {@link IBlockState#canProvidePower()} whenever possible. Implementing/overriding is fine.
    */
   public boolean canProvidePower(BlockState state) {
      return true;
   }

   /**
    * @deprecated call via {@link IBlockState#getStrongPower(IBlockAccess,BlockPos,EnumFacing)} whenever possible.
    * Implementing/overriding is fine.
    */
   public int getStrongPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
      return blockState.getWeakPower(blockAccess, pos, side);
   }

   /**
    * @deprecated call via {@link IBlockState#getWeakPower(IBlockAccess,BlockPos,EnumFacing)} whenever possible.
    * Implementing/overriding is fine.
    */
   public int getWeakPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
      return blockState.get(POWERED) && blockState.get(FACING) == side ? 15 : 0;
   }

   public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
      if (state.getBlock() != oldState.getBlock()) {
         if (!worldIn.isRemote() && state.get(POWERED) && !worldIn.getPendingBlockTicks().isTickScheduled(pos, this)) {
            BlockState blockstate = state.with(POWERED, Boolean.valueOf(false));
            worldIn.setBlockState(pos, blockstate, 18);
            this.updateNeighborsInFront(worldIn, pos, blockstate);
         }

      }
   }

   public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
      if (state.getBlock() != newState.getBlock()) {
         if (!worldIn.isRemote && state.get(POWERED) && worldIn.getPendingBlockTicks().isTickScheduled(pos, this)) {
            this.updateNeighborsInFront(worldIn, pos, state.with(POWERED, Boolean.valueOf(false)));
         }

      }
   }

   public BlockState getStateForPlacement(BlockItemUseContext context) {
      return this.getDefaultState().with(FACING, context.getNearestLookingDirection().getOpposite().getOpposite());
   }
}
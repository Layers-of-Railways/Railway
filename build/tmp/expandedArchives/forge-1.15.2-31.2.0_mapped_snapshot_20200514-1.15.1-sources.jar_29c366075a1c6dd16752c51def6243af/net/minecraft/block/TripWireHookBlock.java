package net.minecraft.block;

import com.google.common.base.MoreObjects;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class TripWireHookBlock extends Block {
   public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
   public static final BooleanProperty ATTACHED = BlockStateProperties.ATTACHED;
   protected static final VoxelShape HOOK_NORTH_AABB = Block.makeCuboidShape(5.0D, 0.0D, 10.0D, 11.0D, 10.0D, 16.0D);
   protected static final VoxelShape HOOK_SOUTH_AABB = Block.makeCuboidShape(5.0D, 0.0D, 0.0D, 11.0D, 10.0D, 6.0D);
   protected static final VoxelShape HOOK_WEST_AABB = Block.makeCuboidShape(10.0D, 0.0D, 5.0D, 16.0D, 10.0D, 11.0D);
   protected static final VoxelShape HOOK_EAST_AABB = Block.makeCuboidShape(0.0D, 0.0D, 5.0D, 6.0D, 10.0D, 11.0D);

   public TripWireHookBlock(Block.Properties properties) {
      super(properties);
      this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH).with(POWERED, Boolean.valueOf(false)).with(ATTACHED, Boolean.valueOf(false)));
   }

   public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
      switch((Direction)state.get(FACING)) {
      case EAST:
      default:
         return HOOK_EAST_AABB;
      case WEST:
         return HOOK_WEST_AABB;
      case SOUTH:
         return HOOK_SOUTH_AABB;
      case NORTH:
         return HOOK_NORTH_AABB;
      }
   }

   public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
      Direction direction = state.get(FACING);
      BlockPos blockpos = pos.offset(direction.getOpposite());
      BlockState blockstate = worldIn.getBlockState(blockpos);
      return direction.getAxis().isHorizontal() && blockstate.isSolidSide(worldIn, blockpos, direction) && !blockstate.canProvidePower();
   }

   /**
    * Update the provided state given the provided neighbor facing and neighbor state, returning a new state.
    * For example, fences make their connections to the passed in state if possible, and wet concrete powder immediately
    * returns its solidified counterpart.
    * Note that this method should ideally consider only the specific face passed in.
    */
   public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
      return facing.getOpposite() == stateIn.get(FACING) && !stateIn.isValidPosition(worldIn, currentPos) ? Blocks.AIR.getDefaultState() : super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
   }

   @Nullable
   public BlockState getStateForPlacement(BlockItemUseContext context) {
      BlockState blockstate = this.getDefaultState().with(POWERED, Boolean.valueOf(false)).with(ATTACHED, Boolean.valueOf(false));
      IWorldReader iworldreader = context.getWorld();
      BlockPos blockpos = context.getPos();
      Direction[] adirection = context.getNearestLookingDirections();

      for(Direction direction : adirection) {
         if (direction.getAxis().isHorizontal()) {
            Direction direction1 = direction.getOpposite();
            blockstate = blockstate.with(FACING, direction1);
            if (blockstate.isValidPosition(iworldreader, blockpos)) {
               return blockstate;
            }
         }
      }

      return null;
   }

   /**
    * Called by ItemBlocks after a block is set in the world, to allow post-place logic
    */
   public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
      this.calculateState(worldIn, pos, state, false, false, -1, (BlockState)null);
   }

   public void calculateState(World worldIn, BlockPos pos, BlockState hookState, boolean p_176260_4_, boolean p_176260_5_, int p_176260_6_, @Nullable BlockState p_176260_7_) {
      Direction direction = hookState.get(FACING);
      boolean flag = hookState.get(ATTACHED);
      boolean flag1 = hookState.get(POWERED);
      boolean flag2 = !p_176260_4_;
      boolean flag3 = false;
      int i = 0;
      BlockState[] ablockstate = new BlockState[42];

      for(int j = 1; j < 42; ++j) {
         BlockPos blockpos = pos.offset(direction, j);
         BlockState blockstate = worldIn.getBlockState(blockpos);
         if (blockstate.getBlock() == Blocks.TRIPWIRE_HOOK) {
            if (blockstate.get(FACING) == direction.getOpposite()) {
               i = j;
            }
            break;
         }

         if (blockstate.getBlock() != Blocks.TRIPWIRE && j != p_176260_6_) {
            ablockstate[j] = null;
            flag2 = false;
         } else {
            if (j == p_176260_6_) {
               blockstate = MoreObjects.firstNonNull(p_176260_7_, blockstate);
            }

            boolean flag4 = !blockstate.get(TripWireBlock.DISARMED);
            boolean flag5 = blockstate.get(TripWireBlock.POWERED);
            flag3 |= flag4 && flag5;
            ablockstate[j] = blockstate;
            if (j == p_176260_6_) {
               worldIn.getPendingBlockTicks().scheduleTick(pos, this, this.tickRate(worldIn));
               flag2 &= flag4;
            }
         }
      }

      flag2 = flag2 & i > 1;
      flag3 = flag3 & flag2;
      BlockState blockstate1 = this.getDefaultState().with(ATTACHED, Boolean.valueOf(flag2)).with(POWERED, Boolean.valueOf(flag3));
      if (i > 0) {
         BlockPos blockpos1 = pos.offset(direction, i);
         Direction direction1 = direction.getOpposite();
         worldIn.setBlockState(blockpos1, blockstate1.with(FACING, direction1), 3);
         this.notifyNeighbors(worldIn, blockpos1, direction1);
         this.playSound(worldIn, blockpos1, flag2, flag3, flag, flag1);
      }

      this.playSound(worldIn, pos, flag2, flag3, flag, flag1);
      if (!p_176260_4_) {
         worldIn.setBlockState(pos, blockstate1.with(FACING, direction), 3);
         if (p_176260_5_) {
            this.notifyNeighbors(worldIn, pos, direction);
         }
      }

      if (flag != flag2) {
         for(int k = 1; k < i; ++k) {
            BlockPos blockpos2 = pos.offset(direction, k);
            BlockState blockstate2 = ablockstate[k];
            if (blockstate2 != null) {
               worldIn.setBlockState(blockpos2, blockstate2.with(ATTACHED, Boolean.valueOf(flag2)), 3);
               if (!worldIn.getBlockState(blockpos2).isAir()) {
                  ;
               }
            }
         }
      }

   }

   public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
      this.calculateState(worldIn, pos, state, false, true, -1, (BlockState)null);
   }

   private void playSound(World worldIn, BlockPos pos, boolean p_180694_3_, boolean p_180694_4_, boolean p_180694_5_, boolean p_180694_6_) {
      if (p_180694_4_ && !p_180694_6_) {
         worldIn.playSound((PlayerEntity)null, pos, SoundEvents.BLOCK_TRIPWIRE_CLICK_ON, SoundCategory.BLOCKS, 0.4F, 0.6F);
      } else if (!p_180694_4_ && p_180694_6_) {
         worldIn.playSound((PlayerEntity)null, pos, SoundEvents.BLOCK_TRIPWIRE_CLICK_OFF, SoundCategory.BLOCKS, 0.4F, 0.5F);
      } else if (p_180694_3_ && !p_180694_5_) {
         worldIn.playSound((PlayerEntity)null, pos, SoundEvents.BLOCK_TRIPWIRE_ATTACH, SoundCategory.BLOCKS, 0.4F, 0.7F);
      } else if (!p_180694_3_ && p_180694_5_) {
         worldIn.playSound((PlayerEntity)null, pos, SoundEvents.BLOCK_TRIPWIRE_DETACH, SoundCategory.BLOCKS, 0.4F, 1.2F / (worldIn.rand.nextFloat() * 0.2F + 0.9F));
      }

   }

   private void notifyNeighbors(World worldIn, BlockPos pos, Direction side) {
      worldIn.notifyNeighborsOfStateChange(pos, this);
      worldIn.notifyNeighborsOfStateChange(pos.offset(side.getOpposite()), this);
   }

   public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
      if (!isMoving && state.getBlock() != newState.getBlock()) {
         boolean flag = state.get(ATTACHED);
         boolean flag1 = state.get(POWERED);
         if (flag || flag1) {
            this.calculateState(worldIn, pos, state, true, false, -1, (BlockState)null);
         }

         if (flag1) {
            worldIn.notifyNeighborsOfStateChange(pos, this);
            worldIn.notifyNeighborsOfStateChange(pos.offset(state.get(FACING).getOpposite()), this);
         }

         super.onReplaced(state, worldIn, pos, newState, isMoving);
      }
   }

   /**
    * @deprecated call via {@link IBlockState#getWeakPower(IBlockAccess,BlockPos,EnumFacing)} whenever possible.
    * Implementing/overriding is fine.
    */
   public int getWeakPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
      return blockState.get(POWERED) ? 15 : 0;
   }

   /**
    * @deprecated call via {@link IBlockState#getStrongPower(IBlockAccess,BlockPos,EnumFacing)} whenever possible.
    * Implementing/overriding is fine.
    */
   public int getStrongPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
      if (!blockState.get(POWERED)) {
         return 0;
      } else {
         return blockState.get(FACING) == side ? 15 : 0;
      }
   }

   /**
    * Can this block provide power. Only wire currently seems to have this change based on its state.
    * @deprecated call via {@link IBlockState#canProvidePower()} whenever possible. Implementing/overriding is fine.
    */
   public boolean canProvidePower(BlockState state) {
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
      builder.add(FACING, POWERED, ATTACHED);
   }
}
package net.minecraft.block;

import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Items;
import net.minecraft.state.BooleanProperty;
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
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class TripWireBlock extends Block {
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
   public static final BooleanProperty ATTACHED = BlockStateProperties.ATTACHED;
   public static final BooleanProperty DISARMED = BlockStateProperties.DISARMED;
   public static final BooleanProperty NORTH = SixWayBlock.NORTH;
   public static final BooleanProperty EAST = SixWayBlock.EAST;
   public static final BooleanProperty SOUTH = SixWayBlock.SOUTH;
   public static final BooleanProperty WEST = SixWayBlock.WEST;
   private static final Map<Direction, BooleanProperty> field_196537_E = FourWayBlock.FACING_TO_PROPERTY_MAP;
   protected static final VoxelShape AABB = Block.makeCuboidShape(0.0D, 1.0D, 0.0D, 16.0D, 2.5D, 16.0D);
   protected static final VoxelShape TRIP_WRITE_ATTACHED_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D);
   private final TripWireHookBlock field_196538_F;

   public TripWireBlock(TripWireHookBlock p_i48305_1_, Block.Properties properties) {
      super(properties);
      this.setDefaultState(this.stateContainer.getBaseState().with(POWERED, Boolean.valueOf(false)).with(ATTACHED, Boolean.valueOf(false)).with(DISARMED, Boolean.valueOf(false)).with(NORTH, Boolean.valueOf(false)).with(EAST, Boolean.valueOf(false)).with(SOUTH, Boolean.valueOf(false)).with(WEST, Boolean.valueOf(false)));
      this.field_196538_F = p_i48305_1_;
   }

   public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
      return state.get(ATTACHED) ? AABB : TRIP_WRITE_ATTACHED_AABB;
   }

   public BlockState getStateForPlacement(BlockItemUseContext context) {
      IBlockReader iblockreader = context.getWorld();
      BlockPos blockpos = context.getPos();
      return this.getDefaultState().with(NORTH, Boolean.valueOf(this.shouldConnectTo(iblockreader.getBlockState(blockpos.north()), Direction.NORTH))).with(EAST, Boolean.valueOf(this.shouldConnectTo(iblockreader.getBlockState(blockpos.east()), Direction.EAST))).with(SOUTH, Boolean.valueOf(this.shouldConnectTo(iblockreader.getBlockState(blockpos.south()), Direction.SOUTH))).with(WEST, Boolean.valueOf(this.shouldConnectTo(iblockreader.getBlockState(blockpos.west()), Direction.WEST)));
   }

   /**
    * Update the provided state given the provided neighbor facing and neighbor state, returning a new state.
    * For example, fences make their connections to the passed in state if possible, and wet concrete powder immediately
    * returns its solidified counterpart.
    * Note that this method should ideally consider only the specific face passed in.
    */
   public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
      return facing.getAxis().isHorizontal() ? stateIn.with(field_196537_E.get(facing), Boolean.valueOf(this.shouldConnectTo(facingState, facing))) : super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
   }

   public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
      if (oldState.getBlock() != state.getBlock()) {
         this.notifyHook(worldIn, pos, state);
      }
   }

   public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
      if (!isMoving && state.getBlock() != newState.getBlock()) {
         this.notifyHook(worldIn, pos, state.with(POWERED, Boolean.valueOf(true)));
      }
   }

   /**
    * Called before the Block is set to air in the world. Called regardless of if the player's tool can actually collect
    * this block
    */
   public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
      if (!worldIn.isRemote && !player.getHeldItemMainhand().isEmpty() && player.getHeldItemMainhand().getItem() == Items.SHEARS) {
         worldIn.setBlockState(pos, state.with(DISARMED, Boolean.valueOf(true)), 4);
      }

      super.onBlockHarvested(worldIn, pos, state, player);
   }

   private void notifyHook(World worldIn, BlockPos pos, BlockState state) {
      for(Direction direction : new Direction[]{Direction.SOUTH, Direction.WEST}) {
         for(int i = 1; i < 42; ++i) {
            BlockPos blockpos = pos.offset(direction, i);
            BlockState blockstate = worldIn.getBlockState(blockpos);
            if (blockstate.getBlock() == this.field_196538_F) {
               if (blockstate.get(TripWireHookBlock.FACING) == direction.getOpposite()) {
                  this.field_196538_F.calculateState(worldIn, blockpos, blockstate, false, true, i, state);
               }
               break;
            }

            if (blockstate.getBlock() != this) {
               break;
            }
         }
      }

   }

   public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
      if (!worldIn.isRemote) {
         if (!state.get(POWERED)) {
            this.updateState(worldIn, pos);
         }
      }
   }

   public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
      if (worldIn.getBlockState(pos).get(POWERED)) {
         this.updateState(worldIn, pos);
      }
   }

   private void updateState(World worldIn, BlockPos pos) {
      BlockState blockstate = worldIn.getBlockState(pos);
      boolean flag = blockstate.get(POWERED);
      boolean flag1 = false;
      List<? extends Entity> list = worldIn.getEntitiesWithinAABBExcludingEntity((Entity)null, blockstate.getShape(worldIn, pos).getBoundingBox().offset(pos));
      if (!list.isEmpty()) {
         for(Entity entity : list) {
            if (!entity.doesEntityNotTriggerPressurePlate()) {
               flag1 = true;
               break;
            }
         }
      }

      if (flag1 != flag) {
         blockstate = blockstate.with(POWERED, Boolean.valueOf(flag1));
         worldIn.setBlockState(pos, blockstate, 3);
         this.notifyHook(worldIn, pos, blockstate);
      }

      if (flag1) {
         worldIn.getPendingBlockTicks().scheduleTick(new BlockPos(pos), this, this.tickRate(worldIn));
      }

   }

   public boolean shouldConnectTo(BlockState p_196536_1_, Direction p_196536_2_) {
      Block block = p_196536_1_.getBlock();
      if (block == this.field_196538_F) {
         return p_196536_1_.get(TripWireHookBlock.FACING) == p_196536_2_.getOpposite();
      } else {
         return block == this;
      }
   }

   /**
    * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed
    * blockstate.
    * @deprecated call via {@link IBlockState#withRotation(Rotation)} whenever possible. Implementing/overriding is
    * fine.
    */
   public BlockState rotate(BlockState state, Rotation rot) {
      switch(rot) {
      case CLOCKWISE_180:
         return state.with(NORTH, state.get(SOUTH)).with(EAST, state.get(WEST)).with(SOUTH, state.get(NORTH)).with(WEST, state.get(EAST));
      case COUNTERCLOCKWISE_90:
         return state.with(NORTH, state.get(EAST)).with(EAST, state.get(SOUTH)).with(SOUTH, state.get(WEST)).with(WEST, state.get(NORTH));
      case CLOCKWISE_90:
         return state.with(NORTH, state.get(WEST)).with(EAST, state.get(NORTH)).with(SOUTH, state.get(EAST)).with(WEST, state.get(SOUTH));
      default:
         return state;
      }
   }

   /**
    * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed
    * blockstate.
    * @deprecated call via {@link IBlockState#withMirror(Mirror)} whenever possible. Implementing/overriding is fine.
    */
   public BlockState mirror(BlockState state, Mirror mirrorIn) {
      switch(mirrorIn) {
      case LEFT_RIGHT:
         return state.with(NORTH, state.get(SOUTH)).with(SOUTH, state.get(NORTH));
      case FRONT_BACK:
         return state.with(EAST, state.get(WEST)).with(WEST, state.get(EAST));
      default:
         return super.mirror(state, mirrorIn);
      }
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
      builder.add(POWERED, ATTACHED, DISARMED, NORTH, EAST, WEST, SOUTH);
   }
}
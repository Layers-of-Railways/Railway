package net.minecraft.block;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.PistonType;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public class PistonHeadBlock extends DirectionalBlock {
   public static final EnumProperty<PistonType> TYPE = BlockStateProperties.PISTON_TYPE;
   public static final BooleanProperty SHORT = BlockStateProperties.SHORT;
   protected static final VoxelShape PISTON_EXTENSION_EAST_AABB = Block.makeCuboidShape(12.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);
   protected static final VoxelShape PISTON_EXTENSION_WEST_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 4.0D, 16.0D, 16.0D);
   protected static final VoxelShape PISTON_EXTENSION_SOUTH_AABB = Block.makeCuboidShape(0.0D, 0.0D, 12.0D, 16.0D, 16.0D, 16.0D);
   protected static final VoxelShape PISTON_EXTENSION_NORTH_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 4.0D);
   protected static final VoxelShape PISTON_EXTENSION_UP_AABB = Block.makeCuboidShape(0.0D, 12.0D, 0.0D, 16.0D, 16.0D, 16.0D);
   protected static final VoxelShape PISTON_EXTENSION_DOWN_AABB = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D);
   protected static final VoxelShape UP_ARM_AABB = Block.makeCuboidShape(6.0D, -4.0D, 6.0D, 10.0D, 12.0D, 10.0D);
   protected static final VoxelShape DOWN_ARM_AABB = Block.makeCuboidShape(6.0D, 4.0D, 6.0D, 10.0D, 20.0D, 10.0D);
   protected static final VoxelShape SOUTH_ARM_AABB = Block.makeCuboidShape(6.0D, 6.0D, -4.0D, 10.0D, 10.0D, 12.0D);
   protected static final VoxelShape NORTH_ARM_AABB = Block.makeCuboidShape(6.0D, 6.0D, 4.0D, 10.0D, 10.0D, 20.0D);
   protected static final VoxelShape EAST_ARM_AABB = Block.makeCuboidShape(-4.0D, 6.0D, 6.0D, 12.0D, 10.0D, 10.0D);
   protected static final VoxelShape WEST_ARM_AABB = Block.makeCuboidShape(4.0D, 6.0D, 6.0D, 20.0D, 10.0D, 10.0D);
   protected static final VoxelShape SHORT_UP_ARM_AABB = Block.makeCuboidShape(6.0D, 0.0D, 6.0D, 10.0D, 12.0D, 10.0D);
   protected static final VoxelShape SHORT_DOWN_ARM_AABB = Block.makeCuboidShape(6.0D, 4.0D, 6.0D, 10.0D, 16.0D, 10.0D);
   protected static final VoxelShape SHORT_SOUTH_ARM_AABB = Block.makeCuboidShape(6.0D, 6.0D, 0.0D, 10.0D, 10.0D, 12.0D);
   protected static final VoxelShape SHORT_NORTH_ARM_AABB = Block.makeCuboidShape(6.0D, 6.0D, 4.0D, 10.0D, 10.0D, 16.0D);
   protected static final VoxelShape SHORT_EAST_ARM_AABB = Block.makeCuboidShape(0.0D, 6.0D, 6.0D, 12.0D, 10.0D, 10.0D);
   protected static final VoxelShape SHORT_WEST_ARM_AABB = Block.makeCuboidShape(4.0D, 6.0D, 6.0D, 16.0D, 10.0D, 10.0D);

   public PistonHeadBlock(Block.Properties properties) {
      super(properties);
      this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH).with(TYPE, PistonType.DEFAULT).with(SHORT, Boolean.valueOf(false)));
   }

   private VoxelShape getExtensionShapeFromState(BlockState state) {
      switch((Direction)state.get(FACING)) {
      case DOWN:
      default:
         return PISTON_EXTENSION_DOWN_AABB;
      case UP:
         return PISTON_EXTENSION_UP_AABB;
      case NORTH:
         return PISTON_EXTENSION_NORTH_AABB;
      case SOUTH:
         return PISTON_EXTENSION_SOUTH_AABB;
      case WEST:
         return PISTON_EXTENSION_WEST_AABB;
      case EAST:
         return PISTON_EXTENSION_EAST_AABB;
      }
   }

   public boolean isTransparent(BlockState state) {
      return true;
   }

   public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
      return VoxelShapes.or(this.getExtensionShapeFromState(state), this.getArmShapeFromState(state));
   }

   private VoxelShape getArmShapeFromState(BlockState state) {
      boolean flag = state.get(SHORT);
      switch((Direction)state.get(FACING)) {
      case DOWN:
      default:
         return flag ? SHORT_DOWN_ARM_AABB : DOWN_ARM_AABB;
      case UP:
         return flag ? SHORT_UP_ARM_AABB : UP_ARM_AABB;
      case NORTH:
         return flag ? SHORT_NORTH_ARM_AABB : NORTH_ARM_AABB;
      case SOUTH:
         return flag ? SHORT_SOUTH_ARM_AABB : SOUTH_ARM_AABB;
      case WEST:
         return flag ? SHORT_WEST_ARM_AABB : WEST_ARM_AABB;
      case EAST:
         return flag ? SHORT_EAST_ARM_AABB : EAST_ARM_AABB;
      }
   }

   /**
    * Called before the Block is set to air in the world. Called regardless of if the player's tool can actually collect
    * this block
    */
   public void onBlockHarvested(World worldIn, BlockPos pos, BlockState state, PlayerEntity player) {
      if (!worldIn.isRemote && player.abilities.isCreativeMode) {
         BlockPos blockpos = pos.offset(state.get(FACING).getOpposite());
         Block block = worldIn.getBlockState(blockpos).getBlock();
         if (block == Blocks.PISTON || block == Blocks.STICKY_PISTON) {
            worldIn.removeBlock(blockpos, false);
         }
      }

      super.onBlockHarvested(worldIn, pos, state, player);
   }

   public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
      if (state.getBlock() != newState.getBlock()) {
         super.onReplaced(state, worldIn, pos, newState, isMoving);
         Direction direction = state.get(FACING).getOpposite();
         pos = pos.offset(direction);
         BlockState blockstate = worldIn.getBlockState(pos);
         if ((blockstate.getBlock() == Blocks.PISTON || blockstate.getBlock() == Blocks.STICKY_PISTON) && blockstate.get(PistonBlock.EXTENDED)) {
            spawnDrops(blockstate, worldIn, pos);
            worldIn.removeBlock(pos, false);
         }

      }
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

   public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
      Block block = worldIn.getBlockState(pos.offset(state.get(FACING).getOpposite())).getBlock();
      return block == Blocks.PISTON || block == Blocks.STICKY_PISTON || block == Blocks.MOVING_PISTON;
   }

   public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
      if (state.isValidPosition(worldIn, pos)) {
         BlockPos blockpos = pos.offset(state.get(FACING).getOpposite());
         worldIn.getBlockState(blockpos).neighborChanged(worldIn, blockpos, blockIn, fromPos, false);
      }

   }

   public ItemStack getItem(IBlockReader worldIn, BlockPos pos, BlockState state) {
      return new ItemStack(state.get(TYPE) == PistonType.STICKY ? Blocks.STICKY_PISTON : Blocks.PISTON);
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
      builder.add(FACING, TYPE, SHORT);
   }

   public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
      return false;
   }
}
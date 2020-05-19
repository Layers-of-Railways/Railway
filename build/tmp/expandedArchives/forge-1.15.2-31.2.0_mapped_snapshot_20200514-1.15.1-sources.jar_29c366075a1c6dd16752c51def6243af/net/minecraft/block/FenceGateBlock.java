package net.minecraft.block;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class FenceGateBlock extends HorizontalBlock {
   public static final BooleanProperty OPEN = BlockStateProperties.OPEN;
   public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
   public static final BooleanProperty IN_WALL = BlockStateProperties.IN_WALL;
   protected static final VoxelShape AABB_HITBOX_ZAXIS = Block.makeCuboidShape(0.0D, 0.0D, 6.0D, 16.0D, 16.0D, 10.0D);
   protected static final VoxelShape AABB_HITBOX_XAXIS = Block.makeCuboidShape(6.0D, 0.0D, 0.0D, 10.0D, 16.0D, 16.0D);
   protected static final VoxelShape AABB_HITBOX_ZAXIS_INWALL = Block.makeCuboidShape(0.0D, 0.0D, 6.0D, 16.0D, 13.0D, 10.0D);
   protected static final VoxelShape AABB_HITBOX_XAXIS_INWALL = Block.makeCuboidShape(6.0D, 0.0D, 0.0D, 10.0D, 13.0D, 16.0D);
   protected static final VoxelShape field_208068_x = Block.makeCuboidShape(0.0D, 0.0D, 6.0D, 16.0D, 24.0D, 10.0D);
   protected static final VoxelShape AABB_COLLISION_BOX_XAXIS = Block.makeCuboidShape(6.0D, 0.0D, 0.0D, 10.0D, 24.0D, 16.0D);
   protected static final VoxelShape field_208069_z = VoxelShapes.or(Block.makeCuboidShape(0.0D, 5.0D, 7.0D, 2.0D, 16.0D, 9.0D), Block.makeCuboidShape(14.0D, 5.0D, 7.0D, 16.0D, 16.0D, 9.0D));
   protected static final VoxelShape AABB_COLLISION_BOX_ZAXIS = VoxelShapes.or(Block.makeCuboidShape(7.0D, 5.0D, 0.0D, 9.0D, 16.0D, 2.0D), Block.makeCuboidShape(7.0D, 5.0D, 14.0D, 9.0D, 16.0D, 16.0D));
   protected static final VoxelShape field_208066_B = VoxelShapes.or(Block.makeCuboidShape(0.0D, 2.0D, 7.0D, 2.0D, 13.0D, 9.0D), Block.makeCuboidShape(14.0D, 2.0D, 7.0D, 16.0D, 13.0D, 9.0D));
   protected static final VoxelShape field_208067_C = VoxelShapes.or(Block.makeCuboidShape(7.0D, 2.0D, 0.0D, 9.0D, 13.0D, 2.0D), Block.makeCuboidShape(7.0D, 2.0D, 14.0D, 9.0D, 13.0D, 16.0D));

   public FenceGateBlock(Block.Properties properties) {
      super(properties);
      this.setDefaultState(this.stateContainer.getBaseState().with(OPEN, Boolean.valueOf(false)).with(POWERED, Boolean.valueOf(false)).with(IN_WALL, Boolean.valueOf(false)));
   }

   public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
      if (state.get(IN_WALL)) {
         return state.get(HORIZONTAL_FACING).getAxis() == Direction.Axis.X ? AABB_HITBOX_XAXIS_INWALL : AABB_HITBOX_ZAXIS_INWALL;
      } else {
         return state.get(HORIZONTAL_FACING).getAxis() == Direction.Axis.X ? AABB_HITBOX_XAXIS : AABB_HITBOX_ZAXIS;
      }
   }

   /**
    * Update the provided state given the provided neighbor facing and neighbor state, returning a new state.
    * For example, fences make their connections to the passed in state if possible, and wet concrete powder immediately
    * returns its solidified counterpart.
    * Note that this method should ideally consider only the specific face passed in.
    */
   public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
      Direction.Axis direction$axis = facing.getAxis();
      if (stateIn.get(HORIZONTAL_FACING).rotateY().getAxis() != direction$axis) {
         return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
      } else {
         boolean flag = this.isWall(facingState) || this.isWall(worldIn.getBlockState(currentPos.offset(facing.getOpposite())));
         return stateIn.with(IN_WALL, Boolean.valueOf(flag));
      }
   }

   public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
      if (state.get(OPEN)) {
         return VoxelShapes.empty();
      } else {
         return state.get(HORIZONTAL_FACING).getAxis() == Direction.Axis.Z ? field_208068_x : AABB_COLLISION_BOX_XAXIS;
      }
   }

   public VoxelShape getRenderShape(BlockState state, IBlockReader worldIn, BlockPos pos) {
      if (state.get(IN_WALL)) {
         return state.get(HORIZONTAL_FACING).getAxis() == Direction.Axis.X ? field_208067_C : field_208066_B;
      } else {
         return state.get(HORIZONTAL_FACING).getAxis() == Direction.Axis.X ? AABB_COLLISION_BOX_ZAXIS : field_208069_z;
      }
   }

   public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
      switch(type) {
      case LAND:
         return state.get(OPEN);
      case WATER:
         return false;
      case AIR:
         return state.get(OPEN);
      default:
         return false;
      }
   }

   public BlockState getStateForPlacement(BlockItemUseContext context) {
      World world = context.getWorld();
      BlockPos blockpos = context.getPos();
      boolean flag = world.isBlockPowered(blockpos);
      Direction direction = context.getPlacementHorizontalFacing();
      Direction.Axis direction$axis = direction.getAxis();
      boolean flag1 = direction$axis == Direction.Axis.Z && (this.isWall(world.getBlockState(blockpos.west())) || this.isWall(world.getBlockState(blockpos.east()))) || direction$axis == Direction.Axis.X && (this.isWall(world.getBlockState(blockpos.north())) || this.isWall(world.getBlockState(blockpos.south())));
      return this.getDefaultState().with(HORIZONTAL_FACING, direction).with(OPEN, Boolean.valueOf(flag)).with(POWERED, Boolean.valueOf(flag)).with(IN_WALL, Boolean.valueOf(flag1));
   }

   private boolean isWall(BlockState p_196380_1_) {
      return p_196380_1_.getBlock().isIn(BlockTags.WALLS);
   }

   public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
      if (state.get(OPEN)) {
         state = state.with(OPEN, Boolean.valueOf(false));
         worldIn.setBlockState(pos, state, 10);
      } else {
         Direction direction = player.getHorizontalFacing();
         if (state.get(HORIZONTAL_FACING) == direction.getOpposite()) {
            state = state.with(HORIZONTAL_FACING, direction);
         }

         state = state.with(OPEN, Boolean.valueOf(true));
         worldIn.setBlockState(pos, state, 10);
      }

      worldIn.playEvent(player, state.get(OPEN) ? 1008 : 1014, pos, 0);
      return ActionResultType.SUCCESS;
   }

   public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
      if (!worldIn.isRemote) {
         boolean flag = worldIn.isBlockPowered(pos);
         if (state.get(POWERED) != flag) {
            worldIn.setBlockState(pos, state.with(POWERED, Boolean.valueOf(flag)).with(OPEN, Boolean.valueOf(flag)), 2);
            if (state.get(OPEN) != flag) {
               worldIn.playEvent((PlayerEntity)null, flag ? 1008 : 1014, pos, 0);
            }
         }

      }
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
      builder.add(HORIZONTAL_FACING, OPEN, POWERED, IN_WALL);
   }

   /**
    * True if the provided direction is parallel to the fence gate's gates
    */
   public static boolean isParallel(BlockState p_220253_0_, Direction p_220253_1_) {
      return p_220253_0_.get(HORIZONTAL_FACING).getAxis() == p_220253_1_.rotateY().getAxis();
   }
}
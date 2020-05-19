package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class CocoaBlock extends HorizontalBlock implements IGrowable {
   public static final IntegerProperty AGE = BlockStateProperties.AGE_0_2;
   protected static final VoxelShape[] COCOA_EAST_AABB = new VoxelShape[]{Block.makeCuboidShape(11.0D, 7.0D, 6.0D, 15.0D, 12.0D, 10.0D), Block.makeCuboidShape(9.0D, 5.0D, 5.0D, 15.0D, 12.0D, 11.0D), Block.makeCuboidShape(7.0D, 3.0D, 4.0D, 15.0D, 12.0D, 12.0D)};
   protected static final VoxelShape[] COCOA_WEST_AABB = new VoxelShape[]{Block.makeCuboidShape(1.0D, 7.0D, 6.0D, 5.0D, 12.0D, 10.0D), Block.makeCuboidShape(1.0D, 5.0D, 5.0D, 7.0D, 12.0D, 11.0D), Block.makeCuboidShape(1.0D, 3.0D, 4.0D, 9.0D, 12.0D, 12.0D)};
   protected static final VoxelShape[] COCOA_NORTH_AABB = new VoxelShape[]{Block.makeCuboidShape(6.0D, 7.0D, 1.0D, 10.0D, 12.0D, 5.0D), Block.makeCuboidShape(5.0D, 5.0D, 1.0D, 11.0D, 12.0D, 7.0D), Block.makeCuboidShape(4.0D, 3.0D, 1.0D, 12.0D, 12.0D, 9.0D)};
   protected static final VoxelShape[] COCOA_SOUTH_AABB = new VoxelShape[]{Block.makeCuboidShape(6.0D, 7.0D, 11.0D, 10.0D, 12.0D, 15.0D), Block.makeCuboidShape(5.0D, 5.0D, 9.0D, 11.0D, 12.0D, 15.0D), Block.makeCuboidShape(4.0D, 3.0D, 7.0D, 12.0D, 12.0D, 15.0D)};

   public CocoaBlock(Block.Properties properties) {
      super(properties);
      this.setDefaultState(this.stateContainer.getBaseState().with(HORIZONTAL_FACING, Direction.NORTH).with(AGE, Integer.valueOf(0)));
   }

   public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
      if (true) {
         int i = state.get(AGE);
         if (i < 2 && net.minecraftforge.common.ForgeHooks.onCropsGrowPre(worldIn, pos, state, worldIn.rand.nextInt(5) == 0)) {
            worldIn.setBlockState(pos, state.with(AGE, Integer.valueOf(i + 1)), 2);
            net.minecraftforge.common.ForgeHooks.onCropsGrowPost(worldIn, pos, state);
         }
      }

   }

   public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
      Block block = worldIn.getBlockState(pos.offset(state.get(HORIZONTAL_FACING))).getBlock();
      return block.isIn(BlockTags.JUNGLE_LOGS);
   }

   public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
      int i = state.get(AGE);
      switch((Direction)state.get(HORIZONTAL_FACING)) {
      case SOUTH:
         return COCOA_SOUTH_AABB[i];
      case NORTH:
      default:
         return COCOA_NORTH_AABB[i];
      case WEST:
         return COCOA_WEST_AABB[i];
      case EAST:
         return COCOA_EAST_AABB[i];
      }
   }

   @Nullable
   public BlockState getStateForPlacement(BlockItemUseContext context) {
      BlockState blockstate = this.getDefaultState();
      IWorldReader iworldreader = context.getWorld();
      BlockPos blockpos = context.getPos();

      for(Direction direction : context.getNearestLookingDirections()) {
         if (direction.getAxis().isHorizontal()) {
            blockstate = blockstate.with(HORIZONTAL_FACING, direction);
            if (blockstate.isValidPosition(iworldreader, blockpos)) {
               return blockstate;
            }
         }
      }

      return null;
   }

   /**
    * Update the provided state given the provided neighbor facing and neighbor state, returning a new state.
    * For example, fences make their connections to the passed in state if possible, and wet concrete powder immediately
    * returns its solidified counterpart.
    * Note that this method should ideally consider only the specific face passed in.
    */
   public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
      return facing == stateIn.get(HORIZONTAL_FACING) && !stateIn.isValidPosition(worldIn, currentPos) ? Blocks.AIR.getDefaultState() : super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
   }

   /**
    * Whether this IGrowable can grow
    */
   public boolean canGrow(IBlockReader worldIn, BlockPos pos, BlockState state, boolean isClient) {
      return state.get(AGE) < 2;
   }

   public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, BlockState state) {
      return true;
   }

   public void grow(ServerWorld worldIn, Random rand, BlockPos pos, BlockState state) {
      worldIn.setBlockState(pos, state.with(AGE, Integer.valueOf(state.get(AGE) + 1)), 2);
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
      builder.add(HORIZONTAL_FACING, AGE);
   }
}
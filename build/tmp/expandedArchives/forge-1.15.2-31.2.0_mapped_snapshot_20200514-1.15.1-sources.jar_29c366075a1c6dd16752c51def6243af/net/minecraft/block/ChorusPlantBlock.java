package net.minecraft.block;

import java.util.Random;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.server.ServerWorld;

public class ChorusPlantBlock extends SixWayBlock {
   protected ChorusPlantBlock(Block.Properties builder) {
      super(0.3125F, builder);
      this.setDefaultState(this.stateContainer.getBaseState().with(NORTH, Boolean.valueOf(false)).with(EAST, Boolean.valueOf(false)).with(SOUTH, Boolean.valueOf(false)).with(WEST, Boolean.valueOf(false)).with(UP, Boolean.valueOf(false)).with(DOWN, Boolean.valueOf(false)));
   }

   public BlockState getStateForPlacement(BlockItemUseContext context) {
      return this.makeConnections(context.getWorld(), context.getPos());
   }

   public BlockState makeConnections(IBlockReader p_196497_1_, BlockPos pos) {
      Block block = p_196497_1_.getBlockState(pos.down()).getBlock();
      Block block1 = p_196497_1_.getBlockState(pos.up()).getBlock();
      Block block2 = p_196497_1_.getBlockState(pos.north()).getBlock();
      Block block3 = p_196497_1_.getBlockState(pos.east()).getBlock();
      Block block4 = p_196497_1_.getBlockState(pos.south()).getBlock();
      Block block5 = p_196497_1_.getBlockState(pos.west()).getBlock();
      return this.getDefaultState().with(DOWN, Boolean.valueOf(block == this || block == Blocks.CHORUS_FLOWER || block == Blocks.END_STONE)).with(UP, Boolean.valueOf(block1 == this || block1 == Blocks.CHORUS_FLOWER)).with(NORTH, Boolean.valueOf(block2 == this || block2 == Blocks.CHORUS_FLOWER)).with(EAST, Boolean.valueOf(block3 == this || block3 == Blocks.CHORUS_FLOWER)).with(SOUTH, Boolean.valueOf(block4 == this || block4 == Blocks.CHORUS_FLOWER)).with(WEST, Boolean.valueOf(block5 == this || block5 == Blocks.CHORUS_FLOWER));
   }

   /**
    * Update the provided state given the provided neighbor facing and neighbor state, returning a new state.
    * For example, fences make their connections to the passed in state if possible, and wet concrete powder immediately
    * returns its solidified counterpart.
    * Note that this method should ideally consider only the specific face passed in.
    */
   public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
      if (!stateIn.isValidPosition(worldIn, currentPos)) {
         worldIn.getPendingBlockTicks().scheduleTick(currentPos, this, 1);
         return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
      } else {
         Block block = facingState.getBlock();
         boolean flag = block == this || block == Blocks.CHORUS_FLOWER || facing == Direction.DOWN && block == Blocks.END_STONE;
         return stateIn.with(FACING_TO_PROPERTY_MAP.get(facing), Boolean.valueOf(flag));
      }
   }

   public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
      if (!state.isValidPosition(worldIn, pos)) {
         worldIn.destroyBlock(pos, true);
      }

   }

   public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
      BlockState blockstate = worldIn.getBlockState(pos.down());
      boolean flag = !worldIn.getBlockState(pos.up()).isAir() && !blockstate.isAir();

      for(Direction direction : Direction.Plane.HORIZONTAL) {
         BlockPos blockpos = pos.offset(direction);
         Block block = worldIn.getBlockState(blockpos).getBlock();
         if (block == this) {
            if (flag) {
               return false;
            }

            Block block1 = worldIn.getBlockState(blockpos.down()).getBlock();
            if (block1 == this || block1 == Blocks.END_STONE) {
               return true;
            }
         }
      }

      Block block2 = blockstate.getBlock();
      return block2 == this || block2 == Blocks.END_STONE;
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
      builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN);
   }

   public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
      return false;
   }
}
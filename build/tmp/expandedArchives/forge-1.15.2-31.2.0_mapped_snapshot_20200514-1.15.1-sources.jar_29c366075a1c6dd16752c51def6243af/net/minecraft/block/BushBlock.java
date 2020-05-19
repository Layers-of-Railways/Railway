package net.minecraft.block;

import net.minecraft.pathfinding.PathType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;

public class BushBlock extends Block implements net.minecraftforge.common.IPlantable {
   protected BushBlock(Block.Properties properties) {
      super(properties);
   }

   protected boolean isValidGround(BlockState state, IBlockReader worldIn, BlockPos pos) {
      Block block = state.getBlock();
      return block == Blocks.GRASS_BLOCK || block == Blocks.DIRT || block == Blocks.COARSE_DIRT || block == Blocks.PODZOL || block == Blocks.FARMLAND;
   }

   /**
    * Update the provided state given the provided neighbor facing and neighbor state, returning a new state.
    * For example, fences make their connections to the passed in state if possible, and wet concrete powder immediately
    * returns its solidified counterpart.
    * Note that this method should ideally consider only the specific face passed in.
    */
   public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
      return !stateIn.isValidPosition(worldIn, currentPos) ? Blocks.AIR.getDefaultState() : super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
   }

   public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
      BlockPos blockpos = pos.down();
      if (state.getBlock() == this) //Forge: This function is called during world gen and placement, before this block is set, so if we are not 'here' then assume it's the pre-check.
         return worldIn.getBlockState(blockpos).canSustainPlant(worldIn, blockpos, Direction.UP, this);
      return this.isValidGround(worldIn.getBlockState(blockpos), worldIn, blockpos);
   }

   public boolean propagatesSkylightDown(BlockState state, IBlockReader reader, BlockPos pos) {
      return true;
   }

   public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
      return type == PathType.AIR && !this.blocksMovement ? true : super.allowsMovement(state, worldIn, pos, type);
   }

   @Override
   public BlockState getPlant(IBlockReader world, BlockPos pos) {
      BlockState state = world.getBlockState(pos);
      if (state.getBlock() != this) return getDefaultState();
      return state;
   }
}
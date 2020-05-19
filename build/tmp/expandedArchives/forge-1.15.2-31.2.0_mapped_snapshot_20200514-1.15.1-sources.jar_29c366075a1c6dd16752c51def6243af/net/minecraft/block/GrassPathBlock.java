package net.minecraft.block;

import java.util.Random;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class GrassPathBlock extends Block {
   protected static final VoxelShape SHAPE = FarmlandBlock.SHAPE;

   protected GrassPathBlock(Block.Properties builder) {
      super(builder);
   }

   public boolean isTransparent(BlockState state) {
      return true;
   }

   public BlockState getStateForPlacement(BlockItemUseContext context) {
      return !this.getDefaultState().isValidPosition(context.getWorld(), context.getPos()) ? Block.nudgeEntitiesWithNewState(this.getDefaultState(), Blocks.DIRT.getDefaultState(), context.getWorld(), context.getPos()) : super.getStateForPlacement(context);
   }

   /**
    * Update the provided state given the provided neighbor facing and neighbor state, returning a new state.
    * For example, fences make their connections to the passed in state if possible, and wet concrete powder immediately
    * returns its solidified counterpart.
    * Note that this method should ideally consider only the specific face passed in.
    */
   public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
      if (facing == Direction.UP && !stateIn.isValidPosition(worldIn, currentPos)) {
         worldIn.getPendingBlockTicks().scheduleTick(currentPos, this, 1);
      }

      return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
   }

   public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
      FarmlandBlock.turnToDirt(state, worldIn, pos);
   }

   public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
      BlockState blockstate = worldIn.getBlockState(pos.up());
      return !blockstate.getMaterial().isSolid() || blockstate.getBlock() instanceof FenceGateBlock;
   }

   public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
      return SHAPE;
   }

   public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
      return false;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isViewBlocking(BlockState state, IBlockReader worldIn, BlockPos pos) {
      return true;
   }
}
package net.minecraft.block;

import java.util.Random;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class CoralWallFanBlock extends DeadCoralWallFanBlock {
   private final Block deadBlock;

   protected CoralWallFanBlock(Block p_i49774_1_, Block.Properties builder) {
      super(builder);
      this.deadBlock = p_i49774_1_;
   }

   public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
      this.updateIfDry(state, worldIn, pos);
   }

   public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
      if (!isInWater(state, worldIn, pos)) {
         worldIn.setBlockState(pos, this.deadBlock.getDefaultState().with(WATERLOGGED, Boolean.valueOf(false)).with(FACING, state.get(FACING)), 2);
      }

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

         this.updateIfDry(stateIn, worldIn, currentPos);
         return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
      }
   }
}
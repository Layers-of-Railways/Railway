package net.minecraft.block;

import net.minecraft.item.BlockItemUseContext;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class ConcretePowderBlock extends FallingBlock {
   private final BlockState solidifiedState;

   public ConcretePowderBlock(Block p_i48423_1_, Block.Properties properties) {
      super(properties);
      this.solidifiedState = p_i48423_1_.getDefaultState();
   }

   public void onEndFalling(World worldIn, BlockPos pos, BlockState fallingState, BlockState hitState) {
      if (func_230137_b_(worldIn, pos, hitState)) {
         worldIn.setBlockState(pos, this.solidifiedState, 3);
      }

   }

   public BlockState getStateForPlacement(BlockItemUseContext context) {
      IBlockReader iblockreader = context.getWorld();
      BlockPos blockpos = context.getPos();
      BlockState blockstate = iblockreader.getBlockState(blockpos);
      return func_230137_b_(iblockreader, blockpos, blockstate) ? this.solidifiedState : super.getStateForPlacement(context);
   }

   private static boolean func_230137_b_(IBlockReader p_230137_0_, BlockPos p_230137_1_, BlockState p_230137_2_) {
      return causesSolidify(p_230137_2_) || isTouchingLiquid(p_230137_0_, p_230137_1_);
   }

   private static boolean isTouchingLiquid(IBlockReader p_196441_0_, BlockPos p_196441_1_) {
      boolean flag = false;
      BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable(p_196441_1_);

      for(Direction direction : Direction.values()) {
         BlockState blockstate = p_196441_0_.getBlockState(blockpos$mutable);
         if (direction != Direction.DOWN || causesSolidify(blockstate)) {
            blockpos$mutable.setPos(p_196441_1_).move(direction);
            blockstate = p_196441_0_.getBlockState(blockpos$mutable);
            if (causesSolidify(blockstate) && !blockstate.isSolidSide(p_196441_0_, p_196441_1_, direction.getOpposite())) {
               flag = true;
               break;
            }
         }
      }

      return flag;
   }

   private static boolean causesSolidify(BlockState p_212566_0_) {
      return p_212566_0_.getFluidState().isTagged(FluidTags.WATER);
   }

   /**
    * Update the provided state given the provided neighbor facing and neighbor state, returning a new state.
    * For example, fences make their connections to the passed in state if possible, and wet concrete powder immediately
    * returns its solidified counterpart.
    * Note that this method should ideally consider only the specific face passed in.
    */
   public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
      return isTouchingLiquid(worldIn, currentPos) ? this.solidifiedState : super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
   }
}
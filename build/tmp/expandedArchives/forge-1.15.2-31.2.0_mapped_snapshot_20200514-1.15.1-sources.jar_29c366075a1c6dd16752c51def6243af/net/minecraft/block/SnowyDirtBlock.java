package net.minecraft.block;

import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class SnowyDirtBlock extends Block {
   public static final BooleanProperty SNOWY = BlockStateProperties.SNOWY;

   protected SnowyDirtBlock(Block.Properties builder) {
      super(builder);
      this.setDefaultState(this.stateContainer.getBaseState().with(SNOWY, Boolean.valueOf(false)));
   }

   /**
    * Update the provided state given the provided neighbor facing and neighbor state, returning a new state.
    * For example, fences make their connections to the passed in state if possible, and wet concrete powder immediately
    * returns its solidified counterpart.
    * Note that this method should ideally consider only the specific face passed in.
    */
   public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
      if (facing != Direction.UP) {
         return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
      } else {
         Block block = facingState.getBlock();
         return stateIn.with(SNOWY, Boolean.valueOf(block == Blocks.SNOW_BLOCK || block == Blocks.SNOW));
      }
   }

   public BlockState getStateForPlacement(BlockItemUseContext context) {
      Block block = context.getWorld().getBlockState(context.getPos().up()).getBlock();
      return this.getDefaultState().with(SNOWY, Boolean.valueOf(block == Blocks.SNOW_BLOCK || block == Blocks.SNOW));
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
      builder.add(SNOWY);
   }
}
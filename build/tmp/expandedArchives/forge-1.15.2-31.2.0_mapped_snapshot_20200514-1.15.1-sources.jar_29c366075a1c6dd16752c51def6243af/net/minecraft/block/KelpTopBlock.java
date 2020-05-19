package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.server.ServerWorld;

public class KelpTopBlock extends Block implements ILiquidContainer {
   public static final IntegerProperty AGE = BlockStateProperties.AGE_0_25;
   protected static final VoxelShape SHAPE = Block.makeCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 9.0D, 16.0D);

   protected KelpTopBlock(Block.Properties builder) {
      super(builder);
      this.setDefaultState(this.stateContainer.getBaseState().with(AGE, Integer.valueOf(0)));
   }

   public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
      return SHAPE;
   }

   @Nullable
   public BlockState getStateForPlacement(BlockItemUseContext context) {
      IFluidState ifluidstate = context.getWorld().getFluidState(context.getPos());
      return ifluidstate.isTagged(FluidTags.WATER) && ifluidstate.getLevel() == 8 ? this.randomAge(context.getWorld()) : null;
   }

   public BlockState randomAge(IWorld p_209906_1_) {
      return this.getDefaultState().with(AGE, Integer.valueOf(p_209906_1_.getRandom().nextInt(25)));
   }

   public IFluidState getFluidState(BlockState state) {
      return Fluids.WATER.getStillFluidState(false);
   }

   public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
      if (!state.isValidPosition(worldIn, pos)) {
         worldIn.destroyBlock(pos, true);
      } else {
         BlockPos blockpos = pos.up();
         BlockState blockstate = worldIn.getBlockState(blockpos);
         if (blockstate.getBlock() == Blocks.WATER && state.get(AGE) < 25 && net.minecraftforge.common.ForgeHooks.onCropsGrowPre(worldIn, blockpos, state, rand.nextDouble() < 0.14D)) {
            worldIn.setBlockState(blockpos, state.cycle(AGE));
            net.minecraftforge.common.ForgeHooks.onCropsGrowPost(worldIn, blockpos, state);
         }

      }
   }

   public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
      BlockPos blockpos = pos.down();
      BlockState blockstate = worldIn.getBlockState(blockpos);
      Block block = blockstate.getBlock();
      if (block == Blocks.MAGMA_BLOCK) {
         return false;
      } else {
         return block == this || block == Blocks.KELP_PLANT || blockstate.isSolidSide(worldIn, blockpos, Direction.UP);
      }
   }

   /**
    * Update the provided state given the provided neighbor facing and neighbor state, returning a new state.
    * For example, fences make their connections to the passed in state if possible, and wet concrete powder immediately
    * returns its solidified counterpart.
    * Note that this method should ideally consider only the specific face passed in.
    */
   public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
      if (!stateIn.isValidPosition(worldIn, currentPos)) {
         if (facing == Direction.DOWN) {
            return Blocks.AIR.getDefaultState();
         }

         worldIn.getPendingBlockTicks().scheduleTick(currentPos, this, 1);
      }

      if (facing == Direction.UP && facingState.getBlock() == this) {
         return Blocks.KELP_PLANT.getDefaultState();
      } else {
         worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
         return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
      }
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
      builder.add(AGE);
   }

   public boolean canContainFluid(IBlockReader worldIn, BlockPos pos, BlockState state, Fluid fluidIn) {
      return false;
   }

   public boolean receiveFluid(IWorld worldIn, BlockPos pos, BlockState state, IFluidState fluidStateIn) {
      return false;
   }
}
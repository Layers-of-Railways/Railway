package net.minecraft.block;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class SeaGrassBlock extends BushBlock implements IGrowable, ILiquidContainer, net.minecraftforge.common.IShearable {
   protected static final VoxelShape SHAPE = Block.makeCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 12.0D, 14.0D);

   protected SeaGrassBlock(Block.Properties properties) {
      super(properties);
   }

   public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
      return SHAPE;
   }

   protected boolean isValidGround(BlockState state, IBlockReader worldIn, BlockPos pos) {
      return state.isSolidSide(worldIn, pos, Direction.UP) && state.getBlock() != Blocks.MAGMA_BLOCK;
   }

   @Nullable
   public BlockState getStateForPlacement(BlockItemUseContext context) {
      IFluidState ifluidstate = context.getWorld().getFluidState(context.getPos());
      return ifluidstate.isTagged(FluidTags.WATER) && ifluidstate.getLevel() == 8 ? super.getStateForPlacement(context) : null;
   }

   /**
    * Update the provided state given the provided neighbor facing and neighbor state, returning a new state.
    * For example, fences make their connections to the passed in state if possible, and wet concrete powder immediately
    * returns its solidified counterpart.
    * Note that this method should ideally consider only the specific face passed in.
    */
   public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
      BlockState blockstate = super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
      if (!blockstate.isAir()) {
         worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
      }

      return blockstate;
   }

   /**
    * Whether this IGrowable can grow
    */
   public boolean canGrow(IBlockReader worldIn, BlockPos pos, BlockState state, boolean isClient) {
      return true;
   }

   public boolean canUseBonemeal(World worldIn, Random rand, BlockPos pos, BlockState state) {
      return true;
   }

   public IFluidState getFluidState(BlockState state) {
      return Fluids.WATER.getStillFluidState(false);
   }

   public void grow(ServerWorld worldIn, Random rand, BlockPos pos, BlockState state) {
      BlockState blockstate = Blocks.TALL_SEAGRASS.getDefaultState();
      BlockState blockstate1 = blockstate.with(TallSeaGrassBlock.field_208065_c, DoubleBlockHalf.UPPER);
      BlockPos blockpos = pos.up();
      if (worldIn.getBlockState(blockpos).getBlock() == Blocks.WATER) {
         worldIn.setBlockState(pos, blockstate, 2);
         worldIn.setBlockState(blockpos, blockstate1, 2);
      }

   }

   public boolean canContainFluid(IBlockReader worldIn, BlockPos pos, BlockState state, Fluid fluidIn) {
      return false;
   }

   public boolean receiveFluid(IWorld worldIn, BlockPos pos, BlockState state, IFluidState fluidStateIn) {
      return false;
   }
}
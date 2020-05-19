package net.minecraft.block;

import javax.annotation.Nullable;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.properties.DoubleBlockHalf;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;

public class TallSeaGrassBlock extends ShearableDoublePlantBlock implements ILiquidContainer {
   public static final EnumProperty<DoubleBlockHalf> field_208065_c = ShearableDoublePlantBlock.PLANT_HALF;
   protected static final VoxelShape SHAPE = Block.makeCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D);

   public TallSeaGrassBlock(Block.Properties p_i49970_1_) {
      super(p_i49970_1_);
   }

   public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
      return SHAPE;
   }

   protected boolean isValidGround(BlockState state, IBlockReader worldIn, BlockPos pos) {
      return state.isSolidSide(worldIn, pos, Direction.UP) && state.getBlock() != Blocks.MAGMA_BLOCK;
   }

   public ItemStack getItem(IBlockReader worldIn, BlockPos pos, BlockState state) {
      return new ItemStack(Blocks.SEAGRASS);
   }

   @Nullable
   public BlockState getStateForPlacement(BlockItemUseContext context) {
      BlockState blockstate = super.getStateForPlacement(context);
      if (blockstate != null) {
         IFluidState ifluidstate = context.getWorld().getFluidState(context.getPos().up());
         if (ifluidstate.isTagged(FluidTags.WATER) && ifluidstate.getLevel() == 8) {
            return blockstate;
         }
      }

      return null;
   }

   public boolean isValidPosition(BlockState state, IWorldReader worldIn, BlockPos pos) {
      if (state.get(field_208065_c) == DoubleBlockHalf.UPPER) {
         BlockState blockstate = worldIn.getBlockState(pos.down());
         return blockstate.getBlock() == this && blockstate.get(field_208065_c) == DoubleBlockHalf.LOWER;
      } else {
         IFluidState ifluidstate = worldIn.getFluidState(pos);
         return super.isValidPosition(state, worldIn, pos) && ifluidstate.isTagged(FluidTags.WATER) && ifluidstate.getLevel() == 8;
      }
   }

   public IFluidState getFluidState(BlockState state) {
      return Fluids.WATER.getStillFluidState(false);
   }

   public boolean canContainFluid(IBlockReader worldIn, BlockPos pos, BlockState state, Fluid fluidIn) {
      return false;
   }

   public boolean receiveFluid(IWorld worldIn, BlockPos pos, BlockState state, IFluidState fluidStateIn) {
      return false;
   }
}
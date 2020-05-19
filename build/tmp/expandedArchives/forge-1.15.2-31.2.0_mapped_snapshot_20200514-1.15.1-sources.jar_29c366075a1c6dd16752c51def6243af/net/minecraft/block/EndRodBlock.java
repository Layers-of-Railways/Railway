package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.material.PushReaction;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.StateContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EndRodBlock extends DirectionalBlock {
   protected static final VoxelShape END_ROD_VERTICAL_AABB = Block.makeCuboidShape(6.0D, 0.0D, 6.0D, 10.0D, 16.0D, 10.0D);
   protected static final VoxelShape END_ROD_NS_AABB = Block.makeCuboidShape(6.0D, 6.0D, 0.0D, 10.0D, 10.0D, 16.0D);
   protected static final VoxelShape END_ROD_EW_AABB = Block.makeCuboidShape(0.0D, 6.0D, 6.0D, 16.0D, 10.0D, 10.0D);

   protected EndRodBlock(Block.Properties builder) {
      super(builder);
      this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.UP));
   }

   /**
    * Returns the blockstate with the given rotation from the passed blockstate. If inapplicable, returns the passed
    * blockstate.
    * @deprecated call via {@link IBlockState#withRotation(Rotation)} whenever possible. Implementing/overriding is
    * fine.
    */
   public BlockState rotate(BlockState state, Rotation rot) {
      return state.with(FACING, rot.rotate(state.get(FACING)));
   }

   /**
    * Returns the blockstate with the given mirror of the passed blockstate. If inapplicable, returns the passed
    * blockstate.
    * @deprecated call via {@link IBlockState#withMirror(Mirror)} whenever possible. Implementing/overriding is fine.
    */
   public BlockState mirror(BlockState state, Mirror mirrorIn) {
      return state.with(FACING, mirrorIn.mirror(state.get(FACING)));
   }

   public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
      switch(state.get(FACING).getAxis()) {
      case X:
      default:
         return END_ROD_EW_AABB;
      case Z:
         return END_ROD_NS_AABB;
      case Y:
         return END_ROD_VERTICAL_AABB;
      }
   }

   public BlockState getStateForPlacement(BlockItemUseContext context) {
      Direction direction = context.getFace();
      BlockState blockstate = context.getWorld().getBlockState(context.getPos().offset(direction.getOpposite()));
      return blockstate.getBlock() == this && blockstate.get(FACING) == direction ? this.getDefaultState().with(FACING, direction.getOpposite()) : this.getDefaultState().with(FACING, direction);
   }

   /**
    * Called periodically clientside on blocks near the player to show effects (like furnace fire particles). Note that
    * this method is unrelated to {@link randomTick} and {@link #needsRandomTick}, and will always be called regardless
    * of whether the block can receive random update ticks
    */
   @OnlyIn(Dist.CLIENT)
   public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
      Direction direction = stateIn.get(FACING);
      double d0 = (double)pos.getX() + 0.55D - (double)(rand.nextFloat() * 0.1F);
      double d1 = (double)pos.getY() + 0.55D - (double)(rand.nextFloat() * 0.1F);
      double d2 = (double)pos.getZ() + 0.55D - (double)(rand.nextFloat() * 0.1F);
      double d3 = (double)(0.4F - (rand.nextFloat() + rand.nextFloat()) * 0.4F);
      if (rand.nextInt(5) == 0) {
         worldIn.addParticle(ParticleTypes.END_ROD, d0 + (double)direction.getXOffset() * d3, d1 + (double)direction.getYOffset() * d3, d2 + (double)direction.getZOffset() * d3, rand.nextGaussian() * 0.005D, rand.nextGaussian() * 0.005D, rand.nextGaussian() * 0.005D);
      }

   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
      builder.add(FACING);
   }

   /**
    * @deprecated call via {@link IBlockState#getMobilityFlag()} whenever possible. Implementing/overriding is fine.
    */
   public PushReaction getPushReaction(BlockState state) {
      return PushReaction.NORMAL;
   }
}
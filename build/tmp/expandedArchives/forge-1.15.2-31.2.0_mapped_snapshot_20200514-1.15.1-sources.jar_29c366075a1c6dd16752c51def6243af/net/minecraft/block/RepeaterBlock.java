package net.minecraft.block;

import java.util.Random;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class RepeaterBlock extends RedstoneDiodeBlock {
   public static final BooleanProperty LOCKED = BlockStateProperties.LOCKED;
   public static final IntegerProperty DELAY = BlockStateProperties.DELAY_1_4;

   protected RepeaterBlock(Block.Properties properties) {
      super(properties);
      this.setDefaultState(this.stateContainer.getBaseState().with(HORIZONTAL_FACING, Direction.NORTH).with(DELAY, Integer.valueOf(1)).with(LOCKED, Boolean.valueOf(false)).with(POWERED, Boolean.valueOf(false)));
   }

   public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
      if (!player.abilities.allowEdit) {
         return ActionResultType.PASS;
      } else {
         worldIn.setBlockState(pos, state.cycle(DELAY), 3);
         return ActionResultType.SUCCESS;
      }
   }

   protected int getDelay(BlockState state) {
      return state.get(DELAY) * 2;
   }

   public BlockState getStateForPlacement(BlockItemUseContext context) {
      BlockState blockstate = super.getStateForPlacement(context);
      return blockstate.with(LOCKED, Boolean.valueOf(this.isLocked(context.getWorld(), context.getPos(), blockstate)));
   }

   /**
    * Update the provided state given the provided neighbor facing and neighbor state, returning a new state.
    * For example, fences make their connections to the passed in state if possible, and wet concrete powder immediately
    * returns its solidified counterpart.
    * Note that this method should ideally consider only the specific face passed in.
    */
   public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
      return !worldIn.isRemote() && facing.getAxis() != stateIn.get(HORIZONTAL_FACING).getAxis() ? stateIn.with(LOCKED, Boolean.valueOf(this.isLocked(worldIn, currentPos, stateIn))) : super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
   }

   public boolean isLocked(IWorldReader worldIn, BlockPos pos, BlockState state) {
      return this.getPowerOnSides(worldIn, pos, state) > 0;
   }

   protected boolean isAlternateInput(BlockState state) {
      return isDiode(state);
   }

   /**
    * Called periodically clientside on blocks near the player to show effects (like furnace fire particles). Note that
    * this method is unrelated to {@link randomTick} and {@link #needsRandomTick}, and will always be called regardless
    * of whether the block can receive random update ticks
    */
   @OnlyIn(Dist.CLIENT)
   public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
      if (stateIn.get(POWERED)) {
         Direction direction = stateIn.get(HORIZONTAL_FACING);
         double d0 = (double)((float)pos.getX() + 0.5F) + (double)(rand.nextFloat() - 0.5F) * 0.2D;
         double d1 = (double)((float)pos.getY() + 0.4F) + (double)(rand.nextFloat() - 0.5F) * 0.2D;
         double d2 = (double)((float)pos.getZ() + 0.5F) + (double)(rand.nextFloat() - 0.5F) * 0.2D;
         float f = -5.0F;
         if (rand.nextBoolean()) {
            f = (float)(stateIn.get(DELAY) * 2 - 1);
         }

         f = f / 16.0F;
         double d3 = (double)(f * (float)direction.getXOffset());
         double d4 = (double)(f * (float)direction.getZOffset());
         worldIn.addParticle(RedstoneParticleData.REDSTONE_DUST, d0 + d3, d1, d2 + d4, 0.0D, 0.0D, 0.0D);
      }
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
      builder.add(HORIZONTAL_FACING, DELAY, LOCKED, POWERED);
   }
}
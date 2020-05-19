package net.minecraft.block;

import java.util.Random;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class FrostedIceBlock extends IceBlock {
   public static final IntegerProperty AGE = BlockStateProperties.AGE_0_3;

   public FrostedIceBlock(Block.Properties properties) {
      super(properties);
      this.setDefaultState(this.stateContainer.getBaseState().with(AGE, Integer.valueOf(0)));
   }

   public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
      if ((rand.nextInt(3) == 0 || this.shouldMelt(worldIn, pos, 4)) && worldIn.getLight(pos) > 11 - state.get(AGE) - state.getOpacity(worldIn, pos) && this.slightlyMelt(state, worldIn, pos)) {
         try (BlockPos.PooledMutable blockpos$pooledmutable = BlockPos.PooledMutable.retain()) {
            for(Direction direction : Direction.values()) {
               blockpos$pooledmutable.setPos(pos).move(direction);
               BlockState blockstate = worldIn.getBlockState(blockpos$pooledmutable);
               if (blockstate.getBlock() == this && !this.slightlyMelt(blockstate, worldIn, blockpos$pooledmutable)) {
                  worldIn.getPendingBlockTicks().scheduleTick(blockpos$pooledmutable, this, MathHelper.nextInt(rand, 20, 40));
               }
            }
         }

      } else {
         worldIn.getPendingBlockTicks().scheduleTick(pos, this, MathHelper.nextInt(rand, 20, 40));
      }
   }

   private boolean slightlyMelt(BlockState state, World worldIn, BlockPos pos) {
      int i = state.get(AGE);
      if (i < 3) {
         worldIn.setBlockState(pos, state.with(AGE, Integer.valueOf(i + 1)), 2);
         return false;
      } else {
         this.turnIntoWater(state, worldIn, pos);
         return true;
      }
   }

   public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
      if (blockIn == this && this.shouldMelt(worldIn, pos, 2)) {
         this.turnIntoWater(state, worldIn, pos);
      }

      super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);
   }

   private boolean shouldMelt(IBlockReader worldIn, BlockPos pos, int neighborsRequired) {
      int i = 0;

      try (BlockPos.PooledMutable blockpos$pooledmutable = BlockPos.PooledMutable.retain()) {
         for(Direction direction : Direction.values()) {
            blockpos$pooledmutable.setPos(pos).move(direction);
            if (worldIn.getBlockState(blockpos$pooledmutable).getBlock() == this) {
               ++i;
               if (i >= neighborsRequired) {
                  boolean flag = false;
                  return flag;
               }
            }
         }

         return true;
      }
   }

   protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
      builder.add(AGE);
   }

   public ItemStack getItem(IBlockReader worldIn, BlockPos pos, BlockState state) {
      return ItemStack.EMPTY;
   }
}
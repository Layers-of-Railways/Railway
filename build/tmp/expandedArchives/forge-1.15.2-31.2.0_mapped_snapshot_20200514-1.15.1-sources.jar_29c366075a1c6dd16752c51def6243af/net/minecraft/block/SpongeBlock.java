package net.minecraft.block;

import com.google.common.collect.Lists;
import java.util.Queue;
import net.minecraft.block.material.Material;
import net.minecraft.fluid.Fluids;
import net.minecraft.fluid.IFluidState;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SpongeBlock extends Block {
   protected SpongeBlock(Block.Properties properties) {
      super(properties);
   }

   public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
      if (oldState.getBlock() != state.getBlock()) {
         this.tryAbsorb(worldIn, pos);
      }
   }

   public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
      this.tryAbsorb(worldIn, pos);
      super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);
   }

   protected void tryAbsorb(World worldIn, BlockPos pos) {
      if (this.absorb(worldIn, pos)) {
         worldIn.setBlockState(pos, Blocks.WET_SPONGE.getDefaultState(), 2);
         worldIn.playEvent(2001, pos, Block.getStateId(Blocks.WATER.getDefaultState()));
      }

   }

   private boolean absorb(World worldIn, BlockPos pos) {
      Queue<Tuple<BlockPos, Integer>> queue = Lists.newLinkedList();
      queue.add(new Tuple<>(pos, 0));
      int i = 0;

      while(!queue.isEmpty()) {
         Tuple<BlockPos, Integer> tuple = queue.poll();
         BlockPos blockpos = tuple.getA();
         int j = tuple.getB();

         for(Direction direction : Direction.values()) {
            BlockPos blockpos1 = blockpos.offset(direction);
            BlockState blockstate = worldIn.getBlockState(blockpos1);
            IFluidState ifluidstate = worldIn.getFluidState(blockpos1);
            Material material = blockstate.getMaterial();
            if (ifluidstate.isTagged(FluidTags.WATER)) {
               if (blockstate.getBlock() instanceof IBucketPickupHandler && ((IBucketPickupHandler)blockstate.getBlock()).pickupFluid(worldIn, blockpos1, blockstate) != Fluids.EMPTY) {
                  ++i;
                  if (j < 6) {
                     queue.add(new Tuple<>(blockpos1, j + 1));
                  }
               } else if (blockstate.getBlock() instanceof FlowingFluidBlock) {
                  worldIn.setBlockState(blockpos1, Blocks.AIR.getDefaultState(), 3);
                  ++i;
                  if (j < 6) {
                     queue.add(new Tuple<>(blockpos1, j + 1));
                  }
               } else if (material == Material.OCEAN_PLANT || material == Material.SEA_GRASS) {
                  TileEntity tileentity = blockstate.getBlock().hasTileEntity() ? worldIn.getTileEntity(blockpos1) : null;
                  spawnDrops(blockstate, worldIn, blockpos1, tileentity);
                  worldIn.setBlockState(blockpos1, Blocks.AIR.getDefaultState(), 3);
                  ++i;
                  if (j < 6) {
                     queue.add(new Tuple<>(blockpos1, j + 1));
                  }
               }
            }
         }

         if (i > 64) {
            break;
         }
      }

      return i > 0;
   }
}
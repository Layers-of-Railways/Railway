package net.minecraft.fluid;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.StateContainer;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class WaterFluid extends FlowingFluid {
   public Fluid getFlowingFluid() {
      return Fluids.FLOWING_WATER;
   }

   public Fluid getStillFluid() {
      return Fluids.WATER;
   }

   public Item getFilledBucket() {
      return Items.WATER_BUCKET;
   }

   @OnlyIn(Dist.CLIENT)
   public void animateTick(World worldIn, BlockPos pos, IFluidState state, Random random) {
      if (!state.isSource() && !state.get(FALLING)) {
         if (random.nextInt(64) == 0) {
            worldIn.playSound((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, SoundEvents.BLOCK_WATER_AMBIENT, SoundCategory.BLOCKS, random.nextFloat() * 0.25F + 0.75F, random.nextFloat() + 0.5F, false);
         }
      } else if (random.nextInt(10) == 0) {
         worldIn.addParticle(ParticleTypes.UNDERWATER, (double)pos.getX() + (double)random.nextFloat(), (double)pos.getY() + (double)random.nextFloat(), (double)pos.getZ() + (double)random.nextFloat(), 0.0D, 0.0D, 0.0D);
      }

   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public IParticleData getDripParticleData() {
      return ParticleTypes.DRIPPING_WATER;
   }

   protected boolean canSourcesMultiply() {
      return true;
   }

   protected void beforeReplacingBlock(IWorld worldIn, BlockPos pos, BlockState state) {
      TileEntity tileentity = state.getBlock().hasTileEntity() ? worldIn.getTileEntity(pos) : null;
      Block.spawnDrops(state, worldIn.getWorld(), pos, tileentity);
   }

   public int getSlopeFindDistance(IWorldReader worldIn) {
      return 4;
   }

   public BlockState getBlockState(IFluidState state) {
      return Blocks.WATER.getDefaultState().with(FlowingFluidBlock.LEVEL, Integer.valueOf(getLevelFromState(state)));
   }

   public boolean isEquivalentTo(Fluid fluidIn) {
      return fluidIn == Fluids.WATER || fluidIn == Fluids.FLOWING_WATER;
   }

   public int getLevelDecreasePerBlock(IWorldReader worldIn) {
      return 1;
   }

   public int getTickRate(IWorldReader p_205569_1_) {
      return 5;
   }

   public boolean canDisplace(IFluidState p_215665_1_, IBlockReader p_215665_2_, BlockPos p_215665_3_, Fluid p_215665_4_, Direction p_215665_5_) {
      return p_215665_5_ == Direction.DOWN && !p_215665_4_.isIn(FluidTags.WATER);
   }

   protected float getExplosionResistance() {
      return 100.0F;
   }

   public static class Flowing extends WaterFluid {
      protected void fillStateContainer(StateContainer.Builder<Fluid, IFluidState> builder) {
         super.fillStateContainer(builder);
         builder.add(LEVEL_1_8);
      }

      public int getLevel(IFluidState p_207192_1_) {
         return p_207192_1_.get(LEVEL_1_8);
      }

      public boolean isSource(IFluidState state) {
         return false;
      }
   }

   public static class Source extends WaterFluid {
      public int getLevel(IFluidState p_207192_1_) {
         return 8;
      }

      public boolean isSource(IFluidState state) {
         return true;
      }
   }
}
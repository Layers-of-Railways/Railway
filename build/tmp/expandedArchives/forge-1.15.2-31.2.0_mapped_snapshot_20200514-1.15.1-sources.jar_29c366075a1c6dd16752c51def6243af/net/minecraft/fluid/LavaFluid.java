package net.minecraft.fluid;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.StateContainer;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class LavaFluid extends FlowingFluid {
   public Fluid getFlowingFluid() {
      return Fluids.FLOWING_LAVA;
   }

   public Fluid getStillFluid() {
      return Fluids.LAVA;
   }

   public Item getFilledBucket() {
      return Items.LAVA_BUCKET;
   }

   @OnlyIn(Dist.CLIENT)
   public void animateTick(World worldIn, BlockPos pos, IFluidState state, Random random) {
      BlockPos blockpos = pos.up();
      if (worldIn.getBlockState(blockpos).isAir() && !worldIn.getBlockState(blockpos).isOpaqueCube(worldIn, blockpos)) {
         if (random.nextInt(100) == 0) {
            double d0 = (double)((float)pos.getX() + random.nextFloat());
            double d1 = (double)(pos.getY() + 1);
            double d2 = (double)((float)pos.getZ() + random.nextFloat());
            worldIn.addParticle(ParticleTypes.LAVA, d0, d1, d2, 0.0D, 0.0D, 0.0D);
            worldIn.playSound(d0, d1, d2, SoundEvents.BLOCK_LAVA_POP, SoundCategory.BLOCKS, 0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false);
         }

         if (random.nextInt(200) == 0) {
            worldIn.playSound((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), SoundEvents.BLOCK_LAVA_AMBIENT, SoundCategory.BLOCKS, 0.2F + random.nextFloat() * 0.2F, 0.9F + random.nextFloat() * 0.15F, false);
         }
      }

   }

   public void randomTick(World p_207186_1_, BlockPos pos, IFluidState state, Random random) {
      if (p_207186_1_.getGameRules().getBoolean(GameRules.DO_FIRE_TICK)) {
         int i = random.nextInt(3);
         if (i > 0) {
            BlockPos blockpos = pos;

            for(int j = 0; j < i; ++j) {
               blockpos = blockpos.add(random.nextInt(3) - 1, 1, random.nextInt(3) - 1);
               if (!p_207186_1_.isBlockPresent(blockpos)) {
                  return;
               }

               BlockState blockstate = p_207186_1_.getBlockState(blockpos);
               if (blockstate.isAir()) {
                  if (this.isSurroundingBlockFlammable(p_207186_1_, blockpos)) {
                     p_207186_1_.setBlockState(blockpos, net.minecraftforge.event.ForgeEventFactory.fireFluidPlaceBlockEvent(p_207186_1_, blockpos, pos, Blocks.FIRE.getDefaultState()));
                     return;
                  }
               } else if (blockstate.getMaterial().blocksMovement()) {
                  return;
               }
            }
         } else {
            for(int k = 0; k < 3; ++k) {
               BlockPos blockpos1 = pos.add(random.nextInt(3) - 1, 0, random.nextInt(3) - 1);
               if (!p_207186_1_.isBlockPresent(blockpos1)) {
                  return;
               }

               if (p_207186_1_.isAirBlock(blockpos1.up()) && this.getCanBlockBurn(p_207186_1_, blockpos1)) {
                  p_207186_1_.setBlockState(blockpos1.up(), net.minecraftforge.event.ForgeEventFactory.fireFluidPlaceBlockEvent(p_207186_1_, blockpos1.up(), pos, Blocks.FIRE.getDefaultState()));
               }
            }
         }

      }
   }

   private boolean isSurroundingBlockFlammable(IWorldReader worldIn, BlockPos pos) {
      for(Direction direction : Direction.values()) {
         if (this.getCanBlockBurn(worldIn, pos.offset(direction))) {
            return true;
         }
      }

      return false;
   }

   private boolean getCanBlockBurn(IWorldReader worldIn, BlockPos pos) {
      return pos.getY() >= 0 && pos.getY() < 256 && !worldIn.isBlockLoaded(pos) ? false : worldIn.getBlockState(pos).getMaterial().isFlammable();
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public IParticleData getDripParticleData() {
      return ParticleTypes.DRIPPING_LAVA;
   }

   protected void beforeReplacingBlock(IWorld worldIn, BlockPos pos, BlockState state) {
      this.triggerEffects(worldIn, pos);
   }

   public int getSlopeFindDistance(IWorldReader worldIn) {
      return worldIn.getDimension().doesWaterVaporize() ? 4 : 2;
   }

   public BlockState getBlockState(IFluidState state) {
      return Blocks.LAVA.getDefaultState().with(FlowingFluidBlock.LEVEL, Integer.valueOf(getLevelFromState(state)));
   }

   public boolean isEquivalentTo(Fluid fluidIn) {
      return fluidIn == Fluids.LAVA || fluidIn == Fluids.FLOWING_LAVA;
   }

   public int getLevelDecreasePerBlock(IWorldReader worldIn) {
      return worldIn.getDimension().doesWaterVaporize() ? 1 : 2;
   }

   public boolean canDisplace(IFluidState p_215665_1_, IBlockReader p_215665_2_, BlockPos p_215665_3_, Fluid p_215665_4_, Direction p_215665_5_) {
      return p_215665_1_.getActualHeight(p_215665_2_, p_215665_3_) >= 0.44444445F && p_215665_4_.isIn(FluidTags.WATER);
   }

   public int getTickRate(IWorldReader p_205569_1_) {
      return p_205569_1_.getDimension().isNether() ? 10 : 30;
   }

   public int func_215667_a(World p_215667_1_, BlockPos p_215667_2_, IFluidState p_215667_3_, IFluidState p_215667_4_) {
      int i = this.getTickRate(p_215667_1_);
      if (!p_215667_3_.isEmpty() && !p_215667_4_.isEmpty() && !p_215667_3_.get(FALLING) && !p_215667_4_.get(FALLING) && p_215667_4_.getActualHeight(p_215667_1_, p_215667_2_) > p_215667_3_.getActualHeight(p_215667_1_, p_215667_2_) && p_215667_1_.getRandom().nextInt(4) != 0) {
         i *= 4;
      }

      return i;
   }

   private void triggerEffects(IWorld p_205581_1_, BlockPos p_205581_2_) {
      p_205581_1_.playEvent(1501, p_205581_2_, 0);
   }

   protected boolean canSourcesMultiply() {
      return false;
   }

   protected void flowInto(IWorld worldIn, BlockPos pos, BlockState blockStateIn, Direction direction, IFluidState fluidStateIn) {
      if (direction == Direction.DOWN) {
         IFluidState ifluidstate = worldIn.getFluidState(pos);
         if (this.isIn(FluidTags.LAVA) && ifluidstate.isTagged(FluidTags.WATER)) {
            if (blockStateIn.getBlock() instanceof FlowingFluidBlock) {
               worldIn.setBlockState(pos, net.minecraftforge.event.ForgeEventFactory.fireFluidPlaceBlockEvent(worldIn, pos, pos, Blocks.STONE.getDefaultState()), 3);
            }

            this.triggerEffects(worldIn, pos);
            return;
         }
      }

      super.flowInto(worldIn, pos, blockStateIn, direction, fluidStateIn);
   }

   protected boolean ticksRandomly() {
      return true;
   }

   protected float getExplosionResistance() {
      return 100.0F;
   }

   public static class Flowing extends LavaFluid {
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

   public static class Source extends LavaFluid {
      public int getLevel(IFluidState p_207192_1_) {
         return 8;
      }

      public boolean isSource(IFluidState state) {
         return true;
      }
   }
}
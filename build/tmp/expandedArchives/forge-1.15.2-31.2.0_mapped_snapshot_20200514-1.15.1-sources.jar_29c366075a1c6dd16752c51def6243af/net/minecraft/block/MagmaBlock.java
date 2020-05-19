package net.minecraft.block;

import java.util.Random;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class MagmaBlock extends Block {
   public MagmaBlock(Block.Properties properties) {
      super(properties);
   }

   /**
    * Called when the given entity walks on this Block
    */
   public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn) {
      if (!entityIn.isImmuneToFire() && entityIn instanceof LivingEntity && !EnchantmentHelper.hasFrostWalker((LivingEntity)entityIn)) {
         entityIn.attackEntityFrom(DamageSource.HOT_FLOOR, 1.0F);
      }

      super.onEntityWalk(worldIn, pos, entityIn);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isEmissiveRendering(BlockState p_225543_1_) {
      return true;
   }

   public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand) {
      BubbleColumnBlock.placeBubbleColumn(worldIn, pos.up(), true);
   }

   /**
    * Update the provided state given the provided neighbor facing and neighbor state, returning a new state.
    * For example, fences make their connections to the passed in state if possible, and wet concrete powder immediately
    * returns its solidified counterpart.
    * Note that this method should ideally consider only the specific face passed in.
    */
   public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
      if (facing == Direction.UP && facingState.getBlock() == Blocks.WATER) {
         worldIn.getPendingBlockTicks().scheduleTick(currentPos, this, this.tickRate(worldIn));
      }

      return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
   }

   /**
    * Performs a random tick on a block.
    */
   public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
      BlockPos blockpos = pos.up();
      if (worldIn.getFluidState(pos).isTagged(FluidTags.WATER)) {
         worldIn.playSound((PlayerEntity)null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (worldIn.rand.nextFloat() - worldIn.rand.nextFloat()) * 0.8F);
         worldIn.spawnParticle(ParticleTypes.LARGE_SMOKE, (double)blockpos.getX() + 0.5D, (double)blockpos.getY() + 0.25D, (double)blockpos.getZ() + 0.5D, 8, 0.5D, 0.25D, 0.5D, 0.0D);
      }

   }

   /**
    * How many world ticks before ticking
    */
   public int tickRate(IWorldReader worldIn) {
      return 20;
   }

   public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
      worldIn.getPendingBlockTicks().scheduleTick(pos, this, this.tickRate(worldIn));
   }

   public boolean canEntitySpawn(BlockState state, IBlockReader worldIn, BlockPos pos, EntityType<?> type) {
      return type.isImmuneToFire();
   }

   public boolean needsPostProcessing(BlockState state, IBlockReader worldIn, BlockPos pos) {
      return true;
   }
}
package net.minecraft.block;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.entity.item.TNTEntity;
import net.minecraft.entity.item.minecart.AbstractMinecartEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class HoneyBlock extends BreakableBlock {
   protected static final VoxelShape field_226930_a_ = Block.makeCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 15.0D, 15.0D);

   public HoneyBlock(Block.Properties properties) {
      super(properties);
   }

   private static boolean func_226937_c_(Entity p_226937_0_) {
      return p_226937_0_ instanceof LivingEntity || p_226937_0_ instanceof AbstractMinecartEntity || p_226937_0_ instanceof TNTEntity || p_226937_0_ instanceof BoatEntity;
   }

   public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
      return field_226930_a_;
   }

   /**
    * Block's chance to react to a living entity falling on it.
    */
   public void onFallenUpon(World worldIn, BlockPos pos, Entity entityIn, float fallDistance) {
      entityIn.playSound(SoundEvents.BLOCK_HONEY_BLOCK_SLIDE, 1.0F, 1.0F);
      if (!worldIn.isRemote) {
         worldIn.setEntityState(entityIn, (byte)54);
      }

      if (entityIn.onLivingFall(fallDistance, 0.2F)) {
         entityIn.playSound(this.soundType.getFallSound(), this.soundType.getVolume() * 0.5F, this.soundType.getPitch() * 0.75F);
      }

   }

   public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
      if (this.func_226935_a_(pos, entityIn)) {
         this.func_226933_a_(entityIn, pos);
         this.func_226938_d_(entityIn);
         this.func_226934_a_(worldIn, entityIn);
      }

      super.onEntityCollision(state, worldIn, pos, entityIn);
   }

   private boolean func_226935_a_(BlockPos p_226935_1_, Entity p_226935_2_) {
      if (p_226935_2_.onGround) {
         return false;
      } else if (p_226935_2_.getPosY() > (double)p_226935_1_.getY() + 0.9375D - 1.0E-7D) {
         return false;
      } else if (p_226935_2_.getMotion().y >= -0.08D) {
         return false;
      } else {
         double d0 = Math.abs((double)p_226935_1_.getX() + 0.5D - p_226935_2_.getPosX());
         double d1 = Math.abs((double)p_226935_1_.getZ() + 0.5D - p_226935_2_.getPosZ());
         double d2 = 0.4375D + (double)(p_226935_2_.getWidth() / 2.0F);
         return d0 + 1.0E-7D > d2 || d1 + 1.0E-7D > d2;
      }
   }

   private void func_226933_a_(Entity p_226933_1_, BlockPos p_226933_2_) {
      if (p_226933_1_ instanceof ServerPlayerEntity && p_226933_1_.world.getGameTime() % 20L == 0L) {
         CriteriaTriggers.field_229864_K_.func_227152_a_((ServerPlayerEntity)p_226933_1_, p_226933_1_.world.getBlockState(p_226933_2_));
      }

   }

   private void func_226938_d_(Entity p_226938_1_) {
      Vec3d vec3d = p_226938_1_.getMotion();
      if (vec3d.y < -0.13D) {
         double d0 = -0.05D / vec3d.y;
         p_226938_1_.setMotion(new Vec3d(vec3d.x * d0, -0.05D, vec3d.z * d0));
      } else {
         p_226938_1_.setMotion(new Vec3d(vec3d.x, -0.05D, vec3d.z));
      }

      p_226938_1_.fallDistance = 0.0F;
   }

   private void func_226934_a_(World p_226934_1_, Entity p_226934_2_) {
      if (func_226937_c_(p_226934_2_)) {
         if (p_226934_1_.rand.nextInt(5) == 0) {
            p_226934_2_.playSound(SoundEvents.BLOCK_HONEY_BLOCK_SLIDE, 1.0F, 1.0F);
         }

         if (!p_226934_1_.isRemote && p_226934_1_.rand.nextInt(5) == 0) {
            p_226934_1_.setEntityState(p_226934_2_, (byte)53);
         }
      }

   }

   @OnlyIn(Dist.CLIENT)
   public static void func_226931_a_(Entity p_226931_0_) {
      func_226932_a_(p_226931_0_, 5);
   }

   @OnlyIn(Dist.CLIENT)
   public static void func_226936_b_(Entity p_226936_0_) {
      func_226932_a_(p_226936_0_, 10);
   }

   @OnlyIn(Dist.CLIENT)
   private static void func_226932_a_(Entity p_226932_0_, int p_226932_1_) {
      if (p_226932_0_.world.isRemote) {
         BlockState blockstate = Blocks.HONEY_BLOCK.getDefaultState();

         for(int i = 0; i < p_226932_1_; ++i) {
            p_226932_0_.world.addParticle(new BlockParticleData(ParticleTypes.BLOCK, blockstate), p_226932_0_.getPosX(), p_226932_0_.getPosY(), p_226932_0_.getPosZ(), 0.0D, 0.0D, 0.0D);
         }

      }
   }
}
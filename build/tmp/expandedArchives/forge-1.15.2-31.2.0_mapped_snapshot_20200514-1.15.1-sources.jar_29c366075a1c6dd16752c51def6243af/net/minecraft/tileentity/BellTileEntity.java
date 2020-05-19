package net.minecraft.tileentity;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class BellTileEntity extends TileEntity implements ITickableTileEntity {
   private long ringTime;
   /** How long the bell has been ringing */
   public int ringingTicks;
   public boolean isRinging;
   public Direction ringDirection;
   private List<LivingEntity> entitiesAtRing;
   private boolean shouldReveal;
   /** Warmup counter until raiders are revealed by glow and particle */
   private int revealWarmup;

   public BellTileEntity() {
      super(TileEntityType.BELL);
   }

   /**
    * See {@link Block#eventReceived} for more information. This must return true serverside before it is called
    * clientside.
    */
   public boolean receiveClientEvent(int id, int type) {
      if (id == 1) {
         this.func_213941_c();
         this.revealWarmup = 0;
         this.ringDirection = Direction.byIndex(type);
         this.ringingTicks = 0;
         this.isRinging = true;
         return true;
      } else {
         return super.receiveClientEvent(id, type);
      }
   }

   public void tick() {
      if (this.isRinging) {
         ++this.ringingTicks;
      }

      if (this.ringingTicks >= 50) {
         this.isRinging = false;
         this.ringingTicks = 0;
      }

      if (this.ringingTicks >= 5 && this.revealWarmup == 0 && this.hasRaidersNearby()) {
         this.shouldReveal = true;
         this.resonate();
      }

      if (this.shouldReveal) {
         if (this.revealWarmup < 40) {
            ++this.revealWarmup;
         } else {
            this.glowRaiders(this.world);
            this.addRaiderParticles(this.world);
            this.shouldReveal = false;
         }
      }

   }

   private void resonate() {
      this.world.playSound((PlayerEntity)null, this.getPos(), SoundEvents.BLOCK_BELL_RESONATE, SoundCategory.BLOCKS, 1.0F, 1.0F);
   }

   public void ring(Direction p_213939_1_) {
      BlockPos blockpos = this.getPos();
      this.ringDirection = p_213939_1_;
      if (this.isRinging) {
         this.ringingTicks = 0;
      } else {
         this.isRinging = true;
      }

      this.world.addBlockEvent(blockpos, this.getBlockState().getBlock(), 1, p_213939_1_.getIndex());
   }

   private void func_213941_c() {
      BlockPos blockpos = this.getPos();
      if (this.world.getGameTime() > this.ringTime + 60L || this.entitiesAtRing == null) {
         this.ringTime = this.world.getGameTime();
         AxisAlignedBB axisalignedbb = (new AxisAlignedBB(blockpos)).grow(48.0D);
         this.entitiesAtRing = this.world.getEntitiesWithinAABB(LivingEntity.class, axisalignedbb);
      }

      if (!this.world.isRemote) {
         for(LivingEntity livingentity : this.entitiesAtRing) {
            if (livingentity.isAlive() && !livingentity.removed && blockpos.withinDistance(livingentity.getPositionVec(), 32.0D)) {
               livingentity.getBrain().setMemory(MemoryModuleType.HEARD_BELL_TIME, this.world.getGameTime());
            }
         }
      }

   }

   private boolean hasRaidersNearby() {
      BlockPos blockpos = this.getPos();

      for(LivingEntity livingentity : this.entitiesAtRing) {
         if (livingentity.isAlive() && !livingentity.removed && blockpos.withinDistance(livingentity.getPositionVec(), 32.0D) && livingentity.getType().isContained(EntityTypeTags.RAIDERS)) {
            return true;
         }
      }

      return false;
   }

   private void glowRaiders(World p_222828_1_) {
      if (!p_222828_1_.isRemote) {
         this.entitiesAtRing.stream().filter(this::isNearbyRaider).forEach(this::glow);
      }
   }

   private void addRaiderParticles(World p_222826_1_) {
      if (p_222826_1_.isRemote) {
         BlockPos blockpos = this.getPos();
         AtomicInteger atomicinteger = new AtomicInteger(16700985);
         int i = (int)this.entitiesAtRing.stream().filter((p_222829_1_) -> {
            return blockpos.withinDistance(p_222829_1_.getPositionVec(), 48.0D);
         }).count();
         this.entitiesAtRing.stream().filter(this::isNearbyRaider).forEach((p_222831_4_) -> {
            float f = 1.0F;
            float f1 = MathHelper.sqrt((p_222831_4_.getPosX() - (double)blockpos.getX()) * (p_222831_4_.getPosX() - (double)blockpos.getX()) + (p_222831_4_.getPosZ() - (double)blockpos.getZ()) * (p_222831_4_.getPosZ() - (double)blockpos.getZ()));
            double d0 = (double)((float)blockpos.getX() + 0.5F) + (double)(1.0F / f1) * (p_222831_4_.getPosX() - (double)blockpos.getX());
            double d1 = (double)((float)blockpos.getZ() + 0.5F) + (double)(1.0F / f1) * (p_222831_4_.getPosZ() - (double)blockpos.getZ());
            int j = MathHelper.clamp((i - 21) / -2, 3, 15);

            for(int k = 0; k < j; ++k) {
               atomicinteger.addAndGet(5);
               double d2 = (double)(atomicinteger.get() >> 16 & 255) / 255.0D;
               double d3 = (double)(atomicinteger.get() >> 8 & 255) / 255.0D;
               double d4 = (double)(atomicinteger.get() & 255) / 255.0D;
               p_222826_1_.addParticle(ParticleTypes.ENTITY_EFFECT, d0, (double)((float)blockpos.getY() + 0.5F), d1, d2, d3, d4);
            }

         });
      }
   }

   private boolean isNearbyRaider(LivingEntity p_222832_1_) {
      return p_222832_1_.isAlive() && !p_222832_1_.removed && this.getPos().withinDistance(p_222832_1_.getPositionVec(), 48.0D) && p_222832_1_.getType().isContained(EntityTypeTags.RAIDERS);
   }

   private void glow(LivingEntity p_222827_1_) {
      p_222827_1_.addPotionEffect(new EffectInstance(Effects.GLOWING, 60));
   }
}
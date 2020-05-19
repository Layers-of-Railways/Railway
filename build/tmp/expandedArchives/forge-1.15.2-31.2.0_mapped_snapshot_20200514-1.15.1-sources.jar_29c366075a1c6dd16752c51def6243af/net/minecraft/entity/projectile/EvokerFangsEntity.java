package net.minecraft.entity.projectile;

import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EvokerFangsEntity extends Entity {
   private int warmupDelayTicks;
   private boolean sentSpikeEvent;
   private int lifeTicks = 22;
   private boolean clientSideAttackStarted;
   private LivingEntity caster;
   private UUID casterUuid;

   public EvokerFangsEntity(EntityType<? extends EvokerFangsEntity> p_i50170_1_, World p_i50170_2_) {
      super(p_i50170_1_, p_i50170_2_);
   }

   public EvokerFangsEntity(World worldIn, double x, double y, double z, float p_i47276_8_, int p_i47276_9_, LivingEntity casterIn) {
      this(EntityType.EVOKER_FANGS, worldIn);
      this.warmupDelayTicks = p_i47276_9_;
      this.setCaster(casterIn);
      this.rotationYaw = p_i47276_8_ * (180F / (float)Math.PI);
      this.setPosition(x, y, z);
   }

   protected void registerData() {
   }

   public void setCaster(@Nullable LivingEntity p_190549_1_) {
      this.caster = p_190549_1_;
      this.casterUuid = p_190549_1_ == null ? null : p_190549_1_.getUniqueID();
   }

   @Nullable
   public LivingEntity getCaster() {
      if (this.caster == null && this.casterUuid != null && this.world instanceof ServerWorld) {
         Entity entity = ((ServerWorld)this.world).getEntityByUuid(this.casterUuid);
         if (entity instanceof LivingEntity) {
            this.caster = (LivingEntity)entity;
         }
      }

      return this.caster;
   }

   /**
    * (abstract) Protected helper method to read subclass entity data from NBT.
    */
   protected void readAdditional(CompoundNBT compound) {
      this.warmupDelayTicks = compound.getInt("Warmup");
      if (compound.hasUniqueId("OwnerUUID")) {
         this.casterUuid = compound.getUniqueId("OwnerUUID");
      }

   }

   protected void writeAdditional(CompoundNBT compound) {
      compound.putInt("Warmup", this.warmupDelayTicks);
      if (this.casterUuid != null) {
         compound.putUniqueId("OwnerUUID", this.casterUuid);
      }

   }

   /**
    * Called to update the entity's position/logic.
    */
   public void tick() {
      super.tick();
      if (this.world.isRemote) {
         if (this.clientSideAttackStarted) {
            --this.lifeTicks;
            if (this.lifeTicks == 14) {
               for(int i = 0; i < 12; ++i) {
                  double d0 = this.getPosX() + (this.rand.nextDouble() * 2.0D - 1.0D) * (double)this.getWidth() * 0.5D;
                  double d1 = this.getPosY() + 0.05D + this.rand.nextDouble();
                  double d2 = this.getPosZ() + (this.rand.nextDouble() * 2.0D - 1.0D) * (double)this.getWidth() * 0.5D;
                  double d3 = (this.rand.nextDouble() * 2.0D - 1.0D) * 0.3D;
                  double d4 = 0.3D + this.rand.nextDouble() * 0.3D;
                  double d5 = (this.rand.nextDouble() * 2.0D - 1.0D) * 0.3D;
                  this.world.addParticle(ParticleTypes.CRIT, d0, d1 + 1.0D, d2, d3, d4, d5);
               }
            }
         }
      } else if (--this.warmupDelayTicks < 0) {
         if (this.warmupDelayTicks == -8) {
            for(LivingEntity livingentity : this.world.getEntitiesWithinAABB(LivingEntity.class, this.getBoundingBox().grow(0.2D, 0.0D, 0.2D))) {
               this.damage(livingentity);
            }
         }

         if (!this.sentSpikeEvent) {
            this.world.setEntityState(this, (byte)4);
            this.sentSpikeEvent = true;
         }

         if (--this.lifeTicks < 0) {
            this.remove();
         }
      }

   }

   private void damage(LivingEntity p_190551_1_) {
      LivingEntity livingentity = this.getCaster();
      if (p_190551_1_.isAlive() && !p_190551_1_.isInvulnerable() && p_190551_1_ != livingentity) {
         if (livingentity == null) {
            p_190551_1_.attackEntityFrom(DamageSource.MAGIC, 6.0F);
         } else {
            if (livingentity.isOnSameTeam(p_190551_1_)) {
               return;
            }

            p_190551_1_.attackEntityFrom(DamageSource.causeIndirectMagicDamage(this, livingentity), 6.0F);
         }

      }
   }

   /**
    * Handler for {@link World#setEntityState}
    */
   @OnlyIn(Dist.CLIENT)
   public void handleStatusUpdate(byte id) {
      super.handleStatusUpdate(id);
      if (id == 4) {
         this.clientSideAttackStarted = true;
         if (!this.isSilent()) {
            this.world.playSound(this.getPosX(), this.getPosY(), this.getPosZ(), SoundEvents.ENTITY_EVOKER_FANGS_ATTACK, this.getSoundCategory(), 1.0F, this.rand.nextFloat() * 0.2F + 0.85F, false);
         }
      }

   }

   @OnlyIn(Dist.CLIENT)
   public float getAnimationProgress(float partialTicks) {
      if (!this.clientSideAttackStarted) {
         return 0.0F;
      } else {
         int i = this.lifeTicks - 2;
         return i <= 0 ? 1.0F : 1.0F - ((float)i - partialTicks) / 20.0F;
      }
   }

   public IPacket<?> createSpawnPacket() {
      return new SSpawnObjectPacket(this);
   }
}
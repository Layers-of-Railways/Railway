package net.minecraft.entity.boss.dragon;

import com.google.common.collect.Lists;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.boss.dragon.phase.IPhase;
import net.minecraft.entity.boss.dragon.phase.PhaseManager;
import net.minecraft.entity.boss.dragon.phase.PhaseType;
import net.minecraft.entity.item.EnderCrystalEntity;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathHeap;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.potion.EffectInstance;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.dimension.EndDimension;
import net.minecraft.world.end.DragonFightManager;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.EndPodiumFeature;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EnderDragonEntity extends MobEntity implements IMob {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final DataParameter<Integer> PHASE = EntityDataManager.createKey(EnderDragonEntity.class, DataSerializers.VARINT);
   private static final EntityPredicate field_213405_bO = (new EntityPredicate()).setDistance(64.0D);
   public final double[][] ringBuffer = new double[64][3];
   public int ringBufferIndex = -1;
   private final EnderDragonPartEntity[] dragonParts;
   public final EnderDragonPartEntity dragonPartHead;
   private final EnderDragonPartEntity dragonPartNeck;
   private final EnderDragonPartEntity dragonPartBody;
   private final EnderDragonPartEntity dragonPartTail1;
   private final EnderDragonPartEntity dragonPartTail2;
   private final EnderDragonPartEntity dragonPartTail3;
   private final EnderDragonPartEntity dragonPartRightWing;
   private final EnderDragonPartEntity dragonPartLeftWing;
   public float prevAnimTime;
   public float animTime;
   public boolean slowed;
   public int deathTicks;
   public float field_226525_bB_;
   @Nullable
   public EnderCrystalEntity closestEnderCrystal;
   @Nullable
   private final DragonFightManager fightManager;
   private final PhaseManager phaseManager;
   private int growlTime = 100;
   private int sittingDamageReceived;
   private final PathPoint[] pathPoints = new PathPoint[24];
   private final int[] neighbors = new int[24];
   private final PathHeap pathFindQueue = new PathHeap();

   public EnderDragonEntity(EntityType<? extends EnderDragonEntity> type, World worldIn) {
      super(EntityType.ENDER_DRAGON, worldIn);
      this.dragonPartHead = new EnderDragonPartEntity(this, "head", 1.0F, 1.0F);
      this.dragonPartNeck = new EnderDragonPartEntity(this, "neck", 3.0F, 3.0F);
      this.dragonPartBody = new EnderDragonPartEntity(this, "body", 5.0F, 3.0F);
      this.dragonPartTail1 = new EnderDragonPartEntity(this, "tail", 2.0F, 2.0F);
      this.dragonPartTail2 = new EnderDragonPartEntity(this, "tail", 2.0F, 2.0F);
      this.dragonPartTail3 = new EnderDragonPartEntity(this, "tail", 2.0F, 2.0F);
      this.dragonPartRightWing = new EnderDragonPartEntity(this, "wing", 4.0F, 2.0F);
      this.dragonPartLeftWing = new EnderDragonPartEntity(this, "wing", 4.0F, 2.0F);
      this.dragonParts = new EnderDragonPartEntity[]{this.dragonPartHead, this.dragonPartNeck, this.dragonPartBody, this.dragonPartTail1, this.dragonPartTail2, this.dragonPartTail3, this.dragonPartRightWing, this.dragonPartLeftWing};
      this.setHealth(this.getMaxHealth());
      this.noClip = true;
      this.ignoreFrustumCheck = true;
      if (!worldIn.isRemote && worldIn.dimension instanceof EndDimension) {
         this.fightManager = ((EndDimension)worldIn.dimension).getDragonFightManager();
      } else {
         this.fightManager = null;
      }

      this.phaseManager = new PhaseManager(this);
   }

   protected void registerAttributes() {
      super.registerAttributes();
      this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(200.0D);
   }

   protected void registerData() {
      super.registerData();
      this.getDataManager().register(PHASE, PhaseType.HOVER.getId());
   }

   /**
    * Returns a double[3] array with movement offsets, used to calculate trailing tail/neck positions. [0] = yaw offset,
    * [1] = y offset, [2] = unused, always 0. Parameters: buffer index offset, partial ticks.
    */
   public double[] getMovementOffsets(int p_70974_1_, float p_70974_2_) {
      if (this.getHealth() <= 0.0F) {
         p_70974_2_ = 0.0F;
      }

      p_70974_2_ = 1.0F - p_70974_2_;
      int i = this.ringBufferIndex - p_70974_1_ & 63;
      int j = this.ringBufferIndex - p_70974_1_ - 1 & 63;
      double[] adouble = new double[3];
      double d0 = this.ringBuffer[i][0];
      double d1 = MathHelper.wrapDegrees(this.ringBuffer[j][0] - d0);
      adouble[0] = d0 + d1 * (double)p_70974_2_;
      d0 = this.ringBuffer[i][1];
      d1 = this.ringBuffer[j][1] - d0;
      adouble[1] = d0 + d1 * (double)p_70974_2_;
      adouble[2] = MathHelper.lerp((double)p_70974_2_, this.ringBuffer[i][2], this.ringBuffer[j][2]);
      return adouble;
   }

   /**
    * Called frequently so the entity can update its state every tick as required. For example, zombies and skeletons
    * use this to react to sunlight and start to burn.
    */
   public void livingTick() {
      if (this.world.isRemote) {
         this.setHealth(this.getHealth());
         if (!this.isSilent()) {
            float f = MathHelper.cos(this.animTime * ((float)Math.PI * 2F));
            float f1 = MathHelper.cos(this.prevAnimTime * ((float)Math.PI * 2F));
            if (f1 <= -0.3F && f >= -0.3F) {
               this.world.playSound(this.getPosX(), this.getPosY(), this.getPosZ(), SoundEvents.ENTITY_ENDER_DRAGON_FLAP, this.getSoundCategory(), 5.0F, 0.8F + this.rand.nextFloat() * 0.3F, false);
            }

            if (!this.phaseManager.getCurrentPhase().getIsStationary() && --this.growlTime < 0) {
               this.world.playSound(this.getPosX(), this.getPosY(), this.getPosZ(), SoundEvents.ENTITY_ENDER_DRAGON_GROWL, this.getSoundCategory(), 2.5F, 0.8F + this.rand.nextFloat() * 0.3F, false);
               this.growlTime = 200 + this.rand.nextInt(200);
            }
         }
      }

      this.prevAnimTime = this.animTime;
      if (this.getHealth() <= 0.0F) {
         float f11 = (this.rand.nextFloat() - 0.5F) * 8.0F;
         float f13 = (this.rand.nextFloat() - 0.5F) * 4.0F;
         float f14 = (this.rand.nextFloat() - 0.5F) * 8.0F;
         this.world.addParticle(ParticleTypes.EXPLOSION, this.getPosX() + (double)f11, this.getPosY() + 2.0D + (double)f13, this.getPosZ() + (double)f14, 0.0D, 0.0D, 0.0D);
      } else {
         this.updateDragonEnderCrystal();
         Vec3d vec3d4 = this.getMotion();
         float f12 = 0.2F / (MathHelper.sqrt(horizontalMag(vec3d4)) * 10.0F + 1.0F);
         f12 = f12 * (float)Math.pow(2.0D, vec3d4.y);
         if (this.phaseManager.getCurrentPhase().getIsStationary()) {
            this.animTime += 0.1F;
         } else if (this.slowed) {
            this.animTime += f12 * 0.5F;
         } else {
            this.animTime += f12;
         }

         this.rotationYaw = MathHelper.wrapDegrees(this.rotationYaw);
         if (this.isAIDisabled()) {
            this.animTime = 0.5F;
         } else {
            if (this.ringBufferIndex < 0) {
               for(int i = 0; i < this.ringBuffer.length; ++i) {
                  this.ringBuffer[i][0] = (double)this.rotationYaw;
                  this.ringBuffer[i][1] = this.getPosY();
               }
            }

            if (++this.ringBufferIndex == this.ringBuffer.length) {
               this.ringBufferIndex = 0;
            }

            this.ringBuffer[this.ringBufferIndex][0] = (double)this.rotationYaw;
            this.ringBuffer[this.ringBufferIndex][1] = this.getPosY();
            if (this.world.isRemote) {
               if (this.newPosRotationIncrements > 0) {
                  double d7 = this.getPosX() + (this.interpTargetX - this.getPosX()) / (double)this.newPosRotationIncrements;
                  double d0 = this.getPosY() + (this.interpTargetY - this.getPosY()) / (double)this.newPosRotationIncrements;
                  double d1 = this.getPosZ() + (this.interpTargetZ - this.getPosZ()) / (double)this.newPosRotationIncrements;
                  double d2 = MathHelper.wrapDegrees(this.interpTargetYaw - (double)this.rotationYaw);
                  this.rotationYaw = (float)((double)this.rotationYaw + d2 / (double)this.newPosRotationIncrements);
                  this.rotationPitch = (float)((double)this.rotationPitch + (this.interpTargetPitch - (double)this.rotationPitch) / (double)this.newPosRotationIncrements);
                  --this.newPosRotationIncrements;
                  this.setPosition(d7, d0, d1);
                  this.setRotation(this.rotationYaw, this.rotationPitch);
               }

               this.phaseManager.getCurrentPhase().clientTick();
            } else {
               IPhase iphase = this.phaseManager.getCurrentPhase();
               iphase.serverTick();
               if (this.phaseManager.getCurrentPhase() != iphase) {
                  iphase = this.phaseManager.getCurrentPhase();
                  iphase.serverTick();
               }

               Vec3d vec3d = iphase.getTargetLocation();
               if (vec3d != null) {
                  double d8 = vec3d.x - this.getPosX();
                  double d9 = vec3d.y - this.getPosY();
                  double d10 = vec3d.z - this.getPosZ();
                  double d3 = d8 * d8 + d9 * d9 + d10 * d10;
                  float f6 = iphase.getMaxRiseOrFall();
                  double d4 = (double)MathHelper.sqrt(d8 * d8 + d10 * d10);
                  if (d4 > 0.0D) {
                     d9 = MathHelper.clamp(d9 / d4, (double)(-f6), (double)f6);
                  }

                  this.setMotion(this.getMotion().add(0.0D, d9 * 0.01D, 0.0D));
                  this.rotationYaw = MathHelper.wrapDegrees(this.rotationYaw);
                  double d5 = MathHelper.clamp(MathHelper.wrapDegrees(180.0D - MathHelper.atan2(d8, d10) * (double)(180F / (float)Math.PI) - (double)this.rotationYaw), -50.0D, 50.0D);
                  Vec3d vec3d1 = vec3d.subtract(this.getPosX(), this.getPosY(), this.getPosZ()).normalize();
                  Vec3d vec3d2 = (new Vec3d((double)MathHelper.sin(this.rotationYaw * ((float)Math.PI / 180F)), this.getMotion().y, (double)(-MathHelper.cos(this.rotationYaw * ((float)Math.PI / 180F))))).normalize();
                  float f8 = Math.max(((float)vec3d2.dotProduct(vec3d1) + 0.5F) / 1.5F, 0.0F);
                  this.field_226525_bB_ *= 0.8F;
                  this.field_226525_bB_ = (float)((double)this.field_226525_bB_ + d5 * (double)iphase.getYawFactor());
                  this.rotationYaw += this.field_226525_bB_ * 0.1F;
                  float f9 = (float)(2.0D / (d3 + 1.0D));
                  float f10 = 0.06F;
                  this.moveRelative(0.06F * (f8 * f9 + (1.0F - f9)), new Vec3d(0.0D, 0.0D, -1.0D));
                  if (this.slowed) {
                     this.move(MoverType.SELF, this.getMotion().scale((double)0.8F));
                  } else {
                     this.move(MoverType.SELF, this.getMotion());
                  }

                  Vec3d vec3d3 = this.getMotion().normalize();
                  double d6 = 0.8D + 0.15D * (vec3d3.dotProduct(vec3d2) + 1.0D) / 2.0D;
                  this.setMotion(this.getMotion().mul(d6, (double)0.91F, d6));
               }
            }

            this.renderYawOffset = this.rotationYaw;
            Vec3d[] avec3d = new Vec3d[this.dragonParts.length];

            for(int j = 0; j < this.dragonParts.length; ++j) {
               avec3d[j] = new Vec3d(this.dragonParts[j].getPosX(), this.dragonParts[j].getPosY(), this.dragonParts[j].getPosZ());
            }

            float f15 = (float)(this.getMovementOffsets(5, 1.0F)[1] - this.getMovementOffsets(10, 1.0F)[1]) * 10.0F * ((float)Math.PI / 180F);
            float f16 = MathHelper.cos(f15);
            float f2 = MathHelper.sin(f15);
            float f17 = this.rotationYaw * ((float)Math.PI / 180F);
            float f3 = MathHelper.sin(f17);
            float f18 = MathHelper.cos(f17);
            this.func_226526_a_(this.dragonPartBody, (double)(f3 * 0.5F), 0.0D, (double)(-f18 * 0.5F));
            this.func_226526_a_(this.dragonPartRightWing, (double)(f18 * 4.5F), 2.0D, (double)(f3 * 4.5F));
            this.func_226526_a_(this.dragonPartLeftWing, (double)(f18 * -4.5F), 2.0D, (double)(f3 * -4.5F));
            if (!this.world.isRemote && this.hurtTime == 0) {
               this.collideWithEntities(this.world.getEntitiesInAABBexcluding(this, this.dragonPartRightWing.getBoundingBox().grow(4.0D, 2.0D, 4.0D).offset(0.0D, -2.0D, 0.0D), EntityPredicates.CAN_AI_TARGET));
               this.collideWithEntities(this.world.getEntitiesInAABBexcluding(this, this.dragonPartLeftWing.getBoundingBox().grow(4.0D, 2.0D, 4.0D).offset(0.0D, -2.0D, 0.0D), EntityPredicates.CAN_AI_TARGET));
               this.attackEntitiesInList(this.world.getEntitiesInAABBexcluding(this, this.dragonPartHead.getBoundingBox().grow(1.0D), EntityPredicates.CAN_AI_TARGET));
               this.attackEntitiesInList(this.world.getEntitiesInAABBexcluding(this, this.dragonPartNeck.getBoundingBox().grow(1.0D), EntityPredicates.CAN_AI_TARGET));
            }

            float f4 = MathHelper.sin(this.rotationYaw * ((float)Math.PI / 180F) - this.field_226525_bB_ * 0.01F);
            float f19 = MathHelper.cos(this.rotationYaw * ((float)Math.PI / 180F) - this.field_226525_bB_ * 0.01F);
            float f5 = this.func_226527_er_();
            this.func_226526_a_(this.dragonPartHead, (double)(f4 * 6.5F * f16), (double)(f5 + f2 * 6.5F), (double)(-f19 * 6.5F * f16));
            this.func_226526_a_(this.dragonPartNeck, (double)(f4 * 5.5F * f16), (double)(f5 + f2 * 5.5F), (double)(-f19 * 5.5F * f16));
            double[] adouble = this.getMovementOffsets(5, 1.0F);

            for(int k = 0; k < 3; ++k) {
               EnderDragonPartEntity enderdragonpartentity = null;
               if (k == 0) {
                  enderdragonpartentity = this.dragonPartTail1;
               }

               if (k == 1) {
                  enderdragonpartentity = this.dragonPartTail2;
               }

               if (k == 2) {
                  enderdragonpartentity = this.dragonPartTail3;
               }

               double[] adouble1 = this.getMovementOffsets(12 + k * 2, 1.0F);
               float f7 = this.rotationYaw * ((float)Math.PI / 180F) + this.simplifyAngle(adouble1[0] - adouble[0]) * ((float)Math.PI / 180F);
               float f20 = MathHelper.sin(f7);
               float f21 = MathHelper.cos(f7);
               float f22 = 1.5F;
               float f23 = (float)(k + 1) * 2.0F;
               this.func_226526_a_(enderdragonpartentity, (double)(-(f3 * 1.5F + f20 * f23) * f16), adouble1[1] - adouble[1] - (double)((f23 + 1.5F) * f2) + 1.5D, (double)((f18 * 1.5F + f21 * f23) * f16));
            }

            if (!this.world.isRemote) {
               this.slowed = this.destroyBlocksInAABB(this.dragonPartHead.getBoundingBox()) | this.destroyBlocksInAABB(this.dragonPartNeck.getBoundingBox()) | this.destroyBlocksInAABB(this.dragonPartBody.getBoundingBox());
               if (this.fightManager != null) {
                  this.fightManager.dragonUpdate(this);
               }
            }

            for(int l = 0; l < this.dragonParts.length; ++l) {
               this.dragonParts[l].prevPosX = avec3d[l].x;
               this.dragonParts[l].prevPosY = avec3d[l].y;
               this.dragonParts[l].prevPosZ = avec3d[l].z;
               this.dragonParts[l].lastTickPosX = avec3d[l].x;
               this.dragonParts[l].lastTickPosY = avec3d[l].y;
               this.dragonParts[l].lastTickPosZ = avec3d[l].z;
            }

         }
      }
   }

   private void func_226526_a_(EnderDragonPartEntity p_226526_1_, double p_226526_2_, double p_226526_4_, double p_226526_6_) {
      p_226526_1_.setPosition(this.getPosX() + p_226526_2_, this.getPosY() + p_226526_4_, this.getPosZ() + p_226526_6_);
   }

   private float func_226527_er_() {
      if (this.phaseManager.getCurrentPhase().getIsStationary()) {
         return -1.0F;
      } else {
         double[] adouble = this.getMovementOffsets(5, 1.0F);
         double[] adouble1 = this.getMovementOffsets(0, 1.0F);
         return (float)(adouble[1] - adouble1[1]);
      }
   }

   /**
    * Updates the state of the enderdragon's current endercrystal.
    */
   private void updateDragonEnderCrystal() {
      if (this.closestEnderCrystal != null) {
         if (this.closestEnderCrystal.removed) {
            this.closestEnderCrystal = null;
         } else if (this.ticksExisted % 10 == 0 && this.getHealth() < this.getMaxHealth()) {
            this.setHealth(this.getHealth() + 1.0F);
         }
      }

      if (this.rand.nextInt(10) == 0) {
         List<EnderCrystalEntity> list = this.world.getEntitiesWithinAABB(EnderCrystalEntity.class, this.getBoundingBox().grow(32.0D));
         EnderCrystalEntity endercrystalentity = null;
         double d0 = Double.MAX_VALUE;

         for(EnderCrystalEntity endercrystalentity1 : list) {
            double d1 = endercrystalentity1.getDistanceSq(this);
            if (d1 < d0) {
               d0 = d1;
               endercrystalentity = endercrystalentity1;
            }
         }

         this.closestEnderCrystal = endercrystalentity;
      }

   }

   /**
    * Pushes all entities inside the list away from the enderdragon.
    */
   private void collideWithEntities(List<Entity> p_70970_1_) {
      double d0 = (this.dragonPartBody.getBoundingBox().minX + this.dragonPartBody.getBoundingBox().maxX) / 2.0D;
      double d1 = (this.dragonPartBody.getBoundingBox().minZ + this.dragonPartBody.getBoundingBox().maxZ) / 2.0D;

      for(Entity entity : p_70970_1_) {
         if (entity instanceof LivingEntity) {
            double d2 = entity.getPosX() - d0;
            double d3 = entity.getPosZ() - d1;
            double d4 = d2 * d2 + d3 * d3;
            entity.addVelocity(d2 / d4 * 4.0D, (double)0.2F, d3 / d4 * 4.0D);
            if (!this.phaseManager.getCurrentPhase().getIsStationary() && ((LivingEntity)entity).getRevengeTimer() < entity.ticksExisted - 2) {
               entity.attackEntityFrom(DamageSource.causeMobDamage(this), 5.0F);
               this.applyEnchantments(this, entity);
            }
         }
      }

   }

   /**
    * Attacks all entities inside this list, dealing 5 hearts of damage.
    */
   private void attackEntitiesInList(List<Entity> p_70971_1_) {
      for(Entity entity : p_70971_1_) {
         if (entity instanceof LivingEntity) {
            entity.attackEntityFrom(DamageSource.causeMobDamage(this), 10.0F);
            this.applyEnchantments(this, entity);
         }
      }

   }

   /**
    * Simplifies the value of a number by adding/subtracting 180 to the point that the number is between -180 and 180.
    */
   private float simplifyAngle(double p_70973_1_) {
      return (float)MathHelper.wrapDegrees(p_70973_1_);
   }

   /**
    * Destroys all blocks that aren't associated with 'The End' inside the given bounding box.
    */
   private boolean destroyBlocksInAABB(AxisAlignedBB p_70972_1_) {
      int i = MathHelper.floor(p_70972_1_.minX);
      int j = MathHelper.floor(p_70972_1_.minY);
      int k = MathHelper.floor(p_70972_1_.minZ);
      int l = MathHelper.floor(p_70972_1_.maxX);
      int i1 = MathHelper.floor(p_70972_1_.maxY);
      int j1 = MathHelper.floor(p_70972_1_.maxZ);
      boolean flag = false;
      boolean flag1 = false;

      for(int k1 = i; k1 <= l; ++k1) {
         for(int l1 = j; l1 <= i1; ++l1) {
            for(int i2 = k; i2 <= j1; ++i2) {
               BlockPos blockpos = new BlockPos(k1, l1, i2);
               BlockState blockstate = this.world.getBlockState(blockpos);
               Block block = blockstate.getBlock();
               if (!blockstate.isAir(this.world, blockpos) && blockstate.getMaterial() != Material.FIRE) {
                  if (net.minecraftforge.common.ForgeHooks.canEntityDestroy(this.world, blockpos, this) && !BlockTags.DRAGON_IMMUNE.contains(block)) {
                     flag1 = this.world.removeBlock(blockpos, false) || flag1;
                  } else {
                     flag = true;
                  }
               }
            }
         }
      }

      if (flag1) {
         BlockPos blockpos1 = new BlockPos(i + this.rand.nextInt(l - i + 1), j + this.rand.nextInt(i1 - j + 1), k + this.rand.nextInt(j1 - k + 1));
         this.world.playEvent(2008, blockpos1, 0);
      }

      return flag;
   }

   public boolean func_213403_a(EnderDragonPartEntity p_213403_1_, DamageSource p_213403_2_, float p_213403_3_) {
      if (this.phaseManager.getCurrentPhase().getType() == PhaseType.DYING) {
         return false;
      } else {
         p_213403_3_ = this.phaseManager.getCurrentPhase().func_221113_a(p_213403_2_, p_213403_3_);
         if (p_213403_1_ != this.dragonPartHead) {
            p_213403_3_ = p_213403_3_ / 4.0F + Math.min(p_213403_3_, 1.0F);
         }

         if (p_213403_3_ < 0.01F) {
            return false;
         } else {
            if (p_213403_2_.getTrueSource() instanceof PlayerEntity || p_213403_2_.isExplosion()) {
               float f = this.getHealth();
               this.attackDragonFrom(p_213403_2_, p_213403_3_);
               if (this.getHealth() <= 0.0F && !this.phaseManager.getCurrentPhase().getIsStationary()) {
                  this.setHealth(1.0F);
                  this.phaseManager.setPhase(PhaseType.DYING);
               }

               if (this.phaseManager.getCurrentPhase().getIsStationary()) {
                  this.sittingDamageReceived = (int)((float)this.sittingDamageReceived + (f - this.getHealth()));
                  if ((float)this.sittingDamageReceived > 0.25F * this.getMaxHealth()) {
                     this.sittingDamageReceived = 0;
                     this.phaseManager.setPhase(PhaseType.TAKEOFF);
                  }
               }
            }

            return true;
         }
      }
   }

   /**
    * Called when the entity is attacked.
    */
   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (source instanceof EntityDamageSource && ((EntityDamageSource)source).getIsThornsDamage()) {
         this.func_213403_a(this.dragonPartBody, source, amount);
      }

      return false;
   }

   /**
    * Provides a way to cause damage to an ender dragon.
    */
   protected boolean attackDragonFrom(DamageSource source, float amount) {
      return super.attackEntityFrom(source, amount);
   }

   /**
    * Called by the /kill command.
    */
   public void onKillCommand() {
      this.remove();
      if (this.fightManager != null) {
         this.fightManager.dragonUpdate(this);
         this.fightManager.processDragonDeath(this);
      }

   }

   /**
    * handles entity death timer, experience orb and particle creation
    */
   protected void onDeathUpdate() {
      if (this.fightManager != null) {
         this.fightManager.dragonUpdate(this);
      }

      ++this.deathTicks;
      if (this.deathTicks >= 180 && this.deathTicks <= 200) {
         float f = (this.rand.nextFloat() - 0.5F) * 8.0F;
         float f1 = (this.rand.nextFloat() - 0.5F) * 4.0F;
         float f2 = (this.rand.nextFloat() - 0.5F) * 8.0F;
         this.world.addParticle(ParticleTypes.EXPLOSION_EMITTER, this.getPosX() + (double)f, this.getPosY() + 2.0D + (double)f1, this.getPosZ() + (double)f2, 0.0D, 0.0D, 0.0D);
      }

      boolean flag = this.world.getGameRules().getBoolean(GameRules.DO_MOB_LOOT);
      int i = 500;
      if (this.fightManager != null && !this.fightManager.hasPreviouslyKilledDragon()) {
         i = 12000;
      }

      if (!this.world.isRemote) {
         if (this.deathTicks > 150 && this.deathTicks % 5 == 0 && flag) {
            this.dropExperience(MathHelper.floor((float)i * 0.08F));
         }

         if (this.deathTicks == 1) {
            this.world.playBroadcastSound(1028, new BlockPos(this), 0);
         }
      }

      this.move(MoverType.SELF, new Vec3d(0.0D, (double)0.1F, 0.0D));
      this.rotationYaw += 20.0F;
      this.renderYawOffset = this.rotationYaw;
      if (this.deathTicks == 200 && !this.world.isRemote) {
         if (flag) {
            this.dropExperience(MathHelper.floor((float)i * 0.2F));
         }

         if (this.fightManager != null) {
            this.fightManager.processDragonDeath(this);
         }

         this.remove();
      }

   }

   private void dropExperience(int p_184668_1_) {
      while(p_184668_1_ > 0) {
         int i = ExperienceOrbEntity.getXPSplit(p_184668_1_);
         p_184668_1_ -= i;
         this.world.addEntity(new ExperienceOrbEntity(this.world, this.getPosX(), this.getPosY(), this.getPosZ(), i));
      }

   }

   /**
    * Generates values for the fields pathPoints, and neighbors, and then returns the nearest pathPoint to the specified
    * position.
    */
   public int initPathPoints() {
      if (this.pathPoints[0] == null) {
         for(int i = 0; i < 24; ++i) {
            int j = 5;
            int l;
            int i1;
            if (i < 12) {
               l = MathHelper.floor(60.0F * MathHelper.cos(2.0F * (-(float)Math.PI + 0.2617994F * (float)i)));
               i1 = MathHelper.floor(60.0F * MathHelper.sin(2.0F * (-(float)Math.PI + 0.2617994F * (float)i)));
            } else if (i < 20) {
               int lvt_3_1_ = i - 12;
               l = MathHelper.floor(40.0F * MathHelper.cos(2.0F * (-(float)Math.PI + ((float)Math.PI / 8F) * (float)lvt_3_1_)));
               i1 = MathHelper.floor(40.0F * MathHelper.sin(2.0F * (-(float)Math.PI + ((float)Math.PI / 8F) * (float)lvt_3_1_)));
               j += 10;
            } else {
               int k1 = i - 20;
               l = MathHelper.floor(20.0F * MathHelper.cos(2.0F * (-(float)Math.PI + ((float)Math.PI / 4F) * (float)k1)));
               i1 = MathHelper.floor(20.0F * MathHelper.sin(2.0F * (-(float)Math.PI + ((float)Math.PI / 4F) * (float)k1)));
            }

            int j1 = Math.max(this.world.getSeaLevel() + 10, this.world.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, new BlockPos(l, 0, i1)).getY() + j);
            this.pathPoints[i] = new PathPoint(l, j1, i1);
         }

         this.neighbors[0] = 6146;
         this.neighbors[1] = 8197;
         this.neighbors[2] = 8202;
         this.neighbors[3] = 16404;
         this.neighbors[4] = 32808;
         this.neighbors[5] = 32848;
         this.neighbors[6] = 65696;
         this.neighbors[7] = 131392;
         this.neighbors[8] = 131712;
         this.neighbors[9] = 263424;
         this.neighbors[10] = 526848;
         this.neighbors[11] = 525313;
         this.neighbors[12] = 1581057;
         this.neighbors[13] = 3166214;
         this.neighbors[14] = 2138120;
         this.neighbors[15] = 6373424;
         this.neighbors[16] = 4358208;
         this.neighbors[17] = 12910976;
         this.neighbors[18] = 9044480;
         this.neighbors[19] = 9706496;
         this.neighbors[20] = 15216640;
         this.neighbors[21] = 13688832;
         this.neighbors[22] = 11763712;
         this.neighbors[23] = 8257536;
      }

      return this.getNearestPpIdx(this.getPosX(), this.getPosY(), this.getPosZ());
   }

   /**
    * Returns the index into pathPoints of the nearest PathPoint.
    */
   public int getNearestPpIdx(double x, double y, double z) {
      float f = 10000.0F;
      int i = 0;
      PathPoint pathpoint = new PathPoint(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z));
      int j = 0;
      if (this.fightManager == null || this.fightManager.getNumAliveCrystals() == 0) {
         j = 12;
      }

      for(int k = j; k < 24; ++k) {
         if (this.pathPoints[k] != null) {
            float f1 = this.pathPoints[k].distanceToSquared(pathpoint);
            if (f1 < f) {
               f = f1;
               i = k;
            }
         }
      }

      return i;
   }

   /**
    * Find and return a path among the circles described by pathPoints, or null if the shortest path would just be
    * directly between the start and finish with no intermediate points.
    *  
    * Starting with pathPoint[startIdx], it searches the neighboring points (and their neighboring points, and so on)
    * until it reaches pathPoint[finishIdx], at which point it calls makePath to seal the deal.
    */
   @Nullable
   public Path findPath(int startIdx, int finishIdx, @Nullable PathPoint andThen) {
      for(int i = 0; i < 24; ++i) {
         PathPoint pathpoint = this.pathPoints[i];
         pathpoint.visited = false;
         pathpoint.distanceToTarget = 0.0F;
         pathpoint.totalPathDistance = 0.0F;
         pathpoint.distanceToNext = 0.0F;
         pathpoint.previous = null;
         pathpoint.index = -1;
      }

      PathPoint pathpoint4 = this.pathPoints[startIdx];
      PathPoint pathpoint5 = this.pathPoints[finishIdx];
      pathpoint4.totalPathDistance = 0.0F;
      pathpoint4.distanceToNext = pathpoint4.distanceTo(pathpoint5);
      pathpoint4.distanceToTarget = pathpoint4.distanceToNext;
      this.pathFindQueue.clearPath();
      this.pathFindQueue.addPoint(pathpoint4);
      PathPoint pathpoint1 = pathpoint4;
      int j = 0;
      if (this.fightManager == null || this.fightManager.getNumAliveCrystals() == 0) {
         j = 12;
      }

      while(!this.pathFindQueue.isPathEmpty()) {
         PathPoint pathpoint2 = this.pathFindQueue.dequeue();
         if (pathpoint2.equals(pathpoint5)) {
            if (andThen != null) {
               andThen.previous = pathpoint5;
               pathpoint5 = andThen;
            }

            return this.makePath(pathpoint4, pathpoint5);
         }

         if (pathpoint2.distanceTo(pathpoint5) < pathpoint1.distanceTo(pathpoint5)) {
            pathpoint1 = pathpoint2;
         }

         pathpoint2.visited = true;
         int k = 0;

         for(int l = 0; l < 24; ++l) {
            if (this.pathPoints[l] == pathpoint2) {
               k = l;
               break;
            }
         }

         for(int i1 = j; i1 < 24; ++i1) {
            if ((this.neighbors[k] & 1 << i1) > 0) {
               PathPoint pathpoint3 = this.pathPoints[i1];
               if (!pathpoint3.visited) {
                  float f = pathpoint2.totalPathDistance + pathpoint2.distanceTo(pathpoint3);
                  if (!pathpoint3.isAssigned() || f < pathpoint3.totalPathDistance) {
                     pathpoint3.previous = pathpoint2;
                     pathpoint3.totalPathDistance = f;
                     pathpoint3.distanceToNext = pathpoint3.distanceTo(pathpoint5);
                     if (pathpoint3.isAssigned()) {
                        this.pathFindQueue.changeDistance(pathpoint3, pathpoint3.totalPathDistance + pathpoint3.distanceToNext);
                     } else {
                        pathpoint3.distanceToTarget = pathpoint3.totalPathDistance + pathpoint3.distanceToNext;
                        this.pathFindQueue.addPoint(pathpoint3);
                     }
                  }
               }
            }
         }
      }

      if (pathpoint1 == pathpoint4) {
         return null;
      } else {
         LOGGER.debug("Failed to find path from {} to {}", startIdx, finishIdx);
         if (andThen != null) {
            andThen.previous = pathpoint1;
            pathpoint1 = andThen;
         }

         return this.makePath(pathpoint4, pathpoint1);
      }
   }

   /**
    * Create and return a new PathEntity defining a path from the start to the finish, using the connections already
    * made by the caller, findPath.
    */
   private Path makePath(PathPoint start, PathPoint finish) {
      List<PathPoint> list = Lists.newArrayList();
      PathPoint pathpoint = finish;
      list.add(0, finish);

      while(pathpoint.previous != null) {
         pathpoint = pathpoint.previous;
         list.add(0, pathpoint);
      }

      return new Path(list, new BlockPos(finish.x, finish.y, finish.z), true);
   }

   public void writeAdditional(CompoundNBT compound) {
      super.writeAdditional(compound);
      compound.putInt("DragonPhase", this.phaseManager.getCurrentPhase().getType().getId());
   }

   /**
    * (abstract) Protected helper method to read subclass entity data from NBT.
    */
   public void readAdditional(CompoundNBT compound) {
      super.readAdditional(compound);
      if (compound.contains("DragonPhase")) {
         this.phaseManager.setPhase(PhaseType.getById(compound.getInt("DragonPhase")));
      }

   }

   /**
    * Makes the entity despawn if requirements are reached
    */
   public void checkDespawn() {
   }

   public EnderDragonPartEntity[] getDragonParts() {
      return this.dragonParts;
   }

   /**
    * Returns true if other Entities should be prevented from moving through this Entity.
    */
   public boolean canBeCollidedWith() {
      return false;
   }

   public SoundCategory getSoundCategory() {
      return SoundCategory.HOSTILE;
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.ENTITY_ENDER_DRAGON_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return SoundEvents.ENTITY_ENDER_DRAGON_HURT;
   }

   /**
    * Returns the volume for the sounds this mob makes.
    */
   protected float getSoundVolume() {
      return 5.0F;
   }

   @OnlyIn(Dist.CLIENT)
   public float getHeadPartYOffset(int p_184667_1_, double[] p_184667_2_, double[] p_184667_3_) {
      IPhase iphase = this.phaseManager.getCurrentPhase();
      PhaseType<? extends IPhase> phasetype = iphase.getType();
      double d0;
      if (phasetype != PhaseType.LANDING && phasetype != PhaseType.TAKEOFF) {
         if (iphase.getIsStationary()) {
            d0 = (double)p_184667_1_;
         } else if (p_184667_1_ == 6) {
            d0 = 0.0D;
         } else {
            d0 = p_184667_3_[1] - p_184667_2_[1];
         }
      } else {
         BlockPos blockpos = this.world.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EndPodiumFeature.END_PODIUM_LOCATION);
         float f = Math.max(MathHelper.sqrt(blockpos.distanceSq(this.getPositionVec(), true)) / 4.0F, 1.0F);
         d0 = (double)((float)p_184667_1_ / f);
      }

      return (float)d0;
   }

   public Vec3d getHeadLookVec(float p_184665_1_) {
      IPhase iphase = this.phaseManager.getCurrentPhase();
      PhaseType<? extends IPhase> phasetype = iphase.getType();
      Vec3d vec3d;
      if (phasetype != PhaseType.LANDING && phasetype != PhaseType.TAKEOFF) {
         if (iphase.getIsStationary()) {
            float f4 = this.rotationPitch;
            float f5 = 1.5F;
            this.rotationPitch = -45.0F;
            vec3d = this.getLook(p_184665_1_);
            this.rotationPitch = f4;
         } else {
            vec3d = this.getLook(p_184665_1_);
         }
      } else {
         BlockPos blockpos = this.world.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, EndPodiumFeature.END_PODIUM_LOCATION);
         float f = Math.max(MathHelper.sqrt(blockpos.distanceSq(this.getPositionVec(), true)) / 4.0F, 1.0F);
         float f1 = 6.0F / f;
         float f2 = this.rotationPitch;
         float f3 = 1.5F;
         this.rotationPitch = -f1 * 1.5F * 5.0F;
         vec3d = this.getLook(p_184665_1_);
         this.rotationPitch = f2;
      }

      return vec3d;
   }

   public void onCrystalDestroyed(EnderCrystalEntity crystal, BlockPos pos, DamageSource dmgSrc) {
      PlayerEntity playerentity;
      if (dmgSrc.getTrueSource() instanceof PlayerEntity) {
         playerentity = (PlayerEntity)dmgSrc.getTrueSource();
      } else {
         playerentity = this.world.getClosestPlayer(field_213405_bO, (double)pos.getX(), (double)pos.getY(), (double)pos.getZ());
      }

      if (crystal == this.closestEnderCrystal) {
         this.func_213403_a(this.dragonPartHead, DamageSource.causeExplosionDamage(playerentity), 10.0F);
      }

      this.phaseManager.getCurrentPhase().onCrystalDestroyed(crystal, pos, dmgSrc, playerentity);
   }

   public void notifyDataManagerChange(DataParameter<?> key) {
      if (PHASE.equals(key) && this.world.isRemote) {
         this.phaseManager.setPhase(PhaseType.getById(this.getDataManager().get(PHASE)));
      }

      super.notifyDataManagerChange(key);
   }

   public PhaseManager getPhaseManager() {
      return this.phaseManager;
   }

   @Nullable
   public DragonFightManager getFightManager() {
      return this.fightManager;
   }

   public boolean addPotionEffect(EffectInstance effectInstanceIn) {
      return false;
   }

   protected boolean canBeRidden(Entity entityIn) {
      return false;
   }

   /**
    * Returns false if this Entity is a boss, true otherwise.
    */
   public boolean isNonBoss() {
      return false;
   }
}
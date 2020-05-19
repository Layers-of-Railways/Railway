package net.minecraft.entity.item;

import java.util.OptionalInt;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.IRendersAsItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SSpawnObjectPacket;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(
   value = Dist.CLIENT,
   _interface = IRendersAsItem.class
)
public class FireworkRocketEntity extends Entity implements IRendersAsItem, IProjectile {
   private static final DataParameter<ItemStack> FIREWORK_ITEM = EntityDataManager.createKey(FireworkRocketEntity.class, DataSerializers.ITEMSTACK);
   private static final DataParameter<OptionalInt> BOOSTED_ENTITY_ID = EntityDataManager.createKey(FireworkRocketEntity.class, DataSerializers.OPTIONAL_VARINT);
   private static final DataParameter<Boolean> field_213895_d = EntityDataManager.createKey(FireworkRocketEntity.class, DataSerializers.BOOLEAN);
   private int fireworkAge;
   private int lifetime;
   private LivingEntity boostedEntity;

   public FireworkRocketEntity(EntityType<? extends FireworkRocketEntity> p_i50164_1_, World p_i50164_2_) {
      super(p_i50164_1_, p_i50164_2_);
   }

   protected void registerData() {
      this.dataManager.register(FIREWORK_ITEM, ItemStack.EMPTY);
      this.dataManager.register(BOOSTED_ENTITY_ID, OptionalInt.empty());
      this.dataManager.register(field_213895_d, false);
   }

   /**
    * Checks if the entity is in range to render.
    */
   @OnlyIn(Dist.CLIENT)
   public boolean isInRangeToRenderDist(double distance) {
      return distance < 4096.0D && !this.isAttachedToEntity();
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isInRangeToRender3d(double x, double y, double z) {
      return super.isInRangeToRender3d(x, y, z) && !this.isAttachedToEntity();
   }

   public FireworkRocketEntity(World worldIn, double x, double y, double z, ItemStack givenItem) {
      super(EntityType.FIREWORK_ROCKET, worldIn);
      this.fireworkAge = 0;
      this.setPosition(x, y, z);
      int i = 1;
      if (!givenItem.isEmpty() && givenItem.hasTag()) {
         this.dataManager.set(FIREWORK_ITEM, givenItem.copy());
         i += givenItem.getOrCreateChildTag("Fireworks").getByte("Flight");
      }

      this.setMotion(this.rand.nextGaussian() * 0.001D, 0.05D, this.rand.nextGaussian() * 0.001D);
      this.lifetime = 10 * i + this.rand.nextInt(6) + this.rand.nextInt(7);
   }

   public FireworkRocketEntity(World p_i47367_1_, ItemStack p_i47367_2_, LivingEntity p_i47367_3_) {
      this(p_i47367_1_, p_i47367_3_.getPosX(), p_i47367_3_.getPosY(), p_i47367_3_.getPosZ(), p_i47367_2_);
      this.dataManager.set(BOOSTED_ENTITY_ID, OptionalInt.of(p_i47367_3_.getEntityId()));
      this.boostedEntity = p_i47367_3_;
   }

   public FireworkRocketEntity(World p_i50165_1_, ItemStack p_i50165_2_, double p_i50165_3_, double p_i50165_5_, double p_i50165_7_, boolean p_i50165_9_) {
      this(p_i50165_1_, p_i50165_3_, p_i50165_5_, p_i50165_7_, p_i50165_2_);
      this.dataManager.set(field_213895_d, p_i50165_9_);
   }

   /**
    * Updates the entity motion clientside, called by packets from the server
    */
   @OnlyIn(Dist.CLIENT)
   public void setVelocity(double x, double y, double z) {
      this.setMotion(x, y, z);
      if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F) {
         float f = MathHelper.sqrt(x * x + z * z);
         this.rotationYaw = (float)(MathHelper.atan2(x, z) * (double)(180F / (float)Math.PI));
         this.rotationPitch = (float)(MathHelper.atan2(y, (double)f) * (double)(180F / (float)Math.PI));
         this.prevRotationYaw = this.rotationYaw;
         this.prevRotationPitch = this.rotationPitch;
      }

   }

   /**
    * Called to update the entity's position/logic.
    */
   public void tick() {
      super.tick();
      if (this.isAttachedToEntity()) {
         if (this.boostedEntity == null) {
            this.dataManager.get(BOOSTED_ENTITY_ID).ifPresent((p_213891_1_) -> {
               Entity entity = this.world.getEntityByID(p_213891_1_);
               if (entity instanceof LivingEntity) {
                  this.boostedEntity = (LivingEntity)entity;
               }

            });
         }

         if (this.boostedEntity != null) {
            if (this.boostedEntity.isElytraFlying()) {
               Vec3d vec3d = this.boostedEntity.getLookVec();
               double d0 = 1.5D;
               double d1 = 0.1D;
               Vec3d vec3d1 = this.boostedEntity.getMotion();
               this.boostedEntity.setMotion(vec3d1.add(vec3d.x * 0.1D + (vec3d.x * 1.5D - vec3d1.x) * 0.5D, vec3d.y * 0.1D + (vec3d.y * 1.5D - vec3d1.y) * 0.5D, vec3d.z * 0.1D + (vec3d.z * 1.5D - vec3d1.z) * 0.5D));
            }

            this.setPosition(this.boostedEntity.getPosX(), this.boostedEntity.getPosY(), this.boostedEntity.getPosZ());
            this.setMotion(this.boostedEntity.getMotion());
         }
      } else {
         if (!this.func_213889_i()) {
            this.setMotion(this.getMotion().mul(1.15D, 1.0D, 1.15D).add(0.0D, 0.04D, 0.0D));
         }

         this.move(MoverType.SELF, this.getMotion());
      }

      Vec3d vec3d2 = this.getMotion();
      RayTraceResult raytraceresult = ProjectileHelper.rayTrace(this, this.getBoundingBox().expand(vec3d2).grow(1.0D), (p_213890_0_) -> {
         return !p_213890_0_.isSpectator() && p_213890_0_.isAlive() && p_213890_0_.canBeCollidedWith();
      }, RayTraceContext.BlockMode.COLLIDER, true);
      if (!this.noClip) {
         this.func_213892_a(raytraceresult);
         this.isAirBorne = true;
      }

      float f = MathHelper.sqrt(horizontalMag(vec3d2));
      this.rotationYaw = (float)(MathHelper.atan2(vec3d2.x, vec3d2.z) * (double)(180F / (float)Math.PI));

      for(this.rotationPitch = (float)(MathHelper.atan2(vec3d2.y, (double)f) * (double)(180F / (float)Math.PI)); this.rotationPitch - this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F) {
         ;
      }

      while(this.rotationPitch - this.prevRotationPitch >= 180.0F) {
         this.prevRotationPitch += 360.0F;
      }

      while(this.rotationYaw - this.prevRotationYaw < -180.0F) {
         this.prevRotationYaw -= 360.0F;
      }

      while(this.rotationYaw - this.prevRotationYaw >= 180.0F) {
         this.prevRotationYaw += 360.0F;
      }

      this.rotationPitch = MathHelper.lerp(0.2F, this.prevRotationPitch, this.rotationPitch);
      this.rotationYaw = MathHelper.lerp(0.2F, this.prevRotationYaw, this.rotationYaw);
      if (this.fireworkAge == 0 && !this.isSilent()) {
         this.world.playSound((PlayerEntity)null, this.getPosX(), this.getPosY(), this.getPosZ(), SoundEvents.ENTITY_FIREWORK_ROCKET_LAUNCH, SoundCategory.AMBIENT, 3.0F, 1.0F);
      }

      ++this.fireworkAge;
      if (this.world.isRemote && this.fireworkAge % 2 < 2) {
         this.world.addParticle(ParticleTypes.FIREWORK, this.getPosX(), this.getPosY() - 0.3D, this.getPosZ(), this.rand.nextGaussian() * 0.05D, -this.getMotion().y * 0.5D, this.rand.nextGaussian() * 0.05D);
      }

      if (!this.world.isRemote && this.fireworkAge > this.lifetime) {
         this.func_213893_k();
      }

   }

   private void func_213893_k() {
      this.world.setEntityState(this, (byte)17);
      this.dealExplosionDamage();
      this.remove();
   }

   protected void func_213892_a(RayTraceResult p_213892_1_) {
      if (p_213892_1_.getType() == RayTraceResult.Type.ENTITY && !this.world.isRemote) {
         this.func_213893_k();
      } else if (this.collided) {
         BlockPos blockpos;
         if (p_213892_1_.getType() == RayTraceResult.Type.BLOCK) {
            blockpos = new BlockPos(((BlockRayTraceResult)p_213892_1_).getPos());
         } else {
            blockpos = new BlockPos(this);
         }

         this.world.getBlockState(blockpos).onEntityCollision(this.world, blockpos, this);
         if (this.func_213894_l()) {
            this.func_213893_k();
         }
      }

   }

   private boolean func_213894_l() {
      ItemStack itemstack = this.dataManager.get(FIREWORK_ITEM);
      CompoundNBT compoundnbt = itemstack.isEmpty() ? null : itemstack.getChildTag("Fireworks");
      ListNBT listnbt = compoundnbt != null ? compoundnbt.getList("Explosions", 10) : null;
      return listnbt != null && !listnbt.isEmpty();
   }

   private void dealExplosionDamage() {
      float f = 0.0F;
      ItemStack itemstack = this.dataManager.get(FIREWORK_ITEM);
      CompoundNBT compoundnbt = itemstack.isEmpty() ? null : itemstack.getChildTag("Fireworks");
      ListNBT listnbt = compoundnbt != null ? compoundnbt.getList("Explosions", 10) : null;
      if (listnbt != null && !listnbt.isEmpty()) {
         f = 5.0F + (float)(listnbt.size() * 2);
      }

      if (f > 0.0F) {
         if (this.boostedEntity != null) {
            this.boostedEntity.attackEntityFrom(DamageSource.FIREWORKS, 5.0F + (float)(listnbt.size() * 2));
         }

         double d0 = 5.0D;
         Vec3d vec3d = this.getPositionVec();

         for(LivingEntity livingentity : this.world.getEntitiesWithinAABB(LivingEntity.class, this.getBoundingBox().grow(5.0D))) {
            if (livingentity != this.boostedEntity && !(this.getDistanceSq(livingentity) > 25.0D)) {
               boolean flag = false;

               for(int i = 0; i < 2; ++i) {
                  Vec3d vec3d1 = new Vec3d(livingentity.getPosX(), livingentity.getPosYHeight(0.5D * (double)i), livingentity.getPosZ());
                  RayTraceResult raytraceresult = this.world.rayTraceBlocks(new RayTraceContext(vec3d, vec3d1, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.NONE, this));
                  if (raytraceresult.getType() == RayTraceResult.Type.MISS) {
                     flag = true;
                     break;
                  }
               }

               if (flag) {
                  float f1 = f * (float)Math.sqrt((5.0D - (double)this.getDistance(livingentity)) / 5.0D);
                  livingentity.attackEntityFrom(DamageSource.FIREWORKS, f1);
               }
            }
         }
      }

   }

   private boolean isAttachedToEntity() {
      return this.dataManager.get(BOOSTED_ENTITY_ID).isPresent();
   }

   public boolean func_213889_i() {
      return this.dataManager.get(field_213895_d);
   }

   /**
    * Handler for {@link World#setEntityState}
    */
   @OnlyIn(Dist.CLIENT)
   public void handleStatusUpdate(byte id) {
      if (id == 17 && this.world.isRemote) {
         if (!this.func_213894_l()) {
            for(int i = 0; i < this.rand.nextInt(3) + 2; ++i) {
               this.world.addParticle(ParticleTypes.POOF, this.getPosX(), this.getPosY(), this.getPosZ(), this.rand.nextGaussian() * 0.05D, 0.005D, this.rand.nextGaussian() * 0.05D);
            }
         } else {
            ItemStack itemstack = this.dataManager.get(FIREWORK_ITEM);
            CompoundNBT compoundnbt = itemstack.isEmpty() ? null : itemstack.getChildTag("Fireworks");
            Vec3d vec3d = this.getMotion();
            this.world.makeFireworks(this.getPosX(), this.getPosY(), this.getPosZ(), vec3d.x, vec3d.y, vec3d.z, compoundnbt);
         }
      }

      super.handleStatusUpdate(id);
   }

   public void writeAdditional(CompoundNBT compound) {
      compound.putInt("Life", this.fireworkAge);
      compound.putInt("LifeTime", this.lifetime);
      ItemStack itemstack = this.dataManager.get(FIREWORK_ITEM);
      if (!itemstack.isEmpty()) {
         compound.put("FireworksItem", itemstack.write(new CompoundNBT()));
      }

      compound.putBoolean("ShotAtAngle", this.dataManager.get(field_213895_d));
   }

   /**
    * (abstract) Protected helper method to read subclass entity data from NBT.
    */
   public void readAdditional(CompoundNBT compound) {
      this.fireworkAge = compound.getInt("Life");
      this.lifetime = compound.getInt("LifeTime");
      ItemStack itemstack = ItemStack.read(compound.getCompound("FireworksItem"));
      if (!itemstack.isEmpty()) {
         this.dataManager.set(FIREWORK_ITEM, itemstack);
      }

      if (compound.contains("ShotAtAngle")) {
         this.dataManager.set(field_213895_d, compound.getBoolean("ShotAtAngle"));
      }

   }

   @OnlyIn(Dist.CLIENT)
   public ItemStack getItem() {
      ItemStack itemstack = this.dataManager.get(FIREWORK_ITEM);
      return itemstack.isEmpty() ? new ItemStack(Items.FIREWORK_ROCKET) : itemstack;
   }

   /**
    * Returns true if it's possible to attack this entity with an item.
    */
   public boolean canBeAttackedWithItem() {
      return false;
   }

   public IPacket<?> createSpawnPacket() {
      return new SSpawnObjectPacket(this);
   }

   /**
    * Similar to setArrowHeading, it's point the throwable entity to a x, y, z direction.
    */
   public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
      float f = MathHelper.sqrt(x * x + y * y + z * z);
      x = x / (double)f;
      y = y / (double)f;
      z = z / (double)f;
      x = x + this.rand.nextGaussian() * (double)0.0075F * (double)inaccuracy;
      y = y + this.rand.nextGaussian() * (double)0.0075F * (double)inaccuracy;
      z = z + this.rand.nextGaussian() * (double)0.0075F * (double)inaccuracy;
      x = x * (double)velocity;
      y = y * (double)velocity;
      z = z * (double)velocity;
      this.setMotion(x, y, z);
   }
}
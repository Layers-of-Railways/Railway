package net.minecraft.entity.item;

import java.util.Map.Entry;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.play.server.SSpawnExperienceOrbPacket;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ExperienceOrbEntity extends Entity {
   public int xpColor;
   public int xpOrbAge;
   public int delayBeforeCanPickup;
   private int xpOrbHealth = 5;
   public int xpValue;
   private PlayerEntity closestPlayer;
   private int xpTargetColor;

   public ExperienceOrbEntity(World worldIn, double x, double y, double z, int expValue) {
      this(EntityType.EXPERIENCE_ORB, worldIn);
      this.setPosition(x, y, z);
      this.rotationYaw = (float)(this.rand.nextDouble() * 360.0D);
      this.setMotion((this.rand.nextDouble() * (double)0.2F - (double)0.1F) * 2.0D, this.rand.nextDouble() * 0.2D * 2.0D, (this.rand.nextDouble() * (double)0.2F - (double)0.1F) * 2.0D);
      this.xpValue = expValue;
   }

   public ExperienceOrbEntity(EntityType<? extends ExperienceOrbEntity> p_i50382_1_, World p_i50382_2_) {
      super(p_i50382_1_, p_i50382_2_);
   }

   protected boolean canTriggerWalking() {
      return false;
   }

   protected void registerData() {
   }

   /**
    * Called to update the entity's position/logic.
    */
   public void tick() {
      super.tick();
      if (this.delayBeforeCanPickup > 0) {
         --this.delayBeforeCanPickup;
      }

      this.prevPosX = this.getPosX();
      this.prevPosY = this.getPosY();
      this.prevPosZ = this.getPosZ();
      if (this.areEyesInFluid(FluidTags.WATER)) {
         this.applyFloatMotion();
      } else if (!this.hasNoGravity()) {
         this.setMotion(this.getMotion().add(0.0D, -0.03D, 0.0D));
      }

      if (this.world.getFluidState(new BlockPos(this)).isTagged(FluidTags.LAVA)) {
         this.setMotion((double)((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F), (double)0.2F, (double)((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F));
         this.playSound(SoundEvents.ENTITY_GENERIC_BURN, 0.4F, 2.0F + this.rand.nextFloat() * 0.4F);
      }

      if (!this.world.hasNoCollisions(this.getBoundingBox())) {
         this.pushOutOfBlocks(this.getPosX(), (this.getBoundingBox().minY + this.getBoundingBox().maxY) / 2.0D, this.getPosZ());
      }

      double d0 = 8.0D;
      if (this.xpTargetColor < this.xpColor - 20 + this.getEntityId() % 100) {
         if (this.closestPlayer == null || this.closestPlayer.getDistanceSq(this) > 64.0D) {
            this.closestPlayer = this.world.getClosestPlayer(this, 8.0D);
         }

         this.xpTargetColor = this.xpColor;
      }

      if (this.closestPlayer != null && this.closestPlayer.isSpectator()) {
         this.closestPlayer = null;
      }

      if (this.closestPlayer != null) {
         Vec3d vec3d = new Vec3d(this.closestPlayer.getPosX() - this.getPosX(), this.closestPlayer.getPosY() + (double)this.closestPlayer.getEyeHeight() / 2.0D - this.getPosY(), this.closestPlayer.getPosZ() - this.getPosZ());
         double d1 = vec3d.lengthSquared();
         if (d1 < 64.0D) {
            double d2 = 1.0D - Math.sqrt(d1) / 8.0D;
            this.setMotion(this.getMotion().add(vec3d.normalize().scale(d2 * d2 * 0.1D)));
         }
      }

      this.move(MoverType.SELF, this.getMotion());
      float f = 0.98F;
      if (this.onGround) {
         BlockPos pos =new BlockPos(this.getPosX(), this.getPosY() - 1.0D, this.getPosZ());
         f = this.world.getBlockState(pos).getSlipperiness(this.world, pos, this) * 0.98F;
      }

      this.setMotion(this.getMotion().mul((double)f, 0.98D, (double)f));
      if (this.onGround) {
         this.setMotion(this.getMotion().mul(1.0D, -0.9D, 1.0D));
      }

      ++this.xpColor;
      ++this.xpOrbAge;
      if (this.xpOrbAge >= 6000) {
         this.remove();
      }

   }

   private void applyFloatMotion() {
      Vec3d vec3d = this.getMotion();
      this.setMotion(vec3d.x * (double)0.99F, Math.min(vec3d.y + (double)5.0E-4F, (double)0.06F), vec3d.z * (double)0.99F);
   }

   /**
    * Plays the {@link #getSplashSound() splash sound}, and the {@link ParticleType#WATER_BUBBLE} and {@link
    * ParticleType#WATER_SPLASH} particles.
    */
   protected void doWaterSplashEffect() {
   }

   /**
    * Will deal the specified amount of fire damage to the entity if the entity isn't immune to fire damage.
    */
   protected void dealFireDamage(int amount) {
      this.attackEntityFrom(DamageSource.IN_FIRE, (float)amount);
   }

   /**
    * Called when the entity is attacked.
    */
   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (this.world.isRemote || this.removed) return false; //Forge: Fixes MC-53850
      if (this.isInvulnerableTo(source)) {
         return false;
      } else {
         this.markVelocityChanged();
         this.xpOrbHealth = (int)((float)this.xpOrbHealth - amount);
         if (this.xpOrbHealth <= 0) {
            this.remove();
         }

         return false;
      }
   }

   public void writeAdditional(CompoundNBT compound) {
      compound.putShort("Health", (short)this.xpOrbHealth);
      compound.putShort("Age", (short)this.xpOrbAge);
      compound.putShort("Value", (short)this.xpValue);
   }

   /**
    * (abstract) Protected helper method to read subclass entity data from NBT.
    */
   public void readAdditional(CompoundNBT compound) {
      this.xpOrbHealth = compound.getShort("Health");
      this.xpOrbAge = compound.getShort("Age");
      this.xpValue = compound.getShort("Value");
   }

   /**
    * Called by a player entity when they collide with an entity
    */
   public void onCollideWithPlayer(PlayerEntity entityIn) {
      if (!this.world.isRemote) {
         if (this.delayBeforeCanPickup == 0 && entityIn.xpCooldown == 0) {
            if (net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.player.PlayerXpEvent.PickupXp(entityIn, this))) return;
            entityIn.xpCooldown = 2;
            entityIn.onItemPickup(this, 1);
            Entry<EquipmentSlotType, ItemStack> entry = EnchantmentHelper.getRandomItemWithEnchantment(Enchantments.MENDING, entityIn);
            if (entry != null) {
               ItemStack itemstack = entry.getValue();
               if (!itemstack.isEmpty() && itemstack.isDamaged()) {
                  int i = Math.min((int)(this.xpValue * itemstack.getXpRepairRatio()), itemstack.getDamage());
                  this.xpValue -= this.durabilityToXp(i);
                  itemstack.setDamage(itemstack.getDamage() - i);
               }
            }

            if (this.xpValue > 0) {
               entityIn.giveExperiencePoints(this.xpValue);
            }

            this.remove();
         }

      }
   }

   private int durabilityToXp(int durability) {
      return durability / 2;
   }

   private int xpToDurability(int xp) {
      return xp * 2;
   }

   /**
    * Returns the XP value of this XP orb.
    */
   public int getXpValue() {
      return this.xpValue;
   }

   /**
    * Returns a number from 1 to 10 based on how much XP this orb is worth. This is used by RenderXPOrb to determine
    * what texture to use.
    */
   @OnlyIn(Dist.CLIENT)
   public int getTextureByXP() {
      if (this.xpValue >= 2477) {
         return 10;
      } else if (this.xpValue >= 1237) {
         return 9;
      } else if (this.xpValue >= 617) {
         return 8;
      } else if (this.xpValue >= 307) {
         return 7;
      } else if (this.xpValue >= 149) {
         return 6;
      } else if (this.xpValue >= 73) {
         return 5;
      } else if (this.xpValue >= 37) {
         return 4;
      } else if (this.xpValue >= 17) {
         return 3;
      } else if (this.xpValue >= 7) {
         return 2;
      } else {
         return this.xpValue >= 3 ? 1 : 0;
      }
   }

   /**
    * Get a fragment of the maximum experience points value for the supplied value of experience points value.
    */
   public static int getXPSplit(int expValue) {
      if (expValue >= 2477) {
         return 2477;
      } else if (expValue >= 1237) {
         return 1237;
      } else if (expValue >= 617) {
         return 617;
      } else if (expValue >= 307) {
         return 307;
      } else if (expValue >= 149) {
         return 149;
      } else if (expValue >= 73) {
         return 73;
      } else if (expValue >= 37) {
         return 37;
      } else if (expValue >= 17) {
         return 17;
      } else if (expValue >= 7) {
         return 7;
      } else {
         return expValue >= 3 ? 3 : 1;
      }
   }

   /**
    * Returns true if it's possible to attack this entity with an item.
    */
   public boolean canBeAttackedWithItem() {
      return false;
   }

   public IPacket<?> createSpawnPacket() {
      return new SSpawnExperienceOrbPacket(this);
   }
}
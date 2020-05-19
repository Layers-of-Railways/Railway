package net.minecraft.entity.projectile;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.world.World;

public class SpectralArrowEntity extends AbstractArrowEntity {
   private int duration = 200;

   public SpectralArrowEntity(EntityType<? extends SpectralArrowEntity> p_i50158_1_, World p_i50158_2_) {
      super(p_i50158_1_, p_i50158_2_);
   }

   public SpectralArrowEntity(World worldIn, LivingEntity shooter) {
      super(EntityType.SPECTRAL_ARROW, shooter, worldIn);
   }

   public SpectralArrowEntity(World worldIn, double x, double y, double z) {
      super(EntityType.SPECTRAL_ARROW, x, y, z, worldIn);
   }

   /**
    * Called to update the entity's position/logic.
    */
   public void tick() {
      super.tick();
      if (this.world.isRemote && !this.inGround) {
         this.world.addParticle(ParticleTypes.INSTANT_EFFECT, this.getPosX(), this.getPosY(), this.getPosZ(), 0.0D, 0.0D, 0.0D);
      }

   }

   protected ItemStack getArrowStack() {
      return new ItemStack(Items.SPECTRAL_ARROW);
   }

   protected void arrowHit(LivingEntity living) {
      super.arrowHit(living);
      EffectInstance effectinstance = new EffectInstance(Effects.GLOWING, this.duration, 0);
      living.addPotionEffect(effectinstance);
   }

   /**
    * (abstract) Protected helper method to read subclass entity data from NBT.
    */
   public void readAdditional(CompoundNBT compound) {
      super.readAdditional(compound);
      if (compound.contains("Duration")) {
         this.duration = compound.getInt("Duration");
      }

   }

   public void writeAdditional(CompoundNBT compound) {
      super.writeAdditional(compound);
      compound.putInt("Duration", this.duration);
   }
}
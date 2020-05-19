package net.minecraft.util;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class EntityDamageSource extends DamageSource {
   @Nullable
   protected final Entity damageSourceEntity;
   /** Whether this EntityDamageSource is from an entity wearing Thorns-enchanted armor. */
   private boolean isThornsDamage;

   public EntityDamageSource(String damageTypeIn, @Nullable Entity damageSourceEntityIn) {
      super(damageTypeIn);
      this.damageSourceEntity = damageSourceEntityIn;
   }

   /**
    * Sets this EntityDamageSource as originating from Thorns armor
    */
   public EntityDamageSource setIsThornsDamage() {
      this.isThornsDamage = true;
      return this;
   }

   public boolean getIsThornsDamage() {
      return this.isThornsDamage;
   }

   /**
    * Retrieves the true causer of the damage, e.g. the player who fired an arrow, the shulker who fired the bullet,
    * etc.
    */
   @Nullable
   public Entity getTrueSource() {
      return this.damageSourceEntity;
   }

   /**
    * Gets the death message that is displayed when the player dies
    */
   public ITextComponent getDeathMessage(LivingEntity entityLivingBaseIn) {
      ItemStack itemstack = this.damageSourceEntity instanceof LivingEntity ? ((LivingEntity)this.damageSourceEntity).getHeldItemMainhand() : ItemStack.EMPTY;
      String s = "death.attack." + this.damageType;
      return !itemstack.isEmpty() && itemstack.hasDisplayName() ? new TranslationTextComponent(s + ".item", entityLivingBaseIn.getDisplayName(), this.damageSourceEntity.getDisplayName(), itemstack.getTextComponent()) : new TranslationTextComponent(s, entityLivingBaseIn.getDisplayName(), this.damageSourceEntity.getDisplayName());
   }

   /**
    * Return whether this damage source will have its damage amount scaled based on the current difficulty.
    */
   public boolean isDifficultyScaled() {
      return this.damageSourceEntity != null && this.damageSourceEntity instanceof LivingEntity && !(this.damageSourceEntity instanceof PlayerEntity);
   }

   /**
    * Gets the location from which the damage originates.
    */
   @Nullable
   public Vec3d getDamageLocation() {
      return this.damageSourceEntity != null ? this.damageSourceEntity.getPositionVec() : null;
   }
}
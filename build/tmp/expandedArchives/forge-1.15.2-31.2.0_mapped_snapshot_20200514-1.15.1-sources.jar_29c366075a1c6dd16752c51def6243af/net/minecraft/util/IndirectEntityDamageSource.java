package net.minecraft.util;

import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class IndirectEntityDamageSource extends EntityDamageSource {
   private final Entity indirectEntity;

   public IndirectEntityDamageSource(String damageTypeIn, Entity source, @Nullable Entity indirectEntityIn) {
      super(damageTypeIn, source);
      this.indirectEntity = indirectEntityIn;
   }

   /**
    * Retrieves the immediate causer of the damage, e.g. the arrow entity, not its shooter
    */
   @Nullable
   public Entity getImmediateSource() {
      return this.damageSourceEntity;
   }

   /**
    * Retrieves the true causer of the damage, e.g. the player who fired an arrow, the shulker who fired the bullet,
    * etc.
    */
   @Nullable
   public Entity getTrueSource() {
      return this.indirectEntity;
   }

   /**
    * Gets the death message that is displayed when the player dies
    */
   public ITextComponent getDeathMessage(LivingEntity entityLivingBaseIn) {
      ITextComponent itextcomponent = this.indirectEntity == null ? this.damageSourceEntity.getDisplayName() : this.indirectEntity.getDisplayName();
      ItemStack itemstack = this.indirectEntity instanceof LivingEntity ? ((LivingEntity)this.indirectEntity).getHeldItemMainhand() : ItemStack.EMPTY;
      String s = "death.attack." + this.damageType;
      String s1 = s + ".item";
      return !itemstack.isEmpty() && itemstack.hasDisplayName() ? new TranslationTextComponent(s1, entityLivingBaseIn.getDisplayName(), itextcomponent, itemstack.getTextComponent()) : new TranslationTextComponent(s, entityLivingBaseIn.getDisplayName(), itextcomponent);
   }
}
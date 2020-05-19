package net.minecraft.potion;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;

public class HealthBoostEffect extends Effect {
   public HealthBoostEffect(EffectType p_i50393_1_, int p_i50393_2_) {
      super(p_i50393_1_, p_i50393_2_);
   }

   public void removeAttributesModifiersFromEntity(LivingEntity entityLivingBaseIn, AbstractAttributeMap attributeMapIn, int amplifier) {
      super.removeAttributesModifiersFromEntity(entityLivingBaseIn, attributeMapIn, amplifier);
      if (entityLivingBaseIn.getHealth() > entityLivingBaseIn.getMaxHealth()) {
         entityLivingBaseIn.setHealth(entityLivingBaseIn.getMaxHealth());
      }

   }
}
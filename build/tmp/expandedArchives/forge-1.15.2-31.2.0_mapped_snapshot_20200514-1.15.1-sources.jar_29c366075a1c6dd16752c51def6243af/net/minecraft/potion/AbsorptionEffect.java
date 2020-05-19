package net.minecraft.potion;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AbstractAttributeMap;

public class AbsorptionEffect extends Effect {
   protected AbsorptionEffect(EffectType p_i50395_1_, int p_i50395_2_) {
      super(p_i50395_1_, p_i50395_2_);
   }

   public void removeAttributesModifiersFromEntity(LivingEntity entityLivingBaseIn, AbstractAttributeMap attributeMapIn, int amplifier) {
      entityLivingBaseIn.setAbsorptionAmount(entityLivingBaseIn.getAbsorptionAmount() - (float)(4 * (amplifier + 1)));
      super.removeAttributesModifiersFromEntity(entityLivingBaseIn, attributeMapIn, amplifier);
   }

   public void applyAttributesModifiersToEntity(LivingEntity entityLivingBaseIn, AbstractAttributeMap attributeMapIn, int amplifier) {
      entityLivingBaseIn.setAbsorptionAmount(entityLivingBaseIn.getAbsorptionAmount() + (float)(4 * (amplifier + 1)));
      super.applyAttributesModifiersToEntity(entityLivingBaseIn, attributeMapIn, amplifier);
   }
}
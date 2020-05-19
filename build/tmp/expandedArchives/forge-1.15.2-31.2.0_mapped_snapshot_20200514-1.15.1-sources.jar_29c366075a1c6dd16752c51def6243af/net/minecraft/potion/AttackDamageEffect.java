package net.minecraft.potion;

import net.minecraft.entity.ai.attributes.AttributeModifier;

public class AttackDamageEffect extends Effect {
   protected final double bonusPerLevel;

   protected AttackDamageEffect(EffectType p_i50394_1_, int p_i50394_2_, double p_i50394_3_) {
      super(p_i50394_1_, p_i50394_2_);
      this.bonusPerLevel = p_i50394_3_;
   }

   public double getAttributeModifierAmount(int amplifier, AttributeModifier modifier) {
      return this.bonusPerLevel * (double)(amplifier + 1);
   }
}
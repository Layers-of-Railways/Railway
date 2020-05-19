package net.minecraft.potion;

import net.minecraft.util.text.TextFormatting;

public enum EffectType {
   BENEFICIAL(TextFormatting.BLUE),
   HARMFUL(TextFormatting.RED),
   NEUTRAL(TextFormatting.BLUE);

   private final TextFormatting color;

   private EffectType(TextFormatting p_i50390_3_) {
      this.color = p_i50390_3_;
   }

   public TextFormatting getColor() {
      return this.color;
   }
}
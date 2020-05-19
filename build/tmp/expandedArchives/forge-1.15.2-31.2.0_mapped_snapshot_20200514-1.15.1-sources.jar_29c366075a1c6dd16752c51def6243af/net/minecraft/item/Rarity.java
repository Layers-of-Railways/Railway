package net.minecraft.item;

import net.minecraft.util.text.TextFormatting;

public enum Rarity implements net.minecraftforge.common.IExtensibleEnum {
   COMMON(TextFormatting.WHITE),
   UNCOMMON(TextFormatting.YELLOW),
   RARE(TextFormatting.AQUA),
   EPIC(TextFormatting.LIGHT_PURPLE);

   public final TextFormatting color;

   private Rarity(TextFormatting p_i48837_3_) {
      this.color = p_i48837_3_;
   }

   public static Rarity create(String name, TextFormatting p_i48837_3_) {
      throw new IllegalStateException("Enum not extended");
   }
}
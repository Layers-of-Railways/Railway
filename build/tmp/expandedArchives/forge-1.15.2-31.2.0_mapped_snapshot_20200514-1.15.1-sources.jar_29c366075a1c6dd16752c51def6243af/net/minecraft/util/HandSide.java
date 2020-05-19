package net.minecraft.util;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public enum HandSide {
   LEFT(new TranslationTextComponent("options.mainHand.left")),
   RIGHT(new TranslationTextComponent("options.mainHand.right"));

   private final ITextComponent handName;

   private HandSide(ITextComponent nameIn) {
      this.handName = nameIn;
   }

   @OnlyIn(Dist.CLIENT)
   public HandSide opposite() {
      return this == LEFT ? RIGHT : LEFT;
   }

   public String toString() {
      return this.handName.getString();
   }
}
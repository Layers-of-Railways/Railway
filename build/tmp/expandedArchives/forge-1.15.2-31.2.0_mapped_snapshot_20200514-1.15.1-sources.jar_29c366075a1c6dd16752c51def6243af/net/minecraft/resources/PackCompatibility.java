package net.minecraft.resources;

import net.minecraft.util.SharedConstants;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public enum PackCompatibility {
   TOO_OLD("old"),
   TOO_NEW("new"),
   COMPATIBLE("compatible");

   private final ITextComponent description;
   private final ITextComponent confirmMessage;

   private PackCompatibility(String id) {
      this.description = new TranslationTextComponent("resourcePack.incompatible." + id);
      this.confirmMessage = new TranslationTextComponent("resourcePack.incompatible.confirm." + id);
   }

   public boolean isCompatible() {
      return this == COMPATIBLE;
   }

   public static PackCompatibility getCompatibility(int packVersionIn) {
      if (packVersionIn < SharedConstants.getVersion().getPackVersion()) {
         return TOO_OLD;
      } else {
         return packVersionIn > SharedConstants.getVersion().getPackVersion() ? TOO_NEW : COMPATIBLE;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public ITextComponent getDescription() {
      return this.description;
   }

   @OnlyIn(Dist.CLIENT)
   public ITextComponent getConfirmMessage() {
      return this.confirmMessage;
   }
}
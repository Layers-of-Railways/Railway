package net.minecraft.client.util;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface ITooltipFlag {
   boolean isAdvanced();

   @OnlyIn(Dist.CLIENT)
   public static enum TooltipFlags implements ITooltipFlag {
      NORMAL(false),
      ADVANCED(true);

      private final boolean isAdvanced;

      private TooltipFlags(boolean advanced) {
         this.isAdvanced = advanced;
      }

      public boolean isAdvanced() {
         return this.isAdvanced;
      }
   }
}
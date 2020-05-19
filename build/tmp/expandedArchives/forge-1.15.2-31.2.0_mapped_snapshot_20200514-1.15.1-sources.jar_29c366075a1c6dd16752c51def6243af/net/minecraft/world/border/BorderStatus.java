package net.minecraft.world.border;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public enum BorderStatus {
   GROWING(4259712),
   SHRINKING(16724016),
   STATIONARY(2138367);

   private final int color;

   private BorderStatus(int color) {
      this.color = color;
   }

   /**
    * Retrieve the color that the border should be while in this state
    */
   public int getColor() {
      return this.color;
   }
}
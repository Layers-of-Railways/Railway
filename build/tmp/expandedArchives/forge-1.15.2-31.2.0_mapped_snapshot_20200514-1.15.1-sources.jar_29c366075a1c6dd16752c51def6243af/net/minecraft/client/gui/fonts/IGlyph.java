package net.minecraft.client.gui.fonts;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface IGlyph {
   float getAdvance();

   default float getAdvance(boolean p_223274_1_) {
      return this.getAdvance() + (p_223274_1_ ? this.getBoldOffset() : 0.0F);
   }

   default float getBearingX() {
      return 0.0F;
   }

   default float getBoldOffset() {
      return 1.0F;
   }

   default float getShadowOffset() {
      return 1.0F;
   }
}
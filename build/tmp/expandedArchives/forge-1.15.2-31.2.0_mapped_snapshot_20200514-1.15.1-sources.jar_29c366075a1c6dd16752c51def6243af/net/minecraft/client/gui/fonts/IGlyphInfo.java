package net.minecraft.client.gui.fonts;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface IGlyphInfo extends IGlyph {
   int getWidth();

   int getHeight();

   void uploadGlyph(int xOffset, int yOffset);

   boolean isColored();

   float getOversample();

   default float func_211198_f() {
      return this.getBearingX();
   }

   default float func_211199_g() {
      return this.func_211198_f() + (float)this.getWidth() / this.getOversample();
   }

   default float func_211200_h() {
      return this.getBearingY();
   }

   default float func_211204_i() {
      return this.func_211200_h() + (float)this.getHeight() / this.getOversample();
   }

   default float getBearingY() {
      return 3.0F;
   }
}
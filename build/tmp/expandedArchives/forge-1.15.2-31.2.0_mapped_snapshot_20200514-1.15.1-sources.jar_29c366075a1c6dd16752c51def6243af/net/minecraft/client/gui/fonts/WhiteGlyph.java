package net.minecraft.client.gui.fonts;

import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public enum WhiteGlyph implements IGlyphInfo {
   INSTANCE;

   private static final NativeImage field_228172_b_ = Util.make(new NativeImage(NativeImage.PixelFormat.RGBA, 5, 8, false), (p_228173_0_) -> {
      for(int i = 0; i < 8; ++i) {
         for(int j = 0; j < 5; ++j) {
            if (j != 0 && j + 1 != 5 && i != 0 && i + 1 != 8) {
               boolean flag = false;
            } else {
               boolean flag1 = true;
            }

            p_228173_0_.setPixelRGBA(j, i, -1);
         }
      }

      p_228173_0_.untrack();
   });

   public int getWidth() {
      return 5;
   }

   public int getHeight() {
      return 8;
   }

   public float getAdvance() {
      return 6.0F;
   }

   public float getOversample() {
      return 1.0F;
   }

   public void uploadGlyph(int xOffset, int yOffset) {
      field_228172_b_.uploadTextureSub(0, xOffset, yOffset, false);
   }

   public boolean isColored() {
      return true;
   }
}
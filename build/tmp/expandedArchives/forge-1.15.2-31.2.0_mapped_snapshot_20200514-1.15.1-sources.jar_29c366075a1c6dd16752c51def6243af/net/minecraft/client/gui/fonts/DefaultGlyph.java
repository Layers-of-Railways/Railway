package net.minecraft.client.gui.fonts;

import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public enum DefaultGlyph implements IGlyphInfo {
   INSTANCE;

   private static final NativeImage NATIVE_IMAGE = Util.make(new NativeImage(NativeImage.PixelFormat.RGBA, 5, 8, false), (p_211580_0_) -> {
      for(int i = 0; i < 8; ++i) {
         for(int j = 0; j < 5; ++j) {
            boolean flag = j == 0 || j + 1 == 5 || i == 0 || i + 1 == 8;
            p_211580_0_.setPixelRGBA(j, i, flag ? -1 : 0);
         }
      }

      p_211580_0_.untrack();
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
      NATIVE_IMAGE.uploadTextureSub(0, xOffset, yOffset, false);
   }

   public boolean isColored() {
      return true;
   }
}
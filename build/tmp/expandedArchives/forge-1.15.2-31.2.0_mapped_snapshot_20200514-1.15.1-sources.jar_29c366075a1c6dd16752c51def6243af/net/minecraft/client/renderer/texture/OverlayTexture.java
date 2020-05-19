package net.minecraft.client.renderer.texture;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class OverlayTexture implements AutoCloseable {
   public static final int NO_OVERLAY = getPackedUV(0, 10);
   private final DynamicTexture texture = new DynamicTexture(16, 16, false);

   public OverlayTexture() {
      NativeImage nativeimage = this.texture.getTextureData();

      for(int i = 0; i < 16; ++i) {
         for(int j = 0; j < 16; ++j) {
            if (i < 8) {
               nativeimage.setPixelRGBA(j, i, -1308622593);
            } else {
               int k = (int)((1.0F - (float)j / 15.0F * 0.75F) * 255.0F);
               nativeimage.setPixelRGBA(j, i, k << 24 | 16777215);
            }
         }
      }

      RenderSystem.activeTexture(33985);
      this.texture.bindTexture();
      RenderSystem.matrixMode(5890);
      RenderSystem.loadIdentity();
      float f = 0.06666667F;
      RenderSystem.scalef(0.06666667F, 0.06666667F, 0.06666667F);
      RenderSystem.matrixMode(5888);
      this.texture.bindTexture();
      nativeimage.uploadTextureSub(0, 0, 0, 0, 0, nativeimage.getWidth(), nativeimage.getHeight(), false, true, false, false);
      RenderSystem.activeTexture(33984);
   }

   public void close() {
      this.texture.close();
   }

   public void setupOverlayColor() {
      RenderSystem.setupOverlayColor(this.texture::getGlTextureId, 16);
   }

   public static int getU(float uIn) {
      return (int)(uIn * 15.0F);
   }

   public static int getV(boolean hurtIn) {
      return hurtIn ? 3 : 10;
   }

   public static int getPackedUV(int uIn, int vIn) {
      return uIn | vIn << 16;
   }

   public static int getPackedUV(float uIn, boolean hurtIn) {
      return getPackedUV(getU(uIn), getV(hurtIn));
   }

   public void teardownOverlayColor() {
      RenderSystem.teardownOverlayColor();
   }
}
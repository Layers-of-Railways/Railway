package net.minecraft.client.renderer.texture;

import net.minecraft.util.Util;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MipmapGenerator {
   private static final float[] POWS22 = Util.make(new float[256], (p_229174_0_) -> {
      for(int i = 0; i < p_229174_0_.length; ++i) {
         p_229174_0_[i] = (float)Math.pow((double)((float)i / 255.0F), 2.2D);
      }

   });

   public static NativeImage[] generateMipmaps(NativeImage imageIn, int mipmapLevelsIn) {
      NativeImage[] anativeimage = new NativeImage[mipmapLevelsIn + 1];
      anativeimage[0] = imageIn;
      if (mipmapLevelsIn > 0) {
         boolean flag = false;

         label51:
         for(int i = 0; i < imageIn.getWidth(); ++i) {
            for(int j = 0; j < imageIn.getHeight(); ++j) {
               if (imageIn.getPixelRGBA(i, j) >> 24 == 0) {
                  flag = true;
                  break label51;
               }
            }
         }

         for(int k1 = 1; k1 <= mipmapLevelsIn; ++k1) {
            NativeImage nativeimage1 = anativeimage[k1 - 1];
            NativeImage nativeimage = new NativeImage(nativeimage1.getWidth() >> 1, nativeimage1.getHeight() >> 1, false);
            int k = nativeimage.getWidth();
            int l = nativeimage.getHeight();

            for(int i1 = 0; i1 < k; ++i1) {
               for(int j1 = 0; j1 < l; ++j1) {
                  nativeimage.setPixelRGBA(i1, j1, alphaBlend(nativeimage1.getPixelRGBA(i1 * 2 + 0, j1 * 2 + 0), nativeimage1.getPixelRGBA(i1 * 2 + 1, j1 * 2 + 0), nativeimage1.getPixelRGBA(i1 * 2 + 0, j1 * 2 + 1), nativeimage1.getPixelRGBA(i1 * 2 + 1, j1 * 2 + 1), flag));
               }
            }

            anativeimage[k1] = nativeimage;
         }
      }

      return anativeimage;
   }

   private static int alphaBlend(int col1, int col2, int col3, int col4, boolean transparent) {
      if (transparent) {
         float f = 0.0F;
         float f1 = 0.0F;
         float f2 = 0.0F;
         float f3 = 0.0F;
         if (col1 >> 24 != 0) {
            f += getPow22(col1 >> 24);
            f1 += getPow22(col1 >> 16);
            f2 += getPow22(col1 >> 8);
            f3 += getPow22(col1 >> 0);
         }

         if (col2 >> 24 != 0) {
            f += getPow22(col2 >> 24);
            f1 += getPow22(col2 >> 16);
            f2 += getPow22(col2 >> 8);
            f3 += getPow22(col2 >> 0);
         }

         if (col3 >> 24 != 0) {
            f += getPow22(col3 >> 24);
            f1 += getPow22(col3 >> 16);
            f2 += getPow22(col3 >> 8);
            f3 += getPow22(col3 >> 0);
         }

         if (col4 >> 24 != 0) {
            f += getPow22(col4 >> 24);
            f1 += getPow22(col4 >> 16);
            f2 += getPow22(col4 >> 8);
            f3 += getPow22(col4 >> 0);
         }

         f = f / 4.0F;
         f1 = f1 / 4.0F;
         f2 = f2 / 4.0F;
         f3 = f3 / 4.0F;
         int i1 = (int)(Math.pow((double)f, 0.45454545454545453D) * 255.0D);
         int j1 = (int)(Math.pow((double)f1, 0.45454545454545453D) * 255.0D);
         int k1 = (int)(Math.pow((double)f2, 0.45454545454545453D) * 255.0D);
         int l1 = (int)(Math.pow((double)f3, 0.45454545454545453D) * 255.0D);
         if (i1 < 96) {
            i1 = 0;
         }

         return i1 << 24 | j1 << 16 | k1 << 8 | l1;
      } else {
         int i = gammaBlend(col1, col2, col3, col4, 24);
         int j = gammaBlend(col1, col2, col3, col4, 16);
         int k = gammaBlend(col1, col2, col3, col4, 8);
         int l = gammaBlend(col1, col2, col3, col4, 0);
         return i << 24 | j << 16 | k << 8 | l;
      }
   }

   private static int gammaBlend(int col1, int col2, int col3, int col4, int bitOffset) {
      float f = getPow22(col1 >> bitOffset);
      float f1 = getPow22(col2 >> bitOffset);
      float f2 = getPow22(col3 >> bitOffset);
      float f3 = getPow22(col4 >> bitOffset);
      float f4 = (float)((double)((float)Math.pow((double)(f + f1 + f2 + f3) * 0.25D, 0.45454545454545453D)));
      return (int)((double)f4 * 255.0D);
   }

   private static float getPow22(int valIn) {
      return POWS22[valIn & 255];
   }
}
package net.minecraft.client.gui.fonts;

import java.io.Closeable;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.renderer.texture.Texture;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FontTexture extends Texture implements Closeable {
   private final ResourceLocation textureLocation;
   private final RenderType field_228158_e_;
   private final RenderType field_228159_f_;
   private final boolean colored;
   private final FontTexture.Entry entry;

   public FontTexture(ResourceLocation resourceLocationIn, boolean coloredIn) {
      this.textureLocation = resourceLocationIn;
      this.colored = coloredIn;
      this.entry = new FontTexture.Entry(0, 0, 256, 256);
      TextureUtil.prepareImage(coloredIn ? NativeImage.PixelFormatGLCode.RGBA : NativeImage.PixelFormatGLCode.INTENSITY, this.getGlTextureId(), 256, 256);
      this.field_228158_e_ = RenderType.getText(resourceLocationIn);
      this.field_228159_f_ = RenderType.getTextSeeThrough(resourceLocationIn);
   }

   public void loadTexture(IResourceManager manager) {
   }

   public void close() {
      this.deleteGlTexture();
   }

   @Nullable
   public TexturedGlyph createTexturedGlyph(IGlyphInfo glyphInfoIn) {
      if (glyphInfoIn.isColored() != this.colored) {
         return null;
      } else {
         FontTexture.Entry fonttexture$entry = this.entry.func_211224_a(glyphInfoIn);
         if (fonttexture$entry != null) {
            this.bindTexture();
            glyphInfoIn.uploadGlyph(fonttexture$entry.xOffset, fonttexture$entry.yOffset);
            float f = 256.0F;
            float f1 = 256.0F;
            float f2 = 0.01F;
            return new TexturedGlyph(this.field_228158_e_, this.field_228159_f_, ((float)fonttexture$entry.xOffset + 0.01F) / 256.0F, ((float)fonttexture$entry.xOffset - 0.01F + (float)glyphInfoIn.getWidth()) / 256.0F, ((float)fonttexture$entry.yOffset + 0.01F) / 256.0F, ((float)fonttexture$entry.yOffset - 0.01F + (float)glyphInfoIn.getHeight()) / 256.0F, glyphInfoIn.func_211198_f(), glyphInfoIn.func_211199_g(), glyphInfoIn.func_211200_h(), glyphInfoIn.func_211204_i());
         } else {
            return null;
         }
      }
   }

   public ResourceLocation getTextureLocation() {
      return this.textureLocation;
   }

   @OnlyIn(Dist.CLIENT)
   static class Entry {
      private final int xOffset;
      private final int yOffset;
      private final int field_211227_c;
      private final int field_211228_d;
      private FontTexture.Entry field_211229_e;
      private FontTexture.Entry field_211230_f;
      private boolean field_211231_g;

      private Entry(int p_i49711_1_, int p_i49711_2_, int p_i49711_3_, int p_i49711_4_) {
         this.xOffset = p_i49711_1_;
         this.yOffset = p_i49711_2_;
         this.field_211227_c = p_i49711_3_;
         this.field_211228_d = p_i49711_4_;
      }

      @Nullable
      FontTexture.Entry func_211224_a(IGlyphInfo p_211224_1_) {
         if (this.field_211229_e != null && this.field_211230_f != null) {
            FontTexture.Entry fonttexture$entry = this.field_211229_e.func_211224_a(p_211224_1_);
            if (fonttexture$entry == null) {
               fonttexture$entry = this.field_211230_f.func_211224_a(p_211224_1_);
            }

            return fonttexture$entry;
         } else if (this.field_211231_g) {
            return null;
         } else {
            int i = p_211224_1_.getWidth();
            int j = p_211224_1_.getHeight();
            if (i <= this.field_211227_c && j <= this.field_211228_d) {
               if (i == this.field_211227_c && j == this.field_211228_d) {
                  this.field_211231_g = true;
                  return this;
               } else {
                  int k = this.field_211227_c - i;
                  int l = this.field_211228_d - j;
                  if (k > l) {
                     this.field_211229_e = new FontTexture.Entry(this.xOffset, this.yOffset, i, this.field_211228_d);
                     this.field_211230_f = new FontTexture.Entry(this.xOffset + i + 1, this.yOffset, this.field_211227_c - i - 1, this.field_211228_d);
                  } else {
                     this.field_211229_e = new FontTexture.Entry(this.xOffset, this.yOffset, this.field_211227_c, j);
                     this.field_211230_f = new FontTexture.Entry(this.xOffset, this.yOffset + j + 1, this.field_211227_c, this.field_211228_d - j - 1);
                  }

                  return this.field_211229_e.func_211224_a(p_211224_1_);
               }
            } else {
               return null;
            }
         }
      }
   }
}
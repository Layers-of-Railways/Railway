package net.minecraft.client.gui.fonts.providers;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.fonts.IGlyphInfo;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class UnicodeTextureGlyphProvider implements IGlyphProvider {
   private static final Logger LOGGER = LogManager.getLogger();
   private final IResourceManager resourceManager;
   private final byte[] sizes;
   private final String template;
   private final Map<ResourceLocation, NativeImage> field_211845_e = Maps.newHashMap();

   public UnicodeTextureGlyphProvider(IResourceManager p_i49737_1_, byte[] p_i49737_2_, String p_i49737_3_) {
      this.resourceManager = p_i49737_1_;
      this.sizes = p_i49737_2_;
      this.template = p_i49737_3_;

      label322:
      for(int i = 0; i < 256; ++i) {
         char c0 = (char)(i * 256);
         ResourceLocation resourcelocation = this.getTextureFor(c0);

         try (
            IResource iresource = this.resourceManager.getResource(resourcelocation);
            NativeImage nativeimage = NativeImage.read(NativeImage.PixelFormat.RGBA, iresource.getInputStream());
         ) {
            if (nativeimage.getWidth() == 256 && nativeimage.getHeight() == 256) {
               int j = 0;

               while(true) {
                  if (j >= 256) {
                     continue label322;
                  }

                  byte b0 = p_i49737_2_[c0 + j];
                  if (b0 != 0 && func_212453_a(b0) > func_212454_b(b0)) {
                     p_i49737_2_[c0 + j] = 0;
                  }

                  ++j;
               }
            }
         } catch (IOException var43) {
            ;
         }

         Arrays.fill(p_i49737_2_, c0, c0 + 256, (byte)0);
      }

   }

   public void close() {
      this.field_211845_e.values().forEach(NativeImage::close);
   }

   private ResourceLocation getTextureFor(char p_211623_1_) {
      ResourceLocation resourcelocation = new ResourceLocation(String.format(this.template, String.format("%02x", p_211623_1_ / 256)));
      return new ResourceLocation(resourcelocation.getNamespace(), "textures/" + resourcelocation.getPath());
   }

   @Nullable
   public IGlyphInfo getGlyphInfo(char character) {
      byte b0 = this.sizes[character];
      if (b0 != 0) {
         NativeImage nativeimage = this.field_211845_e.computeIfAbsent(this.getTextureFor(character), this::loadTexture);
         if (nativeimage != null) {
            int i = func_212453_a(b0);
            return new UnicodeTextureGlyphProvider.GlpyhInfo(character % 16 * 16 + i, (character & 255) / 16 * 16, func_212454_b(b0) - i, 16, nativeimage);
         }
      }

      return null;
   }

   @Nullable
   private NativeImage loadTexture(ResourceLocation p_211255_1_) {
      try (IResource iresource = this.resourceManager.getResource(p_211255_1_)) {
         NativeImage nativeimage = NativeImage.read(NativeImage.PixelFormat.RGBA, iresource.getInputStream());
         return nativeimage;
      } catch (IOException ioexception) {
         LOGGER.error("Couldn't load texture {}", p_211255_1_, ioexception);
         return null;
      }
   }

   private static int func_212453_a(byte p_212453_0_) {
      return p_212453_0_ >> 4 & 15;
   }

   private static int func_212454_b(byte p_212454_0_) {
      return (p_212454_0_ & 15) + 1;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IGlyphProviderFactory {
      private final ResourceLocation sizes;
      private final String template;

      public Factory(ResourceLocation p_i49760_1_, String p_i49760_2_) {
         this.sizes = p_i49760_1_;
         this.template = p_i49760_2_;
      }

      public static IGlyphProviderFactory deserialize(JsonObject p_211629_0_) {
         return new UnicodeTextureGlyphProvider.Factory(new ResourceLocation(JSONUtils.getString(p_211629_0_, "sizes")), JSONUtils.getString(p_211629_0_, "template"));
      }

      @Nullable
      public IGlyphProvider create(IResourceManager resourceManagerIn) {
         try (IResource iresource = Minecraft.getInstance().getResourceManager().getResource(this.sizes)) {
            byte[] abyte = new byte[65536];
            iresource.getInputStream().read(abyte);
            UnicodeTextureGlyphProvider unicodetextureglyphprovider = new UnicodeTextureGlyphProvider(resourceManagerIn, abyte, this.template);
            return unicodetextureglyphprovider;
         } catch (IOException var17) {
            UnicodeTextureGlyphProvider.LOGGER.error("Cannot load {}, unicode glyphs will not render correctly", (Object)this.sizes);
            return null;
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class GlpyhInfo implements IGlyphInfo {
      private final int width;
      private final int height;
      private final int unpackSkipPixels;
      private final int unpackSkipRows;
      private final NativeImage texture;

      private GlpyhInfo(int p_i49758_1_, int p_i49758_2_, int p_i49758_3_, int p_i49758_4_, NativeImage p_i49758_5_) {
         this.width = p_i49758_3_;
         this.height = p_i49758_4_;
         this.unpackSkipPixels = p_i49758_1_;
         this.unpackSkipRows = p_i49758_2_;
         this.texture = p_i49758_5_;
      }

      public float getOversample() {
         return 2.0F;
      }

      public int getWidth() {
         return this.width;
      }

      public int getHeight() {
         return this.height;
      }

      public float getAdvance() {
         return (float)(this.width / 2 + 1);
      }

      public void uploadGlyph(int xOffset, int yOffset) {
         this.texture.uploadTextureSub(0, xOffset, yOffset, this.unpackSkipPixels, this.unpackSkipRows, this.width, this.height, false, false);
      }

      public boolean isColored() {
         return this.texture.getFormat().getPixelSize() > 1;
      }

      public float getShadowOffset() {
         return 0.5F;
      }

      public float getBoldOffset() {
         return 0.5F;
      }
   }
}
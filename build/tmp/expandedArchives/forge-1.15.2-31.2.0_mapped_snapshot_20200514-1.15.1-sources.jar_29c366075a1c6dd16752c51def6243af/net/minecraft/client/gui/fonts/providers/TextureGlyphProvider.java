package net.minecraft.client.gui.fonts.providers;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import it.unimi.dsi.fastutil.chars.Char2ObjectMap;
import it.unimi.dsi.fastutil.chars.Char2ObjectOpenHashMap;
import java.io.IOException;
import java.util.List;
import javax.annotation.Nullable;
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
public class TextureGlyphProvider implements IGlyphProvider {
   private static final Logger LOGGER = LogManager.getLogger();
   private final NativeImage texture;
   private final Char2ObjectMap<TextureGlyphProvider.GlyphInfo> glyphInfos;

   public TextureGlyphProvider(NativeImage nativeImageIn, Char2ObjectMap<TextureGlyphProvider.GlyphInfo> mapGlyphInfosIn) {
      this.texture = nativeImageIn;
      this.glyphInfos = mapGlyphInfosIn;
   }

   public void close() {
      this.texture.close();
   }

   @Nullable
   public IGlyphInfo getGlyphInfo(char character) {
      return this.glyphInfos.get(character);
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IGlyphProviderFactory {
      private final ResourceLocation file;
      private final List<String> chars;
      private final int height;
      private final int ascent;

      public Factory(ResourceLocation textureLocationIn, int heightIn, int ascentIn, List<String> listCharRowsIn) {
         this.file = new ResourceLocation(textureLocationIn.getNamespace(), "textures/" + textureLocationIn.getPath());
         this.chars = listCharRowsIn;
         this.height = heightIn;
         this.ascent = ascentIn;
      }

      public static TextureGlyphProvider.Factory deserialize(JsonObject jsonIn) {
         int i = JSONUtils.getInt(jsonIn, "height", 8);
         int j = JSONUtils.getInt(jsonIn, "ascent");
         if (j > i) {
            throw new JsonParseException("Ascent " + j + " higher than height " + i);
         } else {
            List<String> list = Lists.newArrayList();
            JsonArray jsonarray = JSONUtils.getJsonArray(jsonIn, "chars");

            for(int k = 0; k < jsonarray.size(); ++k) {
               String s = JSONUtils.getString(jsonarray.get(k), "chars[" + k + "]");
               if (k > 0) {
                  int l = s.length();
                  int i1 = list.get(0).length();
                  if (l != i1) {
                     throw new JsonParseException("Elements of chars have to be the same length (found: " + l + ", expected: " + i1 + "), pad with space or \\u0000");
                  }
               }

               list.add(s);
            }

            if (!list.isEmpty() && !list.get(0).isEmpty()) {
               return new TextureGlyphProvider.Factory(new ResourceLocation(JSONUtils.getString(jsonIn, "file")), i, j, list);
            } else {
               throw new JsonParseException("Expected to find data in chars, found none.");
            }
         }
      }

      @Nullable
      public IGlyphProvider create(IResourceManager resourceManagerIn) {
         try (IResource iresource = resourceManagerIn.getResource(this.file)) {
            NativeImage nativeimage = NativeImage.read(NativeImage.PixelFormat.RGBA, iresource.getInputStream());
            int i = nativeimage.getWidth();
            int j = nativeimage.getHeight();
            int k = i / this.chars.get(0).length();
            int l = j / this.chars.size();
            float f = (float)this.height / (float)l;
            Char2ObjectMap<TextureGlyphProvider.GlyphInfo> char2objectmap = new Char2ObjectOpenHashMap<>();

            for(int i1 = 0; i1 < this.chars.size(); ++i1) {
               String s = this.chars.get(i1);

               for(int j1 = 0; j1 < s.length(); ++j1) {
                  char c0 = s.charAt(j1);
                  if (c0 != 0 && c0 != ' ') {
                     int k1 = this.getCharacterWidth(nativeimage, k, l, j1, i1);
                     TextureGlyphProvider.GlyphInfo textureglyphprovider$glyphinfo = char2objectmap.put(c0, new TextureGlyphProvider.GlyphInfo(f, nativeimage, j1 * k, i1 * l, k, l, (int)(0.5D + (double)((float)k1 * f)) + 1, this.ascent));
                     if (textureglyphprovider$glyphinfo != null) {
                        TextureGlyphProvider.LOGGER.warn("Codepoint '{}' declared multiple times in {}", Integer.toHexString(c0), this.file);
                     }
                  }
               }
            }

            TextureGlyphProvider textureglyphprovider = new TextureGlyphProvider(nativeimage, char2objectmap);
            return textureglyphprovider;
         } catch (IOException ioexception) {
            throw new RuntimeException(ioexception.getMessage());
         }
      }

      private int getCharacterWidth(NativeImage nativeImageIn, int charWidthIn, int charHeightInsp, int columnIn, int rowIn) {
         int i;
         for(i = charWidthIn - 1; i >= 0; --i) {
            int j = columnIn * charWidthIn + i;

            for(int k = 0; k < charHeightInsp; ++k) {
               int l = rowIn * charHeightInsp + k;
               if (nativeImageIn.getPixelLuminanceOrAlpha(j, l) != 0) {
                  return i + 1;
               }
            }
         }

         return i + 1;
      }
   }

   @OnlyIn(Dist.CLIENT)
   static final class GlyphInfo implements IGlyphInfo {
      private final float scale;
      private final NativeImage texture;
      private final int unpackSkipPixels;
      private final int unpackSkipRows;
      private final int width;
      private final int height;
      private final int advanceWidth;
      private final int ascent;

      private GlyphInfo(float p_i49748_1_, NativeImage p_i49748_2_, int p_i49748_3_, int p_i49748_4_, int p_i49748_5_, int p_i49748_6_, int p_i49748_7_, int p_i49748_8_) {
         this.scale = p_i49748_1_;
         this.texture = p_i49748_2_;
         this.unpackSkipPixels = p_i49748_3_;
         this.unpackSkipRows = p_i49748_4_;
         this.width = p_i49748_5_;
         this.height = p_i49748_6_;
         this.advanceWidth = p_i49748_7_;
         this.ascent = p_i49748_8_;
      }

      public float getOversample() {
         return 1.0F / this.scale;
      }

      public int getWidth() {
         return this.width;
      }

      public int getHeight() {
         return this.height;
      }

      public float getAdvance() {
         return (float)this.advanceWidth;
      }

      public float getBearingY() {
         return IGlyphInfo.super.getBearingY() + 7.0F - (float)this.ascent;
      }

      public void uploadGlyph(int xOffset, int yOffset) {
         this.texture.uploadTextureSub(0, xOffset, yOffset, this.unpackSkipPixels, this.unpackSkipRows, this.width, this.height, false, false);
      }

      public boolean isColored() {
         return this.texture.getFormat().getPixelSize() > 1;
      }
   }
}
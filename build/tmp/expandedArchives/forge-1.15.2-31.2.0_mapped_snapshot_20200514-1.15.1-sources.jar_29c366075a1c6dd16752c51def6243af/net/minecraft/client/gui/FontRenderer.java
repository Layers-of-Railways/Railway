package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import com.ibm.icu.text.ArabicShaping;
import com.ibm.icu.text.ArabicShapingException;
import com.ibm.icu.text.Bidi;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import net.minecraft.client.gui.fonts.EmptyGlyph;
import net.minecraft.client.gui.fonts.Font;
import net.minecraft.client.gui.fonts.IGlyph;
import net.minecraft.client.gui.fonts.TexturedGlyph;
import net.minecraft.client.gui.fonts.providers.IGlyphProvider;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.TransformationMatrix;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FontRenderer implements AutoCloseable {
   public final int FONT_HEIGHT = 9;
   public final Random random = new Random();
   private final TextureManager textureManager;
   private final Font font;
   private boolean bidiFlag;

   public FontRenderer(TextureManager textureManagerIn, Font fontIn) {
      this.textureManager = textureManagerIn;
      this.font = fontIn;
   }

   public void setGlyphProviders(List<IGlyphProvider> gliphProviders) {
      this.font.setGlyphProviders(gliphProviders);
   }

   public void close() {
      this.font.close();
   }

   /**
    * Draws the specified string with a shadow.
    */
   public int drawStringWithShadow(String text, float x, float y, int color) {
      RenderSystem.enableAlphaTest();
      return this.renderString(text, x, y, color, TransformationMatrix.identity().getMatrix(), true);
   }

   public int drawString(String text, float x, float y, int color) {
      RenderSystem.enableAlphaTest();
      return this.renderString(text, x, y, color, TransformationMatrix.identity().getMatrix(), false);
   }

   /**
    * Apply Unicode Bidirectional Algorithm to string and return a new possibly reordered string for visual rendering.
    */
   public String bidiReorder(String text) {
      try {
         Bidi bidi = new Bidi((new ArabicShaping(8)).shape(text), 127);
         bidi.setReorderingMode(0);
         return bidi.writeReordered(2);
      } catch (ArabicShapingException var3) {
         return text;
      }
   }

   private int renderString(String text, float x, float y, int color, Matrix4f matrix, boolean dropShadow) {
      if (text == null) {
         return 0;
      } else {
         IRenderTypeBuffer.Impl irendertypebuffer$impl = IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer());
         int i = this.renderString(text, x, y, color, dropShadow, matrix, irendertypebuffer$impl, false, 0, 15728880);
         irendertypebuffer$impl.finish();
         return i;
      }
   }

   public int renderString(String text, float x, float y, int color, boolean dropShadow, Matrix4f matrix, IRenderTypeBuffer buffer, boolean transparentIn, int colorBackgroundIn, int packedLight) {
      return this.renderStringAt(text, x, y, color, dropShadow, matrix, buffer, transparentIn, colorBackgroundIn, packedLight);
   }

   private int renderStringAt(String text, float x, float y, int color, boolean dropShadow, Matrix4f matrix, IRenderTypeBuffer buffer, boolean transparentIn, int colorBackgroundIn, int packedLight) {
      if (this.bidiFlag) {
         text = this.bidiReorder(text);
      }

      if ((color & -67108864) == 0) {
         color |= -16777216;
      }

      if (dropShadow) {
         this.renderStringAtPos(text, x, y, color, true, matrix, buffer, transparentIn, colorBackgroundIn, packedLight);
      }

      Matrix4f matrix4f = matrix.copy();
      matrix4f.translate(new Vector3f(0.0F, 0.0F, 0.001F));
      x = this.renderStringAtPos(text, x, y, color, false, matrix4f, buffer, transparentIn, colorBackgroundIn, packedLight);
      return (int)x + (dropShadow ? 1 : 0);
   }

   private float renderStringAtPos(String text, float x, float y, int color, boolean isShadow, Matrix4f matrix, IRenderTypeBuffer buffer, boolean isTransparent, int colorBackgroundIn, int packedLight) {
      float f = isShadow ? 0.25F : 1.0F;
      float f1 = (float)(color >> 16 & 255) / 255.0F * f;
      float f2 = (float)(color >> 8 & 255) / 255.0F * f;
      float f3 = (float)(color & 255) / 255.0F * f;
      float f4 = x;
      float f5 = f1;
      float f6 = f2;
      float f7 = f3;
      float f8 = (float)(color >> 24 & 255) / 255.0F;
      boolean flag = false;
      boolean flag1 = false;
      boolean flag2 = false;
      boolean flag3 = false;
      boolean flag4 = false;
      List<TexturedGlyph.Effect> list = Lists.newArrayList();

      for(int i = 0; i < text.length(); ++i) {
         char c0 = text.charAt(i);
         if (c0 == 167 && i + 1 < text.length()) {
            TextFormatting textformatting = TextFormatting.fromFormattingCode(text.charAt(i + 1));
            if (textformatting != null) {
               if (textformatting.isNormalStyle()) {
                  flag = false;
                  flag1 = false;
                  flag4 = false;
                  flag3 = false;
                  flag2 = false;
                  f5 = f1;
                  f6 = f2;
                  f7 = f3;
               }

               if (textformatting.getColor() != null) {
                  int j = textformatting.getColor();
                  f5 = (float)(j >> 16 & 255) / 255.0F * f;
                  f6 = (float)(j >> 8 & 255) / 255.0F * f;
                  f7 = (float)(j & 255) / 255.0F * f;
               } else if (textformatting == TextFormatting.OBFUSCATED) {
                  flag = true;
               } else if (textformatting == TextFormatting.BOLD) {
                  flag1 = true;
               } else if (textformatting == TextFormatting.STRIKETHROUGH) {
                  flag4 = true;
               } else if (textformatting == TextFormatting.UNDERLINE) {
                  flag3 = true;
               } else if (textformatting == TextFormatting.ITALIC) {
                  flag2 = true;
               }
            }

            ++i;
         } else {
            IGlyph iglyph = this.font.findGlyph(c0);
            TexturedGlyph texturedglyph = flag && c0 != ' ' ? this.font.obfuscate(iglyph) : this.font.getGlyph(c0);
            if (!(texturedglyph instanceof EmptyGlyph)) {
               float f9 = flag1 ? iglyph.getBoldOffset() : 0.0F;
               float f10 = isShadow ? iglyph.getShadowOffset() : 0.0F;
               IVertexBuilder ivertexbuilder = buffer.getBuffer(texturedglyph.getRenderType(isTransparent));
               this.drawGlyph(texturedglyph, flag1, flag2, f9, f4 + f10, y + f10, matrix, ivertexbuilder, f5, f6, f7, f8, packedLight);
            }

            float f15 = iglyph.getAdvance(flag1);
            float f16 = isShadow ? 1.0F : 0.0F;
            if (flag4) {
               list.add(new TexturedGlyph.Effect(f4 + f16 - 1.0F, y + f16 + 4.5F, f4 + f16 + f15, y + f16 + 4.5F - 1.0F, -0.01F, f5, f6, f7, f8));
            }

            if (flag3) {
               list.add(new TexturedGlyph.Effect(f4 + f16 - 1.0F, y + f16 + 9.0F, f4 + f16 + f15, y + f16 + 9.0F - 1.0F, -0.01F, f5, f6, f7, f8));
            }

            f4 += f15;
         }
      }

      if (colorBackgroundIn != 0) {
         float f11 = (float)(colorBackgroundIn >> 24 & 255) / 255.0F;
         float f12 = (float)(colorBackgroundIn >> 16 & 255) / 255.0F;
         float f13 = (float)(colorBackgroundIn >> 8 & 255) / 255.0F;
         float f14 = (float)(colorBackgroundIn & 255) / 255.0F;
         list.add(new TexturedGlyph.Effect(x - 1.0F, y + 9.0F, f4 + 1.0F, y - 1.0F, 0.01F, f12, f13, f14, f11));
      }

      if (!list.isEmpty()) {
         TexturedGlyph texturedglyph1 = this.font.getWhiteGlyph();
         IVertexBuilder ivertexbuilder1 = buffer.getBuffer(texturedglyph1.getRenderType(isTransparent));

         for(TexturedGlyph.Effect texturedglyph$effect : list) {
            texturedglyph1.renderEffect(texturedglyph$effect, matrix, ivertexbuilder1, packedLight);
         }
      }

      return f4;
   }

   private void drawGlyph(TexturedGlyph glyphIn, boolean boldIn, boolean italicIn, float boldOffsetIn, float xIn, float yIn, Matrix4f matrix, IVertexBuilder bufferIn, float redIn, float greenIn, float blueIn, float alphaIn, int packedLight) {
      glyphIn.render(italicIn, xIn, yIn, matrix, bufferIn, redIn, greenIn, blueIn, alphaIn, packedLight);
      if (boldIn) {
         glyphIn.render(italicIn, xIn + boldOffsetIn, yIn, matrix, bufferIn, redIn, greenIn, blueIn, alphaIn, packedLight);
      }

   }

   /**
    * Returns the width of this string. Equivalent of FontMetrics.stringWidth(String s).
    */
   public int getStringWidth(String text) {
      if (text == null) {
         return 0;
      } else {
         float f = 0.0F;
         boolean flag = false;

         for(int i = 0; i < text.length(); ++i) {
            char c0 = text.charAt(i);
            if (c0 == 167 && i < text.length() - 1) {
               ++i;
               TextFormatting textformatting = TextFormatting.fromFormattingCode(text.charAt(i));
               if (textformatting == TextFormatting.BOLD) {
                  flag = true;
               } else if (textformatting != null && textformatting.isNormalStyle()) {
                  flag = false;
               }
            } else {
               f += this.font.findGlyph(c0).getAdvance(flag);
            }
         }

         return MathHelper.ceil(f);
      }
   }

   public float getCharWidth(char character) {
      return character == 167 ? 0.0F : this.font.findGlyph(character).getAdvance(false);
   }

   /**
    * Trims a string to fit a specified Width.
    */
   public String trimStringToWidth(String text, int width) {
      return this.trimStringToWidth(text, width, false);
   }

   /**
    * Trims a string to a specified width, optionally starting from the end and working backwards.
    * <h3>Samples:</h3>
    * (Assuming that {@link #getCharWidth(char)} returns <code>6</code> for all of the characters in
    * <code>0123456789</code> on the current resource pack)
    * <table>
    * <tr><th>Input</th><th>Returns</th></tr>
    * <tr><td><code>trimStringToWidth("0123456789", 1, false)</code></td><td><samp>""</samp></td></tr>
    * <tr><td><code>trimStringToWidth("0123456789", 6, false)</code></td><td><samp>"0"</samp></td></tr>
    * <tr><td><code>trimStringToWidth("0123456789", 29, false)</code></td><td><samp>"0123"</samp></td></tr>
    * <tr><td><code>trimStringToWidth("0123456789", 30, false)</code></td><td><samp>"01234"</samp></td></tr>
    * <tr><td><code>trimStringToWidth("0123456789", 9001, false)</code></td><td><samp>"0123456789"</samp></td></tr>
    * <tr><td><code>trimStringToWidth("0123456789", 1, true)</code></td><td><samp>""</samp></td></tr>
    * <tr><td><code>trimStringToWidth("0123456789", 6, true)</code></td><td><samp>"9"</samp></td></tr>
    * <tr><td><code>trimStringToWidth("0123456789", 29, true)</code></td><td><samp>"6789"</samp></td></tr>
    * <tr><td><code>trimStringToWidth("0123456789", 30, true)</code></td><td><samp>"56789"</samp></td></tr>
    * <tr><td><code>trimStringToWidth("0123456789", 9001, true)</code></td><td><samp>"0123456789"</samp></td></tr>
    * </table>
    */
   public String trimStringToWidth(String text, int width, boolean reverse) {
      StringBuilder stringbuilder = new StringBuilder();
      float f = 0.0F;
      int i = reverse ? text.length() - 1 : 0;
      int j = reverse ? -1 : 1;
      boolean flag = false;
      boolean flag1 = false;

      for(int k = i; k >= 0 && k < text.length() && f < (float)width; k += j) {
         char c0 = text.charAt(k);
         if (flag) {
            flag = false;
            TextFormatting textformatting = TextFormatting.fromFormattingCode(c0);
            if (textformatting == TextFormatting.BOLD) {
               flag1 = true;
            } else if (textformatting != null && textformatting.isNormalStyle()) {
               flag1 = false;
            }
         } else if (c0 == 167) {
            flag = true;
         } else {
            f += this.getCharWidth(c0);
            if (flag1) {
               ++f;
            }
         }

         if (f > (float)width) {
            break;
         }

         if (reverse) {
            stringbuilder.insert(0, c0);
         } else {
            stringbuilder.append(c0);
         }
      }

      return stringbuilder.toString();
   }

   /**
    * Remove all newline characters from the end of the string
    */
   private String trimStringNewline(String text) {
      while(text != null && text.endsWith("\n")) {
         text = text.substring(0, text.length() - 1);
      }

      return text;
   }

   /**
    * Splits and draws a String with wordwrap (maximum length is parameter k)
    */
   public void drawSplitString(String str, int x, int y, int wrapWidth, int textColor) {
      str = this.trimStringNewline(str);
      this.renderSplitString(str, x, y, wrapWidth, textColor);
   }

   private void renderSplitString(String str, int x, int y, int wrapWidth, int textColor) {
      List<String> list = this.listFormattedStringToWidth(str, wrapWidth);
      Matrix4f matrix4f = TransformationMatrix.identity().getMatrix();

      for(String s : list) {
         float f = (float)x;
         if (this.bidiFlag) {
            int i = this.getStringWidth(this.bidiReorder(s));
            f += (float)(wrapWidth - i);
         }

         this.renderString(s, f, (float)y, textColor, matrix4f, false);
         y += 9;
      }

   }

   /**
    * Returns the height (in pixels) of the given string if it is wordwrapped to the given max width.
    */
   public int getWordWrappedHeight(String str, int maxLength) {
      return 9 * this.listFormattedStringToWidth(str, maxLength).size();
   }

   /**
    * Set bidiFlag to control if the Unicode Bidirectional Algorithm should be run before rendering any string.
    */
   public void setBidiFlag(boolean bidiFlagIn) {
      this.bidiFlag = bidiFlagIn;
   }

   /**
    * Breaks a string into a list of pieces where the width of each line is always less than or equal to the provided
    * width. Formatting codes will be preserved between lines.
    */
   public List<String> listFormattedStringToWidth(String str, int wrapWidth) {
      return Arrays.asList(this.wrapFormattedStringToWidth(str, wrapWidth).split("\n"));
   }

   /**
    * Inserts newline and formatting into a string to wrap it within the specified width.
    */
   public String wrapFormattedStringToWidth(String str, int wrapWidth) {
      String s;
      String s1;
      for(s = ""; !str.isEmpty(); s = s + s1 + "\n") {
         int i = this.sizeStringToWidth(str, wrapWidth);
         if (str.length() <= i) {
            return s + str;
         }

         s1 = str.substring(0, i);
         char c0 = str.charAt(i);
         boolean flag = c0 == ' ' || c0 == '\n';
         str = TextFormatting.getFormatString(s1) + str.substring(i + (flag ? 1 : 0));
      }

      return s;
   }

   /**
    * Determines how many characters from the string will fit into the specified width.
    */
   public int sizeStringToWidth(String str, int wrapWidth) {
      int i = Math.max(1, wrapWidth);
      int j = str.length();
      float f = 0.0F;
      int k = 0;
      int l = -1;
      boolean flag = false;

      for(boolean flag1 = true; k < j; ++k) {
         char c0 = str.charAt(k);
         switch(c0) {
         case '\n':
            --k;
            break;
         case ' ':
            l = k;
         default:
            if (f != 0.0F) {
               flag1 = false;
            }

            f += this.getCharWidth(c0);
            if (flag) {
               ++f;
            }
            break;
         case '\u00a7':
            if (k < j - 1) {
               ++k;
               TextFormatting textformatting = TextFormatting.fromFormattingCode(str.charAt(k));
               if (textformatting == TextFormatting.BOLD) {
                  flag = true;
               } else if (textformatting != null && textformatting.isNormalStyle()) {
                  flag = false;
               }
            }
         }

         if (c0 == '\n') {
            ++k;
            l = k;
            break;
         }

         if (f > (float)i) {
            if (flag1) {
               ++k;
            }
            break;
         }
      }

      return k != j && l != -1 && l < k ? l : k;
   }

   public int getWordPosition(String stringIn, int directionIn, int p_216863_3_, boolean p_216863_4_) {
      int i = p_216863_3_;
      boolean flag = directionIn < 0;
      int j = Math.abs(directionIn);

      for(int k = 0; k < j; ++k) {
         if (flag) {
            while(p_216863_4_ && i > 0 && (stringIn.charAt(i - 1) == ' ' || stringIn.charAt(i - 1) == '\n')) {
               --i;
            }

            while(i > 0 && stringIn.charAt(i - 1) != ' ' && stringIn.charAt(i - 1) != '\n') {
               --i;
            }
         } else {
            int l = stringIn.length();
            int i1 = stringIn.indexOf(32, i);
            int j1 = stringIn.indexOf(10, i);
            if (i1 == -1 && j1 == -1) {
               i = -1;
            } else if (i1 != -1 && j1 != -1) {
               i = Math.min(i1, j1);
            } else if (i1 != -1) {
               i = i1;
            } else {
               i = j1;
            }

            if (i == -1) {
               i = l;
            } else {
               while(p_216863_4_ && i < l && (stringIn.charAt(i) == ' ' || stringIn.charAt(i) == '\n')) {
                  ++i;
               }
            }
         }
      }

      return i;
   }

   /**
    * Get bidiFlag that controls if the Unicode Bidirectional Algorithm should be run before rendering any string
    */
   public boolean getBidiFlag() {
      return this.bidiFlag;
   }
}
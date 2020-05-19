package net.minecraft.client.renderer.texture;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.SpriteAwareVertexBuilder;
import net.minecraft.client.resources.data.AnimationFrame;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TextureAtlasSprite implements AutoCloseable, net.minecraftforge.client.extensions.IForgeTextureAtlasSprite {
   private final AtlasTexture atlasTexture;
   private final TextureAtlasSprite.Info spriteInfo;
   private final AnimationMetadataSection animationMetadata;
   protected final NativeImage[] frames;
   private final int[] framesX;
   private final int[] framesY;
   @Nullable
   private final TextureAtlasSprite.InterpolationData interpolationData;
   private final int x;
   private final int y;
   private final float minU;
   private final float maxU;
   private final float minV;
   private final float maxV;
   private int frameCounter;
   private int tickCounter;

   protected TextureAtlasSprite(AtlasTexture atlasTextureIn, TextureAtlasSprite.Info spriteInfoIn, int mipmapLevelsIn, int atlasWidthIn, int atlasHeightIn, int xIn, int yIn, NativeImage imageIn) {
      this.atlasTexture = atlasTextureIn;
      AnimationMetadataSection animationmetadatasection = spriteInfoIn.spriteAnimationMetadata;
      int i = spriteInfoIn.spriteWidth;
      int j = spriteInfoIn.spriteHeight;
      this.x = xIn;
      this.y = yIn;
      this.minU = (float)xIn / (float)atlasWidthIn;
      this.maxU = (float)(xIn + i) / (float)atlasWidthIn;
      this.minV = (float)yIn / (float)atlasHeightIn;
      this.maxV = (float)(yIn + j) / (float)atlasHeightIn;
      int k = imageIn.getWidth() / animationmetadatasection.getFrameWidth(i);
      int l = imageIn.getHeight() / animationmetadatasection.getFrameHeight(j);
      if (animationmetadatasection.getFrameCount() > 0) {
         int i1 = animationmetadatasection.getFrameIndexSet().stream().max(Integer::compareTo).get() + 1;
         this.framesX = new int[i1];
         this.framesY = new int[i1];
         Arrays.fill(this.framesX, -1);
         Arrays.fill(this.framesY, -1);

         for(int j1 : animationmetadatasection.getFrameIndexSet()) {
            if (j1 >= k * l) {
               throw new RuntimeException("invalid frameindex " + j1);
            }

            int k1 = j1 / k;
            int l1 = j1 % k;
            this.framesX[j1] = l1;
            this.framesY[j1] = k1;
         }
      } else {
         List<AnimationFrame> list = Lists.newArrayList();
         int i2 = k * l;
         this.framesX = new int[i2];
         this.framesY = new int[i2];

         for(int j2 = 0; j2 < l; ++j2) {
            for(int k2 = 0; k2 < k; ++k2) {
               int l2 = j2 * k + k2;
               this.framesX[l2] = k2;
               this.framesY[l2] = j2;
               list.add(new AnimationFrame(l2, -1));
            }
         }

         animationmetadatasection = new AnimationMetadataSection(list, i, j, animationmetadatasection.getFrameTime(), animationmetadatasection.isInterpolate());
      }

      this.spriteInfo = new TextureAtlasSprite.Info(spriteInfoIn.spriteLocation, i, j, animationmetadatasection);
      this.animationMetadata = animationmetadatasection;

      try {
         try {
            this.frames = MipmapGenerator.generateMipmaps(imageIn, mipmapLevelsIn);
         } catch (Throwable throwable) {
            CrashReport crashreport1 = CrashReport.makeCrashReport(throwable, "Generating mipmaps for frame");
            CrashReportCategory crashreportcategory1 = crashreport1.makeCategory("Frame being iterated");
            crashreportcategory1.addDetail("First frame", () -> {
               StringBuilder stringbuilder = new StringBuilder();
               if (stringbuilder.length() > 0) {
                  stringbuilder.append(", ");
               }

               stringbuilder.append(imageIn.getWidth()).append("x").append(imageIn.getHeight());
               return stringbuilder.toString();
            });
            throw new ReportedException(crashreport1);
         }
      } catch (Throwable throwable1) {
         CrashReport crashreport = CrashReport.makeCrashReport(throwable1, "Applying mipmap");
         CrashReportCategory crashreportcategory = crashreport.makeCategory("Sprite being mipmapped");
         crashreportcategory.addDetail("Sprite name", () -> {
            return this.getName().toString();
         });
         crashreportcategory.addDetail("Sprite size", () -> {
            return this.getWidth() + " x " + this.getHeight();
         });
         crashreportcategory.addDetail("Sprite frames", () -> {
            return this.getFrameCount() + " frames";
         });
         crashreportcategory.addDetail("Mipmap levels", mipmapLevelsIn);
         throw new ReportedException(crashreport);
      }

      if (animationmetadatasection.isInterpolate()) {
         this.interpolationData = new TextureAtlasSprite.InterpolationData(spriteInfoIn, mipmapLevelsIn);
      } else {
         this.interpolationData = null;
      }

   }

   private void uploadFrames(int index) {
      int i = this.framesX[index] * this.spriteInfo.spriteWidth;
      int j = this.framesY[index] * this.spriteInfo.spriteHeight;
      this.uploadFrames(i, j, this.frames);
   }

   private void uploadFrames(int xOffsetIn, int yOffsetIn, NativeImage[] framesIn) {
      for(int i = 0; i < this.frames.length; ++i) {
         if ((this.spriteInfo.spriteWidth >> i <= 0) || (this.spriteInfo.spriteHeight >> i <= 0)) break;
         framesIn[i].uploadTextureSub(i, this.x >> i, this.y >> i, xOffsetIn >> i, yOffsetIn >> i, this.spriteInfo.spriteWidth >> i, this.spriteInfo.spriteHeight >> i, this.frames.length > 1, false);
      }

   }

   /**
    * Returns the width of the icon, in pixels.
    */
   public int getWidth() {
      return this.spriteInfo.spriteWidth;
   }

   /**
    * Returns the height of the icon, in pixels.
    */
   public int getHeight() {
      return this.spriteInfo.spriteHeight;
   }

   /**
    * Returns the minimum U coordinate to use when rendering with this icon.
    */
   public float getMinU() {
      return this.minU;
   }

   /**
    * Returns the maximum U coordinate to use when rendering with this icon.
    */
   public float getMaxU() {
      return this.maxU;
   }

   /**
    * Gets a U coordinate on the icon. 0 returns uMin and 16 returns uMax. Other arguments return in-between values.
    */
   public float getInterpolatedU(double u) {
      float f = this.maxU - this.minU;
      return this.minU + f * (float)u / 16.0F;
   }

   /**
    * Returns the minimum V coordinate to use when rendering with this icon.
    */
   public float getMinV() {
      return this.minV;
   }

   /**
    * Returns the maximum V coordinate to use when rendering with this icon.
    */
   public float getMaxV() {
      return this.maxV;
   }

   /**
    * Gets a V coordinate on the icon. 0 returns vMin and 16 returns vMax. Other arguments return in-between values.
    */
   public float getInterpolatedV(double v) {
      float f = this.maxV - this.minV;
      return this.minV + f * (float)v / 16.0F;
   }

   public ResourceLocation getName() {
      return this.spriteInfo.spriteLocation;
   }

   public AtlasTexture getAtlasTexture() {
      return this.atlasTexture;
   }

   public int getFrameCount() {
      return this.framesX.length;
   }

   public void close() {
      for(NativeImage nativeimage : this.frames) {
         if (nativeimage != null) {
            nativeimage.close();
         }
      }

      if (this.interpolationData != null) {
         this.interpolationData.close();
      }

   }

   public String toString() {
      int i = this.framesX.length;
      return "TextureAtlasSprite{name='" + this.spriteInfo.spriteLocation + '\'' + ", frameCount=" + i + ", x=" + this.x + ", y=" + this.y + ", height=" + this.spriteInfo.spriteHeight + ", width=" + this.spriteInfo.spriteWidth + ", u0=" + this.minU + ", u1=" + this.maxU + ", v0=" + this.minV + ", v1=" + this.maxV + '}';
   }

   public boolean isPixelTransparent(int frameIndex, int pixelX, int pixelY) {
      return (this.frames[0].getPixelRGBA(pixelX + this.framesX[frameIndex] * this.spriteInfo.spriteWidth, pixelY + this.framesY[frameIndex] * this.spriteInfo.spriteHeight) >> 24 & 255) == 0;
   }

   public void uploadMipmaps() {
      this.uploadFrames(0);
   }

   private float getAtlasSize() {
      float f = (float)this.spriteInfo.spriteWidth / (this.maxU - this.minU);
      float f1 = (float)this.spriteInfo.spriteHeight / (this.maxV - this.minV);
      return Math.max(f1, f);
   }

   public float getUvShrinkRatio() {
      return 4.0F / this.getAtlasSize();
   }

   public void updateAnimation() {
      ++this.tickCounter;
      if (this.tickCounter >= this.animationMetadata.getFrameTimeSingle(this.frameCounter)) {
         int i = this.animationMetadata.getFrameIndex(this.frameCounter);
         int j = this.animationMetadata.getFrameCount() == 0 ? this.getFrameCount() : this.animationMetadata.getFrameCount();
         this.frameCounter = (this.frameCounter + 1) % j;
         this.tickCounter = 0;
         int k = this.animationMetadata.getFrameIndex(this.frameCounter);
         if (i != k && k >= 0 && k < this.getFrameCount()) {
            this.uploadFrames(k);
         }
      } else if (this.interpolationData != null) {
         if (!RenderSystem.isOnRenderThread()) {
            RenderSystem.recordRenderCall(() -> {
               this.interpolationData.uploadInterpolated();
            });
         } else {
            this.interpolationData.uploadInterpolated();
         }
      }

   }

   public boolean hasAnimationMetadata() {
      return this.animationMetadata.getFrameCount() > 1;
   }

   public IVertexBuilder wrapBuffer(IVertexBuilder bufferIn) {
      return new SpriteAwareVertexBuilder(bufferIn, this);
   }

   @OnlyIn(Dist.CLIENT)
   public static final class Info {
      private final ResourceLocation spriteLocation;
      private final int spriteWidth;
      private final int spriteHeight;
      private final AnimationMetadataSection spriteAnimationMetadata;

      public Info(ResourceLocation locationIn, int widthIn, int heightIn, AnimationMetadataSection animationMetadataIn) {
         this.spriteLocation = locationIn;
         this.spriteWidth = widthIn;
         this.spriteHeight = heightIn;
         this.spriteAnimationMetadata = animationMetadataIn;
      }

      public ResourceLocation getSpriteLocation() {
         return this.spriteLocation;
      }

      public int getSpriteWidth() {
         return this.spriteWidth;
      }

      public int getSpriteHeight() {
         return this.spriteHeight;
      }
   }

   @OnlyIn(Dist.CLIENT)
   final class InterpolationData implements AutoCloseable {
      private final NativeImage[] images;

      private InterpolationData(TextureAtlasSprite.Info spriteInfoIn, int mipmapLevelsIn) {
         this.images = new NativeImage[mipmapLevelsIn + 1];

         for(int i = 0; i < this.images.length; ++i) {
            int j = spriteInfoIn.spriteWidth >> i;
            int k = spriteInfoIn.spriteHeight >> i;
            if (this.images[i] == null) {
               this.images[i] = new NativeImage(j, k, false);
            }
         }

      }

      private void uploadInterpolated() {
         double d0 = 1.0D - (double)TextureAtlasSprite.this.tickCounter / (double)TextureAtlasSprite.this.animationMetadata.getFrameTimeSingle(TextureAtlasSprite.this.frameCounter);
         int i = TextureAtlasSprite.this.animationMetadata.getFrameIndex(TextureAtlasSprite.this.frameCounter);
         int j = TextureAtlasSprite.this.animationMetadata.getFrameCount() == 0 ? TextureAtlasSprite.this.getFrameCount() : TextureAtlasSprite.this.animationMetadata.getFrameCount();
         int k = TextureAtlasSprite.this.animationMetadata.getFrameIndex((TextureAtlasSprite.this.frameCounter + 1) % j);
         if (i != k && k >= 0 && k < TextureAtlasSprite.this.getFrameCount()) {
            for(int l = 0; l < this.images.length; ++l) {
               int i1 = TextureAtlasSprite.this.spriteInfo.spriteWidth >> l;
               int j1 = TextureAtlasSprite.this.spriteInfo.spriteHeight >> l;

               for(int k1 = 0; k1 < j1; ++k1) {
                  for(int l1 = 0; l1 < i1; ++l1) {
                     int i2 = this.getPixelColor(i, l, l1, k1);
                     int j2 = this.getPixelColor(k, l, l1, k1);
                     int k2 = this.mix(d0, i2 >> 16 & 255, j2 >> 16 & 255);
                     int l2 = this.mix(d0, i2 >> 8 & 255, j2 >> 8 & 255);
                     int i3 = this.mix(d0, i2 & 255, j2 & 255);
                     this.images[l].setPixelRGBA(l1, k1, i2 & -16777216 | k2 << 16 | l2 << 8 | i3);
                  }
               }
            }

            TextureAtlasSprite.this.uploadFrames(0, 0, this.images);
         }

      }

      private int getPixelColor(int frameIndex, int mipmapLevel, int x, int y) {
         return TextureAtlasSprite.this.frames[mipmapLevel].getPixelRGBA(x + (TextureAtlasSprite.this.framesX[frameIndex] * TextureAtlasSprite.this.spriteInfo.spriteWidth >> mipmapLevel), y + (TextureAtlasSprite.this.framesY[frameIndex] * TextureAtlasSprite.this.spriteInfo.spriteHeight >> mipmapLevel));
      }

      private int mix(double ratio, int val1, int val2) {
         return (int)(ratio * (double)val1 + (1.0D - ratio) * (double)val2);
      }

      public void close() {
         for(NativeImage nativeimage : this.images) {
            if (nativeimage != null) {
               nativeimage.close();
            }
         }

      }
   }

   // Forge Start
   public int getPixelRGBA(int frameIndex, int x, int y) {
       return this.frames[0].getPixelRGBA(x + this.framesX[frameIndex] * getWidth(), y + this.framesY[frameIndex] * getHeight());
   }
}
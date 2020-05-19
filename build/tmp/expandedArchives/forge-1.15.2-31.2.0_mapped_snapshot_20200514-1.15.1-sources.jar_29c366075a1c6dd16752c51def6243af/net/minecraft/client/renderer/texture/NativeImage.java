package net.minecraft.client.renderer.texture;

import com.google.common.base.Charsets;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Base64;
import java.util.EnumSet;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.client.util.LWJGLMemoryUntracker;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.stb.STBIWriteCallback;
import org.lwjgl.stb.STBImage;
import org.lwjgl.stb.STBImageResize;
import org.lwjgl.stb.STBImageWrite;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.stb.STBTruetype;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

@OnlyIn(Dist.CLIENT)
public final class NativeImage implements AutoCloseable {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Set<StandardOpenOption> OPEN_OPTIONS = EnumSet.of(StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
   private final NativeImage.PixelFormat pixelFormat;
   private final int width;
   private final int height;
   private final boolean stbiPointer;
   private long imagePointer;
   private final long size;

   public NativeImage(int widthIn, int heightIn, boolean clear) {
      this(NativeImage.PixelFormat.RGBA, widthIn, heightIn, clear);
   }

   public NativeImage(NativeImage.PixelFormat pixelFormatIn, int widthIn, int heightIn, boolean initialize) {
      this.pixelFormat = pixelFormatIn;
      this.width = widthIn;
      this.height = heightIn;
      this.size = (long)widthIn * (long)heightIn * (long)pixelFormatIn.getPixelSize();
      this.stbiPointer = false;
      if (initialize) {
         this.imagePointer = MemoryUtil.nmemCalloc(1L, this.size);
      } else {
         this.imagePointer = MemoryUtil.nmemAlloc(this.size);
      }

   }

   private NativeImage(NativeImage.PixelFormat pixelFormatIn, int widthIn, int heightIn, boolean stbiPointerIn, long pointer) {
      this.pixelFormat = pixelFormatIn;
      this.width = widthIn;
      this.height = heightIn;
      this.stbiPointer = stbiPointerIn;
      this.imagePointer = pointer;
      this.size = (long)(widthIn * heightIn * pixelFormatIn.getPixelSize());
   }

   public String toString() {
      return "NativeImage[" + this.pixelFormat + " " + this.width + "x" + this.height + "@" + this.imagePointer + (this.stbiPointer ? "S" : "N") + "]";
   }

   public static NativeImage read(InputStream inputStreamIn) throws IOException {
      return read(NativeImage.PixelFormat.RGBA, inputStreamIn);
   }

   public static NativeImage read(@Nullable NativeImage.PixelFormat pixelFormatIn, InputStream inputStreamIn) throws IOException {
      ByteBuffer bytebuffer = null;

      NativeImage nativeimage;
      try {
         bytebuffer = TextureUtil.readToBuffer(inputStreamIn);
         ((Buffer)bytebuffer).rewind();
         nativeimage = read(pixelFormatIn, bytebuffer);
      } finally {
         MemoryUtil.memFree(bytebuffer);
         IOUtils.closeQuietly(inputStreamIn);
      }

      return nativeimage;
   }

   public static NativeImage read(ByteBuffer byteBufferIn) throws IOException {
      return read(NativeImage.PixelFormat.RGBA, byteBufferIn);
   }

   public static NativeImage read(@Nullable NativeImage.PixelFormat pixelFormatIn, ByteBuffer byteBufferIn) throws IOException {
      if (pixelFormatIn != null && !pixelFormatIn.isSerializable()) {
         throw new UnsupportedOperationException("Don't know how to read format " + pixelFormatIn);
      } else if (MemoryUtil.memAddress(byteBufferIn) == 0L) {
         throw new IllegalArgumentException("Invalid buffer");
      } else {
         NativeImage nativeimage;
         try (MemoryStack memorystack = MemoryStack.stackPush()) {
            IntBuffer intbuffer = memorystack.mallocInt(1);
            IntBuffer intbuffer1 = memorystack.mallocInt(1);
            IntBuffer intbuffer2 = memorystack.mallocInt(1);
            ByteBuffer bytebuffer = STBImage.stbi_load_from_memory(byteBufferIn, intbuffer, intbuffer1, intbuffer2, pixelFormatIn == null ? 0 : pixelFormatIn.pixelSize);
            if (bytebuffer == null) {
               throw new IOException("Could not load image: " + STBImage.stbi_failure_reason());
            }

            nativeimage = new NativeImage(pixelFormatIn == null ? NativeImage.PixelFormat.fromChannelCount(intbuffer2.get(0)) : pixelFormatIn, intbuffer.get(0), intbuffer1.get(0), true, MemoryUtil.memAddress(bytebuffer));
         }

         return nativeimage;
      }
   }

   private static void setWrapST(boolean clamp) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      if (clamp) {
         GlStateManager.texParameter(3553, 10242, 10496);
         GlStateManager.texParameter(3553, 10243, 10496);
      } else {
         GlStateManager.texParameter(3553, 10242, 10497);
         GlStateManager.texParameter(3553, 10243, 10497);
      }

   }

   private static void setMinMagFilters(boolean linear, boolean mipmap) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      if (linear) {
         GlStateManager.texParameter(3553, 10241, mipmap ? 9987 : 9729);
         GlStateManager.texParameter(3553, 10240, 9729);
      } else {
         GlStateManager.texParameter(3553, 10241, mipmap ? 9986 : 9728);
         GlStateManager.texParameter(3553, 10240, 9728);
      }

   }

   private void checkImage() {
      if (this.imagePointer == 0L) {
         throw new IllegalStateException("Image is not allocated.");
      }
   }

   public void close() {
      if (this.imagePointer != 0L) {
         if (this.stbiPointer) {
            STBImage.nstbi_image_free(this.imagePointer);
         } else {
            MemoryUtil.nmemFree(this.imagePointer);
         }
      }

      this.imagePointer = 0L;
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public NativeImage.PixelFormat getFormat() {
      return this.pixelFormat;
   }

   public int getPixelRGBA(int x, int y) {
      if (this.pixelFormat != NativeImage.PixelFormat.RGBA) {
         throw new IllegalArgumentException(String.format("getPixelRGBA only works on RGBA images; have %s", this.pixelFormat));
      } else if (x >= 0 && y >= 0 && x < this.width && y < this.height) { //Fix MC-162953 bounds checks in `NativeImage`
         this.checkImage();
         long i = (long)((x + y * this.width) * 4);
         return MemoryUtil.memGetInt(this.imagePointer + i);
      } else {
         throw new IllegalArgumentException(String.format("(%s, %s) outside of image bounds (%s, %s)", x, y, this.width, this.height));
      }
   }

   public void setPixelRGBA(int x, int y, int value) {
      if (this.pixelFormat != NativeImage.PixelFormat.RGBA) {
         throw new IllegalArgumentException(String.format("getPixelRGBA only works on RGBA images; have %s", this.pixelFormat));
      } else if (x >= 0 && y >= 0 && x < this.width && y < this.height) { //Fix MC-162953 bounds checks in `NativeImage`
         this.checkImage();
         long i = (long)((x + y * this.width) * 4);
         MemoryUtil.memPutInt(this.imagePointer + i, value);
      } else {
         throw new IllegalArgumentException(String.format("(%s, %s) outside of image bounds (%s, %s)", x, y, this.width, this.height));
      }
   }

   public byte getPixelLuminanceOrAlpha(int x, int y) {
      if (!this.pixelFormat.hasLuminanceOrAlpha()) {
         throw new IllegalArgumentException(String.format("no luminance or alpha in %s", this.pixelFormat));
      } else if (x >= 0 && y >= 0 && x < this.width && y < this.height) { //Fix MC-162953 bounds checks in `NativeImage`
         int i = (x + y * this.width) * this.pixelFormat.getPixelSize() + this.pixelFormat.getOffsetAlphaBits() / 8;
         return MemoryUtil.memGetByte(this.imagePointer + (long)i);
      } else {
         throw new IllegalArgumentException(String.format("(%s, %s) outside of image bounds (%s, %s)", x, y, this.width, this.height));
      }
   }

   public void blendPixel(int xIn, int yIn, int colIn) {
      if (this.pixelFormat != NativeImage.PixelFormat.RGBA) {
         throw new UnsupportedOperationException("Can only call blendPixel with RGBA format");
      } else {
         int i = this.getPixelRGBA(xIn, yIn);
         float f = (float)getAlpha(colIn) / 255.0F;
         float f1 = (float)getBlue(colIn) / 255.0F;
         float f2 = (float)getGreen(colIn) / 255.0F;
         float f3 = (float)getRed(colIn) / 255.0F;
         float f4 = (float)getAlpha(i) / 255.0F;
         float f5 = (float)getBlue(i) / 255.0F;
         float f6 = (float)getGreen(i) / 255.0F;
         float f7 = (float)getRed(i) / 255.0F;
         float f8 = 1.0F - f;
         float f9 = f * f + f4 * f8;
         float f10 = f1 * f + f5 * f8;
         float f11 = f2 * f + f6 * f8;
         float f12 = f3 * f + f7 * f8;
         if (f9 > 1.0F) {
            f9 = 1.0F;
         }

         if (f10 > 1.0F) {
            f10 = 1.0F;
         }

         if (f11 > 1.0F) {
            f11 = 1.0F;
         }

         if (f12 > 1.0F) {
            f12 = 1.0F;
         }

         int j = (int)(f9 * 255.0F);
         int k = (int)(f10 * 255.0F);
         int l = (int)(f11 * 255.0F);
         int i1 = (int)(f12 * 255.0F);
         this.setPixelRGBA(xIn, yIn, getCombined(j, k, l, i1));
      }
   }

   @Deprecated
   public int[] makePixelArray() {
      if (this.pixelFormat != NativeImage.PixelFormat.RGBA) {
         throw new UnsupportedOperationException("can only call makePixelArray for RGBA images.");
      } else {
         this.checkImage();
         int[] aint = new int[this.getWidth() * this.getHeight()];

         for(int i = 0; i < this.getHeight(); ++i) {
            for(int j = 0; j < this.getWidth(); ++j) {
               int k = this.getPixelRGBA(j, i);
               int l = getAlpha(k);
               int i1 = getBlue(k);
               int j1 = getGreen(k);
               int k1 = getRed(k);
               int l1 = l << 24 | k1 << 16 | j1 << 8 | i1;
               aint[j + i * this.getWidth()] = l1;
            }
         }

         return aint;
      }
   }

   public void uploadTextureSub(int level, int xOffset, int yOffset, boolean mipmap) {
      this.uploadTextureSub(level, xOffset, yOffset, 0, 0, this.width, this.height, false, mipmap);
   }

   public void uploadTextureSub(int level, int xOffset, int yOffset, int unpackSkipPixels, int unpackSkipRows, int widthIn, int heightIn, boolean mipmap, boolean autoClose) {
      this.uploadTextureSub(level, xOffset, yOffset, unpackSkipPixels, unpackSkipRows, widthIn, heightIn, false, false, mipmap, autoClose);
   }

   public void uploadTextureSub(int level, int xOffset, int yOffset, int unpackSkipPixels, int unpackSkipRows, int widthIn, int heightIn, boolean blur, boolean clamp, boolean mipmap, boolean autoClose) {
      if (!RenderSystem.isOnRenderThreadOrInit()) {
         RenderSystem.recordRenderCall(() -> {
            this.uploadTextureSubRaw(level, xOffset, yOffset, unpackSkipPixels, unpackSkipRows, widthIn, heightIn, blur, clamp, mipmap, autoClose);
         });
      } else {
         this.uploadTextureSubRaw(level, xOffset, yOffset, unpackSkipPixels, unpackSkipRows, widthIn, heightIn, blur, clamp, mipmap, autoClose);
      }

   }

   private void uploadTextureSubRaw(int level, int xOffset, int yOffset, int unpackSkipPixels, int unpackSkipRows, int widthIn, int heightIn, boolean blur, boolean clamp, boolean mipmap, boolean autoClose) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
      this.checkImage();
      setMinMagFilters(blur, mipmap);
      setWrapST(clamp);
      if (widthIn == this.getWidth()) {
         GlStateManager.pixelStore(3314, 0);
      } else {
         GlStateManager.pixelStore(3314, this.getWidth());
      }

      GlStateManager.pixelStore(3316, unpackSkipPixels);
      GlStateManager.pixelStore(3315, unpackSkipRows);
      this.pixelFormat.setGlUnpackAlignment();
      GlStateManager.texSubImage2D(3553, level, xOffset, yOffset, widthIn, heightIn, this.pixelFormat.getGlFormat(), 5121, this.imagePointer);
      if (autoClose) {
         this.close();
      }

   }

   public void downloadFromTexture(int level, boolean opaque) {
      RenderSystem.assertThread(RenderSystem::isOnRenderThread);
      this.checkImage();
      this.pixelFormat.setGlPackAlignment();
      GlStateManager.getTexImage(3553, level, this.pixelFormat.getGlFormat(), 5121, this.imagePointer);
      if (opaque && this.pixelFormat.hasAlpha()) {
         for(int i = 0; i < this.getHeight(); ++i) {
            for(int j = 0; j < this.getWidth(); ++j) {
               this.setPixelRGBA(j, i, this.getPixelRGBA(j, i) | 255 << this.pixelFormat.getOffsetAlpha());
            }
         }
      }

   }

   public void write(File fileIn) throws IOException {
      this.write(fileIn.toPath());
   }

   /**
    * Renders given glyph into this image
    */
   public void renderGlyph(STBTTFontinfo info, int glyphIndex, int widthIn, int heightIn, float scaleX, float scaleY, float shiftX, float shiftY, int x, int y) {
      if (x >= 0 && x + widthIn <= this.getWidth() && y >= 0 && y + heightIn <= this.getHeight()) {
         if (this.pixelFormat.getPixelSize() != 1) {
            throw new IllegalArgumentException("Can only write fonts into 1-component images.");
         } else {
            STBTruetype.nstbtt_MakeGlyphBitmapSubpixel(info.address(), this.imagePointer + (long)x + (long)(y * this.getWidth()), widthIn, heightIn, this.getWidth(), scaleX, scaleY, shiftX, shiftY, glyphIndex);
         }
      } else {
         throw new IllegalArgumentException(String.format("Out of bounds: start: (%s, %s) (size: %sx%s); size: %sx%s", x, y, widthIn, heightIn, this.getWidth(), this.getHeight()));
      }
   }

   public void write(Path pathIn) throws IOException {
      if (!this.pixelFormat.isSerializable()) {
         throw new UnsupportedOperationException("Don't know how to write format " + this.pixelFormat);
      } else {
         this.checkImage();

         try (WritableByteChannel writablebytechannel = Files.newByteChannel(pathIn, OPEN_OPTIONS)) {
            if (!this.write(writablebytechannel)) {
               throw new IOException("Could not write image to the PNG file \"" + pathIn.toAbsolutePath() + "\": " + STBImage.stbi_failure_reason());
            }
         }

      }
   }

   public byte[] getBytes() throws IOException {
      byte[] abyte;
      try (
         ByteArrayOutputStream bytearrayoutputstream = new ByteArrayOutputStream();
         WritableByteChannel writablebytechannel = Channels.newChannel(bytearrayoutputstream);
      ) {
         if (!this.write(writablebytechannel)) {
            throw new IOException("Could not write image to byte array: " + STBImage.stbi_failure_reason());
         }

         abyte = bytearrayoutputstream.toByteArray();
      }

      return abyte;
   }

   private boolean write(WritableByteChannel channelIn) throws IOException {
      NativeImage.WriteCallback nativeimage$writecallback = new NativeImage.WriteCallback(channelIn);

      boolean flag;
      try {
         int i = Math.min(this.getHeight(), Integer.MAX_VALUE / this.getWidth() / this.pixelFormat.getPixelSize());
         if (i < this.getHeight()) {
            LOGGER.warn("Dropping image height from {} to {} to fit the size into 32-bit signed int", this.getHeight(), i);
         }

         if (STBImageWrite.nstbi_write_png_to_func(nativeimage$writecallback.address(), 0L, this.getWidth(), i, this.pixelFormat.getPixelSize(), this.imagePointer, 0) != 0) {
            nativeimage$writecallback.propagateException();
            flag = true;
            return flag;
         }

         flag = false;
      } finally {
         nativeimage$writecallback.free();
      }

      return flag;
   }

   public void copyImageData(NativeImage from) {
      if (from.getFormat() != this.pixelFormat) {
         throw new UnsupportedOperationException("Image formats don't match.");
      } else {
         int i = this.pixelFormat.getPixelSize();
         this.checkImage();
         from.checkImage();
         if (this.width == from.width) {
            MemoryUtil.memCopy(from.imagePointer, this.imagePointer, Math.min(this.size, from.size));
         } else {
            int j = Math.min(this.getWidth(), from.getWidth());
            int k = Math.min(this.getHeight(), from.getHeight());

            for(int l = 0; l < k; ++l) {
               int i1 = l * from.getWidth() * i;
               int j1 = l * this.getWidth() * i;
               MemoryUtil.memCopy(from.imagePointer + (long)i1, this.imagePointer + (long)j1, (long)j);
            }
         }

      }
   }

   public void fillAreaRGBA(int x, int y, int widthIn, int heightIn, int value) {
      for(int i = y; i < y + heightIn; ++i) {
         for(int j = x; j < x + widthIn; ++j) {
            this.setPixelRGBA(j, i, value);
         }
      }

   }

   public void copyAreaRGBA(int xFrom, int yFrom, int xToDelta, int yToDelta, int widthIn, int heightIn, boolean mirrorX, boolean mirrorY) {
      for(int i = 0; i < heightIn; ++i) {
         for(int j = 0; j < widthIn; ++j) {
            int k = mirrorX ? widthIn - 1 - j : j;
            int l = mirrorY ? heightIn - 1 - i : i;
            int i1 = this.getPixelRGBA(xFrom + j, yFrom + i);
            this.setPixelRGBA(xFrom + xToDelta + k, yFrom + yToDelta + l, i1);
         }
      }

   }

   public void flip() {
      this.checkImage();

      try (MemoryStack memorystack = MemoryStack.stackPush()) {
         int i = this.pixelFormat.getPixelSize();
         int j = this.getWidth() * i;
         long k = memorystack.nmalloc(j);

         for(int l = 0; l < this.getHeight() / 2; ++l) {
            int i1 = l * this.getWidth() * i;
            int j1 = (this.getHeight() - 1 - l) * this.getWidth() * i;
            MemoryUtil.memCopy(this.imagePointer + (long)i1, k, (long)j);
            MemoryUtil.memCopy(this.imagePointer + (long)j1, this.imagePointer + (long)i1, (long)j);
            MemoryUtil.memCopy(k, this.imagePointer + (long)j1, (long)j);
         }
      }

   }

   public void resizeSubRectTo(int xIn, int yIn, int widthIn, int heightIn, NativeImage imageIn) {
      this.checkImage();
      if (imageIn.getFormat() != this.pixelFormat) {
         throw new UnsupportedOperationException("resizeSubRectTo only works for images of the same format.");
      } else {
         int i = this.pixelFormat.getPixelSize();
         STBImageResize.nstbir_resize_uint8(this.imagePointer + (long)((xIn + yIn * this.getWidth()) * i), widthIn, heightIn, this.getWidth() * i, imageIn.imagePointer, imageIn.getWidth(), imageIn.getHeight(), 0, i);
      }
   }

   public void untrack() {
      LWJGLMemoryUntracker.untrack(this.imagePointer);
   }

   public static NativeImage readBase64(String stringIn) throws IOException {
      byte[] abyte = Base64.getDecoder().decode(stringIn.replaceAll("\n", "").getBytes(Charsets.UTF_8));

      NativeImage nativeimage;
      try (MemoryStack memorystack = MemoryStack.stackPush()) {
         ByteBuffer bytebuffer = memorystack.malloc(abyte.length);
         bytebuffer.put(abyte);
         ((Buffer)bytebuffer).rewind();
         nativeimage = read(bytebuffer);
      }

      return nativeimage;
   }

   public static int getAlpha(int col) {
      return col >> 24 & 255;
   }

   public static int getRed(int col) {
      return col >> 0 & 255;
   }

   public static int getGreen(int col) {
      return col >> 8 & 255;
   }

   public static int getBlue(int col) {
      return col >> 16 & 255;
   }

   public static int getCombined(int alpha, int blue, int green, int red) {
      return (alpha & 255) << 24 | (blue & 255) << 16 | (green & 255) << 8 | (red & 255) << 0;
   }

   @OnlyIn(Dist.CLIENT)
   public static enum PixelFormat {
      RGBA(4, 6408, true, true, true, false, true, 0, 8, 16, 255, 24, true),
      RGB(3, 6407, true, true, true, false, false, 0, 8, 16, 255, 255, true),
      LUMINANCE_ALPHA(2, 6410, false, false, false, true, true, 255, 255, 255, 0, 8, true),
      LUMINANCE(1, 6409, false, false, false, true, false, 0, 0, 0, 0, 255, true);

      private final int pixelSize;
      private final int glFormat;
      private final boolean red;
      private final boolean green;
      private final boolean blue;
      private final boolean hasLuminance;
      private final boolean hasAlpha;
      private final int offsetRed;
      private final int offsetGreen;
      private final int offsetBlue;
      private final int offsetLuminance;
      private final int offsetAlpha;
      private final boolean serializable;

      private PixelFormat(int channelsIn, int glFormatIn, boolean redIn, boolean greenIn, boolean blueIn, boolean luminanceIn, boolean alphaIn, int offsetRedIn, int offsetGreenIn, int offsetBlueIn, int offsetLuminanceIn, int offsetAlphaIn, boolean standardIn) {
         this.pixelSize = channelsIn;
         this.glFormat = glFormatIn;
         this.red = redIn;
         this.green = greenIn;
         this.blue = blueIn;
         this.hasLuminance = luminanceIn;
         this.hasAlpha = alphaIn;
         this.offsetRed = offsetRedIn;
         this.offsetGreen = offsetGreenIn;
         this.offsetBlue = offsetBlueIn;
         this.offsetLuminance = offsetLuminanceIn;
         this.offsetAlpha = offsetAlphaIn;
         this.serializable = standardIn;
      }

      public int getPixelSize() {
         return this.pixelSize;
      }

      public void setGlPackAlignment() {
         RenderSystem.assertThread(RenderSystem::isOnRenderThread);
         GlStateManager.pixelStore(3333, this.getPixelSize());
      }

      public void setGlUnpackAlignment() {
         RenderSystem.assertThread(RenderSystem::isOnRenderThreadOrInit);
         GlStateManager.pixelStore(3317, this.getPixelSize());
      }

      public int getGlFormat() {
         return this.glFormat;
      }

      public boolean hasAlpha() {
         return this.hasAlpha;
      }

      public int getOffsetAlpha() {
         return this.offsetAlpha;
      }

      public boolean hasLuminanceOrAlpha() {
         return this.hasLuminance || this.hasAlpha;
      }

      public int getOffsetAlphaBits() {
         return this.hasLuminance ? this.offsetLuminance : this.offsetAlpha;
      }

      public boolean isSerializable() {
         return this.serializable;
      }

      private static NativeImage.PixelFormat fromChannelCount(int channelsIn) {
         switch(channelsIn) {
         case 1:
            return LUMINANCE;
         case 2:
            return LUMINANCE_ALPHA;
         case 3:
            return RGB;
         case 4:
         default:
            return RGBA;
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static enum PixelFormatGLCode {
      RGBA(6408),
      RGB(6407),
      LUMINANCE_ALPHA(6410),
      LUMINANCE(6409),
      INTENSITY(32841);

      private final int glConstant;

      private PixelFormatGLCode(int glFormatIn) {
         this.glConstant = glFormatIn;
      }

      int getGlFormat() {
         return this.glConstant;
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class WriteCallback extends STBIWriteCallback {
      private final WritableByteChannel channel;
      @Nullable
      private IOException exception;

      private WriteCallback(WritableByteChannel byteChannelIn) {
         this.channel = byteChannelIn;
      }

      public void invoke(long p_invoke_1_, long p_invoke_3_, int p_invoke_5_) {
         ByteBuffer bytebuffer = getData(p_invoke_3_, p_invoke_5_);

         try {
            this.channel.write(bytebuffer);
         } catch (IOException ioexception) {
            this.exception = ioexception;
         }

      }

      public void propagateException() throws IOException {
         if (this.exception != null) {
            throw this.exception;
         }
      }
   }
}
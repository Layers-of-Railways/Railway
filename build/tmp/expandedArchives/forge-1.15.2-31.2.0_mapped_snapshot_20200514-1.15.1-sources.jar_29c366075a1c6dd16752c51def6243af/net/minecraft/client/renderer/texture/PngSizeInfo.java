package net.minecraft.client.renderer.texture;

import java.io.EOFException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.stb.STBIEOFCallback;
import org.lwjgl.stb.STBIIOCallbacks;
import org.lwjgl.stb.STBIReadCallback;
import org.lwjgl.stb.STBISkipCallback;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

@OnlyIn(Dist.CLIENT)
public class PngSizeInfo {
   public final int width;
   public final int height;

   public PngSizeInfo(String p_i51172_1_, InputStream p_i51172_2_) throws IOException {
      try (
         MemoryStack memorystack = MemoryStack.stackPush();
         PngSizeInfo.Reader pngsizeinfo$reader = func_195695_a(p_i51172_2_);
         STBIReadCallback stbireadcallback = STBIReadCallback.create(pngsizeinfo$reader::func_195682_a);
         STBISkipCallback stbiskipcallback = STBISkipCallback.create(pngsizeinfo$reader::func_195686_a);
         STBIEOFCallback stbieofcallback = STBIEOFCallback.create(pngsizeinfo$reader::func_195685_a);
      ) {
         STBIIOCallbacks stbiiocallbacks = STBIIOCallbacks.mallocStack(memorystack);
         stbiiocallbacks.read(stbireadcallback);
         stbiiocallbacks.skip(stbiskipcallback);
         stbiiocallbacks.eof(stbieofcallback);
         IntBuffer intbuffer = memorystack.mallocInt(1);
         IntBuffer intbuffer1 = memorystack.mallocInt(1);
         IntBuffer intbuffer2 = memorystack.mallocInt(1);
         if (!STBImage.stbi_info_from_callbacks(stbiiocallbacks, 0L, intbuffer, intbuffer1, intbuffer2)) {
            throw new IOException("Could not read info from the PNG file " + p_i51172_1_ + " " + STBImage.stbi_failure_reason());
         }

         this.width = intbuffer.get(0);
         this.height = intbuffer1.get(0);
      }

   }

   private static PngSizeInfo.Reader func_195695_a(InputStream p_195695_0_) {
      return (PngSizeInfo.Reader)(p_195695_0_ instanceof FileInputStream ? new PngSizeInfo.ReaderSeekable(((FileInputStream)p_195695_0_).getChannel()) : new PngSizeInfo.ReaderBuffer(Channels.newChannel(p_195695_0_)));
   }

   @OnlyIn(Dist.CLIENT)
   abstract static class Reader implements AutoCloseable {
      protected boolean field_195687_a;

      private Reader() {
      }

      int func_195682_a(long p_195682_1_, long p_195682_3_, int p_195682_5_) {
         try {
            return this.func_195683_b(p_195682_3_, p_195682_5_);
         } catch (IOException var7) {
            this.field_195687_a = true;
            return 0;
         }
      }

      void func_195686_a(long p_195686_1_, int p_195686_3_) {
         try {
            this.func_195684_a(p_195686_3_);
         } catch (IOException var5) {
            this.field_195687_a = true;
         }

      }

      int func_195685_a(long p_195685_1_) {
         return this.field_195687_a ? 1 : 0;
      }

      protected abstract int func_195683_b(long p_195683_1_, int p_195683_3_) throws IOException;

      protected abstract void func_195684_a(int p_195684_1_) throws IOException;

      public abstract void close() throws IOException;
   }

   @OnlyIn(Dist.CLIENT)
   static class ReaderBuffer extends PngSizeInfo.Reader {
      private final ReadableByteChannel channel;
      private long field_195690_c = MemoryUtil.nmemAlloc(128L);
      private int field_195691_d = 128;
      private int field_195692_e;
      private int field_195693_f;

      private ReaderBuffer(ReadableByteChannel p_i48136_1_) {
         this.channel = p_i48136_1_;
      }

      private void func_195688_b(int p_195688_1_) throws IOException {
         ByteBuffer bytebuffer = MemoryUtil.memByteBuffer(this.field_195690_c, this.field_195691_d);
         if (p_195688_1_ + this.field_195693_f > this.field_195691_d) {
            this.field_195691_d = p_195688_1_ + this.field_195693_f;
            bytebuffer = MemoryUtil.memRealloc(bytebuffer, this.field_195691_d);
            this.field_195690_c = MemoryUtil.memAddress(bytebuffer);
         }

         ((Buffer)bytebuffer).position(this.field_195692_e);

         while(p_195688_1_ + this.field_195693_f > this.field_195692_e) {
            try {
               int i = this.channel.read(bytebuffer);
               if (i == -1) {
                  break;
               }
            } finally {
               this.field_195692_e = bytebuffer.position();
            }
         }

      }

      public int func_195683_b(long p_195683_1_, int p_195683_3_) throws IOException {
         this.func_195688_b(p_195683_3_);
         if (p_195683_3_ + this.field_195693_f > this.field_195692_e) {
            p_195683_3_ = this.field_195692_e - this.field_195693_f;
         }

         MemoryUtil.memCopy(this.field_195690_c + (long)this.field_195693_f, p_195683_1_, (long)p_195683_3_);
         this.field_195693_f += p_195683_3_;
         return p_195683_3_;
      }

      public void func_195684_a(int p_195684_1_) throws IOException {
         if (p_195684_1_ > 0) {
            this.func_195688_b(p_195684_1_);
            if (p_195684_1_ + this.field_195693_f > this.field_195692_e) {
               throw new EOFException("Can't skip past the EOF.");
            }
         }

         if (this.field_195693_f + p_195684_1_ < 0) {
            throw new IOException("Can't seek before the beginning: " + (this.field_195693_f + p_195684_1_));
         } else {
            this.field_195693_f += p_195684_1_;
         }
      }

      public void close() throws IOException {
         MemoryUtil.nmemFree(this.field_195690_c);
         this.channel.close();
      }
   }

   @OnlyIn(Dist.CLIENT)
   static class ReaderSeekable extends PngSizeInfo.Reader {
      private final SeekableByteChannel channel;

      private ReaderSeekable(SeekableByteChannel p_i48134_1_) {
         this.channel = p_i48134_1_;
      }

      public int func_195683_b(long p_195683_1_, int p_195683_3_) throws IOException {
         ByteBuffer bytebuffer = MemoryUtil.memByteBuffer(p_195683_1_, p_195683_3_);
         return this.channel.read(bytebuffer);
      }

      public void func_195684_a(int p_195684_1_) throws IOException {
         this.channel.position(this.channel.position() + (long)p_195684_1_);
      }

      public int func_195685_a(long p_195685_1_) {
         return super.func_195685_a(p_195685_1_) != 0 && this.channel.isOpen() ? 1 : 0;
      }

      public void close() throws IOException {
         this.channel.close();
      }
   }
}
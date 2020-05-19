package net.minecraft.client.audio;

import com.google.common.collect.Lists;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.List;
import javax.annotation.Nullable;
import javax.sound.sampled.AudioFormat;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.stb.STBVorbis;
import org.lwjgl.stb.STBVorbisAlloc;
import org.lwjgl.stb.STBVorbisInfo;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

@OnlyIn(Dist.CLIENT)
public class OggAudioStream implements IAudioStream {
   private long field_216461_a;
   private final AudioFormat field_216462_b;
   private final InputStream field_216463_c;
   private ByteBuffer field_216464_d = MemoryUtil.memAlloc(8192);

   public OggAudioStream(InputStream p_i51177_1_) throws IOException {
      this.field_216463_c = p_i51177_1_;
      ((java.nio.Buffer)this.field_216464_d).limit(0);

      try (MemoryStack memorystack = MemoryStack.stackPush()) {
         IntBuffer intbuffer = memorystack.mallocInt(1);
         IntBuffer intbuffer1 = memorystack.mallocInt(1);

         while(this.field_216461_a == 0L) {
            if (!this.func_216456_c()) {
               throw new IOException("Failed to find Ogg header");
            }

            int i = this.field_216464_d.position();
            ((java.nio.Buffer)this.field_216464_d).position(0);
            this.field_216461_a = STBVorbis.stb_vorbis_open_pushdata(this.field_216464_d, intbuffer, intbuffer1, (STBVorbisAlloc)null);
            ((java.nio.Buffer)this.field_216464_d).position(i);
            int j = intbuffer1.get(0);
            if (j == 1) {
               this.func_216459_d();
            } else if (j != 0) {
               throw new IOException("Failed to read Ogg file " + j);
            }
         }

         ((java.nio.Buffer)this.field_216464_d).position(this.field_216464_d.position() + intbuffer.get(0));
         STBVorbisInfo stbvorbisinfo = STBVorbisInfo.mallocStack(memorystack);
         STBVorbis.stb_vorbis_get_info(this.field_216461_a, stbvorbisinfo);
         this.field_216462_b = new AudioFormat((float)stbvorbisinfo.sample_rate(), 16, stbvorbisinfo.channels(), true, false);
      }

   }

   private boolean func_216456_c() throws IOException {
      int i = this.field_216464_d.limit();
      int j = this.field_216464_d.capacity() - i;
      if (j == 0) {
         return true;
      } else {
         byte[] abyte = new byte[j];
         int k = this.field_216463_c.read(abyte);
         if (k == -1) {
            return false;
         } else {
            int l = this.field_216464_d.position();
            ((java.nio.Buffer)this.field_216464_d).limit(i + k);
            ((java.nio.Buffer)this.field_216464_d).position(i);
            this.field_216464_d.put(abyte, 0, k);
            ((java.nio.Buffer)this.field_216464_d).position(l);
            return true;
         }
      }
   }

   private void func_216459_d() {
      boolean flag = this.field_216464_d.position() == 0;
      boolean flag1 = this.field_216464_d.position() == this.field_216464_d.limit();
      if (flag1 && !flag) {
         ((java.nio.Buffer)this.field_216464_d).position(0);
         ((java.nio.Buffer)this.field_216464_d).limit(0);
      } else {
         ByteBuffer bytebuffer = MemoryUtil.memAlloc(flag ? 2 * this.field_216464_d.capacity() : this.field_216464_d.capacity());
         bytebuffer.put(this.field_216464_d);
         MemoryUtil.memFree(this.field_216464_d);
         ((java.nio.Buffer)bytebuffer).flip();
         this.field_216464_d = bytebuffer;
      }

   }

   private boolean func_216460_a(OggAudioStream.Buffer p_216460_1_) throws IOException {
      if (this.field_216461_a == 0L) {
         return false;
      } else {
         try (MemoryStack memorystack = MemoryStack.stackPush()) {
            PointerBuffer pointerbuffer = memorystack.mallocPointer(1);
            IntBuffer intbuffer = memorystack.mallocInt(1);
            IntBuffer intbuffer1 = memorystack.mallocInt(1);

            while(true) {
               int i = STBVorbis.stb_vorbis_decode_frame_pushdata(this.field_216461_a, this.field_216464_d, intbuffer, pointerbuffer, intbuffer1);
               ((java.nio.Buffer)this.field_216464_d).position(this.field_216464_d.position() + i);
               int j = STBVorbis.stb_vorbis_get_error(this.field_216461_a);
               if (j == 1) {
                  this.func_216459_d();
                  if (!this.func_216456_c()) {
                     i = 0;
                     return false;
                  }
               } else {
                  if (j != 0) {
                     throw new IOException("Failed to read Ogg file " + j);
                  }

                  int k = intbuffer1.get(0);
                  if (k != 0) {
                     int l = intbuffer.get(0);
                     PointerBuffer pointerbuffer1 = pointerbuffer.getPointerBuffer(l);
                     if (l != 1) {
                        if (l == 2) {
                           this.func_216458_a(pointerbuffer1.getFloatBuffer(0, k), pointerbuffer1.getFloatBuffer(1, k), p_216460_1_);
                           boolean flag1 = true;
                           return flag1;
                        }

                        throw new IllegalStateException("Invalid number of channels: " + l);
                     }

                     this.func_216457_a(pointerbuffer1.getFloatBuffer(0, k), p_216460_1_);
                     boolean flag = true;
                     return flag;
                  }
               }
            }
         }
      }
   }

   private void func_216457_a(FloatBuffer p_216457_1_, OggAudioStream.Buffer p_216457_2_) {
      while(p_216457_1_.hasRemaining()) {
         p_216457_2_.func_216446_a(p_216457_1_.get());
      }

   }

   private void func_216458_a(FloatBuffer p_216458_1_, FloatBuffer p_216458_2_, OggAudioStream.Buffer p_216458_3_) {
      while(p_216458_1_.hasRemaining() && p_216458_2_.hasRemaining()) {
         p_216458_3_.func_216446_a(p_216458_1_.get());
         p_216458_3_.func_216446_a(p_216458_2_.get());
      }

   }

   public void close() throws IOException {
      if (this.field_216461_a != 0L) {
         STBVorbis.stb_vorbis_close(this.field_216461_a);
         this.field_216461_a = 0L;
      }

      MemoryUtil.memFree(this.field_216464_d);
      this.field_216463_c.close();
   }

   public AudioFormat getAudioFormat() {
      return this.field_216462_b;
   }

   @Nullable
   public ByteBuffer func_216455_a(int p_216455_1_) throws IOException {
      OggAudioStream.Buffer oggaudiostream$buffer = new OggAudioStream.Buffer(p_216455_1_ + 8192);

      while(this.func_216460_a(oggaudiostream$buffer) && oggaudiostream$buffer.field_216451_c < p_216455_1_) {
         ;
      }

      return oggaudiostream$buffer.func_216445_a();
   }

   public ByteBuffer func_216453_b() throws IOException {
      OggAudioStream.Buffer oggaudiostream$buffer = new OggAudioStream.Buffer(16384);

      while(this.func_216460_a(oggaudiostream$buffer)) {
         ;
      }

      return oggaudiostream$buffer.func_216445_a();
   }

   @OnlyIn(Dist.CLIENT)
   static class Buffer {
      private final List<ByteBuffer> field_216449_a = Lists.newArrayList();
      private final int field_216450_b;
      private int field_216451_c;
      private ByteBuffer field_216452_d;

      public Buffer(int p_i50626_1_) {
         this.field_216450_b = p_i50626_1_ + 1 & -2;
         this.func_216447_b();
      }

      private void func_216447_b() {
         this.field_216452_d = BufferUtils.createByteBuffer(this.field_216450_b);
      }

      public void func_216446_a(float p_216446_1_) {
         if (this.field_216452_d.remaining() == 0) {
            ((java.nio.Buffer)this.field_216452_d).flip();
            this.field_216449_a.add(this.field_216452_d);
            this.func_216447_b();
         }

         int i = MathHelper.clamp((int)(p_216446_1_ * 32767.5F - 0.5F), -32768, 32767);
         this.field_216452_d.putShort((short)i);
         this.field_216451_c += 2;
      }

      public ByteBuffer func_216445_a() {
         ((java.nio.Buffer)this.field_216452_d).flip();
         if (this.field_216449_a.isEmpty()) {
            return this.field_216452_d;
         } else {
            ByteBuffer bytebuffer = BufferUtils.createByteBuffer(this.field_216451_c);
            this.field_216449_a.forEach(bytebuffer::put);
            bytebuffer.put(this.field_216452_d);
            ((java.nio.Buffer)bytebuffer).flip();
            return bytebuffer;
         }
      }
   }
}
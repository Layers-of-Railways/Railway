package net.minecraft.world.chunk.storage;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import javax.annotation.Nullable;
import net.minecraft.util.Util;
import net.minecraft.util.math.ChunkPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RegionFile implements AutoCloseable {
   private static final Logger field_227122_a_ = LogManager.getLogger();
   private static final ByteBuffer field_227123_b_ = ByteBuffer.allocateDirect(1);
   private final FileChannel dataFile;
   private final Path field_227124_d_;
   private final RegionFileVersion field_227125_e_;
   private final ByteBuffer field_227126_f_ = ByteBuffer.allocateDirect(8192);
   private final IntBuffer offsets;
   private final IntBuffer chunkTimestamps;
   private final RegionBitmap field_227128_i_ = new RegionBitmap();
   private final Path filePath;

   public RegionFile(File p_i225784_1_, File p_i225784_2_) throws IOException {
      this(p_i225784_1_.toPath(), p_i225784_2_.toPath(), RegionFileVersion.field_227159_b_);
   }

   public RegionFile(Path p_i225785_1_, Path p_i225785_2_, RegionFileVersion p_i225785_3_) throws IOException {
      this.field_227125_e_ = p_i225785_3_;
      this.filePath = p_i225785_1_;
      if (!Files.isDirectory(p_i225785_2_)) {
         throw new IllegalArgumentException("Expected directory, got " + p_i225785_2_.toAbsolutePath());
      } else {
         this.field_227124_d_ = p_i225785_2_;
         this.offsets = this.field_227126_f_.asIntBuffer();
         ((Buffer)this.offsets).limit(1024);
         ((Buffer)this.field_227126_f_).position(4096);
         this.chunkTimestamps = this.field_227126_f_.asIntBuffer();
         this.dataFile = FileChannel.open(p_i225785_1_, StandardOpenOption.CREATE, StandardOpenOption.READ, StandardOpenOption.WRITE);
         this.field_227128_i_.func_227120_a_(0, 2);
         ((Buffer)this.field_227126_f_).position(0);
         int i = this.dataFile.read(this.field_227126_f_, 0L);
         if (i != -1) {
            if (i != 8192) {
               field_227122_a_.warn("Region file {} has truncated header: {}", p_i225785_1_, i);
            }

            for(int j = 0; j < 1024; ++j) {
               int k = this.offsets.get(j);
               if (k != 0) {
                  int l = func_227142_b_(k);
                  int i1 = func_227131_a_(k);
                  if (i1 == 255)
                      i1 = forgeGetRealLength(j, l);
                  this.field_227128_i_.func_227120_a_(l, i1);
               }
            }
         }

      }
   }

   private int forgeGetRealLength(int index, int offset) throws IOException {
       int chunkX = index & 31;
       int chunkZ = (index >> 5) & 31;

       ByteBuffer header = ByteBuffer.allocate(5);
       this.dataFile.read(header, offset * 4096);
       ((Buffer)header).flip();

       if (header.remaining() < 5) {
          field_227122_a_.error("Chunk {},{} in {} header is truncated: expected 5 but read {}", chunkX, chunkZ, this.filePath.getFileName(), header.remaining());
          return 255;
       }

       return (header.getInt() + 4) / 4096 + 1;
   }

   /**
    * In 1.14, Forge added support for large chunks by allowing it to overflow the 255 section limit.
    * Deferring the section size to the 'length' header in front of the chunk data.
    * In 1.15, Mojang solved this issue by adding an external '.mcc' file for large chunks.
    * Here, we attempt to detect and extract these large chunks from Forge's format to Vanilla's
    */
   public RegionFile extractLargeChunks(ChunkPos pos) throws IOException {
       ChunkPos regionBase = new ChunkPos(pos.getRegionCoordX() * 32, pos.getRegionCoordZ() * 32);
       for (int index = 0; index < 1024; index++) {
          int offset = this.offsets.get(index);
          if (func_227131_a_(offset) != 255) //If it's not 255, then it's not possible to be a oversized chunk. Move on.
             continue;
          offset = func_227142_b_(offset);

          ChunkPos chunk = new ChunkPos(regionBase.x + (index & 31), regionBase.z + ((index >> 5) & 31));

          ByteBuffer header = ByteBuffer.allocate(5);
          this.dataFile.read(header, offset * 4096);
          ((Buffer)header).flip();

          if (header.remaining() < 5) {
             field_227122_a_.error("Chunk {} in {} header is truncated: expected 5 but read {}", chunk, this.filePath.getFileName(), header.remaining());
             continue;
          }

          int length = header.getInt();
          byte version = header.get();
          int sectors = (length + 4) / 4096 + 1;
          if (sectors <= 255 || func_227130_a_(version))
             continue; //Not over sized, or already external

          ByteBuffer data = ByteBuffer.allocate(length + 4);
          this.dataFile.read(data, offset * 4096);
          ((Buffer)data).flip();

          if (data.remaining() < length + 4) {
              field_227122_a_.error("Chunk {} in {} is truncated: expected {} but read {}", chunk, this.filePath.getFileName(), length + 4, data.remaining());
              continue;
          }
          func_227135_a_(chunk, data); //Save the chunk data, it'll be spit out to an external file.
       }
       return this;
   }

   private Path func_227145_e_(ChunkPos p_227145_1_) {
      String s = "c." + p_227145_1_.x + "." + p_227145_1_.z + ".mcc";
      return this.field_227124_d_.resolve(s);
   }

   @Nullable
   public synchronized DataInputStream func_222666_a(ChunkPos pos) throws IOException {
      int i = this.getOffset(pos);
      if (i == 0) {
         return null;
      } else {
         int j = func_227142_b_(i);
         int k = func_227131_a_(i);
         int l = k * 4096;
         ByteBuffer bytebuffer = ByteBuffer.allocate(l);
         this.dataFile.read(bytebuffer, (long)(j * 4096));
         ((Buffer)bytebuffer).flip();
         if (bytebuffer.remaining() < 5) {
            field_227122_a_.error("Chunk {} header is truncated: expected {} but read {}", pos, l, bytebuffer.remaining());
            return null;
         } else {
            int i1 = bytebuffer.getInt();
            byte b0 = bytebuffer.get();
            if (i1 == 0) {
               field_227122_a_.warn("Chunk {} is allocated, but stream is missing", (Object)pos);
               return null;
            } else {
               int j1 = i1 - 1;
               if (func_227130_a_(b0)) {
                  if (j1 != 0) {
                     field_227122_a_.warn("Chunk has both internal and external streams");
                  }

                  return this.func_227133_a_(pos, func_227141_b_(b0));
               } else if (j1 > bytebuffer.remaining()) {
                  field_227122_a_.error("Chunk {} stream is truncated: expected {} but read {}", pos, j1, bytebuffer.remaining());
                  return null;
               } else if (j1 < 0) {
                  field_227122_a_.error("Declared size {} of chunk {} is negative", i1, pos);
                  return null;
               } else {
                  return this.func_227134_a_(pos, b0, func_227137_a_(bytebuffer, j1));
               }
            }
         }
      }
   }

   private static boolean func_227130_a_(byte p_227130_0_) {
      return (p_227130_0_ & 128) != 0;
   }

   private static byte func_227141_b_(byte p_227141_0_) {
      return (byte)(p_227141_0_ & -129);
   }

   @Nullable
   private DataInputStream func_227134_a_(ChunkPos p_227134_1_, byte p_227134_2_, InputStream p_227134_3_) throws IOException {
      RegionFileVersion regionfileversion = RegionFileVersion.func_227166_a_(p_227134_2_);
      if (regionfileversion == null) {
         field_227122_a_.error("Chunk {} has invalid chunk stream version {}", p_227134_1_, p_227134_2_);
         return null;
      } else {
         return new DataInputStream(new BufferedInputStream(regionfileversion.func_227168_a_(p_227134_3_)));
      }
   }

   @Nullable
   private DataInputStream func_227133_a_(ChunkPos p_227133_1_, byte p_227133_2_) throws IOException {
      Path path = this.func_227145_e_(p_227133_1_);
      if (!Files.isRegularFile(path)) {
         field_227122_a_.error("External chunk path {} is not file", (Object)path);
         return null;
      } else {
         return this.func_227134_a_(p_227133_1_, p_227133_2_, Files.newInputStream(path));
      }
   }

   private static ByteArrayInputStream func_227137_a_(ByteBuffer p_227137_0_, int p_227137_1_) {
      return new ByteArrayInputStream(p_227137_0_.array(), p_227137_0_.position(), p_227137_1_);
   }

   private int func_227132_a_(int p_227132_1_, int p_227132_2_) {
      return p_227132_1_ << 8 | p_227132_2_;
   }

   private static int func_227131_a_(int p_227131_0_) {
      return p_227131_0_ & 255;
   }

   private static int func_227142_b_(int p_227142_0_) {
      return p_227142_0_ >> 8;
   }

   private static int func_227144_c_(int p_227144_0_) {
      return (p_227144_0_ + 4096 - 1) / 4096;
   }

   public boolean func_222662_b(ChunkPos p_222662_1_) {
      int i = this.getOffset(p_222662_1_);
      if (i == 0) {
         return false;
      } else {
         int j = func_227142_b_(i);
         int k = func_227131_a_(i);
         ByteBuffer bytebuffer = ByteBuffer.allocate(5);

         try {
            this.dataFile.read(bytebuffer, (long)(j * 4096));
            ((Buffer)bytebuffer).flip();
            if (bytebuffer.remaining() != 5) {
               return false;
            } else {
               int l = bytebuffer.getInt();
               byte b0 = bytebuffer.get();
               if (func_227130_a_(b0)) {
                  if (!RegionFileVersion.func_227170_b_(func_227141_b_(b0))) {
                     return false;
                  }

                  if (!Files.isRegularFile(this.func_227145_e_(p_222662_1_))) {
                     return false;
                  }
               } else {
                  if (!RegionFileVersion.func_227170_b_(b0)) {
                     return false;
                  }

                  if (l == 0) {
                     return false;
                  }

                  int i1 = l - 1;
                  if (i1 < 0 || i1 > 4096 * k) {
                     return false;
                  }
               }

               return true;
            }
         } catch (IOException var9) {
            return false;
         }
      }
   }

   public DataOutputStream func_222661_c(ChunkPos p_222661_1_) throws IOException {
      return new DataOutputStream(new BufferedOutputStream(this.field_227125_e_.func_227169_a_(new RegionFile.ChunkBuffer(p_222661_1_))));
   }

   protected synchronized void func_227135_a_(ChunkPos p_227135_1_, ByteBuffer p_227135_2_) throws IOException {
      int i = getIndex(p_227135_1_);
      int j = this.offsets.get(i);
      int k = func_227142_b_(j);
      int l = func_227131_a_(j);
      if (l == 255) l = forgeGetRealLength(i, k); //Forge: Old Forge fix, get real length, so we can free if needed
      int i1 = p_227135_2_.remaining();
      int j1 = func_227144_c_(i1);
      int k1;
      RegionFile.ICompleteCallback regionfile$icompletecallback;
      if (j1 >= 256) {
         Path path = this.func_227145_e_(p_227135_1_);
         field_227122_a_.warn("Saving oversized chunk {} ({} bytes} to external file {}", p_227135_1_, i1, path);
         j1 = 1;
         k1 = this.field_227128_i_.func_227119_a_(j1);
         regionfile$icompletecallback = this.func_227138_a_(path, p_227135_2_);
         ByteBuffer bytebuffer = this.func_227129_a_();
         this.dataFile.write(bytebuffer, (long)(k1 * 4096));
      } else {
         k1 = this.field_227128_i_.func_227119_a_(j1);
         regionfile$icompletecallback = () -> {
            Files.deleteIfExists(this.func_227145_e_(p_227135_1_));
         };
         this.dataFile.write(p_227135_2_, (long)(k1 * 4096));
      }

      int l1 = (int)(Util.millisecondsSinceEpoch() / 1000L);
      this.offsets.put(i, this.func_227132_a_(k1, j1));
      this.chunkTimestamps.put(i, l1);
      this.func_227140_b_();
      regionfile$icompletecallback.run();
      if (k != 0) {
         this.field_227128_i_.func_227121_b_(k, l);
      }

   }

   private ByteBuffer func_227129_a_() {
      ByteBuffer bytebuffer = ByteBuffer.allocate(5);
      bytebuffer.putInt(1);
      bytebuffer.put((byte)(this.field_227125_e_.func_227165_a_() | 128));
      ((Buffer)bytebuffer).flip();
      return bytebuffer;
   }

   private RegionFile.ICompleteCallback func_227138_a_(Path p_227138_1_, ByteBuffer p_227138_2_) throws IOException {
      Path path = Files.createTempFile(this.field_227124_d_, "tmp", (String)null);

      try (FileChannel filechannel = FileChannel.open(path, StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
         ((Buffer)p_227138_2_).position(5);
         filechannel.write(p_227138_2_);
      }

      return () -> {
         Files.move(path, p_227138_1_, StandardCopyOption.REPLACE_EXISTING);
      };
   }

   private void func_227140_b_() throws IOException {
      ((Buffer)this.field_227126_f_).position(0);
      this.dataFile.write(this.field_227126_f_, 0L);
   }

   private int getOffset(ChunkPos p_222660_1_) {
      return this.offsets.get(getIndex(p_222660_1_));
   }

   public boolean contains(ChunkPos p_222667_1_) {
      return this.getOffset(p_222667_1_) != 0;
   }

   private static int getIndex(ChunkPos p_222668_0_) {
      return p_222668_0_.getRegionPositionX() + p_222668_0_.getRegionPositionZ() * 32;
   }

   public void close() throws IOException {
      try {
         this.func_227143_c_();
      } finally {
         try {
            this.func_227140_b_();
         } finally {
            try {
               this.dataFile.force(true);
            } finally {
               this.dataFile.close();
            }
         }
      }

   }

   private void func_227143_c_() throws IOException {
      int i = (int)this.dataFile.size();
      int j = func_227144_c_(i) * 4096;
      if (i != j) {
         ByteBuffer bytebuffer = field_227123_b_.duplicate();
         ((Buffer)bytebuffer).position(0);
         this.dataFile.write(bytebuffer, (long)(j - 1));
      }

   }

   class ChunkBuffer extends ByteArrayOutputStream {
      private final ChunkPos pos;

      public ChunkBuffer(ChunkPos p_i50620_2_) {
         super(8096);
         super.write(0);
         super.write(0);
         super.write(0);
         super.write(0);
         super.write(RegionFile.this.field_227125_e_.func_227165_a_());
         this.pos = p_i50620_2_;
      }

      public void close() throws IOException {
         ByteBuffer bytebuffer = ByteBuffer.wrap(this.buf, 0, this.count);
         bytebuffer.putInt(0, this.count - 5 + 1);
         RegionFile.this.func_227135_a_(this.pos, bytebuffer);
      }
   }

   interface ICompleteCallback {
      void run() throws IOException;
   }
}
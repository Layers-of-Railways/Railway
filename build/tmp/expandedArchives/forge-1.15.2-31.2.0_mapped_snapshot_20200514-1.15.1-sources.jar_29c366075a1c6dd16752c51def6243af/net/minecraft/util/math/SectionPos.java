package net.minecraft.util.math;

import java.util.Spliterators.AbstractSpliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import net.minecraft.entity.Entity;
import net.minecraft.util.Direction;

public class SectionPos extends Vec3i {
   private SectionPos(int p_i50794_1_, int p_i50794_2_, int p_i50794_3_) {
      super(p_i50794_1_, p_i50794_2_, p_i50794_3_);
   }

   public static SectionPos of(int chunkX, int chunkY, int chunkZ) {
      return new SectionPos(chunkX, chunkY, chunkZ);
   }

   public static SectionPos from(BlockPos worldPos) {
      return new SectionPos(toChunk(worldPos.getX()), toChunk(worldPos.getY()), toChunk(worldPos.getZ()));
   }

   public static SectionPos from(ChunkPos xz, int y) {
      return new SectionPos(xz.x, y, xz.z);
   }

   public static SectionPos from(Entity p_218157_0_) {
      return new SectionPos(toChunk(MathHelper.floor(p_218157_0_.getPosX())), toChunk(MathHelper.floor(p_218157_0_.getPosY())), toChunk(MathHelper.floor(p_218157_0_.getPosZ())));
   }

   public static SectionPos from(long p_218170_0_) {
      return new SectionPos(extractX(p_218170_0_), extractY(p_218170_0_), extractZ(p_218170_0_));
   }

   public static long withOffset(long p_218172_0_, Direction p_218172_2_) {
      return withOffset(p_218172_0_, p_218172_2_.getXOffset(), p_218172_2_.getYOffset(), p_218172_2_.getZOffset());
   }

   public static long withOffset(long p_218174_0_, int dx, int dy, int dz) {
      return asLong(extractX(p_218174_0_) + dx, extractY(p_218174_0_) + dy, extractZ(p_218174_0_) + dz);
   }

   public static int toChunk(int worldCoord) {
      return worldCoord >> 4;
   }

   public static int mask(int p_218171_0_) {
      return p_218171_0_ & 15;
   }

   public static short toRelativeOffset(BlockPos p_218150_0_) {
      int i = mask(p_218150_0_.getX());
      int j = mask(p_218150_0_.getY());
      int k = mask(p_218150_0_.getZ());
      return (short)(i << 8 | k << 4 | j);
   }

   public static int toWorld(int chunkCoord) {
      return chunkCoord << 4;
   }

   public static int extractX(long packed) {
      return (int)(packed << 0 >> 42);
   }

   public static int extractY(long packed) {
      return (int)(packed << 44 >> 44);
   }

   public static int extractZ(long packed) {
      return (int)(packed << 22 >> 42);
   }

   public int getSectionX() {
      return this.getX();
   }

   public int getSectionY() {
      return this.getY();
   }

   public int getSectionZ() {
      return this.getZ();
   }

   public int getWorldStartX() {
      return this.getSectionX() << 4;
   }

   public int getWorldStartY() {
      return this.getSectionY() << 4;
   }

   public int getWorldStartZ() {
      return this.getSectionZ() << 4;
   }

   public int getWorldEndX() {
      return (this.getSectionX() << 4) + 15;
   }

   public int getWorldEndY() {
      return (this.getSectionY() << 4) + 15;
   }

   public int getWorldEndZ() {
      return (this.getSectionZ() << 4) + 15;
   }

   public static long worldToSection(long worldPos) {
      return asLong(toChunk(BlockPos.unpackX(worldPos)), toChunk(BlockPos.unpackY(worldPos)), toChunk(BlockPos.unpackZ(worldPos)));
   }

   /**
    * Returns the given section position with Y position set to 0.
    */
   public static long toSectionColumnPos(long p_218169_0_) {
      return p_218169_0_ & -1048576L;
   }

   public BlockPos asBlockPos() {
      return new BlockPos(toWorld(this.getSectionX()), toWorld(this.getSectionY()), toWorld(this.getSectionZ()));
   }

   public BlockPos getCenter() {
      int i = 8;
      return this.asBlockPos().add(8, 8, 8);
   }

   public ChunkPos asChunkPos() {
      return new ChunkPos(this.getSectionX(), this.getSectionZ());
   }

   public static long asLong(int p_218166_0_, int p_218166_1_, int p_218166_2_) {
      long i = 0L;
      i = i | ((long)p_218166_0_ & 4194303L) << 42;
      i = i | ((long)p_218166_1_ & 1048575L) << 0;
      i = i | ((long)p_218166_2_ & 4194303L) << 20;
      return i;
   }

   public long asLong() {
      return asLong(this.getSectionX(), this.getSectionY(), this.getSectionZ());
   }

   public Stream<BlockPos> allBlocksWithin() {
      return BlockPos.getAllInBox(this.getWorldStartX(), this.getWorldStartY(), this.getWorldStartZ(), this.getWorldEndX(), this.getWorldEndY(), this.getWorldEndZ());
   }

   public static Stream<SectionPos> getAllInBox(SectionPos center, int radius) {
      int i = center.getSectionX();
      int j = center.getSectionY();
      int k = center.getSectionZ();
      return getAllInBox(i - radius, j - radius, k - radius, i + radius, j + radius, k + radius);
   }

   public static Stream<SectionPos> func_229421_b_(ChunkPos p_229421_0_, int p_229421_1_) {
      int i = p_229421_0_.x;
      int j = p_229421_0_.z;
      return getAllInBox(i - p_229421_1_, 0, j - p_229421_1_, i + p_229421_1_, 15, j + p_229421_1_);
   }

   public static Stream<SectionPos> getAllInBox(final int p_218168_0_, final int p_218168_1_, final int p_218168_2_, final int p_218168_3_, final int p_218168_4_, final int p_218168_5_) {
      return StreamSupport.stream(new AbstractSpliterator<SectionPos>((long)((p_218168_3_ - p_218168_0_ + 1) * (p_218168_4_ - p_218168_1_ + 1) * (p_218168_5_ - p_218168_2_ + 1)), 64) {
         final CubeCoordinateIterator field_218394_a = new CubeCoordinateIterator(p_218168_0_, p_218168_1_, p_218168_2_, p_218168_3_, p_218168_4_, p_218168_5_);

         public boolean tryAdvance(Consumer<? super SectionPos> p_tryAdvance_1_) {
            if (this.field_218394_a.hasNext()) {
               p_tryAdvance_1_.accept(new SectionPos(this.field_218394_a.getX(), this.field_218394_a.getY(), this.field_218394_a.getZ()));
               return true;
            } else {
               return false;
            }
         }
      }, false);
   }
}
package net.minecraft.util.math;

public class CubeCoordinateIterator {
   private final int startX;
   private final int startY;
   private final int startZ;
   private final int endX;
   private final int endY;
   private final int endZ;
   private int x;
   private int y;
   private int z;
   private boolean started;

   public CubeCoordinateIterator(int p_i50798_1_, int p_i50798_2_, int p_i50798_3_, int p_i50798_4_, int p_i50798_5_, int p_i50798_6_) {
      this.startX = p_i50798_1_;
      this.startY = p_i50798_2_;
      this.startZ = p_i50798_3_;
      this.endX = p_i50798_4_;
      this.endY = p_i50798_5_;
      this.endZ = p_i50798_6_;
   }

   public boolean hasNext() {
      if (!this.started) {
         this.x = this.startX;
         this.y = this.startY;
         this.z = this.startZ;
         this.started = true;
         return true;
      } else if (this.x == this.endX && this.y == this.endY && this.z == this.endZ) {
         return false;
      } else {
         if (this.x < this.endX) {
            ++this.x;
         } else if (this.y < this.endY) {
            this.x = this.startX;
            ++this.y;
         } else if (this.z < this.endZ) {
            this.x = this.startX;
            this.y = this.startY;
            ++this.z;
         }

         return true;
      }
   }

   public int getX() {
      return this.x;
   }

   public int getY() {
      return this.y;
   }

   public int getZ() {
      return this.z;
   }

   public int numBoundariesTouched() {
      int i = 0;
      if (this.x == this.startX || this.x == this.endX) {
         ++i;
      }

      if (this.y == this.startY || this.y == this.endY) {
         ++i;
      }

      if (this.z == this.startZ || this.z == this.endZ) {
         ++i;
      }

      return i;
   }
}
package net.minecraft.util.math;

public class ColumnPos {
   public final int x;
   public final int z;

   public ColumnPos(int x, int z) {
      this.x = x;
      this.z = z;
   }

   public ColumnPos(BlockPos pos) {
      this.x = pos.getX();
      this.z = pos.getZ();
   }

   public String toString() {
      return "[" + this.x + ", " + this.z + "]";
   }

   public int hashCode() {
      int i = 1664525 * this.x + 1013904223;
      int j = 1664525 * (this.z ^ -559038737) + 1013904223;
      return i ^ j;
   }

   public boolean equals(Object p_equals_1_) {
      if (this == p_equals_1_) {
         return true;
      } else if (!(p_equals_1_ instanceof ColumnPos)) {
         return false;
      } else {
         ColumnPos columnpos = (ColumnPos)p_equals_1_;
         return this.x == columnpos.x && this.z == columnpos.z;
      }
   }
}
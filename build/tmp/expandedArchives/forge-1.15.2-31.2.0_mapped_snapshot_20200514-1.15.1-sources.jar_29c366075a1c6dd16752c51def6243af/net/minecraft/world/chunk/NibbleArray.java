package net.minecraft.world.chunk;

import javax.annotation.Nullable;
import net.minecraft.util.Util;

public class NibbleArray {
   @Nullable
   protected byte[] data;

   public NibbleArray() {
   }

   public NibbleArray(byte[] storageArray) {
      this.data = storageArray;
      if (storageArray.length != 2048) {
         throw (IllegalArgumentException)Util.pauseDevMode(new IllegalArgumentException("ChunkNibbleArrays should be 2048 bytes not: " + storageArray.length));
      }
   }

   protected NibbleArray(int p_i49951_1_) {
      this.data = new byte[p_i49951_1_];
   }

   /**
    * Returns the nibble of data corresponding to the passed in x, y, z. y is at most 6 bits, z is at most 4.
    */
   public int get(int x, int y, int z) {
      return this.getFromIndex(this.getCoordinateIndex(x, y, z));
   }

   /**
    * Arguments are x, y, z, val. Sets the nibble of data at x << 11 | z << 7 | y to val.
    */
   public void set(int x, int y, int z, int value) {
      this.setIndex(this.getCoordinateIndex(x, y, z), value);
   }

   protected int getCoordinateIndex(int x, int y, int z) {
      return y << 8 | z << 4 | x;
   }

   private int getFromIndex(int index) {
      if (this.data == null) {
         return 0;
      } else {
         int i = this.getNibbleIndex(index);
         return this.isLowerNibble(index) ? this.data[i] & 15 : this.data[i] >> 4 & 15;
      }
   }

   private void setIndex(int index, int value) {
      if (this.data == null) {
         this.data = new byte[2048];
      }

      int i = this.getNibbleIndex(index);
      if (this.isLowerNibble(index)) {
         this.data[i] = (byte)(this.data[i] & 240 | value & 15);
      } else {
         this.data[i] = (byte)(this.data[i] & 15 | (value & 15) << 4);
      }

   }

   private boolean isLowerNibble(int index) {
      return (index & 1) == 0;
   }

   private int getNibbleIndex(int index) {
      return index >> 1;
   }

   public byte[] getData() {
      if (this.data == null) {
         this.data = new byte[2048];
      }

      return this.data;
   }

   public NibbleArray copy() {
      return this.data == null ? new NibbleArray() : new NibbleArray((byte[])this.data.clone());
   }

   public String toString() {
      StringBuilder stringbuilder = new StringBuilder();

      for(int i = 0; i < 4096; ++i) {
         stringbuilder.append(Integer.toHexString(this.getFromIndex(i)));
         if ((i & 15) == 15) {
            stringbuilder.append("\n");
         }

         if ((i & 255) == 255) {
            stringbuilder.append("\n");
         }
      }

      return stringbuilder.toString();
   }

   public boolean isEmpty() {
      return this.data == null;
   }
}
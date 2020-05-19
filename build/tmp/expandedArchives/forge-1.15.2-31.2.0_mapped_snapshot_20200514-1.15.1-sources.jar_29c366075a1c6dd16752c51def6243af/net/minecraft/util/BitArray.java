package net.minecraft.util;

import java.util.function.IntConsumer;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.Validate;

public class BitArray {
   private final long[] longArray;
   private final int bitsPerEntry;
   private final long maxEntryValue;
   private final int arraySize;

   public BitArray(int bitsPerEntryIn, int arraySizeIn) {
      this(bitsPerEntryIn, arraySizeIn, new long[MathHelper.roundUp(arraySizeIn * bitsPerEntryIn, 64) / 64]);
   }

   public BitArray(int bitsPerEntryIn, int arraySizeIn, long[] data) {
      Validate.inclusiveBetween(1L, 32L, (long)bitsPerEntryIn);
      this.arraySize = arraySizeIn;
      this.bitsPerEntry = bitsPerEntryIn;
      this.longArray = data;
      this.maxEntryValue = (1L << bitsPerEntryIn) - 1L;
      int i = MathHelper.roundUp(arraySizeIn * bitsPerEntryIn, 64) / 64;
      if (data.length != i) {
         throw (RuntimeException)Util.pauseDevMode(new RuntimeException("Invalid length given for storage, got: " + data.length + " but expected: " + i));
      }
   }

   public int swapAt(int index, int value) {
      Validate.inclusiveBetween(0L, (long)(this.arraySize - 1), (long)index);
      Validate.inclusiveBetween(0L, this.maxEntryValue, (long)value);
      int i = index * this.bitsPerEntry;
      int j = i >> 6;
      int k = (index + 1) * this.bitsPerEntry - 1 >> 6;
      int l = i ^ j << 6;
      int i1 = 0;
      i1 = i1 | (int)(this.longArray[j] >>> l & this.maxEntryValue);
      this.longArray[j] = this.longArray[j] & ~(this.maxEntryValue << l) | ((long)value & this.maxEntryValue) << l;
      if (j != k) {
         int j1 = 64 - l;
         int k1 = this.bitsPerEntry - j1;
         i1 |= (int)(this.longArray[k] << j1 & this.maxEntryValue);
         this.longArray[k] = this.longArray[k] >>> k1 << k1 | ((long)value & this.maxEntryValue) >> j1;
      }

      return i1;
   }

   /**
    * Sets the entry at the given location to the given value
    */
   public void setAt(int index, int value) {
      Validate.inclusiveBetween(0L, (long)(this.arraySize - 1), (long)index);
      Validate.inclusiveBetween(0L, this.maxEntryValue, (long)value);
      int i = index * this.bitsPerEntry;
      int j = i >> 6;
      int k = (index + 1) * this.bitsPerEntry - 1 >> 6;
      int l = i ^ j << 6;
      this.longArray[j] = this.longArray[j] & ~(this.maxEntryValue << l) | ((long)value & this.maxEntryValue) << l;
      if (j != k) {
         int i1 = 64 - l;
         int j1 = this.bitsPerEntry - i1;
         this.longArray[k] = this.longArray[k] >>> j1 << j1 | ((long)value & this.maxEntryValue) >> i1;
      }

   }

   /**
    * Gets the entry at the given index
    */
   public int getAt(int index) {
      Validate.inclusiveBetween(0L, (long)(this.arraySize - 1), (long)index);
      int i = index * this.bitsPerEntry;
      int j = i >> 6;
      int k = (index + 1) * this.bitsPerEntry - 1 >> 6;
      int l = i ^ j << 6;
      if (j == k) {
         return (int)(this.longArray[j] >>> l & this.maxEntryValue);
      } else {
         int i1 = 64 - l;
         return (int)((this.longArray[j] >>> l | this.longArray[k] << i1) & this.maxEntryValue);
      }
   }

   /**
    * Gets the long array that is used to store the data in this BitArray. This is useful for sending packet data.
    */
   public long[] getBackingLongArray() {
      return this.longArray;
   }

   public int size() {
      return this.arraySize;
   }

   public int bitsPerEntry() {
      return this.bitsPerEntry;
   }

   public void getAll(IntConsumer consumer) {
      int i = this.longArray.length;
      if (i != 0) {
         int j = 0;
         long k = this.longArray[0];
         long l = i > 1 ? this.longArray[1] : 0L;

         for(int i1 = 0; i1 < this.arraySize; ++i1) {
            int j1 = i1 * this.bitsPerEntry;
            int k1 = j1 >> 6;
            int l1 = (i1 + 1) * this.bitsPerEntry - 1 >> 6;
            int i2 = j1 ^ k1 << 6;
            if (k1 != j) {
               k = l;
               l = k1 + 1 < i ? this.longArray[k1 + 1] : 0L;
               j = k1;
            }

            if (k1 == l1) {
               consumer.accept((int)(k >>> i2 & this.maxEntryValue));
            } else {
               int j2 = 64 - i2;
               consumer.accept((int)((k >>> i2 | l << j2) & this.maxEntryValue));
            }
         }

      }
   }
}
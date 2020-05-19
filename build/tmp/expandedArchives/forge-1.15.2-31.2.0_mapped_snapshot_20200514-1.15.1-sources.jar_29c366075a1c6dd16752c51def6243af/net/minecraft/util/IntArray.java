package net.minecraft.util;

public class IntArray implements IIntArray {
   private final int[] field_221479_a;

   public IntArray(int p_i50063_1_) {
      this.field_221479_a = new int[p_i50063_1_];
   }

   public int get(int index) {
      return this.field_221479_a[index];
   }

   public void set(int index, int value) {
      this.field_221479_a[index] = value;
   }

   public int size() {
      return this.field_221479_a.length;
   }
}
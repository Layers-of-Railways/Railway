package net.minecraft.util.math.shapes;

import it.unimi.dsi.fastutil.doubles.AbstractDoubleList;

public class DoubleRangeList extends AbstractDoubleList {
   private final int field_197854_a;

   DoubleRangeList(int p_i47689_1_) {
      this.field_197854_a = p_i47689_1_;
   }

   public double getDouble(int p_getDouble_1_) {
      return (double)p_getDouble_1_ / (double)this.field_197854_a;
   }

   public int size() {
      return this.field_197854_a + 1;
   }
}
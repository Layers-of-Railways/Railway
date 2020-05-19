package net.minecraft.util.math.shapes;

import it.unimi.dsi.fastutil.doubles.AbstractDoubleList;
import it.unimi.dsi.fastutil.doubles.DoubleList;

public class NonOverlappingMerger extends AbstractDoubleList implements IDoubleListMerger {
   private final DoubleList list1;
   private final DoubleList list2;
   private final boolean field_199640_c;

   public NonOverlappingMerger(DoubleList list1, DoubleList list2, boolean p_i48187_3_) {
      this.list1 = list1;
      this.list2 = list2;
      this.field_199640_c = p_i48187_3_;
   }

   public int size() {
      return this.list1.size() + this.list2.size();
   }

   public boolean forMergedIndexes(IDoubleListMerger.IConsumer consumer) {
      return this.field_199640_c ? this.func_199637_b((p_199636_1_, p_199636_2_, p_199636_3_) -> {
         return consumer.merge(p_199636_2_, p_199636_1_, p_199636_3_);
      }) : this.func_199637_b(consumer);
   }

   private boolean func_199637_b(IDoubleListMerger.IConsumer p_199637_1_) {
      int i = this.list1.size() - 1;

      for(int j = 0; j < i; ++j) {
         if (!p_199637_1_.merge(j, -1, j)) {
            return false;
         }
      }

      if (!p_199637_1_.merge(i, -1, i)) {
         return false;
      } else {
         for(int k = 0; k < this.list2.size(); ++k) {
            if (!p_199637_1_.merge(i, k, i + 1 + k)) {
               return false;
            }
         }

         return true;
      }
   }

   public double getDouble(int p_getDouble_1_) {
      return p_getDouble_1_ < this.list1.size() ? this.list1.getDouble(p_getDouble_1_) : this.list2.getDouble(p_getDouble_1_ - this.list1.size());
   }

   public DoubleList func_212435_a() {
      return this;
   }
}
package net.minecraft.util.math.shapes;

import com.google.common.math.IntMath;
import it.unimi.dsi.fastutil.doubles.DoubleList;

public final class DoubleCubeMergingList implements IDoubleListMerger {
   private final DoubleRangeList field_212436_a;
   private final int firstSize;
   private final int secondSize;
   private final int gcd;

   DoubleCubeMergingList(int firstSize, int secondSize) {
      this.field_212436_a = new DoubleRangeList((int)VoxelShapes.lcm(firstSize, secondSize));
      this.firstSize = firstSize;
      this.secondSize = secondSize;
      this.gcd = IntMath.gcd(firstSize, secondSize);
   }

   public boolean forMergedIndexes(IDoubleListMerger.IConsumer consumer) {
      int i = this.firstSize / this.gcd;
      int j = this.secondSize / this.gcd;

      for(int k = 0; k <= this.field_212436_a.size(); ++k) {
         if (!consumer.merge(k / j, k / i, k)) {
            return false;
         }
      }

      return true;
   }

   public DoubleList func_212435_a() {
      return this.field_212436_a;
   }
}
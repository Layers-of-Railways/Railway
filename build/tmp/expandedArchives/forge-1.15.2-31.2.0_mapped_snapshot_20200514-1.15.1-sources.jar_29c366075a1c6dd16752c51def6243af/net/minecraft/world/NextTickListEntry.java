package net.minecraft.world;

import java.util.Comparator;
import net.minecraft.util.math.BlockPos;

public class NextTickListEntry<T> {
   private static long nextTickEntryID;
   private final T target;
   public final BlockPos position;
   public final long scheduledTime;
   public final TickPriority priority;
   private final long tickEntryID;

   public NextTickListEntry(BlockPos positionIn, T p_i48977_2_) {
      this(positionIn, p_i48977_2_, 0L, TickPriority.NORMAL);
   }

   public NextTickListEntry(BlockPos positionIn, T p_i48978_2_, long scheduledTimeIn, TickPriority priorityIn) {
      this.tickEntryID = (long)(nextTickEntryID++);
      this.position = positionIn.toImmutable();
      this.target = p_i48978_2_;
      this.scheduledTime = scheduledTimeIn;
      this.priority = priorityIn;
   }

   public boolean equals(Object p_equals_1_) {
      if (!(p_equals_1_ instanceof NextTickListEntry)) {
         return false;
      } else {
         NextTickListEntry<?> nextticklistentry = (NextTickListEntry)p_equals_1_;
         return this.position.equals(nextticklistentry.position) && this.target == nextticklistentry.target;
      }
   }

   public int hashCode() {
      return this.position.hashCode();
   }

   public static <T> Comparator<NextTickListEntry<T>> func_223192_a() {
      return Comparator.<NextTickListEntry<T>>comparingLong((p_226710_0_) -> {
         return p_226710_0_.scheduledTime;
      }).thenComparing((p_226709_0_) -> {
         return p_226709_0_.priority;
      }).thenComparingLong((p_226708_0_) -> {
         return p_226708_0_.tickEntryID;
      });
   }

   public String toString() {
      return this.target + ": " + this.position + ", " + this.scheduledTime + ", " + this.priority + ", " + this.tickEntryID;
   }

   public T getTarget() {
      return this.target;
   }
}
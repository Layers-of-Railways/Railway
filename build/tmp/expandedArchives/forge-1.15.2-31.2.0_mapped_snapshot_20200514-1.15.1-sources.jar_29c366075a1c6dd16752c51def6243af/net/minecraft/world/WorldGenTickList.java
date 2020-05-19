package net.minecraft.world;

import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.util.math.BlockPos;

public class WorldGenTickList<T> implements ITickList<T> {
   private final Function<BlockPos, ITickList<T>> tickListProvider;

   public WorldGenTickList(Function<BlockPos, ITickList<T>> tickListProviderIn) {
      this.tickListProvider = tickListProviderIn;
   }

   public boolean isTickScheduled(BlockPos pos, T itemIn) {
      return this.tickListProvider.apply(pos).isTickScheduled(pos, itemIn);
   }

   public void scheduleTick(BlockPos pos, T itemIn, int scheduledTime, TickPriority priority) {
      this.tickListProvider.apply(pos).scheduleTick(pos, itemIn, scheduledTime, priority);
   }

   /**
    * Checks if this position/item is scheduled to be updated this tick
    */
   public boolean isTickPending(BlockPos pos, T obj) {
      return false;
   }

   public void addAll(Stream<NextTickListEntry<T>> p_219497_1_) {
      p_219497_1_.forEach((p_219507_1_) -> {
         this.tickListProvider.apply(p_219507_1_.position).addAll(Stream.of(p_219507_1_));
      });
   }
}
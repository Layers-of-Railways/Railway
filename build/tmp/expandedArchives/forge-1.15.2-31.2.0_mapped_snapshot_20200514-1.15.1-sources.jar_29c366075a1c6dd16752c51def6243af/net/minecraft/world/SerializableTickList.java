package net.minecraft.world;

import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerTickList;

public class SerializableTickList<T> implements ITickList<T> {
   private final Set<NextTickListEntry<T>> ticks;
   private final Function<T, ResourceLocation> toId;

   public SerializableTickList(Function<T, ResourceLocation> p_i50010_1_, List<NextTickListEntry<T>> p_i50010_2_) {
      this(p_i50010_1_, Sets.newHashSet(p_i50010_2_));
   }

   private SerializableTickList(Function<T, ResourceLocation> p_i51499_1_, Set<NextTickListEntry<T>> p_i51499_2_) {
      this.ticks = p_i51499_2_;
      this.toId = p_i51499_1_;
   }

   public boolean isTickScheduled(BlockPos pos, T itemIn) {
      return false;
   }

   public void scheduleTick(BlockPos pos, T itemIn, int scheduledTime, TickPriority priority) {
      this.ticks.add(new NextTickListEntry<>(pos, itemIn, (long)scheduledTime, priority));
   }

   /**
    * Checks if this position/item is scheduled to be updated this tick
    */
   public boolean isTickPending(BlockPos pos, T obj) {
      return false;
   }

   public void addAll(Stream<NextTickListEntry<T>> p_219497_1_) {
      p_219497_1_.forEach(this.ticks::add);
   }

   public Stream<NextTickListEntry<T>> ticks() {
      return this.ticks.stream();
   }

   public ListNBT save(long p_219498_1_) {
      return ServerTickList.func_219502_a(this.toId, this.ticks, p_219498_1_);
   }

   public static <T> SerializableTickList<T> create(ListNBT p_222984_0_, Function<T, ResourceLocation> p_222984_1_, Function<ResourceLocation, T> p_222984_2_) {
      Set<NextTickListEntry<T>> set = Sets.newHashSet();

      for(int i = 0; i < p_222984_0_.size(); ++i) {
         CompoundNBT compoundnbt = p_222984_0_.getCompound(i);
         T t = p_222984_2_.apply(new ResourceLocation(compoundnbt.getString("i")));
         if (t != null) {
            set.add(new NextTickListEntry<>(new BlockPos(compoundnbt.getInt("x"), compoundnbt.getInt("y"), compoundnbt.getInt("z")), t, (long)compoundnbt.getInt("t"), TickPriority.getPriority(compoundnbt.getInt("p"))));
         }
      }

      return new SerializableTickList<>(p_222984_1_, set);
   }
}
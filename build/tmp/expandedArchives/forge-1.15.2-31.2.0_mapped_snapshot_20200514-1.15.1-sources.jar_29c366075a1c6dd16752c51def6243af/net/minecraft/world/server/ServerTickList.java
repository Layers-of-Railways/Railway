package net.minecraft.world.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.ITickList;
import net.minecraft.world.NextTickListEntry;
import net.minecraft.world.TickPriority;

public class ServerTickList<T> implements ITickList<T> {
   protected final Predicate<T> filter;
   private final Function<T, ResourceLocation> serializer;
   private final Function<ResourceLocation, T> deserializer;
   private final Set<NextTickListEntry<T>> pendingTickListEntriesHashSet = Sets.newHashSet();
   private final TreeSet<NextTickListEntry<T>> pendingTickListEntriesTreeSet = Sets.newTreeSet(NextTickListEntry.func_223192_a());
   private final ServerWorld world;
   private final Queue<NextTickListEntry<T>> pendingTickListEntriesThisTick = Queues.newArrayDeque();
   /** Entries from {@link #pendingTickListEntriesThisTick} that have already run this tick. */
   private final List<NextTickListEntry<T>> entriesRunThisTick = Lists.newArrayList();
   private final Consumer<NextTickListEntry<T>> tickFunction;

   public ServerTickList(ServerWorld worldIn, Predicate<T> filter, Function<T, ResourceLocation> serializerIn, Function<ResourceLocation, T> deserializerIn, Consumer<NextTickListEntry<T>> tickFunctionIn) {
      this.filter = filter;
      this.serializer = serializerIn;
      this.deserializer = deserializerIn;
      this.world = worldIn;
      this.tickFunction = tickFunctionIn;
   }

   public void tick() {
      int i = this.pendingTickListEntriesTreeSet.size();
      if (i != this.pendingTickListEntriesHashSet.size()) {
         throw new IllegalStateException("TickNextTick list out of synch");
      } else {
         if (i > 65536) {
            i = 65536;
         }

         ServerChunkProvider serverchunkprovider = this.world.getChunkProvider();
         Iterator<NextTickListEntry<T>> iterator = this.pendingTickListEntriesTreeSet.iterator();
         this.world.getProfiler().startSection("cleaning");

         while(i > 0 && iterator.hasNext()) {
            NextTickListEntry<T> nextticklistentry = iterator.next();
            if (nextticklistentry.scheduledTime > this.world.getGameTime()) {
               break;
            }

            if (serverchunkprovider.canTick(nextticklistentry.position)) {
               iterator.remove();
               this.pendingTickListEntriesHashSet.remove(nextticklistentry);
               this.pendingTickListEntriesThisTick.add(nextticklistentry);
               --i;
            }
         }

         this.world.getProfiler().endStartSection("ticking");

         NextTickListEntry<T> nextticklistentry1;
         while((nextticklistentry1 = this.pendingTickListEntriesThisTick.poll()) != null) {
            if (serverchunkprovider.canTick(nextticklistentry1.position)) {
               try {
                  this.entriesRunThisTick.add(nextticklistentry1);
                  this.tickFunction.accept(nextticklistentry1);
               } catch (Throwable throwable) {
                  CrashReport crashreport = CrashReport.makeCrashReport(throwable, "Exception while ticking");
                  CrashReportCategory crashreportcategory = crashreport.makeCategory("Block being ticked");
                  CrashReportCategory.addBlockInfo(crashreportcategory, nextticklistentry1.position, (BlockState)null);
                  throw new ReportedException(crashreport);
               }
            } else {
               this.scheduleTick(nextticklistentry1.position, nextticklistentry1.getTarget(), 0);
            }
         }

         this.world.getProfiler().endSection();
         this.entriesRunThisTick.clear();
         this.pendingTickListEntriesThisTick.clear();
      }
   }

   /**
    * Checks if this position/item is scheduled to be updated this tick
    */
   public boolean isTickPending(BlockPos pos, T obj) {
      return this.pendingTickListEntriesThisTick.contains(new NextTickListEntry(pos, obj));
   }

   public void addAll(Stream<NextTickListEntry<T>> p_219497_1_) {
      p_219497_1_.forEach(this::addEntry);
   }

   public List<NextTickListEntry<T>> getPending(ChunkPos pos, boolean remove, boolean skipCompleted) {
      int i = (pos.x << 4) - 2;
      int j = i + 16 + 2;
      int k = (pos.z << 4) - 2;
      int l = k + 16 + 2;
      return this.getPending(new MutableBoundingBox(i, 0, k, j, 256, l), remove, skipCompleted);
   }

   public List<NextTickListEntry<T>> getPending(MutableBoundingBox p_205366_1_, boolean remove, boolean skipCompleted) {
      List<NextTickListEntry<T>> list = this.getEntries((List<NextTickListEntry<T>>)null, this.pendingTickListEntriesTreeSet, p_205366_1_, remove);
      if (remove && list != null) {
         this.pendingTickListEntriesHashSet.removeAll(list);
      }

      list = this.getEntries(list, this.pendingTickListEntriesThisTick, p_205366_1_, remove);
      if (!skipCompleted) {
         list = this.getEntries(list, this.entriesRunThisTick, p_205366_1_, remove);
      }

      return list == null ? Collections.emptyList() : list;
   }

   @Nullable
   private List<NextTickListEntry<T>> getEntries(@Nullable List<NextTickListEntry<T>> result, Collection<NextTickListEntry<T>> entries, MutableBoundingBox bb, boolean remove) {
      Iterator<NextTickListEntry<T>> iterator = entries.iterator();

      while(iterator.hasNext()) {
         NextTickListEntry<T> nextticklistentry = iterator.next();
         BlockPos blockpos = nextticklistentry.position;
         if (blockpos.getX() >= bb.minX && blockpos.getX() < bb.maxX && blockpos.getZ() >= bb.minZ && blockpos.getZ() < bb.maxZ) {
            if (remove) {
               iterator.remove();
            }

            if (result == null) {
               result = Lists.newArrayList();
            }

            result.add(nextticklistentry);
         }
      }

      return result;
   }

   public void copyTicks(MutableBoundingBox area, BlockPos offset) {
      for(NextTickListEntry<T> nextticklistentry : this.getPending(area, false, false)) {
         if (area.isVecInside(nextticklistentry.position)) {
            BlockPos blockpos = nextticklistentry.position.add(offset);
            T t = nextticklistentry.getTarget();
            this.addEntry(new NextTickListEntry<>(blockpos, t, nextticklistentry.scheduledTime, nextticklistentry.priority));
         }
      }

   }

   public ListNBT func_219503_a(ChunkPos p_219503_1_) {
      List<NextTickListEntry<T>> list = this.getPending(p_219503_1_, false, true);
      return func_219502_a(this.serializer, list, this.world.getGameTime());
   }

   public static <T> ListNBT func_219502_a(Function<T, ResourceLocation> p_219502_0_, Iterable<NextTickListEntry<T>> p_219502_1_, long p_219502_2_) {
      ListNBT listnbt = new ListNBT();

      for(NextTickListEntry<T> nextticklistentry : p_219502_1_) {
         CompoundNBT compoundnbt = new CompoundNBT();
         compoundnbt.putString("i", p_219502_0_.apply(nextticklistentry.getTarget()).toString());
         compoundnbt.putInt("x", nextticklistentry.position.getX());
         compoundnbt.putInt("y", nextticklistentry.position.getY());
         compoundnbt.putInt("z", nextticklistentry.position.getZ());
         compoundnbt.putInt("t", (int)(nextticklistentry.scheduledTime - p_219502_2_));
         compoundnbt.putInt("p", nextticklistentry.priority.getPriority());
         listnbt.add(compoundnbt);
      }

      return listnbt;
   }

   public boolean isTickScheduled(BlockPos pos, T itemIn) {
      return this.pendingTickListEntriesHashSet.contains(new NextTickListEntry(pos, itemIn));
   }

   public void scheduleTick(BlockPos pos, T itemIn, int scheduledTime, TickPriority priority) {
      if (!this.filter.test(itemIn)) {
         this.addEntry(new NextTickListEntry<>(pos, itemIn, (long)scheduledTime + this.world.getGameTime(), priority));
      }

   }

   private void addEntry(NextTickListEntry<T> p_219504_1_) {
      if (!this.pendingTickListEntriesHashSet.contains(p_219504_1_)) {
         this.pendingTickListEntriesHashSet.add(p_219504_1_);
         this.pendingTickListEntriesTreeSet.add(p_219504_1_);
      }

   }

   public int func_225420_a() {
      return this.pendingTickListEntriesHashSet.size();
   }
}
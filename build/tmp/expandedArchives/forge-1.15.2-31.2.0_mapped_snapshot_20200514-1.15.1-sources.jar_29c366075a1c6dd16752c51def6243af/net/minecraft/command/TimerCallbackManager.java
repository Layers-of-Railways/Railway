package net.minecraft.command;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.primitives.UnsignedLong;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TimerCallbackManager<T> {
   private static final Logger LOGGER = LogManager.getLogger();
   private final TimerCallbackSerializers<T> field_216334_b;
   private final Queue<TimerCallbackManager.Entry<T>> entries = new PriorityQueue<>(sorter());
   private UnsignedLong nextUniqueId = UnsignedLong.ZERO;
   private final Table<String, Long, TimerCallbackManager.Entry<T>> byName = HashBasedTable.create();

   private static <T> Comparator<TimerCallbackManager.Entry<T>> sorter() {
      return Comparator.<TimerCallbackManager.Entry<T>>comparingLong((p_227578_0_) -> {
         return p_227578_0_.triggerTime;
      }).thenComparing((p_227577_0_) -> {
         return p_227577_0_.uniqueId;
      });
   }

   public TimerCallbackManager(TimerCallbackSerializers<T> p_i51188_1_) {
      this.field_216334_b = p_i51188_1_;
   }

   public void run(T p_216331_1_, long gameTime) {
      while(true) {
         TimerCallbackManager.Entry<T> entry = this.entries.peek();
         if (entry == null || entry.triggerTime > gameTime) {
            return;
         }

         this.entries.remove();
         this.byName.remove(entry.name, gameTime);
         entry.callback.run(p_216331_1_, this, gameTime);
      }
   }

   public void func_227576_a_(String p_227576_1_, long p_227576_2_, ITimerCallback<T> p_227576_4_) {
      if (!this.byName.contains(p_227576_1_, p_227576_2_)) {
         this.nextUniqueId = this.nextUniqueId.plus(UnsignedLong.ONE);
         TimerCallbackManager.Entry<T> entry = new TimerCallbackManager.Entry<>(p_227576_2_, this.nextUniqueId, p_227576_1_, p_227576_4_);
         this.byName.put(p_227576_1_, p_227576_2_, entry);
         this.entries.add(entry);
      }
   }

   public int func_227575_a_(String p_227575_1_) {
      Collection<TimerCallbackManager.Entry<T>> collection = this.byName.row(p_227575_1_).values();
      collection.forEach(this.entries::remove);
      int i = collection.size();
      collection.clear();
      return i;
   }

   public Set<String> func_227574_a_() {
      return Collections.unmodifiableSet(this.byName.rowKeySet());
   }

   private void readEntry(CompoundNBT p_216329_1_) {
      CompoundNBT compoundnbt = p_216329_1_.getCompound("Callback");
      ITimerCallback<T> itimercallback = this.field_216334_b.func_216341_a(compoundnbt);
      if (itimercallback != null) {
         String s = p_216329_1_.getString("Name");
         long i = p_216329_1_.getLong("TriggerTime");
         this.func_227576_a_(s, i, itimercallback);
      }

   }

   public void read(ListNBT p_216323_1_) {
      this.entries.clear();
      this.byName.clear();
      this.nextUniqueId = UnsignedLong.ZERO;
      if (!p_216323_1_.isEmpty()) {
         if (p_216323_1_.getTagType() != 10) {
            LOGGER.warn("Invalid format of events: " + p_216323_1_);
         } else {
            for(INBT inbt : p_216323_1_) {
               this.readEntry((CompoundNBT)inbt);
            }

         }
      }
   }

   private CompoundNBT writeEntry(TimerCallbackManager.Entry<T> p_216332_1_) {
      CompoundNBT compoundnbt = new CompoundNBT();
      compoundnbt.putString("Name", p_216332_1_.name);
      compoundnbt.putLong("TriggerTime", p_216332_1_.triggerTime);
      compoundnbt.put("Callback", this.field_216334_b.func_216339_a(p_216332_1_.callback));
      return compoundnbt;
   }

   public ListNBT write() {
      ListNBT listnbt = new ListNBT();
      this.entries.stream().sorted(sorter()).map(this::writeEntry).forEach(listnbt::add);
      return listnbt;
   }

   public static class Entry<T> {
      public final long triggerTime;
      public final UnsignedLong uniqueId;
      public final String name;
      public final ITimerCallback<T> callback;

      private Entry(long p_i50837_1_, UnsignedLong p_i50837_3_, String p_i50837_4_, ITimerCallback<T> p_i50837_5_) {
         this.triggerTime = p_i50837_1_;
         this.uniqueId = p_i50837_3_;
         this.name = p_i50837_4_;
         this.callback = p_i50837_5_;
      }
   }
}
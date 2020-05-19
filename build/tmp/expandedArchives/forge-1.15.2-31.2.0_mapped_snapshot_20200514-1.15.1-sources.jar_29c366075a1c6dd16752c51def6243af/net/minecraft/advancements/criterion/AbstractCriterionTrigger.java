package net.minecraft.advancements.criterion;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.advancements.ICriterionInstance;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;

public abstract class AbstractCriterionTrigger<T extends ICriterionInstance> implements ICriterionTrigger<T> {
   private final Map<PlayerAdvancements, Set<ICriterionTrigger.Listener<T>>> field_227069_a_ = Maps.newIdentityHashMap();

   public final void addListener(PlayerAdvancements playerAdvancementsIn, ICriterionTrigger.Listener<T> listener) {
      this.field_227069_a_.computeIfAbsent(playerAdvancementsIn, (p_227072_0_) -> {
         return Sets.newHashSet();
      }).add(listener);
   }

   public final void removeListener(PlayerAdvancements playerAdvancementsIn, ICriterionTrigger.Listener<T> listener) {
      Set<ICriterionTrigger.Listener<T>> set = this.field_227069_a_.get(playerAdvancementsIn);
      if (set != null) {
         set.remove(listener);
         if (set.isEmpty()) {
            this.field_227069_a_.remove(playerAdvancementsIn);
         }
      }

   }

   public final void removeAllListeners(PlayerAdvancements playerAdvancementsIn) {
      this.field_227069_a_.remove(playerAdvancementsIn);
   }

   protected void func_227070_a_(PlayerAdvancements p_227070_1_, Predicate<T> p_227070_2_) {
      Set<ICriterionTrigger.Listener<T>> set = this.field_227069_a_.get(p_227070_1_);
      if (set != null) {
         List<ICriterionTrigger.Listener<T>> list = null;

         for(ICriterionTrigger.Listener<T> listener : set) {
            if (p_227070_2_.test(listener.getCriterionInstance())) {
               if (list == null) {
                  list = Lists.newArrayList();
               }

               list.add(listener);
            }
         }

         if (list != null) {
            for(ICriterionTrigger.Listener<T> listener1 : list) {
               listener1.grantCriterion(p_227070_1_);
            }
         }

      }
   }

   protected void func_227071_b_(PlayerAdvancements p_227071_1_) {
      Set<ICriterionTrigger.Listener<T>> set = this.field_227069_a_.get(p_227071_1_);
      if (set != null && !set.isEmpty()) {
         for(ICriterionTrigger.Listener<T> listener : ImmutableSet.copyOf(set)) {
            listener.grantCriterion(p_227071_1_);
         }
      }

   }
}
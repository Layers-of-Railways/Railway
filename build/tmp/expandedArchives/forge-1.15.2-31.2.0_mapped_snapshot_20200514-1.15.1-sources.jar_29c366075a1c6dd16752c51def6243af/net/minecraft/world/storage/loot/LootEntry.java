package net.minecraft.world.storage.loot;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.conditions.ILootCondition;
import net.minecraft.world.storage.loot.conditions.LootConditionManager;

public abstract class LootEntry implements ILootEntry {
   /** Conditions for the loot entry to be applied. */
   protected final ILootCondition[] conditions;
   private final Predicate<LootContext> field_216143_c;

   protected LootEntry(ILootCondition[] p_i51254_1_) {
      this.conditions = p_i51254_1_;
      this.field_216143_c = LootConditionManager.and(p_i51254_1_);
   }

   public void func_225579_a_(ValidationTracker p_225579_1_) {
      for(int i = 0; i < this.conditions.length; ++i) {
         this.conditions[i].func_225580_a_(p_225579_1_.func_227534_b_(".condition[" + i + "]"));
      }

   }

   protected final boolean test(LootContext p_216141_1_) {
      return this.field_216143_c.test(p_216141_1_);
   }

   public abstract static class Builder<T extends LootEntry.Builder<T>> implements ILootConditionConsumer<T> {
      private final List<ILootCondition> field_216082_a = Lists.newArrayList();

      protected abstract T func_212845_d_();

      public T acceptCondition(ILootCondition.IBuilder conditionBuilder) {
         this.field_216082_a.add(conditionBuilder.build());
         return (T)this.func_212845_d_();
      }

      public final T cast() {
         return (T)this.func_212845_d_();
      }

      protected ILootCondition[] func_216079_f() {
         return this.field_216082_a.toArray(new ILootCondition[0]);
      }

      public AlternativesLootEntry.Builder alternatively(LootEntry.Builder<?> p_216080_1_) {
         return new AlternativesLootEntry.Builder(this, p_216080_1_);
      }

      public abstract LootEntry build();
   }

   public abstract static class Serializer<T extends LootEntry> {
      private final ResourceLocation field_216184_a;
      private final Class<T> field_216185_b;

      protected Serializer(ResourceLocation p_i50544_1_, Class<T> p_i50544_2_) {
         this.field_216184_a = p_i50544_1_;
         this.field_216185_b = p_i50544_2_;
      }

      public ResourceLocation func_216182_a() {
         return this.field_216184_a;
      }

      public Class<T> func_216183_b() {
         return this.field_216185_b;
      }

      public abstract void serialize(JsonObject json, T entryIn, JsonSerializationContext context);

      public abstract T deserialize(JsonObject json, JsonDeserializationContext context, ILootCondition[] conditionsIn);
   }
}
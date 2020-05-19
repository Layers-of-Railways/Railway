package net.minecraft.world.storage.loot;

import com.google.common.collect.Lists;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.storage.loot.conditions.ILootCondition;
import net.minecraft.world.storage.loot.conditions.LootConditionManager;
import net.minecraft.world.storage.loot.functions.ILootFunction;
import net.minecraft.world.storage.loot.functions.LootFunctionManager;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.mutable.MutableInt;

public class LootPool {
   private final String name;
   private final List<LootEntry> lootEntries;
   private final List<ILootCondition> conditions;
   private final Predicate<LootContext> combinedConditions;
   private final ILootFunction[] functions;
   private final BiFunction<ItemStack, LootContext, ItemStack> combinedFunctions;
   private IRandomRange rolls;
   private RandomValueRange bonusRolls;

   private LootPool(LootEntry[] p_i51268_1_, ILootCondition[] p_i51268_2_, ILootFunction[] p_i51268_3_, IRandomRange p_i51268_4_, RandomValueRange p_i51268_5_, String name) {
      this.name = name;
      this.lootEntries = Lists.newArrayList(p_i51268_1_);
      this.conditions = Lists.newArrayList(p_i51268_2_);
      this.combinedConditions = LootConditionManager.and(p_i51268_2_);
      this.functions = p_i51268_3_;
      this.combinedFunctions = LootFunctionManager.combine(p_i51268_3_);
      this.rolls = p_i51268_4_;
      this.bonusRolls = p_i51268_5_;
   }

   private void generateRoll(Consumer<ItemStack> p_216095_1_, LootContext p_216095_2_) {
      Random random = p_216095_2_.getRandom();
      List<ILootGenerator> list = Lists.newArrayList();
      MutableInt mutableint = new MutableInt();

      for(LootEntry lootentry : this.lootEntries) {
         lootentry.expand(p_216095_2_, (p_216097_3_) -> {
            int k = p_216097_3_.getEffectiveWeight(p_216095_2_.getLuck());
            if (k > 0) {
               list.add(p_216097_3_);
               mutableint.add(k);
            }

         });
      }

      int i = list.size();
      if (mutableint.intValue() != 0 && i != 0) {
         if (i == 1) {
            list.get(0).func_216188_a(p_216095_1_, p_216095_2_);
         } else {
            int j = random.nextInt(mutableint.intValue());

            for(ILootGenerator ilootgenerator : list) {
               j -= ilootgenerator.getEffectiveWeight(p_216095_2_.getLuck());
               if (j < 0) {
                  ilootgenerator.func_216188_a(p_216095_1_, p_216095_2_);
                  return;
               }
            }

         }
      }
   }

   public void generate(Consumer<ItemStack> p_216091_1_, LootContext p_216091_2_) {
      if (this.combinedConditions.test(p_216091_2_)) {
         Consumer<ItemStack> consumer = ILootFunction.func_215858_a(this.combinedFunctions, p_216091_1_, p_216091_2_);
         Random random = p_216091_2_.getRandom();
         int i = this.rolls.generateInt(random) + MathHelper.floor(this.bonusRolls.generateFloat(random) * p_216091_2_.getLuck());

         for(int j = 0; j < i; ++j) {
            this.generateRoll(consumer, p_216091_2_);
         }

      }
   }

   public void func_227505_a_(ValidationTracker p_227505_1_) {
      for(int i = 0; i < this.conditions.size(); ++i) {
         this.conditions.get(i).func_225580_a_(p_227505_1_.func_227534_b_(".condition[" + i + "]"));
      }

      for(int j = 0; j < this.functions.length; ++j) {
         this.functions[j].func_225580_a_(p_227505_1_.func_227534_b_(".functions[" + j + "]"));
      }

      for(int k = 0; k < this.lootEntries.size(); ++k) {
         this.lootEntries.get(k).func_225579_a_(p_227505_1_.func_227534_b_(".entries[" + k + "]"));
      }

   }

   public static LootPool.Builder builder() {
      return new LootPool.Builder();
   }

   public static class Builder implements ILootFunctionConsumer<LootPool.Builder>, ILootConditionConsumer<LootPool.Builder> {
      private final List<LootEntry> entries = Lists.newArrayList();
      private final List<ILootCondition> conditions = Lists.newArrayList();
      private final List<ILootFunction> functions = Lists.newArrayList();
      private IRandomRange rolls = new RandomValueRange(1.0F);
      private RandomValueRange bonusRolls = new RandomValueRange(0.0F, 0.0F);
      private String name;

      public LootPool.Builder rolls(IRandomRange rollsIn) {
         this.rolls = rollsIn;
         return this;
      }

      public LootPool.Builder cast() {
         return this;
      }

      public LootPool.Builder addEntry(LootEntry.Builder<?> entriesBuilder) {
         this.entries.add(entriesBuilder.build());
         return this;
      }

      public LootPool.Builder acceptCondition(ILootCondition.IBuilder conditionBuilder) {
         this.conditions.add(conditionBuilder.build());
         return this;
      }

      public LootPool.Builder acceptFunction(ILootFunction.IBuilder functionBuilder) {
         this.functions.add(functionBuilder.build());
         return this;
      }

      public LootPool.Builder name(String name) {
         this.name = name;
         return this;
      }

      public LootPool.Builder bonusRolls(float min, float max) {
         this.bonusRolls = new RandomValueRange(min, max);
         return this;
      }

      public LootPool build() {
         if (this.rolls == null) {
            throw new IllegalArgumentException("Rolls not set");
         } else {
            return new LootPool(this.entries.toArray(new LootEntry[0]), this.conditions.toArray(new ILootCondition[0]), this.functions.toArray(new ILootFunction[0]), this.rolls, this.bonusRolls, name);
         }
      }
   }

   //======================== FORGE START =============================================
   private boolean isFrozen = false;
   public void freeze() { this.isFrozen = true; }
   public boolean isFrozen(){ return this.isFrozen; }
   private void checkFrozen() {
      if (this.isFrozen())
         throw new RuntimeException("Attempted to modify LootPool after being frozen!");
   }
   public String getName(){ return this.name; }
   public IRandomRange getRolls()      { return this.rolls; }
   public IRandomRange getBonusRolls() { return this.bonusRolls; }
   public void setRolls     (RandomValueRange v){ checkFrozen(); this.rolls = v; }
   public void setBonusRolls(RandomValueRange v){ checkFrozen(); this.bonusRolls = v; }
   /*
   public LootEntry getEntry(String name) {
      return lootEntries.stream().filter(e -> name.equals(e.getEntryName())).findFirst().orElse(null);
   }
   public LootEntry removeEntry(String name) {
      checkFrozen();
      for (LootEntry entry : this.lootEntries) {
         if (name.equals(entry.getEntryName())) {
            this.lootEntries.remove(entry);
            return entry;
         }
      }
      return null;
   }
   public void addEntry(LootEntry entry) {
      checkFrozen();
      if (lootEntries.stream().anyMatch(e -> e == entry || e.getEntryName().equals(entry.getEntryName())))
         throw new RuntimeException("Attempted to add a duplicate entry to pool: " + entry.getEntryName());
      this.lootEntries.add(entry);
   }
   */
   //======================== FORGE END ===============================================

   public static class Serializer implements JsonDeserializer<LootPool>, JsonSerializer<LootPool> {
      public LootPool deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         JsonObject jsonobject = JSONUtils.getJsonObject(p_deserialize_1_, "loot pool");
         LootEntry[] alootentry = JSONUtils.deserializeClass(jsonobject, "entries", p_deserialize_3_, LootEntry[].class);
         ILootCondition[] ailootcondition = JSONUtils.deserializeClass(jsonobject, "conditions", new ILootCondition[0], p_deserialize_3_, ILootCondition[].class);
         ILootFunction[] ailootfunction = JSONUtils.deserializeClass(jsonobject, "functions", new ILootFunction[0], p_deserialize_3_, ILootFunction[].class);
         IRandomRange irandomrange = RandomRanges.deserialize(jsonobject.get("rolls"), p_deserialize_3_);
         RandomValueRange randomvaluerange = JSONUtils.deserializeClass(jsonobject, "bonus_rolls", new RandomValueRange(0.0F, 0.0F), p_deserialize_3_, RandomValueRange.class);
         return new LootPool(alootentry, ailootcondition, ailootfunction, irandomrange, randomvaluerange, net.minecraftforge.common.ForgeHooks.readPoolName(jsonobject));
      }

      public JsonElement serialize(LootPool p_serialize_1_, Type p_serialize_2_, JsonSerializationContext p_serialize_3_) {
         JsonObject jsonobject = new JsonObject();
         if (p_serialize_1_.name != null && !p_serialize_1_.name.startsWith("custom#"))
            jsonobject.add("name", p_serialize_3_.serialize(p_serialize_1_.name));
         jsonobject.add("rolls", RandomRanges.serialize(p_serialize_1_.rolls, p_serialize_3_));
         jsonobject.add("entries", p_serialize_3_.serialize(p_serialize_1_.lootEntries));
         if (p_serialize_1_.bonusRolls.getMin() != 0.0F && p_serialize_1_.bonusRolls.getMax() != 0.0F) {
            jsonobject.add("bonus_rolls", p_serialize_3_.serialize(p_serialize_1_.bonusRolls));
         }

         if (!p_serialize_1_.conditions.isEmpty()) {
            jsonobject.add("conditions", p_serialize_3_.serialize(p_serialize_1_.conditions));
         }

         if (!ArrayUtils.isEmpty((Object[])p_serialize_1_.functions)) {
            jsonobject.add("functions", p_serialize_3_.serialize(p_serialize_1_.functions));
         }

         return jsonobject;
      }
   }
}
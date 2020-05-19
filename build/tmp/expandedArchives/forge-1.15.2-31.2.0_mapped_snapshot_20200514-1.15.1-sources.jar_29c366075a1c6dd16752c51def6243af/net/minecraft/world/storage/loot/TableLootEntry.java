package net.minecraft.world.storage.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.function.Consumer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.conditions.ILootCondition;
import net.minecraft.world.storage.loot.functions.ILootFunction;

public class TableLootEntry extends StandaloneLootEntry {
   private final ResourceLocation table;

   private TableLootEntry(ResourceLocation tableIn, int weightIn, int qualityIn, ILootCondition[] conditionsIn, ILootFunction[] functionsIn) {
      super(weightIn, qualityIn, conditionsIn, functionsIn);
      this.table = tableIn;
   }

   public void func_216154_a(Consumer<ItemStack> p_216154_1_, LootContext p_216154_2_) {
      LootTable loottable = p_216154_2_.getLootTable(this.table);
      loottable.recursiveGenerate(p_216154_2_, p_216154_1_);
   }

   public void func_225579_a_(ValidationTracker p_225579_1_) {
      if (p_225579_1_.func_227532_a_(this.table)) {
         p_225579_1_.addProblem("Table " + this.table + " is recursively called");
      } else {
         super.func_225579_a_(p_225579_1_);
         LootTable loottable = p_225579_1_.func_227539_c_(this.table);
         if (loottable == null) {
            p_225579_1_.addProblem("Unknown loot table called " + this.table);
         } else {
            loottable.func_227506_a_(p_225579_1_.func_227531_a_("->{" + this.table + "}", this.table));
         }

      }
   }

   public static StandaloneLootEntry.Builder<?> builder(ResourceLocation tableIn) {
      return builder((p_216173_1_, p_216173_2_, p_216173_3_, p_216173_4_) -> {
         return new TableLootEntry(tableIn, p_216173_1_, p_216173_2_, p_216173_3_, p_216173_4_);
      });
   }

   public static class Serializer extends StandaloneLootEntry.Serializer<TableLootEntry> {
      public Serializer() {
         super(new ResourceLocation("loot_table"), TableLootEntry.class);
      }

      public void serialize(JsonObject json, TableLootEntry entryIn, JsonSerializationContext context) {
         super.serialize(json, entryIn, context);
         json.addProperty("name", entryIn.table.toString());
      }

      protected TableLootEntry func_212829_b_(JsonObject p_212829_1_, JsonDeserializationContext p_212829_2_, int p_212829_3_, int p_212829_4_, ILootCondition[] p_212829_5_, ILootFunction[] p_212829_6_) {
         ResourceLocation resourcelocation = new ResourceLocation(JSONUtils.getString(p_212829_1_, "name"));
         return new TableLootEntry(resourcelocation, p_212829_3_, p_212829_4_, p_212829_5_, p_212829_6_);
      }
   }
}
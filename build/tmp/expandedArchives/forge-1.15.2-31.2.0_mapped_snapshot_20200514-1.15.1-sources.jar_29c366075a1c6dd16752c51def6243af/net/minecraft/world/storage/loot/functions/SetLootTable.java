package net.minecraft.world.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootFunction;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.ValidationTracker;
import net.minecraft.world.storage.loot.conditions.ILootCondition;

public class SetLootTable extends LootFunction {
   private final ResourceLocation field_215928_a;
   private final long field_215929_c;

   private SetLootTable(ILootCondition[] p_i51224_1_, ResourceLocation p_i51224_2_, long p_i51224_3_) {
      super(p_i51224_1_);
      this.field_215928_a = p_i51224_2_;
      this.field_215929_c = p_i51224_3_;
   }

   public ItemStack doApply(ItemStack stack, LootContext context) {
      if (stack.isEmpty()) {
         return stack;
      } else {
         CompoundNBT compoundnbt = new CompoundNBT();
         compoundnbt.putString("LootTable", this.field_215928_a.toString());
         if (this.field_215929_c != 0L) {
            compoundnbt.putLong("LootTableSeed", this.field_215929_c);
         }

         stack.getOrCreateTag().put("BlockEntityTag", compoundnbt);
         return stack;
      }
   }

   public void func_225580_a_(ValidationTracker p_225580_1_) {
      if (p_225580_1_.func_227532_a_(this.field_215928_a)) {
         p_225580_1_.addProblem("Table " + this.field_215928_a + " is recursively called");
      } else {
         super.func_225580_a_(p_225580_1_);
         LootTable loottable = p_225580_1_.func_227539_c_(this.field_215928_a);
         if (loottable == null) {
            p_225580_1_.addProblem("Unknown loot table called " + this.field_215928_a);
         } else {
            loottable.func_227506_a_(p_225580_1_.func_227531_a_("->{" + this.field_215928_a + "}", this.field_215928_a));
         }

      }
   }

   public static class Serializer extends LootFunction.Serializer<SetLootTable> {
      protected Serializer() {
         super(new ResourceLocation("set_loot_table"), SetLootTable.class);
      }

      public void serialize(JsonObject object, SetLootTable functionClazz, JsonSerializationContext serializationContext) {
         super.serialize(object, functionClazz, serializationContext);
         object.addProperty("name", functionClazz.field_215928_a.toString());
         if (functionClazz.field_215929_c != 0L) {
            object.addProperty("seed", functionClazz.field_215929_c);
         }

      }

      public SetLootTable deserialize(JsonObject object, JsonDeserializationContext deserializationContext, ILootCondition[] conditionsIn) {
         ResourceLocation resourcelocation = new ResourceLocation(JSONUtils.getString(object, "name"));
         long i = JSONUtils.func_219796_a(object, "seed", 0L);
         return new SetLootTable(conditionsIn, resourcelocation, i);
      }
   }
}
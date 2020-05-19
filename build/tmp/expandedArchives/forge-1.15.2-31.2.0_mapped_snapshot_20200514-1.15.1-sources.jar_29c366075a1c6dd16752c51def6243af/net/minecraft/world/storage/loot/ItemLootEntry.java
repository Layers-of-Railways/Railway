package net.minecraft.world.storage.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.function.Consumer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.storage.loot.conditions.ILootCondition;
import net.minecraft.world.storage.loot.functions.ILootFunction;

public class ItemLootEntry extends StandaloneLootEntry {
   private final Item item;

   private ItemLootEntry(Item itemIn, int weightIn, int qualityIn, ILootCondition[] conditionsIn, ILootFunction[] functionsIn) {
      super(weightIn, qualityIn, conditionsIn, functionsIn);
      this.item = itemIn;
   }

   public void func_216154_a(Consumer<ItemStack> p_216154_1_, LootContext p_216154_2_) {
      p_216154_1_.accept(new ItemStack(this.item));
   }

   public static StandaloneLootEntry.Builder<?> builder(IItemProvider itemIn) {
      return builder((p_216169_1_, p_216169_2_, p_216169_3_, p_216169_4_) -> {
         return new ItemLootEntry(itemIn.asItem(), p_216169_1_, p_216169_2_, p_216169_3_, p_216169_4_);
      });
   }

   public static class Serializer extends StandaloneLootEntry.Serializer<ItemLootEntry> {
      public Serializer() {
         super(new ResourceLocation("item"), ItemLootEntry.class);
      }

      public void serialize(JsonObject json, ItemLootEntry entryIn, JsonSerializationContext context) {
         super.serialize(json, entryIn, context);
         ResourceLocation resourcelocation = Registry.ITEM.getKey(entryIn.item);
         if (resourcelocation == null) {
            throw new IllegalArgumentException("Can't serialize unknown item " + entryIn.item);
         } else {
            json.addProperty("name", resourcelocation.toString());
         }
      }

      protected ItemLootEntry func_212829_b_(JsonObject p_212829_1_, JsonDeserializationContext p_212829_2_, int p_212829_3_, int p_212829_4_, ILootCondition[] p_212829_5_, ILootFunction[] p_212829_6_) {
         Item item = JSONUtils.getItem(p_212829_1_, "name");
         return new ItemLootEntry(item, p_212829_3_, p_212829_4_, p_212829_5_, p_212829_6_);
      }
   }
}
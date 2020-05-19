package net.minecraft.world.storage.loot.conditions;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameter;
import net.minecraft.world.storage.loot.LootParameters;

public class TableBonus implements ILootCondition {
   private final Enchantment enchantment;
   private final float[] chances;

   private TableBonus(Enchantment enchantment, float[] chances) {
      this.enchantment = enchantment;
      this.chances = chances;
   }

   public Set<LootParameter<?>> getRequiredParameters() {
      return ImmutableSet.of(LootParameters.TOOL);
   }

   public boolean test(LootContext p_test_1_) {
      ItemStack itemstack = p_test_1_.get(LootParameters.TOOL);
      int i = itemstack != null ? EnchantmentHelper.getEnchantmentLevel(this.enchantment, itemstack) : 0;
      float f = this.chances[Math.min(i, this.chances.length - 1)];
      return p_test_1_.getRandom().nextFloat() < f;
   }

   public static ILootCondition.IBuilder builder(Enchantment enchantmentIn, float... chancesIn) {
      return () -> {
         return new TableBonus(enchantmentIn, chancesIn);
      };
   }

   public static class Serializer extends ILootCondition.AbstractSerializer<TableBonus> {
      public Serializer() {
         super(new ResourceLocation("table_bonus"), TableBonus.class);
      }

      public void serialize(JsonObject json, TableBonus value, JsonSerializationContext context) {
         json.addProperty("enchantment", Registry.ENCHANTMENT.getKey(value.enchantment).toString());
         json.add("chances", context.serialize(value.chances));
      }

      public TableBonus deserialize(JsonObject json, JsonDeserializationContext context) {
         ResourceLocation resourcelocation = new ResourceLocation(JSONUtils.getString(json, "enchantment"));
         Enchantment enchantment = Registry.ENCHANTMENT.getValue(resourcelocation).orElseThrow(() -> {
            return new JsonParseException("Invalid enchantment id: " + resourcelocation);
         });
         float[] afloat = JSONUtils.deserializeClass(json, "chances", context, float[].class);
         return new TableBonus(enchantment, afloat);
      }
   }
}
package net.minecraft.world.storage.loot.conditions;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import java.util.Set;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameter;
import net.minecraft.world.storage.loot.LootParameters;

public class MatchTool implements ILootCondition {
   private final ItemPredicate predicate;

   public MatchTool(ItemPredicate predicate) {
      this.predicate = predicate;
   }

   public Set<LootParameter<?>> getRequiredParameters() {
      return ImmutableSet.of(LootParameters.TOOL);
   }

   public boolean test(LootContext p_test_1_) {
      ItemStack itemstack = p_test_1_.get(LootParameters.TOOL);
      return itemstack != null && this.predicate.test(itemstack);
   }

   public static ILootCondition.IBuilder builder(ItemPredicate.Builder p_216012_0_) {
      return () -> {
         return new MatchTool(p_216012_0_.build());
      };
   }

   public static class Serializer extends ILootCondition.AbstractSerializer<MatchTool> {
      protected Serializer() {
         super(new ResourceLocation("match_tool"), MatchTool.class);
      }

      public void serialize(JsonObject json, MatchTool value, JsonSerializationContext context) {
         json.add("predicate", value.predicate.serialize());
      }

      public MatchTool deserialize(JsonObject json, JsonDeserializationContext context) {
         ItemPredicate itempredicate = ItemPredicate.deserialize(json.get("predicate"));
         return new MatchTool(itempredicate);
      }
   }
}
package net.minecraft.advancements.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class ItemDurabilityTrigger extends AbstractCriterionTrigger<ItemDurabilityTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("item_durability_changed");

   public ResourceLocation getId() {
      return ID;
   }

   /**
    * Deserialize a ICriterionInstance of this trigger from the data in the JSON.
    */
   public ItemDurabilityTrigger.Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
      ItemPredicate itempredicate = ItemPredicate.deserialize(json.get("item"));
      MinMaxBounds.IntBound minmaxbounds$intbound = MinMaxBounds.IntBound.fromJson(json.get("durability"));
      MinMaxBounds.IntBound minmaxbounds$intbound1 = MinMaxBounds.IntBound.fromJson(json.get("delta"));
      return new ItemDurabilityTrigger.Instance(itempredicate, minmaxbounds$intbound, minmaxbounds$intbound1);
   }

   public void trigger(ServerPlayerEntity player, ItemStack itemIn, int newDurability) {
      this.func_227070_a_(player.getAdvancements(), (p_226653_2_) -> {
         return p_226653_2_.test(itemIn, newDurability);
      });
   }

   public static class Instance extends CriterionInstance {
      private final ItemPredicate item;
      private final MinMaxBounds.IntBound durability;
      private final MinMaxBounds.IntBound delta;

      public Instance(ItemPredicate p_i49703_1_, MinMaxBounds.IntBound p_i49703_2_, MinMaxBounds.IntBound p_i49703_3_) {
         super(ItemDurabilityTrigger.ID);
         this.item = p_i49703_1_;
         this.durability = p_i49703_2_;
         this.delta = p_i49703_3_;
      }

      public static ItemDurabilityTrigger.Instance forItemDamage(ItemPredicate p_211182_0_, MinMaxBounds.IntBound p_211182_1_) {
         return new ItemDurabilityTrigger.Instance(p_211182_0_, p_211182_1_, MinMaxBounds.IntBound.UNBOUNDED);
      }

      public boolean test(ItemStack item, int p_193197_2_) {
         if (!this.item.test(item)) {
            return false;
         } else if (!this.durability.test(item.getMaxDamage() - p_193197_2_)) {
            return false;
         } else {
            return this.delta.test(item.getDamage() - p_193197_2_);
         }
      }

      public JsonElement serialize() {
         JsonObject jsonobject = new JsonObject();
         jsonobject.add("item", this.item.serialize());
         jsonobject.add("durability", this.durability.serialize());
         jsonobject.add("delta", this.delta.serialize());
         return jsonobject;
      }
   }
}
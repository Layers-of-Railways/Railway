package net.minecraft.advancements.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.entity.merchant.villager.AbstractVillagerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class VillagerTradeTrigger extends AbstractCriterionTrigger<VillagerTradeTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("villager_trade");

   public ResourceLocation getId() {
      return ID;
   }

   /**
    * Deserialize a ICriterionInstance of this trigger from the data in the JSON.
    */
   public VillagerTradeTrigger.Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
      EntityPredicate entitypredicate = EntityPredicate.deserialize(json.get("villager"));
      ItemPredicate itempredicate = ItemPredicate.deserialize(json.get("item"));
      return new VillagerTradeTrigger.Instance(entitypredicate, itempredicate);
   }

   public void func_215114_a(ServerPlayerEntity p_215114_1_, AbstractVillagerEntity p_215114_2_, ItemStack p_215114_3_) {
      this.func_227070_a_(p_215114_1_.getAdvancements(), (p_227267_3_) -> {
         return p_227267_3_.func_215125_a(p_215114_1_, p_215114_2_, p_215114_3_);
      });
   }

   public static class Instance extends CriterionInstance {
      private final EntityPredicate villager;
      private final ItemPredicate item;

      public Instance(EntityPredicate villager, ItemPredicate item) {
         super(VillagerTradeTrigger.ID);
         this.villager = villager;
         this.item = item;
      }

      public static VillagerTradeTrigger.Instance any() {
         return new VillagerTradeTrigger.Instance(EntityPredicate.ANY, ItemPredicate.ANY);
      }

      public boolean func_215125_a(ServerPlayerEntity p_215125_1_, AbstractVillagerEntity p_215125_2_, ItemStack p_215125_3_) {
         if (!this.villager.test(p_215125_1_, p_215125_2_)) {
            return false;
         } else {
            return this.item.test(p_215125_3_);
         }
      }

      public JsonElement serialize() {
         JsonObject jsonobject = new JsonObject();
         jsonobject.add("item", this.item.serialize());
         jsonobject.add("villager", this.villager.serialize());
         return jsonobject;
      }
   }
}
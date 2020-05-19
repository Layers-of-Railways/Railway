package net.minecraft.advancements.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class FilledBucketTrigger extends AbstractCriterionTrigger<FilledBucketTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("filled_bucket");

   public ResourceLocation getId() {
      return ID;
   }

   /**
    * Deserialize a ICriterionInstance of this trigger from the data in the JSON.
    */
   public FilledBucketTrigger.Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
      ItemPredicate itempredicate = ItemPredicate.deserialize(json.get("item"));
      return new FilledBucketTrigger.Instance(itempredicate);
   }

   public void trigger(ServerPlayerEntity p_204817_1_, ItemStack p_204817_2_) {
      this.func_227070_a_(p_204817_1_.getAdvancements(), (p_226627_1_) -> {
         return p_226627_1_.test(p_204817_2_);
      });
   }

   public static class Instance extends CriterionInstance {
      private final ItemPredicate item;

      public Instance(ItemPredicate item) {
         super(FilledBucketTrigger.ID);
         this.item = item;
      }

      public static FilledBucketTrigger.Instance forItem(ItemPredicate p_204827_0_) {
         return new FilledBucketTrigger.Instance(p_204827_0_);
      }

      public boolean test(ItemStack p_204826_1_) {
         return this.item.test(p_204826_1_);
      }

      public JsonElement serialize() {
         JsonObject jsonobject = new JsonObject();
         jsonobject.add("item", this.item.serialize());
         return jsonobject;
      }
   }
}
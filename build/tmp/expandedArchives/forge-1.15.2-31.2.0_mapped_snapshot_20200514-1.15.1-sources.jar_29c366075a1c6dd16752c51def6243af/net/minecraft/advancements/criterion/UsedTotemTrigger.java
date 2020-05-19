package net.minecraft.advancements.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;

public class UsedTotemTrigger extends AbstractCriterionTrigger<UsedTotemTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("used_totem");

   public ResourceLocation getId() {
      return ID;
   }

   /**
    * Deserialize a ICriterionInstance of this trigger from the data in the JSON.
    */
   public UsedTotemTrigger.Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
      ItemPredicate itempredicate = ItemPredicate.deserialize(json.get("item"));
      return new UsedTotemTrigger.Instance(itempredicate);
   }

   public void trigger(ServerPlayerEntity player, ItemStack item) {
      this.func_227070_a_(player.getAdvancements(), (p_227409_1_) -> {
         return p_227409_1_.test(item);
      });
   }

   public static class Instance extends CriterionInstance {
      private final ItemPredicate item;

      public Instance(ItemPredicate item) {
         super(UsedTotemTrigger.ID);
         this.item = item;
      }

      public static UsedTotemTrigger.Instance usedTotem(IItemProvider p_203941_0_) {
         return new UsedTotemTrigger.Instance(ItemPredicate.Builder.create().item(p_203941_0_).build());
      }

      public boolean test(ItemStack item) {
         return this.item.test(item);
      }

      public JsonElement serialize() {
         JsonObject jsonobject = new JsonObject();
         jsonobject.add("item", this.item.serialize());
         return jsonobject;
      }
   }
}
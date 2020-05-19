package net.minecraft.advancements.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.tags.Tag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;

public class ConsumeItemTrigger extends AbstractCriterionTrigger<ConsumeItemTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("consume_item");

   public ResourceLocation getId() {
      return ID;
   }

   /**
    * Deserialize a ICriterionInstance of this trigger from the data in the JSON.
    */
   public ConsumeItemTrigger.Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
      return new ConsumeItemTrigger.Instance(ItemPredicate.deserialize(json.get("item")));
   }

   public void trigger(ServerPlayerEntity player, ItemStack item) {
      this.func_227070_a_(player.getAdvancements(), (p_226325_1_) -> {
         return p_226325_1_.test(item);
      });
   }

   public static class Instance extends CriterionInstance {
      private final ItemPredicate item;

      public Instance(ItemPredicate item) {
         super(ConsumeItemTrigger.ID);
         this.item = item;
      }

      public static ConsumeItemTrigger.Instance any() {
         return new ConsumeItemTrigger.Instance(ItemPredicate.ANY);
      }

      public static ConsumeItemTrigger.Instance forItem(IItemProvider p_203913_0_) {
         return new ConsumeItemTrigger.Instance(new ItemPredicate((Tag<Item>)null, p_203913_0_.asItem(), MinMaxBounds.IntBound.UNBOUNDED, MinMaxBounds.IntBound.UNBOUNDED, EnchantmentPredicate.field_226534_b_, EnchantmentPredicate.field_226534_b_, (Potion)null, NBTPredicate.ANY));
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
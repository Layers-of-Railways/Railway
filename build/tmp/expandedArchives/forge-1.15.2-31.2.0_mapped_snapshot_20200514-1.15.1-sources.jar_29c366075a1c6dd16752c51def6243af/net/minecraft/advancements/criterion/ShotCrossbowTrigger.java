package net.minecraft.advancements.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;

public class ShotCrossbowTrigger extends AbstractCriterionTrigger<ShotCrossbowTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("shot_crossbow");

   public ResourceLocation getId() {
      return ID;
   }

   /**
    * Deserialize a ICriterionInstance of this trigger from the data in the JSON.
    */
   public ShotCrossbowTrigger.Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
      ItemPredicate itempredicate = ItemPredicate.deserialize(json.get("item"));
      return new ShotCrossbowTrigger.Instance(itempredicate);
   }

   public void func_215111_a(ServerPlayerEntity shooter, ItemStack stack) {
      this.func_227070_a_(shooter.getAdvancements(), (p_227037_1_) -> {
         return p_227037_1_.func_215121_a(stack);
      });
   }

   public static class Instance extends CriterionInstance {
      private final ItemPredicate itemPredicate;

      public Instance(ItemPredicate itemPredicateIn) {
         super(ShotCrossbowTrigger.ID);
         this.itemPredicate = itemPredicateIn;
      }

      public static ShotCrossbowTrigger.Instance create(IItemProvider itemProvider) {
         return new ShotCrossbowTrigger.Instance(ItemPredicate.Builder.create().item(itemProvider).build());
      }

      public boolean func_215121_a(ItemStack p_215121_1_) {
         return this.itemPredicate.test(p_215121_1_);
      }

      public JsonElement serialize() {
         JsonObject jsonobject = new JsonObject();
         jsonobject.add("item", this.itemPredicate.serialize());
         return jsonobject;
      }
   }
}
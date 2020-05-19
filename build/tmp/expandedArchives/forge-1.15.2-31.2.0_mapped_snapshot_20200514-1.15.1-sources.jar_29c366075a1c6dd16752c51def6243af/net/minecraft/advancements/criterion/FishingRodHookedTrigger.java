package net.minecraft.advancements.criterion;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Collection;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.entity.projectile.FishingBobberEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class FishingRodHookedTrigger extends AbstractCriterionTrigger<FishingRodHookedTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("fishing_rod_hooked");

   public ResourceLocation getId() {
      return ID;
   }

   /**
    * Deserialize a ICriterionInstance of this trigger from the data in the JSON.
    */
   public FishingRodHookedTrigger.Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
      ItemPredicate itempredicate = ItemPredicate.deserialize(json.get("rod"));
      EntityPredicate entitypredicate = EntityPredicate.deserialize(json.get("entity"));
      ItemPredicate itempredicate1 = ItemPredicate.deserialize(json.get("item"));
      return new FishingRodHookedTrigger.Instance(itempredicate, entitypredicate, itempredicate1);
   }

   public void trigger(ServerPlayerEntity p_204820_1_, ItemStack p_204820_2_, FishingBobberEntity p_204820_3_, Collection<ItemStack> p_204820_4_) {
      this.func_227070_a_(p_204820_1_.getAdvancements(), (p_226628_4_) -> {
         return p_226628_4_.test(p_204820_1_, p_204820_2_, p_204820_3_, p_204820_4_);
      });
   }

   public static class Instance extends CriterionInstance {
      private final ItemPredicate rod;
      private final EntityPredicate entity;
      private final ItemPredicate item;

      public Instance(ItemPredicate rod, EntityPredicate entity, ItemPredicate item) {
         super(FishingRodHookedTrigger.ID);
         this.rod = rod;
         this.entity = entity;
         this.item = item;
      }

      public static FishingRodHookedTrigger.Instance create(ItemPredicate p_204829_0_, EntityPredicate p_204829_1_, ItemPredicate p_204829_2_) {
         return new FishingRodHookedTrigger.Instance(p_204829_0_, p_204829_1_, p_204829_2_);
      }

      public boolean test(ServerPlayerEntity p_204830_1_, ItemStack p_204830_2_, FishingBobberEntity p_204830_3_, Collection<ItemStack> p_204830_4_) {
         if (!this.rod.test(p_204830_2_)) {
            return false;
         } else if (!this.entity.test(p_204830_1_, p_204830_3_.caughtEntity)) {
            return false;
         } else {
            if (this.item != ItemPredicate.ANY) {
               boolean flag = false;
               if (p_204830_3_.caughtEntity instanceof ItemEntity) {
                  ItemEntity itementity = (ItemEntity)p_204830_3_.caughtEntity;
                  if (this.item.test(itementity.getItem())) {
                     flag = true;
                  }
               }

               for(ItemStack itemstack : p_204830_4_) {
                  if (this.item.test(itemstack)) {
                     flag = true;
                     break;
                  }
               }

               if (!flag) {
                  return false;
               }
            }

            return true;
         }
      }

      public JsonElement serialize() {
         JsonObject jsonobject = new JsonObject();
         jsonobject.add("rod", this.rod.serialize());
         jsonobject.add("entity", this.entity.serialize());
         jsonobject.add("item", this.item.serialize());
         return jsonobject;
      }
   }
}
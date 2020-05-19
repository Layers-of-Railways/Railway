package net.minecraft.advancements.criterion;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.tags.Tag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

public class InventoryChangeTrigger extends AbstractCriterionTrigger<InventoryChangeTrigger.Instance> {
   private static final ResourceLocation ID = new ResourceLocation("inventory_changed");

   public ResourceLocation getId() {
      return ID;
   }

   /**
    * Deserialize a ICriterionInstance of this trigger from the data in the JSON.
    */
   public InventoryChangeTrigger.Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
      JsonObject jsonobject = JSONUtils.getJsonObject(json, "slots", new JsonObject());
      MinMaxBounds.IntBound minmaxbounds$intbound = MinMaxBounds.IntBound.fromJson(jsonobject.get("occupied"));
      MinMaxBounds.IntBound minmaxbounds$intbound1 = MinMaxBounds.IntBound.fromJson(jsonobject.get("full"));
      MinMaxBounds.IntBound minmaxbounds$intbound2 = MinMaxBounds.IntBound.fromJson(jsonobject.get("empty"));
      ItemPredicate[] aitempredicate = ItemPredicate.deserializeArray(json.get("items"));
      return new InventoryChangeTrigger.Instance(minmaxbounds$intbound, minmaxbounds$intbound1, minmaxbounds$intbound2, aitempredicate);
   }

   public void trigger(ServerPlayerEntity player, PlayerInventory inventory) {
      this.func_227070_a_(player.getAdvancements(), (p_226650_1_) -> {
         return p_226650_1_.test(inventory);
      });
   }

   public static class Instance extends CriterionInstance {
      private final MinMaxBounds.IntBound occupied;
      private final MinMaxBounds.IntBound full;
      private final MinMaxBounds.IntBound empty;
      private final ItemPredicate[] items;

      public Instance(MinMaxBounds.IntBound p_i49710_1_, MinMaxBounds.IntBound p_i49710_2_, MinMaxBounds.IntBound p_i49710_3_, ItemPredicate[] p_i49710_4_) {
         super(InventoryChangeTrigger.ID);
         this.occupied = p_i49710_1_;
         this.full = p_i49710_2_;
         this.empty = p_i49710_3_;
         this.items = p_i49710_4_;
      }

      public static InventoryChangeTrigger.Instance forItems(ItemPredicate... p_203923_0_) {
         return new InventoryChangeTrigger.Instance(MinMaxBounds.IntBound.UNBOUNDED, MinMaxBounds.IntBound.UNBOUNDED, MinMaxBounds.IntBound.UNBOUNDED, p_203923_0_);
      }

      public static InventoryChangeTrigger.Instance forItems(IItemProvider... p_203922_0_) {
         ItemPredicate[] aitempredicate = new ItemPredicate[p_203922_0_.length];

         for(int i = 0; i < p_203922_0_.length; ++i) {
            aitempredicate[i] = new ItemPredicate((Tag<Item>)null, p_203922_0_[i].asItem(), MinMaxBounds.IntBound.UNBOUNDED, MinMaxBounds.IntBound.UNBOUNDED, EnchantmentPredicate.field_226534_b_, EnchantmentPredicate.field_226534_b_, (Potion)null, NBTPredicate.ANY);
         }

         return forItems(aitempredicate);
      }

      public JsonElement serialize() {
         JsonObject jsonobject = new JsonObject();
         if (!this.occupied.isUnbounded() || !this.full.isUnbounded() || !this.empty.isUnbounded()) {
            JsonObject jsonobject1 = new JsonObject();
            jsonobject1.add("occupied", this.occupied.serialize());
            jsonobject1.add("full", this.full.serialize());
            jsonobject1.add("empty", this.empty.serialize());
            jsonobject.add("slots", jsonobject1);
         }

         if (this.items.length > 0) {
            JsonArray jsonarray = new JsonArray();

            for(ItemPredicate itempredicate : this.items) {
               jsonarray.add(itempredicate.serialize());
            }

            jsonobject.add("items", jsonarray);
         }

         return jsonobject;
      }

      public boolean test(PlayerInventory inventory) {
         int i = 0;
         int j = 0;
         int k = 0;
         List<ItemPredicate> list = Lists.newArrayList(this.items);

         for(int l = 0; l < inventory.getSizeInventory(); ++l) {
            ItemStack itemstack = inventory.getStackInSlot(l);
            if (itemstack.isEmpty()) {
               ++j;
            } else {
               ++k;
               if (itemstack.getCount() >= itemstack.getMaxStackSize()) {
                  ++i;
               }

               Iterator<ItemPredicate> iterator = list.iterator();

               while(iterator.hasNext()) {
                  ItemPredicate itempredicate = iterator.next();
                  if (itempredicate.test(itemstack)) {
                     iterator.remove();
                  }
               }
            }
         }

         if (!this.full.test(i)) {
            return false;
         } else if (!this.empty.test(j)) {
            return false;
         } else if (!this.occupied.test(k)) {
            return false;
         } else {
            return list.isEmpty();
         }
      }
   }
}
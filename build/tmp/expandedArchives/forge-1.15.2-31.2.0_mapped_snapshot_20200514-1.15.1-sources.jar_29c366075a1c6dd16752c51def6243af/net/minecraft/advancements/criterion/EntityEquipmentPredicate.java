package net.minecraft.advancements.criterion;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Items;
import net.minecraft.util.JSONUtils;
import net.minecraft.world.raid.Raid;

public class EntityEquipmentPredicate {
   public static final EntityEquipmentPredicate ANY = new EntityEquipmentPredicate(ItemPredicate.ANY, ItemPredicate.ANY, ItemPredicate.ANY, ItemPredicate.ANY, ItemPredicate.ANY, ItemPredicate.ANY);
   public static final EntityEquipmentPredicate WEARING_ILLAGER_BANNER = new EntityEquipmentPredicate(ItemPredicate.Builder.create().item(Items.WHITE_BANNER).nbt(Raid.createIllagerBanner().getTag()).build(), ItemPredicate.ANY, ItemPredicate.ANY, ItemPredicate.ANY, ItemPredicate.ANY, ItemPredicate.ANY);
   private final ItemPredicate head;
   private final ItemPredicate chest;
   private final ItemPredicate legs;
   private final ItemPredicate feet;
   private final ItemPredicate mainHand;
   private final ItemPredicate offHand;

   public EntityEquipmentPredicate(ItemPredicate p_i50809_1_, ItemPredicate p_i50809_2_, ItemPredicate p_i50809_3_, ItemPredicate p_i50809_4_, ItemPredicate p_i50809_5_, ItemPredicate p_i50809_6_) {
      this.head = p_i50809_1_;
      this.chest = p_i50809_2_;
      this.legs = p_i50809_3_;
      this.feet = p_i50809_4_;
      this.mainHand = p_i50809_5_;
      this.offHand = p_i50809_6_;
   }

   public boolean test(@Nullable Entity p_217955_1_) {
      if (this == ANY) {
         return true;
      } else if (!(p_217955_1_ instanceof LivingEntity)) {
         return false;
      } else {
         LivingEntity livingentity = (LivingEntity)p_217955_1_;
         if (!this.head.test(livingentity.getItemStackFromSlot(EquipmentSlotType.HEAD))) {
            return false;
         } else if (!this.chest.test(livingentity.getItemStackFromSlot(EquipmentSlotType.CHEST))) {
            return false;
         } else if (!this.legs.test(livingentity.getItemStackFromSlot(EquipmentSlotType.LEGS))) {
            return false;
         } else if (!this.feet.test(livingentity.getItemStackFromSlot(EquipmentSlotType.FEET))) {
            return false;
         } else if (!this.mainHand.test(livingentity.getItemStackFromSlot(EquipmentSlotType.MAINHAND))) {
            return false;
         } else {
            return this.offHand.test(livingentity.getItemStackFromSlot(EquipmentSlotType.OFFHAND));
         }
      }
   }

   public static EntityEquipmentPredicate deserialize(@Nullable JsonElement p_217956_0_) {
      if (p_217956_0_ != null && !p_217956_0_.isJsonNull()) {
         JsonObject jsonobject = JSONUtils.getJsonObject(p_217956_0_, "equipment");
         ItemPredicate itempredicate = ItemPredicate.deserialize(jsonobject.get("head"));
         ItemPredicate itempredicate1 = ItemPredicate.deserialize(jsonobject.get("chest"));
         ItemPredicate itempredicate2 = ItemPredicate.deserialize(jsonobject.get("legs"));
         ItemPredicate itempredicate3 = ItemPredicate.deserialize(jsonobject.get("feet"));
         ItemPredicate itempredicate4 = ItemPredicate.deserialize(jsonobject.get("mainhand"));
         ItemPredicate itempredicate5 = ItemPredicate.deserialize(jsonobject.get("offhand"));
         return new EntityEquipmentPredicate(itempredicate, itempredicate1, itempredicate2, itempredicate3, itempredicate4, itempredicate5);
      } else {
         return ANY;
      }
   }

   public JsonElement serialize() {
      if (this == ANY) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject jsonobject = new JsonObject();
         jsonobject.add("head", this.head.serialize());
         jsonobject.add("chest", this.chest.serialize());
         jsonobject.add("legs", this.legs.serialize());
         jsonobject.add("feet", this.feet.serialize());
         jsonobject.add("mainhand", this.mainHand.serialize());
         jsonobject.add("offhand", this.offHand.serialize());
         return jsonobject;
      }
   }
}
package net.minecraft.advancements.criterion;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class EnchantmentPredicate {
   public static final EnchantmentPredicate ANY = new EnchantmentPredicate();
   public static final EnchantmentPredicate[] field_226534_b_ = new EnchantmentPredicate[0];
   private final Enchantment enchantment;
   private final MinMaxBounds.IntBound levels;

   public EnchantmentPredicate() {
      this.enchantment = null;
      this.levels = MinMaxBounds.IntBound.UNBOUNDED;
   }

   public EnchantmentPredicate(@Nullable Enchantment p_i49723_1_, MinMaxBounds.IntBound p_i49723_2_) {
      this.enchantment = p_i49723_1_;
      this.levels = p_i49723_2_;
   }

   public boolean test(Map<Enchantment, Integer> enchantmentsIn) {
      if (this.enchantment != null) {
         if (!enchantmentsIn.containsKey(this.enchantment)) {
            return false;
         }

         int i = enchantmentsIn.get(this.enchantment);
         if (this.levels != null && !this.levels.test(i)) {
            return false;
         }
      } else if (this.levels != null) {
         for(Integer integer : enchantmentsIn.values()) {
            if (this.levels.test(integer)) {
               return true;
            }
         }

         return false;
      }

      return true;
   }

   public JsonElement serialize() {
      if (this == ANY) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject jsonobject = new JsonObject();
         if (this.enchantment != null) {
            jsonobject.addProperty("enchantment", Registry.ENCHANTMENT.getKey(this.enchantment).toString());
         }

         jsonobject.add("levels", this.levels.serialize());
         return jsonobject;
      }
   }

   public static EnchantmentPredicate deserialize(@Nullable JsonElement element) {
      if (element != null && !element.isJsonNull()) {
         JsonObject jsonobject = JSONUtils.getJsonObject(element, "enchantment");
         Enchantment enchantment = null;
         if (jsonobject.has("enchantment")) {
            ResourceLocation resourcelocation = new ResourceLocation(JSONUtils.getString(jsonobject, "enchantment"));
            enchantment = Registry.ENCHANTMENT.getValue(resourcelocation).orElseThrow(() -> {
               return new JsonSyntaxException("Unknown enchantment '" + resourcelocation + "'");
            });
         }

         MinMaxBounds.IntBound minmaxbounds$intbound = MinMaxBounds.IntBound.fromJson(jsonobject.get("levels"));
         return new EnchantmentPredicate(enchantment, minmaxbounds$intbound);
      } else {
         return ANY;
      }
   }

   public static EnchantmentPredicate[] deserializeArray(@Nullable JsonElement element) {
      if (element != null && !element.isJsonNull()) {
         JsonArray jsonarray = JSONUtils.getJsonArray(element, "enchantments");
         EnchantmentPredicate[] aenchantmentpredicate = new EnchantmentPredicate[jsonarray.size()];

         for(int i = 0; i < aenchantmentpredicate.length; ++i) {
            aenchantmentpredicate[i] = deserialize(jsonarray.get(i));
         }

         return aenchantmentpredicate;
      } else {
         return field_226534_b_;
      }
   }
}
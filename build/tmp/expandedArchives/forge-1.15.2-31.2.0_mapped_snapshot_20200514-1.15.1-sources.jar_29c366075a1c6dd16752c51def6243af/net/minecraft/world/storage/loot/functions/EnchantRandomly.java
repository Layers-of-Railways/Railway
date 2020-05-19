package net.minecraft.world.storage.loot.functions;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootFunction;
import net.minecraft.world.storage.loot.conditions.ILootCondition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EnchantRandomly extends LootFunction {
   private static final Logger LOGGER = LogManager.getLogger();
   private final List<Enchantment> enchantments;

   private EnchantRandomly(ILootCondition[] p_i51238_1_, Collection<Enchantment> p_i51238_2_) {
      super(p_i51238_1_);
      this.enchantments = ImmutableList.copyOf(p_i51238_2_);
   }

   public ItemStack doApply(ItemStack stack, LootContext context) {
      Random random = context.getRandom();
      Enchantment enchantment;
      if (this.enchantments.isEmpty()) {
         List<Enchantment> list = Lists.newArrayList();

         for(Enchantment enchantment1 : Registry.ENCHANTMENT) {
            if (stack.getItem() == Items.BOOK || enchantment1.canApply(stack)) {
               list.add(enchantment1);
            }
         }

         if (list.isEmpty()) {
            LOGGER.warn("Couldn't find a compatible enchantment for {}", (Object)stack);
            return stack;
         }

         enchantment = list.get(random.nextInt(list.size()));
      } else {
         enchantment = this.enchantments.get(random.nextInt(this.enchantments.size()));
      }

      int i = MathHelper.nextInt(random, enchantment.getMinLevel(), enchantment.getMaxLevel());
      if (stack.getItem() == Items.BOOK) {
         stack = new ItemStack(Items.ENCHANTED_BOOK);
         EnchantedBookItem.addEnchantment(stack, new EnchantmentData(enchantment, i));
      } else {
         stack.addEnchantment(enchantment, i);
      }

      return stack;
   }

   public static LootFunction.Builder<?> func_215900_c() {
      return builder((p_215899_0_) -> {
         return new EnchantRandomly(p_215899_0_, ImmutableList.of());
      });
   }

   public static class Serializer extends LootFunction.Serializer<EnchantRandomly> {
      public Serializer() {
         super(new ResourceLocation("enchant_randomly"), EnchantRandomly.class);
      }

      public void serialize(JsonObject object, EnchantRandomly functionClazz, JsonSerializationContext serializationContext) {
         super.serialize(object, functionClazz, serializationContext);
         if (!functionClazz.enchantments.isEmpty()) {
            JsonArray jsonarray = new JsonArray();

            for(Enchantment enchantment : functionClazz.enchantments) {
               ResourceLocation resourcelocation = Registry.ENCHANTMENT.getKey(enchantment);
               if (resourcelocation == null) {
                  throw new IllegalArgumentException("Don't know how to serialize enchantment " + enchantment);
               }

               jsonarray.add(new JsonPrimitive(resourcelocation.toString()));
            }

            object.add("enchantments", jsonarray);
         }

      }

      public EnchantRandomly deserialize(JsonObject object, JsonDeserializationContext deserializationContext, ILootCondition[] conditionsIn) {
         List<Enchantment> list = Lists.newArrayList();
         if (object.has("enchantments")) {
            for(JsonElement jsonelement : JSONUtils.getJsonArray(object, "enchantments")) {
               String s = JSONUtils.getString(jsonelement, "enchantment");
               Enchantment enchantment = Registry.ENCHANTMENT.getValue(new ResourceLocation(s)).orElseThrow(() -> {
                  return new JsonSyntaxException("Unknown enchantment '" + s + "'");
               });
               list.add(enchantment);
            }
         }

         return new EnchantRandomly(conditionsIn, list);
      }
   }
}
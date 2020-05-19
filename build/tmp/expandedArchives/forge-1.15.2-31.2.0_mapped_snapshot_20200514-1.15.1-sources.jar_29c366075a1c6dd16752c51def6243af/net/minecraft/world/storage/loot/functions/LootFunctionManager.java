package net.minecraft.world.storage.loot.functions;

import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.function.BiFunction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;

public class LootFunctionManager {
   private static final Map<ResourceLocation, ILootFunction.Serializer<?>> NAME_TO_SERIALIZER_MAP = Maps.newHashMap();
   private static final Map<Class<? extends ILootFunction>, ILootFunction.Serializer<?>> CLASS_TO_SERIALIZER_MAP = Maps.newHashMap();
   public static final BiFunction<ItemStack, LootContext, ItemStack> IDENTITY = (p_216240_0_, p_216240_1_) -> {
      return p_216240_0_;
   };

   public static <T extends ILootFunction> void registerFunction(ILootFunction.Serializer<? extends T> serializer) {
      ResourceLocation resourcelocation = serializer.getFunctionName();
      Class<T> oclass = (Class<T>)serializer.getFunctionClass();
      if (NAME_TO_SERIALIZER_MAP.containsKey(resourcelocation)) {
         throw new IllegalArgumentException("Can't re-register item function name " + resourcelocation);
      } else if (CLASS_TO_SERIALIZER_MAP.containsKey(oclass)) {
         throw new IllegalArgumentException("Can't re-register item function class " + oclass.getName());
      } else {
         NAME_TO_SERIALIZER_MAP.put(resourcelocation, serializer);
         CLASS_TO_SERIALIZER_MAP.put(oclass, serializer);
      }
   }

   public static ILootFunction.Serializer<?> getSerializerForName(ResourceLocation location) {
      ILootFunction.Serializer<?> serializer = NAME_TO_SERIALIZER_MAP.get(location);
      if (serializer == null) {
         throw new IllegalArgumentException("Unknown loot item function '" + location + "'");
      } else {
         return serializer;
      }
   }

   public static <T extends ILootFunction> ILootFunction.Serializer<T> getSerializerFor(T functionClass) {
      ILootFunction.Serializer<T> serializer = (ILootFunction.Serializer<T>)CLASS_TO_SERIALIZER_MAP.get(functionClass.getClass());
      if (serializer == null) {
         throw new IllegalArgumentException("Unknown loot item function " + functionClass);
      } else {
         return serializer;
      }
   }

   public static BiFunction<ItemStack, LootContext, ItemStack> combine(BiFunction<ItemStack, LootContext, ItemStack>[] p_216241_0_) {
      switch(p_216241_0_.length) {
      case 0:
         return IDENTITY;
      case 1:
         return p_216241_0_[0];
      case 2:
         BiFunction<ItemStack, LootContext, ItemStack> bifunction = p_216241_0_[0];
         BiFunction<ItemStack, LootContext, ItemStack> bifunction1 = p_216241_0_[1];
         return (p_216239_2_, p_216239_3_) -> {
            return bifunction1.apply(bifunction.apply(p_216239_2_, p_216239_3_), p_216239_3_);
         };
      default:
         return (p_216238_1_, p_216238_2_) -> {
            for(BiFunction<ItemStack, LootContext, ItemStack> bifunction2 : p_216241_0_) {
               p_216238_1_ = bifunction2.apply(p_216238_1_, p_216238_2_);
            }

            return p_216238_1_;
         };
      }
   }

   static {
      registerFunction(new SetCount.Serializer());
      registerFunction(new EnchantWithLevels.Serializer());
      registerFunction(new EnchantRandomly.Serializer());
      registerFunction(new SetNBT.Serializer());
      registerFunction(new Smelt.Serializer());
      registerFunction(new LootingEnchantBonus.Serializer());
      registerFunction(new SetDamage.Serializer());
      registerFunction(new SetAttributes.Serializer());
      registerFunction(new SetName.Serializer());
      registerFunction(new ExplorationMap.Serializer());
      registerFunction(new SetStewEffect.Serializer());
      registerFunction(new CopyName.Serializer());
      registerFunction(new SetContents.Serializer());
      registerFunction(new LimitCount.Serializer());
      registerFunction(new ApplyBonus.Serializer());
      registerFunction(new SetLootTable.Serializer());
      registerFunction(new ExplosionDecay.Serializer());
      registerFunction(new SetLore.Serializer());
      registerFunction(new FillPlayerHead.Serializer());
      registerFunction(new CopyNbt.Serializer());
      registerFunction(new CopyBlockState.Serializer());
   }

   public static class Serializer implements JsonDeserializer<ILootFunction>, JsonSerializer<ILootFunction> {
      public ILootFunction deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         JsonObject jsonobject = JSONUtils.getJsonObject(p_deserialize_1_, "function");
         ResourceLocation resourcelocation = new ResourceLocation(JSONUtils.getString(jsonobject, "function"));

         ILootFunction.Serializer<?> serializer;
         try {
            serializer = LootFunctionManager.getSerializerForName(resourcelocation);
         } catch (IllegalArgumentException var8) {
            throw new JsonSyntaxException("Unknown function '" + resourcelocation + "'");
         }

         return serializer.deserialize(jsonobject, p_deserialize_3_);
      }

      public JsonElement serialize(ILootFunction p_serialize_1_, Type p_serialize_2_, JsonSerializationContext p_serialize_3_) {
         ILootFunction.Serializer<ILootFunction> serializer = LootFunctionManager.getSerializerFor(p_serialize_1_);
         JsonObject jsonobject = new JsonObject();
         jsonobject.addProperty("function", serializer.getFunctionName().toString());
         serializer.serialize(jsonobject, p_serialize_1_, p_serialize_3_);
         return jsonobject;
      }
   }
}
package net.minecraft.world.storage.loot.conditions;

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
import java.util.function.Predicate;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;

public class LootConditionManager {
   private static final Map<ResourceLocation, ILootCondition.AbstractSerializer<?>> BY_NAME = Maps.newHashMap();
   private static final Map<Class<? extends ILootCondition>, ILootCondition.AbstractSerializer<?>> BY_CLASS = Maps.newHashMap();

   public static <T extends ILootCondition> void registerCondition(ILootCondition.AbstractSerializer<? extends T> condition) {
      ResourceLocation resourcelocation = condition.getLootTableLocation();
      Class<T> oclass = (Class<T>)condition.getConditionClass();
      if (BY_NAME.containsKey(resourcelocation)) {
         throw new IllegalArgumentException("Can't re-register item condition name " + resourcelocation);
      } else if (BY_CLASS.containsKey(oclass)) {
         throw new IllegalArgumentException("Can't re-register item condition class " + oclass.getName());
      } else {
         BY_NAME.put(resourcelocation, condition);
         BY_CLASS.put(oclass, condition);
      }
   }

   public static ILootCondition.AbstractSerializer<?> getSerializerForName(ResourceLocation location) {
      ILootCondition.AbstractSerializer<?> abstractserializer = BY_NAME.get(location);
      if (abstractserializer == null) {
         throw new IllegalArgumentException("Unknown loot item condition '" + location + "'");
      } else {
         return abstractserializer;
      }
   }

   public static <T extends ILootCondition> ILootCondition.AbstractSerializer<T> getSerializerFor(T conditionClass) {
      ILootCondition.AbstractSerializer<T> abstractserializer = (ILootCondition.AbstractSerializer<T>)BY_CLASS.get(conditionClass.getClass());
      if (abstractserializer == null) {
         throw new IllegalArgumentException("Unknown loot item condition " + conditionClass);
      } else {
         return abstractserializer;
      }
   }

   public static <T> Predicate<T> and(Predicate<T>[] p_216305_0_) {
      switch(p_216305_0_.length) {
      case 0:
         return (p_216304_0_) -> {
            return true;
         };
      case 1:
         return p_216305_0_[0];
      case 2:
         return p_216305_0_[0].and(p_216305_0_[1]);
      default:
         return (p_216307_1_) -> {
            for(Predicate<T> predicate : p_216305_0_) {
               if (!predicate.test(p_216307_1_)) {
                  return false;
               }
            }

            return true;
         };
      }
   }

   public static <T> Predicate<T> or(Predicate<T>[] p_216306_0_) {
      switch(p_216306_0_.length) {
      case 0:
         return (p_216308_0_) -> {
            return false;
         };
      case 1:
         return p_216306_0_[0];
      case 2:
         return p_216306_0_[0].or(p_216306_0_[1]);
      default:
         return (p_216309_1_) -> {
            for(Predicate<T> predicate : p_216306_0_) {
               if (predicate.test(p_216309_1_)) {
                  return true;
               }
            }

            return false;
         };
      }
   }

   static {
      registerCondition(new Inverted.Serializer());
      registerCondition(new Alternative.Serializer());
      registerCondition(new RandomChance.Serializer());
      registerCondition(new RandomChanceWithLooting.Serializer());
      registerCondition(new EntityHasProperty.Serializer());
      registerCondition(new KilledByPlayer.Serializer());
      registerCondition(new EntityHasScore.Serializer());
      registerCondition(new BlockStateProperty.Serializer());
      registerCondition(new MatchTool.Serializer());
      registerCondition(new TableBonus.Serializer());
      registerCondition(new SurvivesExplosion.Serializer());
      registerCondition(new DamageSourceProperties.Serializer());
      registerCondition(new LocationCheck.Serializer());
      registerCondition(new WeatherCheck.Serializer());
      registerCondition(new Reference.Serializer());
      registerCondition(new TimeCheck.Serializer());
   }

   public static class Serializer implements JsonDeserializer<ILootCondition>, JsonSerializer<ILootCondition> {
      public ILootCondition deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         JsonObject jsonobject = JSONUtils.getJsonObject(p_deserialize_1_, "condition");
         ResourceLocation resourcelocation = new ResourceLocation(JSONUtils.getString(jsonobject, "condition"));

         ILootCondition.AbstractSerializer<?> abstractserializer;
         try {
            abstractserializer = LootConditionManager.getSerializerForName(resourcelocation);
         } catch (IllegalArgumentException var8) {
            throw new JsonSyntaxException("Unknown condition '" + resourcelocation + "'");
         }

         return abstractserializer.deserialize(jsonobject, p_deserialize_3_);
      }

      public JsonElement serialize(ILootCondition p_serialize_1_, Type p_serialize_2_, JsonSerializationContext p_serialize_3_) {
         ILootCondition.AbstractSerializer<ILootCondition> abstractserializer = LootConditionManager.getSerializerFor(p_serialize_1_);
         JsonObject jsonobject = new JsonObject();
         jsonobject.addProperty("condition", abstractserializer.getLootTableLocation().toString());
         abstractserializer.serialize(jsonobject, p_serialize_1_, p_serialize_3_);
         return jsonobject;
      }
   }
}
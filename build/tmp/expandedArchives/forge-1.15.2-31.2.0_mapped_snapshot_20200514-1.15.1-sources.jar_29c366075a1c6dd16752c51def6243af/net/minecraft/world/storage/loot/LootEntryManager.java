package net.minecraft.world.storage.loot;

import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.Map;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.conditions.ILootCondition;
import org.apache.commons.lang3.ArrayUtils;

public class LootEntryManager {
   private static final Map<ResourceLocation, LootEntry.Serializer<?>> field_216197_a = Maps.newHashMap();
   private static final Map<Class<?>, LootEntry.Serializer<?>> field_216198_b = Maps.newHashMap();

   private static void func_216194_a(LootEntry.Serializer<?> p_216194_0_) {
      field_216197_a.put(p_216194_0_.func_216182_a(), p_216194_0_);
      field_216198_b.put(p_216194_0_.func_216183_b(), p_216194_0_);
   }

   static {
      func_216194_a(ParentedLootEntry.func_216145_a(new ResourceLocation("alternatives"), AlternativesLootEntry.class, AlternativesLootEntry::new));
      func_216194_a(ParentedLootEntry.func_216145_a(new ResourceLocation("sequence"), SequenceLootEntry.class, SequenceLootEntry::new));
      func_216194_a(ParentedLootEntry.func_216145_a(new ResourceLocation("group"), GroupLootEntry.class, GroupLootEntry::new));
      func_216194_a(new EmptyLootEntry.Serializer());
      func_216194_a(new ItemLootEntry.Serializer());
      func_216194_a(new TableLootEntry.Serializer());
      func_216194_a(new DynamicLootEntry.Serializer());
      func_216194_a(new TagLootEntry.Serializer());
   }

   public static class Serializer implements JsonDeserializer<LootEntry>, JsonSerializer<LootEntry> {
      public LootEntry deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) {
         JsonObject jsonobject = JSONUtils.getJsonObject(p_deserialize_1_, "entry");
         ResourceLocation resourcelocation = new ResourceLocation(JSONUtils.getString(jsonobject, "type"));
         LootEntry.Serializer<?> serializer = LootEntryManager.field_216197_a.get(resourcelocation);
         if (serializer == null) {
            throw new JsonParseException("Unknown item type: " + resourcelocation);
         } else {
            ILootCondition[] ailootcondition = JSONUtils.deserializeClass(jsonobject, "conditions", new ILootCondition[0], p_deserialize_3_, ILootCondition[].class);
            return serializer.deserialize(jsonobject, p_deserialize_3_, ailootcondition);
         }
      }

      public JsonElement serialize(LootEntry p_serialize_1_, Type p_serialize_2_, JsonSerializationContext p_serialize_3_) {
         JsonObject jsonobject = new JsonObject();
         LootEntry.Serializer<LootEntry> serializer = func_216193_a(p_serialize_1_.getClass());
         jsonobject.addProperty("type", serializer.func_216182_a().toString());
         if (!ArrayUtils.isEmpty((Object[])p_serialize_1_.conditions)) {
            jsonobject.add("conditions", p_serialize_3_.serialize(p_serialize_1_.conditions));
         }

         serializer.serialize(jsonobject, p_serialize_1_, p_serialize_3_);
         return jsonobject;
      }

      private static LootEntry.Serializer<LootEntry> func_216193_a(Class<?> p_216193_0_) {
         LootEntry.Serializer<?> serializer = LootEntryManager.field_216198_b.get(p_216193_0_);
         if (serializer == null) {
            throw new JsonParseException("Unknown item type: " + p_216193_0_);
         } else {
            return (LootEntry.Serializer<LootEntry>)serializer;
         }
      }
   }
}
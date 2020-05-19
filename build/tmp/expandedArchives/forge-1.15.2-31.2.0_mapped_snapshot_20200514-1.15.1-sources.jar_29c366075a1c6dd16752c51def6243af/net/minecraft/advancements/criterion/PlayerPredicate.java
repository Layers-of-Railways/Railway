package net.minecraft.advancements.criterion;

import com.google.common.collect.Maps;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementManager;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.CriterionProgress;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.crafting.RecipeBook;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatType;
import net.minecraft.stats.StatisticsManager;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.GameType;

public class PlayerPredicate {
   public static final PlayerPredicate field_226989_a_ = (new PlayerPredicate.Default()).func_227012_b_();
   private final MinMaxBounds.IntBound level;
   private final GameType gamemode;
   private final Map<Stat<?>, MinMaxBounds.IntBound> stats;
   private final Object2BooleanMap<ResourceLocation> field_226993_e_;
   private final Map<ResourceLocation, PlayerPredicate.IAdvancementPredicate> field_226994_f_;

   private static PlayerPredicate.IAdvancementPredicate func_227004_b_(JsonElement p_227004_0_) {
      if (p_227004_0_.isJsonPrimitive()) {
         boolean flag = p_227004_0_.getAsBoolean();
         return new PlayerPredicate.CompletedAdvancementPredicate(flag);
      } else {
         Object2BooleanMap<String> object2booleanmap = new Object2BooleanOpenHashMap<>();
         JsonObject jsonobject = JSONUtils.getJsonObject(p_227004_0_, "criterion data");
         jsonobject.entrySet().forEach((p_227003_1_) -> {
            boolean flag1 = JSONUtils.getBoolean(p_227003_1_.getValue(), "criterion test");
            object2booleanmap.put(p_227003_1_.getKey(), flag1);
         });
         return new PlayerPredicate.CriteriaPredicate(object2booleanmap);
      }
   }

   private PlayerPredicate(MinMaxBounds.IntBound p_i225770_1_, GameType p_i225770_2_, Map<Stat<?>, MinMaxBounds.IntBound> p_i225770_3_, Object2BooleanMap<ResourceLocation> p_i225770_4_, Map<ResourceLocation, PlayerPredicate.IAdvancementPredicate> p_i225770_5_) {
      this.level = p_i225770_1_;
      this.gamemode = p_i225770_2_;
      this.stats = p_i225770_3_;
      this.field_226993_e_ = p_i225770_4_;
      this.field_226994_f_ = p_i225770_5_;
   }

   public boolean func_226998_a_(Entity p_226998_1_) {
      if (this == field_226989_a_) {
         return true;
      } else if (!(p_226998_1_ instanceof ServerPlayerEntity)) {
         return false;
      } else {
         ServerPlayerEntity serverplayerentity = (ServerPlayerEntity)p_226998_1_;
         if (!this.level.test(serverplayerentity.experienceLevel)) {
            return false;
         } else if (this.gamemode != GameType.NOT_SET && this.gamemode != serverplayerentity.interactionManager.getGameType()) {
            return false;
         } else {
            StatisticsManager statisticsmanager = serverplayerentity.getStats();

            for(Entry<Stat<?>, MinMaxBounds.IntBound> entry : this.stats.entrySet()) {
               int i = statisticsmanager.getValue(entry.getKey());
               if (!entry.getValue().test(i)) {
                  return false;
               }
            }

            RecipeBook recipebook = serverplayerentity.getRecipeBook();

            for(it.unimi.dsi.fastutil.objects.Object2BooleanMap.Entry<ResourceLocation> entry2 : this.field_226993_e_.object2BooleanEntrySet()) {
               if (recipebook.func_226144_b_(entry2.getKey()) != entry2.getBooleanValue()) {
                  return false;
               }
            }

            if (!this.field_226994_f_.isEmpty()) {
               PlayerAdvancements playeradvancements = serverplayerentity.getAdvancements();
               AdvancementManager advancementmanager = serverplayerentity.getServer().getAdvancementManager();

               for(Entry<ResourceLocation, PlayerPredicate.IAdvancementPredicate> entry1 : this.field_226994_f_.entrySet()) {
                  Advancement advancement = advancementmanager.getAdvancement(entry1.getKey());
                  if (advancement == null || !entry1.getValue().test(playeradvancements.getProgress(advancement))) {
                     return false;
                  }
               }
            }

            return true;
         }
      }
   }

   public static PlayerPredicate func_227000_a_(@Nullable JsonElement p_227000_0_) {
      if (p_227000_0_ != null && !p_227000_0_.isJsonNull()) {
         JsonObject jsonobject = JSONUtils.getJsonObject(p_227000_0_, "player");
         MinMaxBounds.IntBound minmaxbounds$intbound = MinMaxBounds.IntBound.fromJson(jsonobject.get("level"));
         String s = JSONUtils.getString(jsonobject, "gamemode", "");
         GameType gametype = GameType.parseGameTypeWithDefault(s, GameType.NOT_SET);
         Map<Stat<?>, MinMaxBounds.IntBound> map = Maps.newHashMap();
         JsonArray jsonarray = JSONUtils.getJsonArray(jsonobject, "stats", (JsonArray)null);
         if (jsonarray != null) {
            for(JsonElement jsonelement : jsonarray) {
               JsonObject jsonobject1 = JSONUtils.getJsonObject(jsonelement, "stats entry");
               ResourceLocation resourcelocation = new ResourceLocation(JSONUtils.getString(jsonobject1, "type"));
               StatType<?> stattype = Registry.STATS.getOrDefault(resourcelocation);
               if (stattype == null) {
                  throw new JsonParseException("Invalid stat type: " + resourcelocation);
               }

               ResourceLocation resourcelocation1 = new ResourceLocation(JSONUtils.getString(jsonobject1, "stat"));
               Stat<?> stat = func_226997_a_(stattype, resourcelocation1);
               MinMaxBounds.IntBound minmaxbounds$intbound1 = MinMaxBounds.IntBound.fromJson(jsonobject1.get("value"));
               map.put(stat, minmaxbounds$intbound1);
            }
         }

         Object2BooleanMap<ResourceLocation> object2booleanmap = new Object2BooleanOpenHashMap<>();
         JsonObject jsonobject2 = JSONUtils.getJsonObject(jsonobject, "recipes", new JsonObject());

         for(Entry<String, JsonElement> entry : jsonobject2.entrySet()) {
            ResourceLocation resourcelocation2 = new ResourceLocation(entry.getKey());
            boolean flag = JSONUtils.getBoolean(entry.getValue(), "recipe present");
            object2booleanmap.put(resourcelocation2, flag);
         }

         Map<ResourceLocation, PlayerPredicate.IAdvancementPredicate> map1 = Maps.newHashMap();
         JsonObject jsonobject3 = JSONUtils.getJsonObject(jsonobject, "advancements", new JsonObject());

         for(Entry<String, JsonElement> entry1 : jsonobject3.entrySet()) {
            ResourceLocation resourcelocation3 = new ResourceLocation(entry1.getKey());
            PlayerPredicate.IAdvancementPredicate playerpredicate$iadvancementpredicate = func_227004_b_(entry1.getValue());
            map1.put(resourcelocation3, playerpredicate$iadvancementpredicate);
         }

         return new PlayerPredicate(minmaxbounds$intbound, gametype, map, object2booleanmap, map1);
      } else {
         return field_226989_a_;
      }
   }

   private static <T> Stat<T> func_226997_a_(StatType<T> p_226997_0_, ResourceLocation p_226997_1_) {
      Registry<T> registry = p_226997_0_.getRegistry();
      T t = registry.getOrDefault(p_226997_1_);
      if (t == null) {
         throw new JsonParseException("Unknown object " + p_226997_1_ + " for stat type " + Registry.STATS.getKey(p_226997_0_));
      } else {
         return p_226997_0_.get(t);
      }
   }

   private static <T> ResourceLocation func_226996_a_(Stat<T> p_226996_0_) {
      return p_226996_0_.getType().getRegistry().getKey(p_226996_0_.getValue());
   }

   public JsonElement serialize() {
      if (this == field_226989_a_) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject jsonobject = new JsonObject();
         jsonobject.add("level", this.level.serialize());
         if (this.gamemode != GameType.NOT_SET) {
            jsonobject.addProperty("gamemode", this.gamemode.getName());
         }

         if (!this.stats.isEmpty()) {
            JsonArray jsonarray = new JsonArray();
            this.stats.forEach((p_226999_1_, p_226999_2_) -> {
               JsonObject jsonobject3 = new JsonObject();
               jsonobject3.addProperty("type", Registry.STATS.getKey(p_226999_1_.getType()).toString());
               jsonobject3.addProperty("stat", func_226996_a_(p_226999_1_).toString());
               jsonobject3.add("value", p_226999_2_.serialize());
               jsonarray.add(jsonobject3);
            });
            jsonobject.add("stats", jsonarray);
         }

         if (!this.field_226993_e_.isEmpty()) {
            JsonObject jsonobject1 = new JsonObject();
            this.field_226993_e_.forEach((p_227002_1_, p_227002_2_) -> {
               jsonobject1.addProperty(p_227002_1_.toString(), p_227002_2_);
            });
            jsonobject.add("recipes", jsonobject1);
         }

         if (!this.field_226994_f_.isEmpty()) {
            JsonObject jsonobject2 = new JsonObject();
            this.field_226994_f_.forEach((p_227001_1_, p_227001_2_) -> {
               jsonobject2.add(p_227001_1_.toString(), p_227001_2_.func_225544_a_());
            });
            jsonobject.add("advancements", jsonobject2);
         }

         return jsonobject;
      }
   }

   static class CompletedAdvancementPredicate implements PlayerPredicate.IAdvancementPredicate {
      private final boolean field_227006_a_;

      public CompletedAdvancementPredicate(boolean p_i225773_1_) {
         this.field_227006_a_ = p_i225773_1_;
      }

      public JsonElement func_225544_a_() {
         return new JsonPrimitive(this.field_227006_a_);
      }

      public boolean test(AdvancementProgress p_test_1_) {
         return p_test_1_.isDone() == this.field_227006_a_;
      }
   }

   static class CriteriaPredicate implements PlayerPredicate.IAdvancementPredicate {
      private final Object2BooleanMap<String> field_227005_a_;

      public CriteriaPredicate(Object2BooleanMap<String> p_i225772_1_) {
         this.field_227005_a_ = p_i225772_1_;
      }

      public JsonElement func_225544_a_() {
         JsonObject jsonobject = new JsonObject();
         this.field_227005_a_.forEach(jsonobject::addProperty);
         return jsonobject;
      }

      public boolean test(AdvancementProgress p_test_1_) {
         for(it.unimi.dsi.fastutil.objects.Object2BooleanMap.Entry<String> entry : this.field_227005_a_.object2BooleanEntrySet()) {
            CriterionProgress criterionprogress = p_test_1_.getCriterionProgress(entry.getKey());
            if (criterionprogress == null || criterionprogress.isObtained() != entry.getBooleanValue()) {
               return false;
            }
         }

         return true;
      }
   }

   public static class Default {
      private MinMaxBounds.IntBound field_227007_a_ = MinMaxBounds.IntBound.UNBOUNDED;
      private GameType field_227008_b_ = GameType.NOT_SET;
      private final Map<Stat<?>, MinMaxBounds.IntBound> field_227009_c_ = Maps.newHashMap();
      private final Object2BooleanMap<ResourceLocation> field_227010_d_ = new Object2BooleanOpenHashMap<>();
      private final Map<ResourceLocation, PlayerPredicate.IAdvancementPredicate> field_227011_e_ = Maps.newHashMap();

      public PlayerPredicate func_227012_b_() {
         return new PlayerPredicate(this.field_227007_a_, this.field_227008_b_, this.field_227009_c_, this.field_227010_d_, this.field_227011_e_);
      }
   }

   interface IAdvancementPredicate extends Predicate<AdvancementProgress> {
      JsonElement func_225544_a_();
   }
}
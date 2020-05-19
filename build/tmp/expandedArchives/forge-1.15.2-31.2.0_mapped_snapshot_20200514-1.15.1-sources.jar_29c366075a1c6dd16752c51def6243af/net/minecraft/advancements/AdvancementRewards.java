package net.minecraft.advancements;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.command.FunctionObject;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameterSets;
import net.minecraft.world.storage.loot.LootParameters;

public class AdvancementRewards {
   public static final AdvancementRewards EMPTY = new AdvancementRewards(0, new ResourceLocation[0], new ResourceLocation[0], FunctionObject.CacheableFunction.EMPTY);
   private final int experience;
   private final ResourceLocation[] loot;
   private final ResourceLocation[] recipes;
   private final FunctionObject.CacheableFunction function;

   public AdvancementRewards(int experience, ResourceLocation[] loot, ResourceLocation[] recipes, FunctionObject.CacheableFunction function) {
      this.experience = experience;
      this.loot = loot;
      this.recipes = recipes;
      this.function = function;
   }

   public void apply(ServerPlayerEntity player) {
      player.giveExperiencePoints(this.experience);
      LootContext lootcontext = (new LootContext.Builder(player.getServerWorld())).withParameter(LootParameters.THIS_ENTITY, player).withParameter(LootParameters.POSITION, new BlockPos(player)).withRandom(player.getRNG()).withLuck(player.getLuck()).build(LootParameterSets.ADVANCEMENT); // FORGE: luck to LootContext
      boolean flag = false;

      for(ResourceLocation resourcelocation : this.loot) {
         for(ItemStack itemstack : player.server.getLootTableManager().getLootTableFromLocation(resourcelocation).generate(lootcontext)) {
            if (player.addItemStackToInventory(itemstack)) {
               player.world.playSound((PlayerEntity)null, player.getPosX(), player.getPosY(), player.getPosZ(), SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, ((player.getRNG().nextFloat() - player.getRNG().nextFloat()) * 0.7F + 1.0F) * 2.0F);
               flag = true;
            } else {
               ItemEntity itementity = player.dropItem(itemstack, false);
               if (itementity != null) {
                  itementity.setNoPickupDelay();
                  itementity.setOwnerId(player.getUniqueID());
               }
            }
         }
      }

      if (flag) {
         player.container.detectAndSendChanges();
      }

      if (this.recipes.length > 0) {
         player.unlockRecipes(this.recipes);
      }

      MinecraftServer minecraftserver = player.server;
      this.function.func_218039_a(minecraftserver.getFunctionManager()).ifPresent((p_215098_2_) -> {
         minecraftserver.getFunctionManager().execute(p_215098_2_, player.getCommandSource().withFeedbackDisabled().withPermissionLevel(2));
      });
   }

   public String toString() {
      return "AdvancementRewards{experience=" + this.experience + ", loot=" + Arrays.toString((Object[])this.loot) + ", recipes=" + Arrays.toString((Object[])this.recipes) + ", function=" + this.function + '}';
   }

   public JsonElement serialize() {
      if (this == EMPTY) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject jsonobject = new JsonObject();
         if (this.experience != 0) {
            jsonobject.addProperty("experience", this.experience);
         }

         if (this.loot.length > 0) {
            JsonArray jsonarray = new JsonArray();

            for(ResourceLocation resourcelocation : this.loot) {
               jsonarray.add(resourcelocation.toString());
            }

            jsonobject.add("loot", jsonarray);
         }

         if (this.recipes.length > 0) {
            JsonArray jsonarray1 = new JsonArray();

            for(ResourceLocation resourcelocation1 : this.recipes) {
               jsonarray1.add(resourcelocation1.toString());
            }

            jsonobject.add("recipes", jsonarray1);
         }

         if (this.function.getId() != null) {
            jsonobject.addProperty("function", this.function.getId().toString());
         }

         return jsonobject;
      }
   }

   public static class Builder {
      private int experience;
      private final List<ResourceLocation> loot = Lists.newArrayList();
      private final List<ResourceLocation> recipes = Lists.newArrayList();
      @Nullable
      private ResourceLocation function;

      /**
       * Creates a new builder with the given amount of experience as a reward
       */
      public static AdvancementRewards.Builder experience(int experienceIn) {
         return (new AdvancementRewards.Builder()).addExperience(experienceIn);
      }

      /**
       * Adds the given amount of experience. (Not a direct setter)
       */
      public AdvancementRewards.Builder addExperience(int experienceIn) {
         this.experience += experienceIn;
         return this;
      }

      /**
       * Creates a new builder with the given recipe as a reward.
       */
      public static AdvancementRewards.Builder recipe(ResourceLocation recipeIn) {
         return (new AdvancementRewards.Builder()).addRecipe(recipeIn);
      }

      /**
       * Adds the given recipe to the rewards.
       */
      public AdvancementRewards.Builder addRecipe(ResourceLocation recipeIn) {
         this.recipes.add(recipeIn);
         return this;
      }

      public AdvancementRewards build() {
         return new AdvancementRewards(this.experience, this.loot.toArray(new ResourceLocation[0]), this.recipes.toArray(new ResourceLocation[0]), this.function == null ? FunctionObject.CacheableFunction.EMPTY : new FunctionObject.CacheableFunction(this.function));
      }
   }

   public static class Deserializer implements JsonDeserializer<AdvancementRewards> {
      public AdvancementRewards deserialize(JsonElement p_deserialize_1_, Type p_deserialize_2_, JsonDeserializationContext p_deserialize_3_) throws JsonParseException {
         JsonObject jsonobject = JSONUtils.getJsonObject(p_deserialize_1_, "rewards");
         int i = JSONUtils.getInt(jsonobject, "experience", 0);
         JsonArray jsonarray = JSONUtils.getJsonArray(jsonobject, "loot", new JsonArray());
         ResourceLocation[] aresourcelocation = new ResourceLocation[jsonarray.size()];

         for(int j = 0; j < aresourcelocation.length; ++j) {
            aresourcelocation[j] = new ResourceLocation(JSONUtils.getString(jsonarray.get(j), "loot[" + j + "]"));
         }

         JsonArray jsonarray1 = JSONUtils.getJsonArray(jsonobject, "recipes", new JsonArray());
         ResourceLocation[] aresourcelocation1 = new ResourceLocation[jsonarray1.size()];

         for(int k = 0; k < aresourcelocation1.length; ++k) {
            aresourcelocation1[k] = new ResourceLocation(JSONUtils.getString(jsonarray1.get(k), "recipes[" + k + "]"));
         }

         FunctionObject.CacheableFunction functionobject$cacheablefunction;
         if (jsonobject.has("function")) {
            functionobject$cacheablefunction = new FunctionObject.CacheableFunction(new ResourceLocation(JSONUtils.getString(jsonobject, "function")));
         } else {
            functionobject$cacheablefunction = FunctionObject.CacheableFunction.EMPTY;
         }

         return new AdvancementRewards(i, aresourcelocation, aresourcelocation1, functionobject$cacheablefunction);
      }
   }
}
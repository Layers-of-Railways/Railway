package net.minecraft.world.storage.loot.conditions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import net.minecraft.advancements.criterion.LocationPredicate;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameters;

public class LocationCheck implements ILootCondition {
   private final LocationPredicate predicate;
   private final BlockPos field_227564_b_;

   public LocationCheck(LocationPredicate p_i225895_1_, BlockPos p_i225895_2_) {
      this.predicate = p_i225895_1_;
      this.field_227564_b_ = p_i225895_2_;
   }

   public boolean test(LootContext p_test_1_) {
      BlockPos blockpos = p_test_1_.get(LootParameters.POSITION);
      return blockpos != null && this.predicate.test(p_test_1_.getWorld(), (float)(blockpos.getX() + this.field_227564_b_.getX()), (float)(blockpos.getY() + this.field_227564_b_.getY()), (float)(blockpos.getZ() + this.field_227564_b_.getZ()));
   }

   public static ILootCondition.IBuilder builder(LocationPredicate.Builder p_215975_0_) {
      return () -> {
         return new LocationCheck(p_215975_0_.build(), BlockPos.ZERO);
      };
   }

   public static class Serializer extends ILootCondition.AbstractSerializer<LocationCheck> {
      public Serializer() {
         super(new ResourceLocation("location_check"), LocationCheck.class);
      }

      public void serialize(JsonObject json, LocationCheck value, JsonSerializationContext context) {
         json.add("predicate", value.predicate.serialize());
         if (value.field_227564_b_.getX() != 0) {
            json.addProperty("offsetX", value.field_227564_b_.getX());
         }

         if (value.field_227564_b_.getY() != 0) {
            json.addProperty("offsetY", value.field_227564_b_.getY());
         }

         if (value.field_227564_b_.getZ() != 0) {
            json.addProperty("offsetZ", value.field_227564_b_.getZ());
         }

      }

      public LocationCheck deserialize(JsonObject json, JsonDeserializationContext context) {
         LocationPredicate locationpredicate = LocationPredicate.deserialize(json.get("predicate"));
         int i = JSONUtils.getInt(json, "offsetX", 0);
         int j = JSONUtils.getInt(json, "offsetY", 0);
         int k = JSONUtils.getInt(json, "offsetZ", 0);
         return new LocationCheck(locationpredicate, new BlockPos(i, j, k));
      }
   }
}
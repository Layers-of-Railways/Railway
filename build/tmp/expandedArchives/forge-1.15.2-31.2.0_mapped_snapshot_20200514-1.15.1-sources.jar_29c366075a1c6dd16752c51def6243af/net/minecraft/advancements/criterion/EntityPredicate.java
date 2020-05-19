package net.minecraft.advancements.criterion;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.scoreboard.Team;
import net.minecraft.tags.Tag;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.server.ServerWorld;

public class EntityPredicate {
   public static final EntityPredicate ANY = new EntityPredicate(EntityTypePredicate.ANY, DistancePredicate.ANY, LocationPredicate.ANY, MobEffectsPredicate.ANY, NBTPredicate.ANY, EntityFlagsPredicate.ALWAYS_TRUE, EntityEquipmentPredicate.ANY, PlayerPredicate.field_226989_a_, (String)null, (ResourceLocation)null);
   public static final EntityPredicate[] ANY_ARRAY = new EntityPredicate[0];
   private final EntityTypePredicate type;
   private final DistancePredicate distance;
   private final LocationPredicate location;
   private final MobEffectsPredicate effects;
   private final NBTPredicate nbt;
   private final EntityFlagsPredicate flags;
   private final EntityEquipmentPredicate equipment;
   private final PlayerPredicate player;
   @Nullable
   private final String team;
   @Nullable
   private final ResourceLocation catType;

   private EntityPredicate(EntityTypePredicate p_i225735_1_, DistancePredicate p_i225735_2_, LocationPredicate p_i225735_3_, MobEffectsPredicate p_i225735_4_, NBTPredicate p_i225735_5_, EntityFlagsPredicate p_i225735_6_, EntityEquipmentPredicate p_i225735_7_, PlayerPredicate p_i225735_8_, @Nullable String p_i225735_9_, @Nullable ResourceLocation p_i225735_10_) {
      this.type = p_i225735_1_;
      this.distance = p_i225735_2_;
      this.location = p_i225735_3_;
      this.effects = p_i225735_4_;
      this.nbt = p_i225735_5_;
      this.flags = p_i225735_6_;
      this.equipment = p_i225735_7_;
      this.player = p_i225735_8_;
      this.team = p_i225735_9_;
      this.catType = p_i225735_10_;
   }

   public boolean test(ServerPlayerEntity player, @Nullable Entity entity) {
      return this.func_217993_a(player.getServerWorld(), player.getPositionVec(), entity);
   }

   public boolean func_217993_a(ServerWorld p_217993_1_, @Nullable Vec3d p_217993_2_, @Nullable Entity p_217993_3_) {
      if (this == ANY) {
         return true;
      } else if (p_217993_3_ == null) {
         return false;
      } else if (!this.type.test(p_217993_3_.getType())) {
         return false;
      } else {
         if (p_217993_2_ == null) {
            if (this.distance != DistancePredicate.ANY) {
               return false;
            }
         } else if (!this.distance.test(p_217993_2_.x, p_217993_2_.y, p_217993_2_.z, p_217993_3_.getPosX(), p_217993_3_.getPosY(), p_217993_3_.getPosZ())) {
            return false;
         }

         if (!this.location.test(p_217993_1_, p_217993_3_.getPosX(), p_217993_3_.getPosY(), p_217993_3_.getPosZ())) {
            return false;
         } else if (!this.effects.test(p_217993_3_)) {
            return false;
         } else if (!this.nbt.test(p_217993_3_)) {
            return false;
         } else if (!this.flags.test(p_217993_3_)) {
            return false;
         } else if (!this.equipment.test(p_217993_3_)) {
            return false;
         } else if (!this.player.func_226998_a_(p_217993_3_)) {
            return false;
         } else {
            if (this.team != null) {
               Team team = p_217993_3_.getTeam();
               if (team == null || !this.team.equals(team.getName())) {
                  return false;
               }
            }

            return this.catType == null || p_217993_3_ instanceof CatEntity && ((CatEntity)p_217993_3_).getCatTypeName().equals(this.catType);
         }
      }
   }

   public static EntityPredicate deserialize(@Nullable JsonElement element) {
      if (element != null && !element.isJsonNull()) {
         JsonObject jsonobject = JSONUtils.getJsonObject(element, "entity");
         EntityTypePredicate entitytypepredicate = EntityTypePredicate.deserialize(jsonobject.get("type"));
         DistancePredicate distancepredicate = DistancePredicate.deserialize(jsonobject.get("distance"));
         LocationPredicate locationpredicate = LocationPredicate.deserialize(jsonobject.get("location"));
         MobEffectsPredicate mobeffectspredicate = MobEffectsPredicate.deserialize(jsonobject.get("effects"));
         NBTPredicate nbtpredicate = NBTPredicate.deserialize(jsonobject.get("nbt"));
         EntityFlagsPredicate entityflagspredicate = EntityFlagsPredicate.deserialize(jsonobject.get("flags"));
         EntityEquipmentPredicate entityequipmentpredicate = EntityEquipmentPredicate.deserialize(jsonobject.get("equipment"));
         PlayerPredicate playerpredicate = PlayerPredicate.func_227000_a_(jsonobject.get("player"));
         String s = JSONUtils.getString(jsonobject, "team", (String)null);
         ResourceLocation resourcelocation = jsonobject.has("catType") ? new ResourceLocation(JSONUtils.getString(jsonobject, "catType")) : null;
         return (new EntityPredicate.Builder()).type(entitytypepredicate).distance(distancepredicate).location(locationpredicate).effects(mobeffectspredicate).nbt(nbtpredicate).flags(entityflagspredicate).equipment(entityequipmentpredicate).func_226613_a_(playerpredicate).func_226614_a_(s).func_217988_b(resourcelocation).build();
      } else {
         return ANY;
      }
   }

   public static EntityPredicate[] deserializeArray(@Nullable JsonElement array) {
      if (array != null && !array.isJsonNull()) {
         JsonArray jsonarray = JSONUtils.getJsonArray(array, "entities");
         EntityPredicate[] aentitypredicate = new EntityPredicate[jsonarray.size()];

         for(int i = 0; i < jsonarray.size(); ++i) {
            aentitypredicate[i] = deserialize(jsonarray.get(i));
         }

         return aentitypredicate;
      } else {
         return ANY_ARRAY;
      }
   }

   public JsonElement serialize() {
      if (this == ANY) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject jsonobject = new JsonObject();
         jsonobject.add("type", this.type.serialize());
         jsonobject.add("distance", this.distance.serialize());
         jsonobject.add("location", this.location.serialize());
         jsonobject.add("effects", this.effects.serialize());
         jsonobject.add("nbt", this.nbt.serialize());
         jsonobject.add("flags", this.flags.serialize());
         jsonobject.add("equipment", this.equipment.serialize());
         jsonobject.add("player", this.player.serialize());
         jsonobject.addProperty("team", this.team);
         if (this.catType != null) {
            jsonobject.addProperty("catType", this.catType.toString());
         }

         return jsonobject;
      }
   }

   public static JsonElement serializeArray(EntityPredicate[] predicates) {
      if (predicates == ANY_ARRAY) {
         return JsonNull.INSTANCE;
      } else {
         JsonArray jsonarray = new JsonArray();

         for(EntityPredicate entitypredicate : predicates) {
            JsonElement jsonelement = entitypredicate.serialize();
            if (!jsonelement.isJsonNull()) {
               jsonarray.add(jsonelement);
            }
         }

         return jsonarray;
      }
   }

   public static class Builder {
      private EntityTypePredicate type = EntityTypePredicate.ANY;
      private DistancePredicate distance = DistancePredicate.ANY;
      private LocationPredicate location = LocationPredicate.ANY;
      private MobEffectsPredicate effects = MobEffectsPredicate.ANY;
      private NBTPredicate nbt = NBTPredicate.ANY;
      private EntityFlagsPredicate flags = EntityFlagsPredicate.ALWAYS_TRUE;
      private EntityEquipmentPredicate equipment = EntityEquipmentPredicate.ANY;
      private PlayerPredicate field_226611_h_ = PlayerPredicate.field_226989_a_;
      private String field_226612_i_;
      private ResourceLocation catType;

      public static EntityPredicate.Builder create() {
         return new EntityPredicate.Builder();
      }

      public EntityPredicate.Builder type(EntityType<?> typeIn) {
         this.type = EntityTypePredicate.func_217999_b(typeIn);
         return this;
      }

      public EntityPredicate.Builder type(Tag<EntityType<?>> typeIn) {
         this.type = EntityTypePredicate.func_217998_a(typeIn);
         return this;
      }

      public EntityPredicate.Builder func_217986_a(ResourceLocation catTypeIn) {
         this.catType = catTypeIn;
         return this;
      }

      public EntityPredicate.Builder type(EntityTypePredicate typeIn) {
         this.type = typeIn;
         return this;
      }

      public EntityPredicate.Builder distance(DistancePredicate distanceIn) {
         this.distance = distanceIn;
         return this;
      }

      public EntityPredicate.Builder location(LocationPredicate locationIn) {
         this.location = locationIn;
         return this;
      }

      public EntityPredicate.Builder effects(MobEffectsPredicate effectsIn) {
         this.effects = effectsIn;
         return this;
      }

      public EntityPredicate.Builder nbt(NBTPredicate nbtIn) {
         this.nbt = nbtIn;
         return this;
      }

      public EntityPredicate.Builder flags(EntityFlagsPredicate flagsIn) {
         this.flags = flagsIn;
         return this;
      }

      public EntityPredicate.Builder equipment(EntityEquipmentPredicate equipmentIn) {
         this.equipment = equipmentIn;
         return this;
      }

      public EntityPredicate.Builder func_226613_a_(PlayerPredicate p_226613_1_) {
         this.field_226611_h_ = p_226613_1_;
         return this;
      }

      public EntityPredicate.Builder func_226614_a_(@Nullable String p_226614_1_) {
         this.field_226612_i_ = p_226614_1_;
         return this;
      }

      public EntityPredicate.Builder func_217988_b(@Nullable ResourceLocation catTypeIn) {
         this.catType = catTypeIn;
         return this;
      }

      public EntityPredicate build() {
         return new EntityPredicate(this.type, this.distance, this.location, this.effects, this.nbt, this.flags, this.equipment, this.field_226611_h_, this.field_226612_i_, this.catType);
      }
   }
}
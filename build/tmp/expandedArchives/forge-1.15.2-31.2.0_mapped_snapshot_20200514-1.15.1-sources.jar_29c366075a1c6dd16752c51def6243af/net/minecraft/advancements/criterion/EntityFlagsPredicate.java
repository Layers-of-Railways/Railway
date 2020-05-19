package net.minecraft.advancements.criterion;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.JSONUtils;

public class EntityFlagsPredicate {
   public static final EntityFlagsPredicate ALWAYS_TRUE = (new EntityFlagsPredicate.Builder()).build();
   @Nullable
   private final Boolean onFire;
   @Nullable
   private final Boolean field_226569_c_;
   @Nullable
   private final Boolean sprinting;
   @Nullable
   private final Boolean swimming;
   @Nullable
   private final Boolean baby;

   public EntityFlagsPredicate(@Nullable Boolean p_i50808_1_, @Nullable Boolean p_i50808_2_, @Nullable Boolean p_i50808_3_, @Nullable Boolean p_i50808_4_, @Nullable Boolean p_i50808_5_) {
      this.onFire = p_i50808_1_;
      this.field_226569_c_ = p_i50808_2_;
      this.sprinting = p_i50808_3_;
      this.swimming = p_i50808_4_;
      this.baby = p_i50808_5_;
   }

   public boolean test(Entity p_217974_1_) {
      if (this.onFire != null && p_217974_1_.isBurning() != this.onFire) {
         return false;
      } else if (this.field_226569_c_ != null && p_217974_1_.isCrouching() != this.field_226569_c_) {
         return false;
      } else if (this.sprinting != null && p_217974_1_.isSprinting() != this.sprinting) {
         return false;
      } else if (this.swimming != null && p_217974_1_.isSwimming() != this.swimming) {
         return false;
      } else {
         return this.baby == null || !(p_217974_1_ instanceof LivingEntity) || ((LivingEntity)p_217974_1_).isChild() == this.baby;
      }
   }

   @Nullable
   private static Boolean getBoolean(JsonObject p_217977_0_, String p_217977_1_) {
      return p_217977_0_.has(p_217977_1_) ? JSONUtils.getBoolean(p_217977_0_, p_217977_1_) : null;
   }

   public static EntityFlagsPredicate deserialize(@Nullable JsonElement p_217975_0_) {
      if (p_217975_0_ != null && !p_217975_0_.isJsonNull()) {
         JsonObject jsonobject = JSONUtils.getJsonObject(p_217975_0_, "entity flags");
         Boolean obool = getBoolean(jsonobject, "is_on_fire");
         Boolean obool1 = getBoolean(jsonobject, "is_sneaking");
         Boolean obool2 = getBoolean(jsonobject, "is_sprinting");
         Boolean obool3 = getBoolean(jsonobject, "is_swimming");
         Boolean obool4 = getBoolean(jsonobject, "is_baby");
         return new EntityFlagsPredicate(obool, obool1, obool2, obool3, obool4);
      } else {
         return ALWAYS_TRUE;
      }
   }

   private void putBoolean(JsonObject p_217978_1_, String p_217978_2_, @Nullable Boolean p_217978_3_) {
      if (p_217978_3_ != null) {
         p_217978_1_.addProperty(p_217978_2_, p_217978_3_);
      }

   }

   public JsonElement serialize() {
      if (this == ALWAYS_TRUE) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject jsonobject = new JsonObject();
         this.putBoolean(jsonobject, "is_on_fire", this.onFire);
         this.putBoolean(jsonobject, "is_sneaking", this.field_226569_c_);
         this.putBoolean(jsonobject, "is_sprinting", this.sprinting);
         this.putBoolean(jsonobject, "is_swimming", this.swimming);
         this.putBoolean(jsonobject, "is_baby", this.baby);
         return jsonobject;
      }
   }

   public static class Builder {
      @Nullable
      private Boolean field_217969_a;
      @Nullable
      private Boolean field_226570_b_;
      @Nullable
      private Boolean field_217971_c;
      @Nullable
      private Boolean field_217972_d;
      @Nullable
      private Boolean field_217973_e;

      public static EntityFlagsPredicate.Builder create() {
         return new EntityFlagsPredicate.Builder();
      }

      public EntityFlagsPredicate.Builder onFire(@Nullable Boolean p_217968_1_) {
         this.field_217969_a = p_217968_1_;
         return this;
      }

      public EntityFlagsPredicate build() {
         return new EntityFlagsPredicate(this.field_217969_a, this.field_226570_b_, this.field_217971_c, this.field_217972_d, this.field_217973_e);
      }
   }
}
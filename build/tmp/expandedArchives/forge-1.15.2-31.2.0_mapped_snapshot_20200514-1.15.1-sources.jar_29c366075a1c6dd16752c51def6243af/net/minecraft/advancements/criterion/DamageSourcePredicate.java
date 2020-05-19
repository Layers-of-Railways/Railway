package net.minecraft.advancements.criterion;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.server.ServerWorld;

public class DamageSourcePredicate {
   public static final DamageSourcePredicate ANY = DamageSourcePredicate.Builder.damageType().build();
   private final Boolean isProjectile;
   private final Boolean isExplosion;
   private final Boolean bypassesArmor;
   private final Boolean bypassesInvulnerability;
   private final Boolean bypassesMagic;
   private final Boolean isFire;
   private final Boolean isMagic;
   private final Boolean field_217953_i;
   private final EntityPredicate directEntity;
   private final EntityPredicate sourceEntity;

   public DamageSourcePredicate(@Nullable Boolean p_i50810_1_, @Nullable Boolean p_i50810_2_, @Nullable Boolean p_i50810_3_, @Nullable Boolean p_i50810_4_, @Nullable Boolean p_i50810_5_, @Nullable Boolean p_i50810_6_, @Nullable Boolean p_i50810_7_, @Nullable Boolean p_i50810_8_, EntityPredicate p_i50810_9_, EntityPredicate p_i50810_10_) {
      this.isProjectile = p_i50810_1_;
      this.isExplosion = p_i50810_2_;
      this.bypassesArmor = p_i50810_3_;
      this.bypassesInvulnerability = p_i50810_4_;
      this.bypassesMagic = p_i50810_5_;
      this.isFire = p_i50810_6_;
      this.isMagic = p_i50810_7_;
      this.field_217953_i = p_i50810_8_;
      this.directEntity = p_i50810_9_;
      this.sourceEntity = p_i50810_10_;
   }

   public boolean test(ServerPlayerEntity player, DamageSource source) {
      return this.func_217952_a(player.getServerWorld(), player.getPositionVec(), source);
   }

   public boolean func_217952_a(ServerWorld p_217952_1_, Vec3d p_217952_2_, DamageSource p_217952_3_) {
      if (this == ANY) {
         return true;
      } else if (this.isProjectile != null && this.isProjectile != p_217952_3_.isProjectile()) {
         return false;
      } else if (this.isExplosion != null && this.isExplosion != p_217952_3_.isExplosion()) {
         return false;
      } else if (this.bypassesArmor != null && this.bypassesArmor != p_217952_3_.isUnblockable()) {
         return false;
      } else if (this.bypassesInvulnerability != null && this.bypassesInvulnerability != p_217952_3_.canHarmInCreative()) {
         return false;
      } else if (this.bypassesMagic != null && this.bypassesMagic != p_217952_3_.isDamageAbsolute()) {
         return false;
      } else if (this.isFire != null && this.isFire != p_217952_3_.isFireDamage()) {
         return false;
      } else if (this.isMagic != null && this.isMagic != p_217952_3_.isMagicDamage()) {
         return false;
      } else if (this.field_217953_i != null && this.field_217953_i != (p_217952_3_ == DamageSource.LIGHTNING_BOLT)) {
         return false;
      } else if (!this.directEntity.func_217993_a(p_217952_1_, p_217952_2_, p_217952_3_.getImmediateSource())) {
         return false;
      } else {
         return this.sourceEntity.func_217993_a(p_217952_1_, p_217952_2_, p_217952_3_.getTrueSource());
      }
   }

   public static DamageSourcePredicate deserialize(@Nullable JsonElement element) {
      if (element != null && !element.isJsonNull()) {
         JsonObject jsonobject = JSONUtils.getJsonObject(element, "damage type");
         Boolean obool = optionalBoolean(jsonobject, "is_projectile");
         Boolean obool1 = optionalBoolean(jsonobject, "is_explosion");
         Boolean obool2 = optionalBoolean(jsonobject, "bypasses_armor");
         Boolean obool3 = optionalBoolean(jsonobject, "bypasses_invulnerability");
         Boolean obool4 = optionalBoolean(jsonobject, "bypasses_magic");
         Boolean obool5 = optionalBoolean(jsonobject, "is_fire");
         Boolean obool6 = optionalBoolean(jsonobject, "is_magic");
         Boolean obool7 = optionalBoolean(jsonobject, "is_lightning");
         EntityPredicate entitypredicate = EntityPredicate.deserialize(jsonobject.get("direct_entity"));
         EntityPredicate entitypredicate1 = EntityPredicate.deserialize(jsonobject.get("source_entity"));
         return new DamageSourcePredicate(obool, obool1, obool2, obool3, obool4, obool5, obool6, obool7, entitypredicate, entitypredicate1);
      } else {
         return ANY;
      }
   }

   @Nullable
   private static Boolean optionalBoolean(JsonObject object, String memberName) {
      return object.has(memberName) ? JSONUtils.getBoolean(object, memberName) : null;
   }

   public JsonElement serialize() {
      if (this == ANY) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject jsonobject = new JsonObject();
         this.addProperty(jsonobject, "is_projectile", this.isProjectile);
         this.addProperty(jsonobject, "is_explosion", this.isExplosion);
         this.addProperty(jsonobject, "bypasses_armor", this.bypassesArmor);
         this.addProperty(jsonobject, "bypasses_invulnerability", this.bypassesInvulnerability);
         this.addProperty(jsonobject, "bypasses_magic", this.bypassesMagic);
         this.addProperty(jsonobject, "is_fire", this.isFire);
         this.addProperty(jsonobject, "is_magic", this.isMagic);
         this.addProperty(jsonobject, "is_lightning", this.field_217953_i);
         jsonobject.add("direct_entity", this.directEntity.serialize());
         jsonobject.add("source_entity", this.sourceEntity.serialize());
         return jsonobject;
      }
   }

   /**
    * Adds a property if the value is not null.
    */
   private void addProperty(JsonObject obj, String key, @Nullable Boolean value) {
      if (value != null) {
         obj.addProperty(key, value);
      }

   }

   public static class Builder {
      private Boolean isProjectile;
      private Boolean isExplosion;
      private Boolean bypassesArmor;
      private Boolean bypassesInvulnerability;
      private Boolean bypassesMagic;
      private Boolean isFire;
      private Boolean isMagic;
      private Boolean field_217951_h;
      private EntityPredicate directEntity = EntityPredicate.ANY;
      private EntityPredicate sourceEntity = EntityPredicate.ANY;

      public static DamageSourcePredicate.Builder damageType() {
         return new DamageSourcePredicate.Builder();
      }

      public DamageSourcePredicate.Builder isProjectile(Boolean p_203978_1_) {
         this.isProjectile = p_203978_1_;
         return this;
      }

      public DamageSourcePredicate.Builder func_217950_h(Boolean p_217950_1_) {
         this.field_217951_h = p_217950_1_;
         return this;
      }

      public DamageSourcePredicate.Builder direct(EntityPredicate.Builder p_203980_1_) {
         this.directEntity = p_203980_1_.build();
         return this;
      }

      public DamageSourcePredicate build() {
         return new DamageSourcePredicate(this.isProjectile, this.isExplosion, this.bypassesArmor, this.bypassesInvulnerability, this.bypassesMagic, this.isFire, this.isMagic, this.field_217951_h, this.directEntity, this.sourceEntity);
      }
   }
}
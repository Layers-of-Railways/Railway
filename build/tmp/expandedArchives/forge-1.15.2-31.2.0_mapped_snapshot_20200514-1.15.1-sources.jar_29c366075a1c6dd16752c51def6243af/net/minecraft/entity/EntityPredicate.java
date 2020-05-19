package net.minecraft.entity;

import java.util.function.Predicate;
import javax.annotation.Nullable;

public class EntityPredicate {
   public static final EntityPredicate DEFAULT = new EntityPredicate();
   private double distance = -1.0D;
   private boolean allowInvulnerable;
   private boolean friendlyFire;
   private boolean requireLineOfSight;
   private boolean skipAttackChecks;
   private boolean useVisibilityModifier = true;
   private Predicate<LivingEntity> customPredicate;

   public EntityPredicate setDistance(double distanceIn) {
      this.distance = distanceIn;
      return this;
   }

   public EntityPredicate allowInvulnerable() {
      this.allowInvulnerable = true;
      return this;
   }

   public EntityPredicate allowFriendlyFire() {
      this.friendlyFire = true;
      return this;
   }

   public EntityPredicate setLineOfSiteRequired() {
      this.requireLineOfSight = true;
      return this;
   }

   public EntityPredicate setSkipAttackChecks() {
      this.skipAttackChecks = true;
      return this;
   }

   public EntityPredicate setUseInvisibilityCheck() {
      this.useVisibilityModifier = false;
      return this;
   }

   public EntityPredicate setCustomPredicate(@Nullable Predicate<LivingEntity> p_221012_1_) {
      this.customPredicate = p_221012_1_;
      return this;
   }

   public boolean canTarget(@Nullable LivingEntity attacker, LivingEntity target) {
      if (attacker == target) {
         return false;
      } else if (target.isSpectator()) {
         return false;
      } else if (!target.isAlive()) {
         return false;
      } else if (!this.allowInvulnerable && target.isInvulnerable()) {
         return false;
      } else if (this.customPredicate != null && !this.customPredicate.test(target)) {
         return false;
      } else {
         if (attacker != null) {
            if (!this.skipAttackChecks) {
               if (!attacker.canAttack(target)) {
                  return false;
               }

               if (!attacker.canAttack(target.getType())) {
                  return false;
               }
            }

            if (!this.friendlyFire && attacker.isOnSameTeam(target)) {
               return false;
            }

            if (this.distance > 0.0D) {
               double d0 = this.useVisibilityModifier ? target.getVisibilityMultiplier(attacker) : 1.0D;
               double d1 = this.distance * d0;
               double d2 = attacker.getDistanceSq(target.getPosX(), target.getPosY(), target.getPosZ());
               if (d2 > d1 * d1) {
                  return false;
               }
            }

            if (!this.requireLineOfSight && attacker instanceof MobEntity && !((MobEntity)attacker).getEntitySenses().canSee(target)) {
               return false;
            }
         }

         return true;
      }
   }
}
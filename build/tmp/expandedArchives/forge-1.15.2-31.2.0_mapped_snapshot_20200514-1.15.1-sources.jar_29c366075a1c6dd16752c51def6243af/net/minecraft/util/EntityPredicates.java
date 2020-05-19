package net.minecraft.util;

import com.google.common.base.Predicates;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.Team;

public final class EntityPredicates {
   public static final Predicate<Entity> IS_ALIVE = Entity::isAlive;
   public static final Predicate<LivingEntity> IS_LIVING_ALIVE = LivingEntity::isAlive;
   /** Selects only entities which are neither ridden by anything nor ride on anything */
   public static final Predicate<Entity> IS_STANDALONE = (p_200821_0_) -> {
      return p_200821_0_.isAlive() && !p_200821_0_.isBeingRidden() && !p_200821_0_.isPassenger();
   };
   public static final Predicate<Entity> HAS_INVENTORY = (p_200822_0_) -> {
      return p_200822_0_ instanceof IInventory && p_200822_0_.isAlive();
   };
   public static final Predicate<Entity> CAN_AI_TARGET = (p_200824_0_) -> {
      return !(p_200824_0_ instanceof PlayerEntity) || !p_200824_0_.isSpectator() && !((PlayerEntity)p_200824_0_).isCreative();
   };
   /** Selects entities which are either not players or players that are not spectating */
   public static final Predicate<Entity> NOT_SPECTATING = (p_200818_0_) -> {
      return !p_200818_0_.isSpectator();
   };

   public static Predicate<Entity> withinRange(double x, double y, double z, double range) {
      double d0 = range * range;
      return (p_200819_8_) -> {
         return p_200819_8_ != null && p_200819_8_.getDistanceSq(x, y, z) <= d0;
      };
   }

   public static Predicate<Entity> pushableBy(Entity entityIn) {
      Team team = entityIn.getTeam();
      Team.CollisionRule team$collisionrule = team == null ? Team.CollisionRule.ALWAYS : team.getCollisionRule();
      return (Predicate<Entity>)(team$collisionrule == Team.CollisionRule.NEVER ? Predicates.alwaysFalse() : NOT_SPECTATING.and((p_210290_3_) -> {
         if (!p_210290_3_.canBePushed()) {
            return false;
         } else if (!entityIn.world.isRemote || p_210290_3_ instanceof PlayerEntity && ((PlayerEntity)p_210290_3_).isUser()) {
            Team team1 = p_210290_3_.getTeam();
            Team.CollisionRule team$collisionrule1 = team1 == null ? Team.CollisionRule.ALWAYS : team1.getCollisionRule();
            if (team$collisionrule1 == Team.CollisionRule.NEVER) {
               return false;
            } else {
               boolean flag = team != null && team.isSameTeam(team1);
               if ((team$collisionrule == Team.CollisionRule.PUSH_OWN_TEAM || team$collisionrule1 == Team.CollisionRule.PUSH_OWN_TEAM) && flag) {
                  return false;
               } else {
                  return team$collisionrule != Team.CollisionRule.PUSH_OTHER_TEAMS && team$collisionrule1 != Team.CollisionRule.PUSH_OTHER_TEAMS || flag;
               }
            }
         } else {
            return false;
         }
      }));
   }

   public static Predicate<Entity> notRiding(Entity entityIn) {
      return (p_210289_1_) -> {
         while(true) {
            if (p_210289_1_.isPassenger()) {
               p_210289_1_ = p_210289_1_.getRidingEntity();
               if (p_210289_1_ != entityIn) {
                  continue;
               }

               return false;
            }

            return true;
         }
      };
   }

   public static class ArmoredMob implements Predicate<Entity> {
      private final ItemStack armor;

      public ArmoredMob(ItemStack armor) {
         this.armor = armor;
      }

      public boolean test(@Nullable Entity p_test_1_) {
         if (!p_test_1_.isAlive()) {
            return false;
         } else if (!(p_test_1_ instanceof LivingEntity)) {
            return false;
         } else {
            LivingEntity livingentity = (LivingEntity)p_test_1_;
            return livingentity.canPickUpItem(this.armor);
         }
      }
   }
}
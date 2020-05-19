package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import java.util.List;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.AxisAlignedBB;

public class DefendVillageTargetGoal extends TargetGoal {
   private final IronGolemEntity irongolem;
   private LivingEntity villageAgressorTarget;
   private final EntityPredicate field_223190_c = (new EntityPredicate()).setDistance(64.0D);

   public DefendVillageTargetGoal(IronGolemEntity ironGolemIn) {
      super(ironGolemIn, false, true);
      this.irongolem = ironGolemIn;
      this.setMutexFlags(EnumSet.of(Goal.Flag.TARGET));
   }

   /**
    * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
    * method as well.
    */
   public boolean shouldExecute() {
      AxisAlignedBB axisalignedbb = this.irongolem.getBoundingBox().grow(10.0D, 8.0D, 10.0D);
      List<LivingEntity> list = this.irongolem.world.getTargettableEntitiesWithinAABB(VillagerEntity.class, this.field_223190_c, this.irongolem, axisalignedbb);
      List<PlayerEntity> list1 = this.irongolem.world.getTargettablePlayersWithinAABB(this.field_223190_c, this.irongolem, axisalignedbb);

      for(LivingEntity livingentity : list) {
         VillagerEntity villagerentity = (VillagerEntity)livingentity;

         for(PlayerEntity playerentity : list1) {
            int i = villagerentity.getPlayerReputation(playerentity);
            if (i <= -100) {
               this.villageAgressorTarget = playerentity;
            }
         }
      }

      if (this.villageAgressorTarget == null) {
         return false;
      } else {
         return !(this.villageAgressorTarget instanceof PlayerEntity) || !this.villageAgressorTarget.isSpectator() && !((PlayerEntity)this.villageAgressorTarget).isCreative();
      }
   }

   /**
    * Execute a one shot task or start executing a continuous task
    */
   public void startExecuting() {
      this.irongolem.setAttackTarget(this.villageAgressorTarget);
      super.startExecuting();
   }
}
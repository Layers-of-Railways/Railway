package net.minecraft.entity.monster;

import net.minecraft.entity.CreatureAttribute;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.OpenDoorGoal;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class AbstractIllagerEntity extends AbstractRaiderEntity {
   protected AbstractIllagerEntity(EntityType<? extends AbstractIllagerEntity> type, World worldIn) {
      super(type, worldIn);
   }

   protected void registerGoals() {
      super.registerGoals();
   }

   public CreatureAttribute getCreatureAttribute() {
      return CreatureAttribute.ILLAGER;
   }

   @OnlyIn(Dist.CLIENT)
   public AbstractIllagerEntity.ArmPose getArmPose() {
      return AbstractIllagerEntity.ArmPose.CROSSED;
   }

   @OnlyIn(Dist.CLIENT)
   public static enum ArmPose {
      CROSSED,
      ATTACKING,
      SPELLCASTING,
      BOW_AND_ARROW,
      CROSSBOW_HOLD,
      CROSSBOW_CHARGE,
      CELEBRATING,
      NEUTRAL;
   }

   public class RaidOpenDoorGoal extends OpenDoorGoal {
      public RaidOpenDoorGoal(AbstractRaiderEntity p_i51284_2_) {
         super(p_i51284_2_, false);
      }

      /**
       * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
       * method as well.
       */
      public boolean shouldExecute() {
         return super.shouldExecute() && AbstractIllagerEntity.this.isRaidActive();
      }
   }
}
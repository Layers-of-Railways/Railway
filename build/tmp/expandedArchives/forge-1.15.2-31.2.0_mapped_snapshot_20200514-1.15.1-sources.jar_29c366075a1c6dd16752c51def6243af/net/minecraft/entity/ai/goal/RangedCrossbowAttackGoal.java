package net.minecraft.entity.ai.goal;

import java.util.EnumSet;
import net.minecraft.entity.ICrossbowUser;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class RangedCrossbowAttackGoal<T extends MonsterEntity & IRangedAttackMob & ICrossbowUser> extends Goal {
   private final T field_220748_a;
   private RangedCrossbowAttackGoal.CrossbowState field_220749_b = RangedCrossbowAttackGoal.CrossbowState.UNCHARGED;
   private final double field_220750_c;
   private final float field_220751_d;
   private int field_220752_e;
   private int field_220753_f;

   public RangedCrossbowAttackGoal(T p_i50322_1_, double p_i50322_2_, float p_i50322_4_) {
      this.field_220748_a = p_i50322_1_;
      this.field_220750_c = p_i50322_2_;
      this.field_220751_d = p_i50322_4_ * p_i50322_4_;
      this.setMutexFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
   }

   /**
    * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
    * method as well.
    */
   public boolean shouldExecute() {
      return this.func_220746_h() && this.func_220745_g();
   }

   private boolean func_220745_g() {
      return this.field_220748_a.isHolding(Items.CROSSBOW);
   }

   /**
    * Returns whether an in-progress EntityAIBase should continue executing
    */
   public boolean shouldContinueExecuting() {
      return this.func_220746_h() && (this.shouldExecute() || !this.field_220748_a.getNavigator().noPath()) && this.func_220745_g();
   }

   private boolean func_220746_h() {
      return this.field_220748_a.getAttackTarget() != null && this.field_220748_a.getAttackTarget().isAlive();
   }

   /**
    * Reset the task's internal state. Called when this task is interrupted by another one
    */
   public void resetTask() {
      super.resetTask();
      this.field_220748_a.setAggroed(false);
      this.field_220748_a.setAttackTarget((LivingEntity)null);
      this.field_220752_e = 0;
      if (this.field_220748_a.isHandActive()) {
         this.field_220748_a.resetActiveHand();
         ((ICrossbowUser)this.field_220748_a).setCharging(false);
         CrossbowItem.setCharged(this.field_220748_a.getActiveItemStack(), false);
      }

   }

   /**
    * Keep ticking a continuous task that has already been started
    */
   public void tick() {
      LivingEntity livingentity = this.field_220748_a.getAttackTarget();
      if (livingentity != null) {
         boolean flag = this.field_220748_a.getEntitySenses().canSee(livingentity);
         boolean flag1 = this.field_220752_e > 0;
         if (flag != flag1) {
            this.field_220752_e = 0;
         }

         if (flag) {
            ++this.field_220752_e;
         } else {
            --this.field_220752_e;
         }

         double d0 = this.field_220748_a.getDistanceSq(livingentity);
         boolean flag2 = (d0 > (double)this.field_220751_d || this.field_220752_e < 5) && this.field_220753_f == 0;
         if (flag2) {
            this.field_220748_a.getNavigator().tryMoveToEntityLiving(livingentity, this.func_220747_j() ? this.field_220750_c : this.field_220750_c * 0.5D);
         } else {
            this.field_220748_a.getNavigator().clearPath();
         }

         this.field_220748_a.getLookController().setLookPositionWithEntity(livingentity, 30.0F, 30.0F);
         if (this.field_220749_b == RangedCrossbowAttackGoal.CrossbowState.UNCHARGED) {
            if (!flag2) {
               this.field_220748_a.setActiveHand(ProjectileHelper.getHandWith(this.field_220748_a, Items.CROSSBOW));
               this.field_220749_b = RangedCrossbowAttackGoal.CrossbowState.CHARGING;
               ((ICrossbowUser)this.field_220748_a).setCharging(true);
            }
         } else if (this.field_220749_b == RangedCrossbowAttackGoal.CrossbowState.CHARGING) {
            if (!this.field_220748_a.isHandActive()) {
               this.field_220749_b = RangedCrossbowAttackGoal.CrossbowState.UNCHARGED;
            }

            int i = this.field_220748_a.getItemInUseMaxCount();
            ItemStack itemstack = this.field_220748_a.getActiveItemStack();
            if (i >= CrossbowItem.getChargeTime(itemstack)) {
               this.field_220748_a.stopActiveHand();
               this.field_220749_b = RangedCrossbowAttackGoal.CrossbowState.CHARGED;
               this.field_220753_f = 20 + this.field_220748_a.getRNG().nextInt(20);
               ((ICrossbowUser)this.field_220748_a).setCharging(false);
            }
         } else if (this.field_220749_b == RangedCrossbowAttackGoal.CrossbowState.CHARGED) {
            --this.field_220753_f;
            if (this.field_220753_f == 0) {
               this.field_220749_b = RangedCrossbowAttackGoal.CrossbowState.READY_TO_ATTACK;
            }
         } else if (this.field_220749_b == RangedCrossbowAttackGoal.CrossbowState.READY_TO_ATTACK && flag) {
            ((IRangedAttackMob)this.field_220748_a).attackEntityWithRangedAttack(livingentity, 1.0F);
            ItemStack itemstack1 = this.field_220748_a.getHeldItem(ProjectileHelper.getHandWith(this.field_220748_a, Items.CROSSBOW));
            CrossbowItem.setCharged(itemstack1, false);
            this.field_220749_b = RangedCrossbowAttackGoal.CrossbowState.UNCHARGED;
         }

      }
   }

   private boolean func_220747_j() {
      return this.field_220749_b == RangedCrossbowAttackGoal.CrossbowState.UNCHARGED;
   }

   static enum CrossbowState {
      UNCHARGED,
      CHARGING,
      CHARGED,
      READY_TO_ATTACK;
   }
}
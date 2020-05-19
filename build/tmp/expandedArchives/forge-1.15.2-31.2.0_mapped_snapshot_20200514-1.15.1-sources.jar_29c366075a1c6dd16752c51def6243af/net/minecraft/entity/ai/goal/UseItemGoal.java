package net.minecraft.entity.ai.goal;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.entity.MobEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;

public class UseItemGoal<T extends MobEntity> extends Goal {
   private final T field_220766_a;
   private final ItemStack field_220767_b;
   private final Predicate<? super T> field_220768_c;
   private final SoundEvent field_220769_d;

   public UseItemGoal(T p_i50319_1_, ItemStack p_i50319_2_, @Nullable SoundEvent p_i50319_3_, Predicate<? super T> p_i50319_4_) {
      this.field_220766_a = p_i50319_1_;
      this.field_220767_b = p_i50319_2_;
      this.field_220769_d = p_i50319_3_;
      this.field_220768_c = p_i50319_4_;
   }

   /**
    * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
    * method as well.
    */
   public boolean shouldExecute() {
      return this.field_220768_c.test(this.field_220766_a);
   }

   /**
    * Returns whether an in-progress EntityAIBase should continue executing
    */
   public boolean shouldContinueExecuting() {
      return this.field_220766_a.isHandActive();
   }

   /**
    * Execute a one shot task or start executing a continuous task
    */
   public void startExecuting() {
      this.field_220766_a.setItemStackToSlot(EquipmentSlotType.MAINHAND, this.field_220767_b.copy());
      this.field_220766_a.setActiveHand(Hand.MAIN_HAND);
   }

   /**
    * Reset the task's internal state. Called when this task is interrupted by another one
    */
   public void resetTask() {
      this.field_220766_a.setItemStackToSlot(EquipmentSlotType.MAINHAND, ItemStack.EMPTY);
      if (this.field_220769_d != null) {
         this.field_220766_a.playSound(this.field_220769_d, 1.0F, this.field_220766_a.getRNG().nextFloat() * 0.2F + 0.9F);
      }

   }
}
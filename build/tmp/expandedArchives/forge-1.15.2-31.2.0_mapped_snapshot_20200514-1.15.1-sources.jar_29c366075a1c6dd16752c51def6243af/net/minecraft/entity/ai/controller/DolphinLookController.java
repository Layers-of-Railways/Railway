package net.minecraft.entity.ai.controller;

import net.minecraft.entity.MobEntity;
import net.minecraft.util.math.MathHelper;

public class DolphinLookController extends LookController {
   private final int field_205139_h;

   public DolphinLookController(MobEntity p_i48942_1_, int p_i48942_2_) {
      super(p_i48942_1_);
      this.field_205139_h = p_i48942_2_;
   }

   /**
    * Updates look
    */
   public void tick() {
      if (this.isLooking) {
         this.isLooking = false;
         this.mob.rotationYawHead = this.clampedRotate(this.mob.rotationYawHead, this.getTargetYaw() + 20.0F, this.deltaLookYaw);
         this.mob.rotationPitch = this.clampedRotate(this.mob.rotationPitch, this.getTargetPitch() + 10.0F, this.deltaLookPitch);
      } else {
         if (this.mob.getNavigator().noPath()) {
            this.mob.rotationPitch = this.clampedRotate(this.mob.rotationPitch, 0.0F, 5.0F);
         }

         this.mob.rotationYawHead = this.clampedRotate(this.mob.rotationYawHead, this.mob.renderYawOffset, this.deltaLookYaw);
      }

      float f = MathHelper.wrapDegrees(this.mob.rotationYawHead - this.mob.renderYawOffset);
      if (f < (float)(-this.field_205139_h)) {
         this.mob.renderYawOffset -= 4.0F;
      } else if (f > (float)this.field_205139_h) {
         this.mob.renderYawOffset += 4.0F;
      }

   }
}
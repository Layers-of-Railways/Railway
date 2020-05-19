package net.minecraft.entity.ai.controller;

import net.minecraft.entity.MobEntity;
import net.minecraft.util.math.MathHelper;

public class BodyController {
   private final MobEntity mob;
   private int rotationTickCounter;
   private float prevRenderYawHead;

   public BodyController(MobEntity mob) {
      this.mob = mob;
   }

   /**
    * Update the Head and Body rendenring angles
    */
   public void updateRenderAngles() {
      if (this.func_220662_f()) {
         this.mob.renderYawOffset = this.mob.rotationYaw;
         this.func_220664_c();
         this.prevRenderYawHead = this.mob.rotationYawHead;
         this.rotationTickCounter = 0;
      } else {
         if (this.func_220661_e()) {
            if (Math.abs(this.mob.rotationYawHead - this.prevRenderYawHead) > 15.0F) {
               this.rotationTickCounter = 0;
               this.prevRenderYawHead = this.mob.rotationYawHead;
               this.func_220663_b();
            } else {
               ++this.rotationTickCounter;
               if (this.rotationTickCounter > 10) {
                  this.func_220665_d();
               }
            }
         }

      }
   }

   private void func_220663_b() {
      this.mob.renderYawOffset = MathHelper.func_219800_b(this.mob.renderYawOffset, this.mob.rotationYawHead, (float)this.mob.getHorizontalFaceSpeed());
   }

   private void func_220664_c() {
      this.mob.rotationYawHead = MathHelper.func_219800_b(this.mob.rotationYawHead, this.mob.renderYawOffset, (float)this.mob.getHorizontalFaceSpeed());
   }

   private void func_220665_d() {
      int i = this.rotationTickCounter - 10;
      float f = MathHelper.clamp((float)i / 10.0F, 0.0F, 1.0F);
      float f1 = (float)this.mob.getHorizontalFaceSpeed() * (1.0F - f);
      this.mob.renderYawOffset = MathHelper.func_219800_b(this.mob.renderYawOffset, this.mob.rotationYawHead, f1);
   }

   private boolean func_220661_e() {
      return this.mob.getPassengers().isEmpty() || !(this.mob.getPassengers().get(0) instanceof MobEntity);
   }

   private boolean func_220662_f() {
      double d0 = this.mob.getPosX() - this.mob.prevPosX;
      double d1 = this.mob.getPosZ() - this.mob.prevPosZ;
      return d0 * d0 + d1 * d1 > (double)2.5000003E-7F;
   }
}
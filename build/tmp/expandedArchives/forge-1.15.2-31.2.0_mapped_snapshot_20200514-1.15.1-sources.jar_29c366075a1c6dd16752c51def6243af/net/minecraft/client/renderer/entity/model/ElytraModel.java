package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ElytraModel<T extends LivingEntity> extends AgeableModel<T> {
   private final ModelRenderer rightWing;
   private final ModelRenderer leftWing = new ModelRenderer(this, 22, 0);

   public ElytraModel() {
      this.leftWing.addBox(-10.0F, 0.0F, 0.0F, 10.0F, 20.0F, 2.0F, 1.0F);
      this.rightWing = new ModelRenderer(this, 22, 0);
      this.rightWing.mirror = true;
      this.rightWing.addBox(0.0F, 0.0F, 0.0F, 10.0F, 20.0F, 2.0F, 1.0F);
   }

   protected Iterable<ModelRenderer> getHeadParts() {
      return ImmutableList.of();
   }

   protected Iterable<ModelRenderer> getBodyParts() {
      return ImmutableList.of(this.leftWing, this.rightWing);
   }

   /**
    * Sets this entity's model rotation angles
    */
   public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
      float f = 0.2617994F;
      float f1 = -0.2617994F;
      float f2 = 0.0F;
      float f3 = 0.0F;
      if (entityIn.isElytraFlying()) {
         float f4 = 1.0F;
         Vec3d vec3d = entityIn.getMotion();
         if (vec3d.y < 0.0D) {
            Vec3d vec3d1 = vec3d.normalize();
            f4 = 1.0F - (float)Math.pow(-vec3d1.y, 1.5D);
         }

         f = f4 * 0.34906584F + (1.0F - f4) * f;
         f1 = f4 * (-(float)Math.PI / 2F) + (1.0F - f4) * f1;
      } else if (entityIn.isCrouching()) {
         f = 0.6981317F;
         f1 = (-(float)Math.PI / 4F);
         f2 = 3.0F;
         f3 = 0.08726646F;
      }

      this.leftWing.rotationPointX = 5.0F;
      this.leftWing.rotationPointY = f2;
      if (entityIn instanceof AbstractClientPlayerEntity) {
         AbstractClientPlayerEntity abstractclientplayerentity = (AbstractClientPlayerEntity)entityIn;
         abstractclientplayerentity.rotateElytraX = (float)((double)abstractclientplayerentity.rotateElytraX + (double)(f - abstractclientplayerentity.rotateElytraX) * 0.1D);
         abstractclientplayerentity.rotateElytraY = (float)((double)abstractclientplayerentity.rotateElytraY + (double)(f3 - abstractclientplayerentity.rotateElytraY) * 0.1D);
         abstractclientplayerentity.rotateElytraZ = (float)((double)abstractclientplayerentity.rotateElytraZ + (double)(f1 - abstractclientplayerentity.rotateElytraZ) * 0.1D);
         this.leftWing.rotateAngleX = abstractclientplayerentity.rotateElytraX;
         this.leftWing.rotateAngleY = abstractclientplayerentity.rotateElytraY;
         this.leftWing.rotateAngleZ = abstractclientplayerentity.rotateElytraZ;
      } else {
         this.leftWing.rotateAngleX = f;
         this.leftWing.rotateAngleZ = f1;
         this.leftWing.rotateAngleY = f3;
      }

      this.rightWing.rotationPointX = -this.leftWing.rotationPointX;
      this.rightWing.rotateAngleY = -this.leftWing.rotateAngleY;
      this.rightWing.rotationPointY = this.leftWing.rotationPointY;
      this.rightWing.rotateAngleX = this.leftWing.rotateAngleX;
      this.rightWing.rotateAngleZ = -this.leftWing.rotateAngleZ;
   }
}
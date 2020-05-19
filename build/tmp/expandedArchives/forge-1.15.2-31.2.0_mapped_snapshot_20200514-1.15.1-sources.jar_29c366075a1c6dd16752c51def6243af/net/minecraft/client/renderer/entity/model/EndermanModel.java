package net.minecraft.client.renderer.entity.model;

import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EndermanModel<T extends LivingEntity> extends BipedModel<T> {
   public boolean isCarrying;
   public boolean isAttacking;

   public EndermanModel(float scale) {
      super(0.0F, -14.0F, 64, 32);
      float f = -14.0F;
      this.bipedHeadwear = new ModelRenderer(this, 0, 16);
      this.bipedHeadwear.addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, scale - 0.5F);
      this.bipedHeadwear.setRotationPoint(0.0F, -14.0F, 0.0F);
      this.bipedBody = new ModelRenderer(this, 32, 16);
      this.bipedBody.addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, scale);
      this.bipedBody.setRotationPoint(0.0F, -14.0F, 0.0F);
      this.bipedRightArm = new ModelRenderer(this, 56, 0);
      this.bipedRightArm.addBox(-1.0F, -2.0F, -1.0F, 2.0F, 30.0F, 2.0F, scale);
      this.bipedRightArm.setRotationPoint(-3.0F, -12.0F, 0.0F);
      this.bipedLeftArm = new ModelRenderer(this, 56, 0);
      this.bipedLeftArm.mirror = true;
      this.bipedLeftArm.addBox(-1.0F, -2.0F, -1.0F, 2.0F, 30.0F, 2.0F, scale);
      this.bipedLeftArm.setRotationPoint(5.0F, -12.0F, 0.0F);
      this.bipedRightLeg = new ModelRenderer(this, 56, 0);
      this.bipedRightLeg.addBox(-1.0F, 0.0F, -1.0F, 2.0F, 30.0F, 2.0F, scale);
      this.bipedRightLeg.setRotationPoint(-2.0F, -2.0F, 0.0F);
      this.bipedLeftLeg = new ModelRenderer(this, 56, 0);
      this.bipedLeftLeg.mirror = true;
      this.bipedLeftLeg.addBox(-1.0F, 0.0F, -1.0F, 2.0F, 30.0F, 2.0F, scale);
      this.bipedLeftLeg.setRotationPoint(2.0F, -2.0F, 0.0F);
   }

   /**
    * Sets this entity's model rotation angles
    */
   public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
      super.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
      this.bipedHead.showModel = true;
      float f = -14.0F;
      this.bipedBody.rotateAngleX = 0.0F;
      this.bipedBody.rotationPointY = -14.0F;
      this.bipedBody.rotationPointZ = -0.0F;
      this.bipedRightLeg.rotateAngleX -= 0.0F;
      this.bipedLeftLeg.rotateAngleX -= 0.0F;
      this.bipedRightArm.rotateAngleX = (float)((double)this.bipedRightArm.rotateAngleX * 0.5D);
      this.bipedLeftArm.rotateAngleX = (float)((double)this.bipedLeftArm.rotateAngleX * 0.5D);
      this.bipedRightLeg.rotateAngleX = (float)((double)this.bipedRightLeg.rotateAngleX * 0.5D);
      this.bipedLeftLeg.rotateAngleX = (float)((double)this.bipedLeftLeg.rotateAngleX * 0.5D);
      float f1 = 0.4F;
      if (this.bipedRightArm.rotateAngleX > 0.4F) {
         this.bipedRightArm.rotateAngleX = 0.4F;
      }

      if (this.bipedLeftArm.rotateAngleX > 0.4F) {
         this.bipedLeftArm.rotateAngleX = 0.4F;
      }

      if (this.bipedRightArm.rotateAngleX < -0.4F) {
         this.bipedRightArm.rotateAngleX = -0.4F;
      }

      if (this.bipedLeftArm.rotateAngleX < -0.4F) {
         this.bipedLeftArm.rotateAngleX = -0.4F;
      }

      if (this.bipedRightLeg.rotateAngleX > 0.4F) {
         this.bipedRightLeg.rotateAngleX = 0.4F;
      }

      if (this.bipedLeftLeg.rotateAngleX > 0.4F) {
         this.bipedLeftLeg.rotateAngleX = 0.4F;
      }

      if (this.bipedRightLeg.rotateAngleX < -0.4F) {
         this.bipedRightLeg.rotateAngleX = -0.4F;
      }

      if (this.bipedLeftLeg.rotateAngleX < -0.4F) {
         this.bipedLeftLeg.rotateAngleX = -0.4F;
      }

      if (this.isCarrying) {
         this.bipedRightArm.rotateAngleX = -0.5F;
         this.bipedLeftArm.rotateAngleX = -0.5F;
         this.bipedRightArm.rotateAngleZ = 0.05F;
         this.bipedLeftArm.rotateAngleZ = -0.05F;
      }

      this.bipedRightArm.rotationPointZ = 0.0F;
      this.bipedLeftArm.rotationPointZ = 0.0F;
      this.bipedRightLeg.rotationPointZ = 0.0F;
      this.bipedLeftLeg.rotationPointZ = 0.0F;
      this.bipedRightLeg.rotationPointY = -5.0F;
      this.bipedLeftLeg.rotationPointY = -5.0F;
      this.bipedHead.rotationPointZ = -0.0F;
      this.bipedHead.rotationPointY = -13.0F;
      this.bipedHeadwear.rotationPointX = this.bipedHead.rotationPointX;
      this.bipedHeadwear.rotationPointY = this.bipedHead.rotationPointY;
      this.bipedHeadwear.rotationPointZ = this.bipedHead.rotationPointZ;
      this.bipedHeadwear.rotateAngleX = this.bipedHead.rotateAngleX;
      this.bipedHeadwear.rotateAngleY = this.bipedHead.rotateAngleY;
      this.bipedHeadwear.rotateAngleZ = this.bipedHead.rotateAngleZ;
      if (this.isAttacking) {
         float f2 = 1.0F;
         this.bipedHead.rotationPointY -= 5.0F;
      }

      float f3 = -14.0F;
      this.bipedRightArm.setRotationPoint(-5.0F, -12.0F, 0.0F);
      this.bipedLeftArm.setRotationPoint(5.0F, -12.0F, 0.0F);
   }
}
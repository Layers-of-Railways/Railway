package net.minecraft.client.renderer.entity.model;

import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.passive.PolarBearEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PolarBearModel<T extends PolarBearEntity> extends QuadrupedModel<T> {
   public PolarBearModel() {
      super(12, 0.0F, true, 16.0F, 4.0F, 2.25F, 2.0F, 24);
      this.textureWidth = 128;
      this.textureHeight = 64;
      this.headModel = new ModelRenderer(this, 0, 0);
      this.headModel.addBox(-3.5F, -3.0F, -3.0F, 7.0F, 7.0F, 7.0F, 0.0F);
      this.headModel.setRotationPoint(0.0F, 10.0F, -16.0F);
      this.headModel.setTextureOffset(0, 44).addBox(-2.5F, 1.0F, -6.0F, 5.0F, 3.0F, 3.0F, 0.0F);
      this.headModel.setTextureOffset(26, 0).addBox(-4.5F, -4.0F, -1.0F, 2.0F, 2.0F, 1.0F, 0.0F);
      ModelRenderer modelrenderer = this.headModel.setTextureOffset(26, 0);
      modelrenderer.mirror = true;
      modelrenderer.addBox(2.5F, -4.0F, -1.0F, 2.0F, 2.0F, 1.0F, 0.0F);
      this.body = new ModelRenderer(this);
      this.body.setTextureOffset(0, 19).addBox(-5.0F, -13.0F, -7.0F, 14.0F, 14.0F, 11.0F, 0.0F);
      this.body.setTextureOffset(39, 0).addBox(-4.0F, -25.0F, -7.0F, 12.0F, 12.0F, 10.0F, 0.0F);
      this.body.setRotationPoint(-2.0F, 9.0F, 12.0F);
      int i = 10;
      this.legBackRight = new ModelRenderer(this, 50, 22);
      this.legBackRight.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 10.0F, 8.0F, 0.0F);
      this.legBackRight.setRotationPoint(-3.5F, 14.0F, 6.0F);
      this.legBackLeft = new ModelRenderer(this, 50, 22);
      this.legBackLeft.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 10.0F, 8.0F, 0.0F);
      this.legBackLeft.setRotationPoint(3.5F, 14.0F, 6.0F);
      this.legFrontRight = new ModelRenderer(this, 50, 40);
      this.legFrontRight.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 10.0F, 6.0F, 0.0F);
      this.legFrontRight.setRotationPoint(-2.5F, 14.0F, -7.0F);
      this.legFrontLeft = new ModelRenderer(this, 50, 40);
      this.legFrontLeft.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 10.0F, 6.0F, 0.0F);
      this.legFrontLeft.setRotationPoint(2.5F, 14.0F, -7.0F);
      --this.legBackRight.rotationPointX;
      ++this.legBackLeft.rotationPointX;
      this.legBackRight.rotationPointZ += 0.0F;
      this.legBackLeft.rotationPointZ += 0.0F;
      --this.legFrontRight.rotationPointX;
      ++this.legFrontLeft.rotationPointX;
      --this.legFrontRight.rotationPointZ;
      --this.legFrontLeft.rotationPointZ;
   }

   /**
    * Sets this entity's model rotation angles
    */
   public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
      super.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
      float f = ageInTicks - (float)entityIn.ticksExisted;
      float f1 = entityIn.getStandingAnimationScale(f);
      f1 = f1 * f1;
      float f2 = 1.0F - f1;
      this.body.rotateAngleX = ((float)Math.PI / 2F) - f1 * (float)Math.PI * 0.35F;
      this.body.rotationPointY = 9.0F * f2 + 11.0F * f1;
      this.legFrontRight.rotationPointY = 14.0F * f2 - 6.0F * f1;
      this.legFrontRight.rotationPointZ = -8.0F * f2 - 4.0F * f1;
      this.legFrontRight.rotateAngleX -= f1 * (float)Math.PI * 0.45F;
      this.legFrontLeft.rotationPointY = this.legFrontRight.rotationPointY;
      this.legFrontLeft.rotationPointZ = this.legFrontRight.rotationPointZ;
      this.legFrontLeft.rotateAngleX -= f1 * (float)Math.PI * 0.45F;
      if (this.isChild) {
         this.headModel.rotationPointY = 10.0F * f2 - 9.0F * f1;
         this.headModel.rotationPointZ = -16.0F * f2 - 7.0F * f1;
      } else {
         this.headModel.rotationPointY = 10.0F * f2 - 14.0F * f1;
         this.headModel.rotationPointZ = -16.0F * f2 - 3.0F * f1;
      }

      this.headModel.rotateAngleX += f1 * (float)Math.PI * 0.15F;
   }
}
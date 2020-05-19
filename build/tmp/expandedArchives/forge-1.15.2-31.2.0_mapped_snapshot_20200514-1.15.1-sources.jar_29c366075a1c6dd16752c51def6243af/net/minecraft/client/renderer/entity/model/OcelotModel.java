package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class OcelotModel<T extends Entity> extends AgeableModel<T> {
   protected final ModelRenderer ocelotBackLeftLeg;
   protected final ModelRenderer ocelotBackRightLeg;
   protected final ModelRenderer ocelotFrontLeftLeg;
   protected final ModelRenderer ocelotFrontRightLeg;
   protected final ModelRenderer ocelotTail;
   protected final ModelRenderer ocelotTail2;
   protected final ModelRenderer ocelotHead;
   protected final ModelRenderer ocelotBody;
   protected int state = 1;

   public OcelotModel(float p_i51064_1_) {
      super(true, 10.0F, 4.0F);
      this.ocelotHead = new ModelRenderer(this);
      this.ocelotHead.addBox("main", -2.5F, -2.0F, -3.0F, 5, 4, 5, p_i51064_1_, 0, 0);
      this.ocelotHead.addBox("nose", -1.5F, 0.0F, -4.0F, 3, 2, 2, p_i51064_1_, 0, 24);
      this.ocelotHead.addBox("ear1", -2.0F, -3.0F, 0.0F, 1, 1, 2, p_i51064_1_, 0, 10);
      this.ocelotHead.addBox("ear2", 1.0F, -3.0F, 0.0F, 1, 1, 2, p_i51064_1_, 6, 10);
      this.ocelotHead.setRotationPoint(0.0F, 15.0F, -9.0F);
      this.ocelotBody = new ModelRenderer(this, 20, 0);
      this.ocelotBody.addBox(-2.0F, 3.0F, -8.0F, 4.0F, 16.0F, 6.0F, p_i51064_1_);
      this.ocelotBody.setRotationPoint(0.0F, 12.0F, -10.0F);
      this.ocelotTail = new ModelRenderer(this, 0, 15);
      this.ocelotTail.addBox(-0.5F, 0.0F, 0.0F, 1.0F, 8.0F, 1.0F, p_i51064_1_);
      this.ocelotTail.rotateAngleX = 0.9F;
      this.ocelotTail.setRotationPoint(0.0F, 15.0F, 8.0F);
      this.ocelotTail2 = new ModelRenderer(this, 4, 15);
      this.ocelotTail2.addBox(-0.5F, 0.0F, 0.0F, 1.0F, 8.0F, 1.0F, p_i51064_1_);
      this.ocelotTail2.setRotationPoint(0.0F, 20.0F, 14.0F);
      this.ocelotBackLeftLeg = new ModelRenderer(this, 8, 13);
      this.ocelotBackLeftLeg.addBox(-1.0F, 0.0F, 1.0F, 2.0F, 6.0F, 2.0F, p_i51064_1_);
      this.ocelotBackLeftLeg.setRotationPoint(1.1F, 18.0F, 5.0F);
      this.ocelotBackRightLeg = new ModelRenderer(this, 8, 13);
      this.ocelotBackRightLeg.addBox(-1.0F, 0.0F, 1.0F, 2.0F, 6.0F, 2.0F, p_i51064_1_);
      this.ocelotBackRightLeg.setRotationPoint(-1.1F, 18.0F, 5.0F);
      this.ocelotFrontLeftLeg = new ModelRenderer(this, 40, 0);
      this.ocelotFrontLeftLeg.addBox(-1.0F, 0.0F, 0.0F, 2.0F, 10.0F, 2.0F, p_i51064_1_);
      this.ocelotFrontLeftLeg.setRotationPoint(1.2F, 14.1F, -5.0F);
      this.ocelotFrontRightLeg = new ModelRenderer(this, 40, 0);
      this.ocelotFrontRightLeg.addBox(-1.0F, 0.0F, 0.0F, 2.0F, 10.0F, 2.0F, p_i51064_1_);
      this.ocelotFrontRightLeg.setRotationPoint(-1.2F, 14.1F, -5.0F);
   }

   protected Iterable<ModelRenderer> getHeadParts() {
      return ImmutableList.of(this.ocelotHead);
   }

   protected Iterable<ModelRenderer> getBodyParts() {
      return ImmutableList.of(this.ocelotBody, this.ocelotBackLeftLeg, this.ocelotBackRightLeg, this.ocelotFrontLeftLeg, this.ocelotFrontRightLeg, this.ocelotTail, this.ocelotTail2);
   }

   /**
    * Sets this entity's model rotation angles
    */
   public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
      this.ocelotHead.rotateAngleX = headPitch * ((float)Math.PI / 180F);
      this.ocelotHead.rotateAngleY = netHeadYaw * ((float)Math.PI / 180F);
      if (this.state != 3) {
         this.ocelotBody.rotateAngleX = ((float)Math.PI / 2F);
         if (this.state == 2) {
            this.ocelotBackLeftLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * limbSwingAmount;
            this.ocelotBackRightLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + 0.3F) * limbSwingAmount;
            this.ocelotFrontLeftLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI + 0.3F) * limbSwingAmount;
            this.ocelotFrontRightLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * limbSwingAmount;
            this.ocelotTail2.rotateAngleX = 1.7278761F + ((float)Math.PI / 10F) * MathHelper.cos(limbSwing) * limbSwingAmount;
         } else {
            this.ocelotBackLeftLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * limbSwingAmount;
            this.ocelotBackRightLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * limbSwingAmount;
            this.ocelotFrontLeftLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * limbSwingAmount;
            this.ocelotFrontRightLeg.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * limbSwingAmount;
            if (this.state == 1) {
               this.ocelotTail2.rotateAngleX = 1.7278761F + ((float)Math.PI / 4F) * MathHelper.cos(limbSwing) * limbSwingAmount;
            } else {
               this.ocelotTail2.rotateAngleX = 1.7278761F + 0.47123894F * MathHelper.cos(limbSwing) * limbSwingAmount;
            }
         }
      }

   }

   public void setLivingAnimations(T entityIn, float limbSwing, float limbSwingAmount, float partialTick) {
      this.ocelotBody.rotationPointY = 12.0F;
      this.ocelotBody.rotationPointZ = -10.0F;
      this.ocelotHead.rotationPointY = 15.0F;
      this.ocelotHead.rotationPointZ = -9.0F;
      this.ocelotTail.rotationPointY = 15.0F;
      this.ocelotTail.rotationPointZ = 8.0F;
      this.ocelotTail2.rotationPointY = 20.0F;
      this.ocelotTail2.rotationPointZ = 14.0F;
      this.ocelotFrontLeftLeg.rotationPointY = 14.1F;
      this.ocelotFrontLeftLeg.rotationPointZ = -5.0F;
      this.ocelotFrontRightLeg.rotationPointY = 14.1F;
      this.ocelotFrontRightLeg.rotationPointZ = -5.0F;
      this.ocelotBackLeftLeg.rotationPointY = 18.0F;
      this.ocelotBackLeftLeg.rotationPointZ = 5.0F;
      this.ocelotBackRightLeg.rotationPointY = 18.0F;
      this.ocelotBackRightLeg.rotationPointZ = 5.0F;
      this.ocelotTail.rotateAngleX = 0.9F;
      if (entityIn.isCrouching()) {
         ++this.ocelotBody.rotationPointY;
         this.ocelotHead.rotationPointY += 2.0F;
         ++this.ocelotTail.rotationPointY;
         this.ocelotTail2.rotationPointY += -4.0F;
         this.ocelotTail2.rotationPointZ += 2.0F;
         this.ocelotTail.rotateAngleX = ((float)Math.PI / 2F);
         this.ocelotTail2.rotateAngleX = ((float)Math.PI / 2F);
         this.state = 0;
      } else if (entityIn.isSprinting()) {
         this.ocelotTail2.rotationPointY = this.ocelotTail.rotationPointY;
         this.ocelotTail2.rotationPointZ += 2.0F;
         this.ocelotTail.rotateAngleX = ((float)Math.PI / 2F);
         this.ocelotTail2.rotateAngleX = ((float)Math.PI / 2F);
         this.state = 2;
      } else {
         this.state = 1;
      }

   }
}
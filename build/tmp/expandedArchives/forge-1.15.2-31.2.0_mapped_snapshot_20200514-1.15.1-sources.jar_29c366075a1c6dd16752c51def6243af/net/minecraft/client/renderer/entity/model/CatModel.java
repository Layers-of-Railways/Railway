package net.minecraft.client.renderer.entity.model;

import net.minecraft.entity.passive.CatEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CatModel<T extends CatEntity> extends OcelotModel<T> {
   private float field_217155_m;
   private float field_217156_n;
   private float field_217157_o;

   public CatModel(float p_i51069_1_) {
      super(p_i51069_1_);
   }

   public void setLivingAnimations(T entityIn, float limbSwing, float limbSwingAmount, float partialTick) {
      this.field_217155_m = entityIn.func_213408_v(partialTick);
      this.field_217156_n = entityIn.func_213421_w(partialTick);
      this.field_217157_o = entityIn.func_213424_x(partialTick);
      if (this.field_217155_m <= 0.0F) {
         this.ocelotHead.rotateAngleX = 0.0F;
         this.ocelotHead.rotateAngleZ = 0.0F;
         this.ocelotFrontLeftLeg.rotateAngleX = 0.0F;
         this.ocelotFrontLeftLeg.rotateAngleZ = 0.0F;
         this.ocelotFrontRightLeg.rotateAngleX = 0.0F;
         this.ocelotFrontRightLeg.rotateAngleZ = 0.0F;
         this.ocelotFrontRightLeg.rotationPointX = -1.2F;
         this.ocelotBackLeftLeg.rotateAngleX = 0.0F;
         this.ocelotBackRightLeg.rotateAngleX = 0.0F;
         this.ocelotBackRightLeg.rotateAngleZ = 0.0F;
         this.ocelotBackRightLeg.rotationPointX = -1.1F;
         this.ocelotBackRightLeg.rotationPointY = 18.0F;
      }

      super.setLivingAnimations(entityIn, limbSwing, limbSwingAmount, partialTick);
      if (entityIn.isSitting()) {
         this.ocelotBody.rotateAngleX = ((float)Math.PI / 4F);
         this.ocelotBody.rotationPointY += -4.0F;
         this.ocelotBody.rotationPointZ += 5.0F;
         this.ocelotHead.rotationPointY += -3.3F;
         ++this.ocelotHead.rotationPointZ;
         this.ocelotTail.rotationPointY += 8.0F;
         this.ocelotTail.rotationPointZ += -2.0F;
         this.ocelotTail2.rotationPointY += 2.0F;
         this.ocelotTail2.rotationPointZ += -0.8F;
         this.ocelotTail.rotateAngleX = 1.7278761F;
         this.ocelotTail2.rotateAngleX = 2.670354F;
         this.ocelotFrontLeftLeg.rotateAngleX = -0.15707964F;
         this.ocelotFrontLeftLeg.rotationPointY = 16.1F;
         this.ocelotFrontLeftLeg.rotationPointZ = -7.0F;
         this.ocelotFrontRightLeg.rotateAngleX = -0.15707964F;
         this.ocelotFrontRightLeg.rotationPointY = 16.1F;
         this.ocelotFrontRightLeg.rotationPointZ = -7.0F;
         this.ocelotBackLeftLeg.rotateAngleX = (-(float)Math.PI / 2F);
         this.ocelotBackLeftLeg.rotationPointY = 21.0F;
         this.ocelotBackLeftLeg.rotationPointZ = 1.0F;
         this.ocelotBackRightLeg.rotateAngleX = (-(float)Math.PI / 2F);
         this.ocelotBackRightLeg.rotationPointY = 21.0F;
         this.ocelotBackRightLeg.rotationPointZ = 1.0F;
         this.state = 3;
      }

   }

   /**
    * Sets this entity's model rotation angles
    */
   public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
      super.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
      if (this.field_217155_m > 0.0F) {
         this.ocelotHead.rotateAngleZ = ModelUtils.func_228283_a_(this.ocelotHead.rotateAngleZ, -1.2707963F, this.field_217155_m);
         this.ocelotHead.rotateAngleY = ModelUtils.func_228283_a_(this.ocelotHead.rotateAngleY, 1.2707963F, this.field_217155_m);
         this.ocelotFrontLeftLeg.rotateAngleX = -1.2707963F;
         this.ocelotFrontRightLeg.rotateAngleX = -0.47079635F;
         this.ocelotFrontRightLeg.rotateAngleZ = -0.2F;
         this.ocelotFrontRightLeg.rotationPointX = -0.2F;
         this.ocelotBackLeftLeg.rotateAngleX = -0.4F;
         this.ocelotBackRightLeg.rotateAngleX = 0.5F;
         this.ocelotBackRightLeg.rotateAngleZ = -0.5F;
         this.ocelotBackRightLeg.rotationPointX = -0.3F;
         this.ocelotBackRightLeg.rotationPointY = 20.0F;
         this.ocelotTail.rotateAngleX = ModelUtils.func_228283_a_(this.ocelotTail.rotateAngleX, 0.8F, this.field_217156_n);
         this.ocelotTail2.rotateAngleX = ModelUtils.func_228283_a_(this.ocelotTail2.rotateAngleX, -0.4F, this.field_217156_n);
      }

      if (this.field_217157_o > 0.0F) {
         this.ocelotHead.rotateAngleX = ModelUtils.func_228283_a_(this.ocelotHead.rotateAngleX, -0.58177644F, this.field_217157_o);
      }

   }
}
package net.minecraft.client.renderer.entity.model;

import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractZombieModel<T extends MonsterEntity> extends BipedModel<T> {
   protected AbstractZombieModel(float modelSize, float yOffsetIn, int textureWidthIn, int textureHeightIn) {
      super(modelSize, yOffsetIn, textureWidthIn, textureHeightIn);
   }

   /**
    * Sets this entity's model rotation angles
    */
   public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
      super.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
      boolean flag = this.isAggressive(entityIn);
      float f = MathHelper.sin(this.swingProgress * (float)Math.PI);
      float f1 = MathHelper.sin((1.0F - (1.0F - this.swingProgress) * (1.0F - this.swingProgress)) * (float)Math.PI);
      this.bipedRightArm.rotateAngleZ = 0.0F;
      this.bipedLeftArm.rotateAngleZ = 0.0F;
      this.bipedRightArm.rotateAngleY = -(0.1F - f * 0.6F);
      this.bipedLeftArm.rotateAngleY = 0.1F - f * 0.6F;
      float f2 = -(float)Math.PI / (flag ? 1.5F : 2.25F);
      this.bipedRightArm.rotateAngleX = f2;
      this.bipedLeftArm.rotateAngleX = f2;
      this.bipedRightArm.rotateAngleX += f * 1.2F - f1 * 0.4F;
      this.bipedLeftArm.rotateAngleX += f * 1.2F - f1 * 0.4F;
      this.bipedRightArm.rotateAngleZ += MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
      this.bipedLeftArm.rotateAngleZ -= MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
      this.bipedRightArm.rotateAngleX += MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
      this.bipedLeftArm.rotateAngleX -= MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
   }

   public abstract boolean isAggressive(T entityIn);
}
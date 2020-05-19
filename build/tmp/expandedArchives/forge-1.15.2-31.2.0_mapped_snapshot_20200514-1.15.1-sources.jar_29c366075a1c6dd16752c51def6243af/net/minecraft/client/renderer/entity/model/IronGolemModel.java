package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class IronGolemModel<T extends IronGolemEntity> extends SegmentedModel<T> {
   private final ModelRenderer ironGolemHead;
   private final ModelRenderer ironGolemBody;
   private final ModelRenderer ironGolemRightArm;
   private final ModelRenderer ironGolemLeftArm;
   private final ModelRenderer ironGolemLeftLeg;
   private final ModelRenderer ironGolemRightLeg;

   public IronGolemModel() {
      int i = 128;
      int j = 128;
      this.ironGolemHead = (new ModelRenderer(this)).setTextureSize(128, 128);
      this.ironGolemHead.setRotationPoint(0.0F, -7.0F, -2.0F);
      this.ironGolemHead.setTextureOffset(0, 0).addBox(-4.0F, -12.0F, -5.5F, 8.0F, 10.0F, 8.0F, 0.0F);
      this.ironGolemHead.setTextureOffset(24, 0).addBox(-1.0F, -5.0F, -7.5F, 2.0F, 4.0F, 2.0F, 0.0F);
      this.ironGolemBody = (new ModelRenderer(this)).setTextureSize(128, 128);
      this.ironGolemBody.setRotationPoint(0.0F, -7.0F, 0.0F);
      this.ironGolemBody.setTextureOffset(0, 40).addBox(-9.0F, -2.0F, -6.0F, 18.0F, 12.0F, 11.0F, 0.0F);
      this.ironGolemBody.setTextureOffset(0, 70).addBox(-4.5F, 10.0F, -3.0F, 9.0F, 5.0F, 6.0F, 0.5F);
      this.ironGolemRightArm = (new ModelRenderer(this)).setTextureSize(128, 128);
      this.ironGolemRightArm.setRotationPoint(0.0F, -7.0F, 0.0F);
      this.ironGolemRightArm.setTextureOffset(60, 21).addBox(-13.0F, -2.5F, -3.0F, 4.0F, 30.0F, 6.0F, 0.0F);
      this.ironGolemLeftArm = (new ModelRenderer(this)).setTextureSize(128, 128);
      this.ironGolemLeftArm.setRotationPoint(0.0F, -7.0F, 0.0F);
      this.ironGolemLeftArm.setTextureOffset(60, 58).addBox(9.0F, -2.5F, -3.0F, 4.0F, 30.0F, 6.0F, 0.0F);
      this.ironGolemLeftLeg = (new ModelRenderer(this, 0, 22)).setTextureSize(128, 128);
      this.ironGolemLeftLeg.setRotationPoint(-4.0F, 11.0F, 0.0F);
      this.ironGolemLeftLeg.setTextureOffset(37, 0).addBox(-3.5F, -3.0F, -3.0F, 6.0F, 16.0F, 5.0F, 0.0F);
      this.ironGolemRightLeg = (new ModelRenderer(this, 0, 22)).setTextureSize(128, 128);
      this.ironGolemRightLeg.mirror = true;
      this.ironGolemRightLeg.setTextureOffset(60, 0).setRotationPoint(5.0F, 11.0F, 0.0F);
      this.ironGolemRightLeg.addBox(-3.5F, -3.0F, -3.0F, 6.0F, 16.0F, 5.0F, 0.0F);
   }

   public Iterable<ModelRenderer> getParts() {
      return ImmutableList.of(this.ironGolemHead, this.ironGolemBody, this.ironGolemLeftLeg, this.ironGolemRightLeg, this.ironGolemRightArm, this.ironGolemLeftArm);
   }

   /**
    * Sets this entity's model rotation angles
    */
   public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
      this.ironGolemHead.rotateAngleY = netHeadYaw * ((float)Math.PI / 180F);
      this.ironGolemHead.rotateAngleX = headPitch * ((float)Math.PI / 180F);
      this.ironGolemLeftLeg.rotateAngleX = -1.5F * this.triangleWave(limbSwing, 13.0F) * limbSwingAmount;
      this.ironGolemRightLeg.rotateAngleX = 1.5F * this.triangleWave(limbSwing, 13.0F) * limbSwingAmount;
      this.ironGolemLeftLeg.rotateAngleY = 0.0F;
      this.ironGolemRightLeg.rotateAngleY = 0.0F;
   }

   public void setLivingAnimations(T entityIn, float limbSwing, float limbSwingAmount, float partialTick) {
      int i = entityIn.getAttackTimer();
      if (i > 0) {
         this.ironGolemRightArm.rotateAngleX = -2.0F + 1.5F * this.triangleWave((float)i - partialTick, 10.0F);
         this.ironGolemLeftArm.rotateAngleX = -2.0F + 1.5F * this.triangleWave((float)i - partialTick, 10.0F);
      } else {
         int j = entityIn.getHoldRoseTick();
         if (j > 0) {
            this.ironGolemRightArm.rotateAngleX = -0.8F + 0.025F * this.triangleWave((float)j, 70.0F);
            this.ironGolemLeftArm.rotateAngleX = 0.0F;
         } else {
            this.ironGolemRightArm.rotateAngleX = (-0.2F + 1.5F * this.triangleWave(limbSwing, 13.0F)) * limbSwingAmount;
            this.ironGolemLeftArm.rotateAngleX = (-0.2F - 1.5F * this.triangleWave(limbSwing, 13.0F)) * limbSwingAmount;
         }
      }

   }

   private float triangleWave(float p_78172_1_, float p_78172_2_) {
      return (Math.abs(p_78172_1_ % p_78172_2_ - p_78172_2_ * 0.5F) - p_78172_2_ * 0.25F) / (p_78172_2_ * 0.25F);
   }

   public ModelRenderer getArmHoldingRose() {
      return this.ironGolemRightArm;
   }
}
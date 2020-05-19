package net.minecraft.client.renderer.entity.model;

import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.monster.ZombieEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DrownedModel<T extends ZombieEntity> extends ZombieModel<T> {
   public DrownedModel(float p_i48915_1_, float p_i48915_2_, int p_i48915_3_, int p_i48915_4_) {
      super(p_i48915_1_, p_i48915_2_, p_i48915_3_, p_i48915_4_);
      this.bipedRightArm = new ModelRenderer(this, 32, 48);
      this.bipedRightArm.addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, p_i48915_1_);
      this.bipedRightArm.setRotationPoint(-5.0F, 2.0F + p_i48915_2_, 0.0F);
      this.bipedRightLeg = new ModelRenderer(this, 16, 48);
      this.bipedRightLeg.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, p_i48915_1_);
      this.bipedRightLeg.setRotationPoint(-1.9F, 12.0F + p_i48915_2_, 0.0F);
   }

   public DrownedModel(float p_i49398_1_, boolean p_i49398_2_) {
      super(p_i49398_1_, 0.0F, 64, p_i49398_2_ ? 32 : 64);
   }

   public void setLivingAnimations(T entityIn, float limbSwing, float limbSwingAmount, float partialTick) {
      this.rightArmPose = BipedModel.ArmPose.EMPTY;
      this.leftArmPose = BipedModel.ArmPose.EMPTY;
      ItemStack itemstack = entityIn.getHeldItem(Hand.MAIN_HAND);
      if (itemstack.getItem() == Items.TRIDENT && entityIn.isAggressive()) {
         if (entityIn.getPrimaryHand() == HandSide.RIGHT) {
            this.rightArmPose = BipedModel.ArmPose.THROW_SPEAR;
         } else {
            this.leftArmPose = BipedModel.ArmPose.THROW_SPEAR;
         }
      }

      super.setLivingAnimations(entityIn, limbSwing, limbSwingAmount, partialTick);
   }

   /**
    * Sets this entity's model rotation angles
    */
   public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
      super.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
      if (this.leftArmPose == BipedModel.ArmPose.THROW_SPEAR) {
         this.bipedLeftArm.rotateAngleX = this.bipedLeftArm.rotateAngleX * 0.5F - (float)Math.PI;
         this.bipedLeftArm.rotateAngleY = 0.0F;
      }

      if (this.rightArmPose == BipedModel.ArmPose.THROW_SPEAR) {
         this.bipedRightArm.rotateAngleX = this.bipedRightArm.rotateAngleX * 0.5F - (float)Math.PI;
         this.bipedRightArm.rotateAngleY = 0.0F;
      }

      if (this.swimAnimation > 0.0F) {
         this.bipedRightArm.rotateAngleX = this.rotLerpRad(this.bipedRightArm.rotateAngleX, -2.5132742F, this.swimAnimation) + this.swimAnimation * 0.35F * MathHelper.sin(0.1F * ageInTicks);
         this.bipedLeftArm.rotateAngleX = this.rotLerpRad(this.bipedLeftArm.rotateAngleX, -2.5132742F, this.swimAnimation) - this.swimAnimation * 0.35F * MathHelper.sin(0.1F * ageInTicks);
         this.bipedRightArm.rotateAngleZ = this.rotLerpRad(this.bipedRightArm.rotateAngleZ, -0.15F, this.swimAnimation);
         this.bipedLeftArm.rotateAngleZ = this.rotLerpRad(this.bipedLeftArm.rotateAngleZ, 0.15F, this.swimAnimation);
         this.bipedLeftLeg.rotateAngleX -= this.swimAnimation * 0.55F * MathHelper.sin(0.1F * ageInTicks);
         this.bipedRightLeg.rotateAngleX += this.swimAnimation * 0.55F * MathHelper.sin(0.1F * ageInTicks);
         this.bipedHead.rotateAngleX = 0.0F;
      }

   }
}
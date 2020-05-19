package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.monster.AbstractIllagerEntity;
import net.minecraft.util.HandSide;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class IllagerModel<T extends AbstractIllagerEntity> extends SegmentedModel<T> implements IHasArm, IHasHead {
   private final ModelRenderer head;
   private final ModelRenderer hat;
   private final ModelRenderer body;
   private final ModelRenderer arms;
   private final ModelRenderer field_217143_g;
   private final ModelRenderer field_217144_h;
   private final ModelRenderer rightArm;
   private final ModelRenderer leftArm;
   private float field_217145_m;

   public IllagerModel(float scaleFactor, float p_i47227_2_, int textureWidthIn, int textureHeightIn) {
      this.head = (new ModelRenderer(this)).setTextureSize(textureWidthIn, textureHeightIn);
      this.head.setRotationPoint(0.0F, 0.0F + p_i47227_2_, 0.0F);
      this.head.setTextureOffset(0, 0).addBox(-4.0F, -10.0F, -4.0F, 8.0F, 10.0F, 8.0F, scaleFactor);
      this.hat = (new ModelRenderer(this, 32, 0)).setTextureSize(textureWidthIn, textureHeightIn);
      this.hat.addBox(-4.0F, -10.0F, -4.0F, 8.0F, 12.0F, 8.0F, scaleFactor + 0.45F);
      this.head.addChild(this.hat);
      this.hat.showModel = false;
      ModelRenderer modelrenderer = (new ModelRenderer(this)).setTextureSize(textureWidthIn, textureHeightIn);
      modelrenderer.setRotationPoint(0.0F, p_i47227_2_ - 2.0F, 0.0F);
      modelrenderer.setTextureOffset(24, 0).addBox(-1.0F, -1.0F, -6.0F, 2.0F, 4.0F, 2.0F, scaleFactor);
      this.head.addChild(modelrenderer);
      this.body = (new ModelRenderer(this)).setTextureSize(textureWidthIn, textureHeightIn);
      this.body.setRotationPoint(0.0F, 0.0F + p_i47227_2_, 0.0F);
      this.body.setTextureOffset(16, 20).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 12.0F, 6.0F, scaleFactor);
      this.body.setTextureOffset(0, 38).addBox(-4.0F, 0.0F, -3.0F, 8.0F, 18.0F, 6.0F, scaleFactor + 0.5F);
      this.arms = (new ModelRenderer(this)).setTextureSize(textureWidthIn, textureHeightIn);
      this.arms.setRotationPoint(0.0F, 0.0F + p_i47227_2_ + 2.0F, 0.0F);
      this.arms.setTextureOffset(44, 22).addBox(-8.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, scaleFactor);
      ModelRenderer modelrenderer1 = (new ModelRenderer(this, 44, 22)).setTextureSize(textureWidthIn, textureHeightIn);
      modelrenderer1.mirror = true;
      modelrenderer1.addBox(4.0F, -2.0F, -2.0F, 4.0F, 8.0F, 4.0F, scaleFactor);
      this.arms.addChild(modelrenderer1);
      this.arms.setTextureOffset(40, 38).addBox(-4.0F, 2.0F, -2.0F, 8.0F, 4.0F, 4.0F, scaleFactor);
      this.field_217143_g = (new ModelRenderer(this, 0, 22)).setTextureSize(textureWidthIn, textureHeightIn);
      this.field_217143_g.setRotationPoint(-2.0F, 12.0F + p_i47227_2_, 0.0F);
      this.field_217143_g.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, scaleFactor);
      this.field_217144_h = (new ModelRenderer(this, 0, 22)).setTextureSize(textureWidthIn, textureHeightIn);
      this.field_217144_h.mirror = true;
      this.field_217144_h.setRotationPoint(2.0F, 12.0F + p_i47227_2_, 0.0F);
      this.field_217144_h.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, scaleFactor);
      this.rightArm = (new ModelRenderer(this, 40, 46)).setTextureSize(textureWidthIn, textureHeightIn);
      this.rightArm.addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, scaleFactor);
      this.rightArm.setRotationPoint(-5.0F, 2.0F + p_i47227_2_, 0.0F);
      this.leftArm = (new ModelRenderer(this, 40, 46)).setTextureSize(textureWidthIn, textureHeightIn);
      this.leftArm.mirror = true;
      this.leftArm.addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, scaleFactor);
      this.leftArm.setRotationPoint(5.0F, 2.0F + p_i47227_2_, 0.0F);
   }

   public Iterable<ModelRenderer> getParts() {
      return ImmutableList.of(this.head, this.body, this.field_217143_g, this.field_217144_h, this.arms, this.rightArm, this.leftArm);
   }

   /**
    * Sets this entity's model rotation angles
    */
   public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
      this.head.rotateAngleY = netHeadYaw * ((float)Math.PI / 180F);
      this.head.rotateAngleX = headPitch * ((float)Math.PI / 180F);
      this.arms.rotationPointY = 3.0F;
      this.arms.rotationPointZ = -1.0F;
      this.arms.rotateAngleX = -0.75F;
      if (this.isSitting) {
         this.rightArm.rotateAngleX = (-(float)Math.PI / 5F);
         this.rightArm.rotateAngleY = 0.0F;
         this.rightArm.rotateAngleZ = 0.0F;
         this.leftArm.rotateAngleX = (-(float)Math.PI / 5F);
         this.leftArm.rotateAngleY = 0.0F;
         this.leftArm.rotateAngleZ = 0.0F;
         this.field_217143_g.rotateAngleX = -1.4137167F;
         this.field_217143_g.rotateAngleY = ((float)Math.PI / 10F);
         this.field_217143_g.rotateAngleZ = 0.07853982F;
         this.field_217144_h.rotateAngleX = -1.4137167F;
         this.field_217144_h.rotateAngleY = (-(float)Math.PI / 10F);
         this.field_217144_h.rotateAngleZ = -0.07853982F;
      } else {
         this.rightArm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * 2.0F * limbSwingAmount * 0.5F;
         this.rightArm.rotateAngleY = 0.0F;
         this.rightArm.rotateAngleZ = 0.0F;
         this.leftArm.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 2.0F * limbSwingAmount * 0.5F;
         this.leftArm.rotateAngleY = 0.0F;
         this.leftArm.rotateAngleZ = 0.0F;
         this.field_217143_g.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount * 0.5F;
         this.field_217143_g.rotateAngleY = 0.0F;
         this.field_217143_g.rotateAngleZ = 0.0F;
         this.field_217144_h.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount * 0.5F;
         this.field_217144_h.rotateAngleY = 0.0F;
         this.field_217144_h.rotateAngleZ = 0.0F;
      }

      AbstractIllagerEntity.ArmPose abstractillagerentity$armpose = entityIn.getArmPose();
      if (abstractillagerentity$armpose == AbstractIllagerEntity.ArmPose.ATTACKING) {
         float f = MathHelper.sin(this.swingProgress * (float)Math.PI);
         float f1 = MathHelper.sin((1.0F - (1.0F - this.swingProgress) * (1.0F - this.swingProgress)) * (float)Math.PI);
         this.rightArm.rotateAngleZ = 0.0F;
         this.leftArm.rotateAngleZ = 0.0F;
         this.rightArm.rotateAngleY = 0.15707964F;
         this.leftArm.rotateAngleY = -0.15707964F;
         if (entityIn.getPrimaryHand() == HandSide.RIGHT) {
            this.rightArm.rotateAngleX = -1.8849558F + MathHelper.cos(ageInTicks * 0.09F) * 0.15F;
            this.leftArm.rotateAngleX = -0.0F + MathHelper.cos(ageInTicks * 0.19F) * 0.5F;
            this.rightArm.rotateAngleX += f * 2.2F - f1 * 0.4F;
            this.leftArm.rotateAngleX += f * 1.2F - f1 * 0.4F;
         } else {
            this.rightArm.rotateAngleX = -0.0F + MathHelper.cos(ageInTicks * 0.19F) * 0.5F;
            this.leftArm.rotateAngleX = -1.8849558F + MathHelper.cos(ageInTicks * 0.09F) * 0.15F;
            this.rightArm.rotateAngleX += f * 1.2F - f1 * 0.4F;
            this.leftArm.rotateAngleX += f * 2.2F - f1 * 0.4F;
         }

         this.rightArm.rotateAngleZ += MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
         this.leftArm.rotateAngleZ -= MathHelper.cos(ageInTicks * 0.09F) * 0.05F + 0.05F;
         this.rightArm.rotateAngleX += MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
         this.leftArm.rotateAngleX -= MathHelper.sin(ageInTicks * 0.067F) * 0.05F;
      } else if (abstractillagerentity$armpose == AbstractIllagerEntity.ArmPose.SPELLCASTING) {
         this.rightArm.rotationPointZ = 0.0F;
         this.rightArm.rotationPointX = -5.0F;
         this.leftArm.rotationPointZ = 0.0F;
         this.leftArm.rotationPointX = 5.0F;
         this.rightArm.rotateAngleX = MathHelper.cos(ageInTicks * 0.6662F) * 0.25F;
         this.leftArm.rotateAngleX = MathHelper.cos(ageInTicks * 0.6662F) * 0.25F;
         this.rightArm.rotateAngleZ = 2.3561945F;
         this.leftArm.rotateAngleZ = -2.3561945F;
         this.rightArm.rotateAngleY = 0.0F;
         this.leftArm.rotateAngleY = 0.0F;
      } else if (abstractillagerentity$armpose == AbstractIllagerEntity.ArmPose.BOW_AND_ARROW) {
         this.rightArm.rotateAngleY = -0.1F + this.head.rotateAngleY;
         this.rightArm.rotateAngleX = (-(float)Math.PI / 2F) + this.head.rotateAngleX;
         this.leftArm.rotateAngleX = -0.9424779F + this.head.rotateAngleX;
         this.leftArm.rotateAngleY = this.head.rotateAngleY - 0.4F;
         this.leftArm.rotateAngleZ = ((float)Math.PI / 2F);
      } else if (abstractillagerentity$armpose == AbstractIllagerEntity.ArmPose.CROSSBOW_HOLD) {
         this.rightArm.rotateAngleY = -0.3F + this.head.rotateAngleY;
         this.leftArm.rotateAngleY = 0.6F + this.head.rotateAngleY;
         this.rightArm.rotateAngleX = (-(float)Math.PI / 2F) + this.head.rotateAngleX + 0.1F;
         this.leftArm.rotateAngleX = -1.5F + this.head.rotateAngleX;
      } else if (abstractillagerentity$armpose == AbstractIllagerEntity.ArmPose.CROSSBOW_CHARGE) {
         this.rightArm.rotateAngleY = -0.8F;
         this.rightArm.rotateAngleX = -0.97079635F;
         this.leftArm.rotateAngleX = -0.97079635F;
         float f2 = MathHelper.clamp(this.field_217145_m, 0.0F, 25.0F);
         this.leftArm.rotateAngleY = MathHelper.lerp(f2 / 25.0F, 0.4F, 0.85F);
         this.leftArm.rotateAngleX = MathHelper.lerp(f2 / 25.0F, this.leftArm.rotateAngleX, (-(float)Math.PI / 2F));
      } else if (abstractillagerentity$armpose == AbstractIllagerEntity.ArmPose.CELEBRATING) {
         this.rightArm.rotationPointZ = 0.0F;
         this.rightArm.rotationPointX = -5.0F;
         this.rightArm.rotateAngleX = MathHelper.cos(ageInTicks * 0.6662F) * 0.05F;
         this.rightArm.rotateAngleZ = 2.670354F;
         this.rightArm.rotateAngleY = 0.0F;
         this.leftArm.rotationPointZ = 0.0F;
         this.leftArm.rotationPointX = 5.0F;
         this.leftArm.rotateAngleX = MathHelper.cos(ageInTicks * 0.6662F) * 0.05F;
         this.leftArm.rotateAngleZ = -2.3561945F;
         this.leftArm.rotateAngleY = 0.0F;
      }

      boolean flag = abstractillagerentity$armpose == AbstractIllagerEntity.ArmPose.CROSSED;
      this.arms.showModel = flag;
      this.leftArm.showModel = !flag;
      this.rightArm.showModel = !flag;
   }

   public void setLivingAnimations(T entityIn, float limbSwing, float limbSwingAmount, float partialTick) {
      this.field_217145_m = (float)entityIn.getItemInUseMaxCount();
      super.setLivingAnimations(entityIn, limbSwing, limbSwingAmount, partialTick);
   }

   private ModelRenderer getArm(HandSide p_191216_1_) {
      return p_191216_1_ == HandSide.LEFT ? this.leftArm : this.rightArm;
   }

   public ModelRenderer func_205062_a() {
      return this.hat;
   }

   public ModelRenderer getModelHead() {
      return this.head;
   }

   public void translateHand(HandSide sideIn, MatrixStack matrixStackIn) {
      this.getArm(sideIn).translateRotate(matrixStackIn);
   }
}
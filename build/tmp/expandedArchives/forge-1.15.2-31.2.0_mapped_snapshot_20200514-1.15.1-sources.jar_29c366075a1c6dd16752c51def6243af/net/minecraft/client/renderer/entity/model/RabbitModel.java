package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RabbitModel<T extends RabbitEntity> extends EntityModel<T> {
   /** The Rabbit's Left Foot */
   private final ModelRenderer rabbitLeftFoot = new ModelRenderer(this, 26, 24);
   /** The Rabbit's Right Foot */
   private final ModelRenderer rabbitRightFoot;
   /** The Rabbit's Left Thigh */
   private final ModelRenderer rabbitLeftThigh;
   /** The Rabbit's Right Thigh */
   private final ModelRenderer rabbitRightThigh;
   /** The Rabbit's Body */
   private final ModelRenderer rabbitBody;
   /** The Rabbit's Left Arm */
   private final ModelRenderer rabbitLeftArm;
   /** The Rabbit's Right Arm */
   private final ModelRenderer rabbitRightArm;
   /** The Rabbit's Head */
   private final ModelRenderer rabbitHead;
   /** The Rabbit's Right Ear */
   private final ModelRenderer rabbitRightEar;
   /** The Rabbit's Left Ear */
   private final ModelRenderer rabbitLeftEar;
   /** The Rabbit's Tail */
   private final ModelRenderer rabbitTail;
   /** The Rabbit's Nose */
   private final ModelRenderer rabbitNose;
   private float jumpRotation;

   public RabbitModel() {
      this.rabbitLeftFoot.addBox(-1.0F, 5.5F, -3.7F, 2.0F, 1.0F, 7.0F);
      this.rabbitLeftFoot.setRotationPoint(3.0F, 17.5F, 3.7F);
      this.rabbitLeftFoot.mirror = true;
      this.setRotationOffset(this.rabbitLeftFoot, 0.0F, 0.0F, 0.0F);
      this.rabbitRightFoot = new ModelRenderer(this, 8, 24);
      this.rabbitRightFoot.addBox(-1.0F, 5.5F, -3.7F, 2.0F, 1.0F, 7.0F);
      this.rabbitRightFoot.setRotationPoint(-3.0F, 17.5F, 3.7F);
      this.rabbitRightFoot.mirror = true;
      this.setRotationOffset(this.rabbitRightFoot, 0.0F, 0.0F, 0.0F);
      this.rabbitLeftThigh = new ModelRenderer(this, 30, 15);
      this.rabbitLeftThigh.addBox(-1.0F, 0.0F, 0.0F, 2.0F, 4.0F, 5.0F);
      this.rabbitLeftThigh.setRotationPoint(3.0F, 17.5F, 3.7F);
      this.rabbitLeftThigh.mirror = true;
      this.setRotationOffset(this.rabbitLeftThigh, -0.34906584F, 0.0F, 0.0F);
      this.rabbitRightThigh = new ModelRenderer(this, 16, 15);
      this.rabbitRightThigh.addBox(-1.0F, 0.0F, 0.0F, 2.0F, 4.0F, 5.0F);
      this.rabbitRightThigh.setRotationPoint(-3.0F, 17.5F, 3.7F);
      this.rabbitRightThigh.mirror = true;
      this.setRotationOffset(this.rabbitRightThigh, -0.34906584F, 0.0F, 0.0F);
      this.rabbitBody = new ModelRenderer(this, 0, 0);
      this.rabbitBody.addBox(-3.0F, -2.0F, -10.0F, 6.0F, 5.0F, 10.0F);
      this.rabbitBody.setRotationPoint(0.0F, 19.0F, 8.0F);
      this.rabbitBody.mirror = true;
      this.setRotationOffset(this.rabbitBody, -0.34906584F, 0.0F, 0.0F);
      this.rabbitLeftArm = new ModelRenderer(this, 8, 15);
      this.rabbitLeftArm.addBox(-1.0F, 0.0F, -1.0F, 2.0F, 7.0F, 2.0F);
      this.rabbitLeftArm.setRotationPoint(3.0F, 17.0F, -1.0F);
      this.rabbitLeftArm.mirror = true;
      this.setRotationOffset(this.rabbitLeftArm, -0.17453292F, 0.0F, 0.0F);
      this.rabbitRightArm = new ModelRenderer(this, 0, 15);
      this.rabbitRightArm.addBox(-1.0F, 0.0F, -1.0F, 2.0F, 7.0F, 2.0F);
      this.rabbitRightArm.setRotationPoint(-3.0F, 17.0F, -1.0F);
      this.rabbitRightArm.mirror = true;
      this.setRotationOffset(this.rabbitRightArm, -0.17453292F, 0.0F, 0.0F);
      this.rabbitHead = new ModelRenderer(this, 32, 0);
      this.rabbitHead.addBox(-2.5F, -4.0F, -5.0F, 5.0F, 4.0F, 5.0F);
      this.rabbitHead.setRotationPoint(0.0F, 16.0F, -1.0F);
      this.rabbitHead.mirror = true;
      this.setRotationOffset(this.rabbitHead, 0.0F, 0.0F, 0.0F);
      this.rabbitRightEar = new ModelRenderer(this, 52, 0);
      this.rabbitRightEar.addBox(-2.5F, -9.0F, -1.0F, 2.0F, 5.0F, 1.0F);
      this.rabbitRightEar.setRotationPoint(0.0F, 16.0F, -1.0F);
      this.rabbitRightEar.mirror = true;
      this.setRotationOffset(this.rabbitRightEar, 0.0F, -0.2617994F, 0.0F);
      this.rabbitLeftEar = new ModelRenderer(this, 58, 0);
      this.rabbitLeftEar.addBox(0.5F, -9.0F, -1.0F, 2.0F, 5.0F, 1.0F);
      this.rabbitLeftEar.setRotationPoint(0.0F, 16.0F, -1.0F);
      this.rabbitLeftEar.mirror = true;
      this.setRotationOffset(this.rabbitLeftEar, 0.0F, 0.2617994F, 0.0F);
      this.rabbitTail = new ModelRenderer(this, 52, 6);
      this.rabbitTail.addBox(-1.5F, -1.5F, 0.0F, 3.0F, 3.0F, 2.0F);
      this.rabbitTail.setRotationPoint(0.0F, 20.0F, 7.0F);
      this.rabbitTail.mirror = true;
      this.setRotationOffset(this.rabbitTail, -0.3490659F, 0.0F, 0.0F);
      this.rabbitNose = new ModelRenderer(this, 32, 9);
      this.rabbitNose.addBox(-0.5F, -2.5F, -5.5F, 1.0F, 1.0F, 1.0F);
      this.rabbitNose.setRotationPoint(0.0F, 16.0F, -1.0F);
      this.rabbitNose.mirror = true;
      this.setRotationOffset(this.rabbitNose, 0.0F, 0.0F, 0.0F);
   }

   private void setRotationOffset(ModelRenderer renderer, float x, float y, float z) {
      renderer.rotateAngleX = x;
      renderer.rotateAngleY = y;
      renderer.rotateAngleZ = z;
   }

   public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
      if (this.isChild) {
         float f = 1.5F;
         matrixStackIn.push();
         matrixStackIn.scale(0.56666666F, 0.56666666F, 0.56666666F);
         matrixStackIn.translate(0.0D, 1.375D, 0.125D);
         ImmutableList.of(this.rabbitHead, this.rabbitLeftEar, this.rabbitRightEar, this.rabbitNose).forEach((p_228292_8_) -> {
            p_228292_8_.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
         });
         matrixStackIn.pop();
         matrixStackIn.push();
         matrixStackIn.scale(0.4F, 0.4F, 0.4F);
         matrixStackIn.translate(0.0D, 2.25D, 0.0D);
         ImmutableList.of(this.rabbitLeftFoot, this.rabbitRightFoot, this.rabbitLeftThigh, this.rabbitRightThigh, this.rabbitBody, this.rabbitLeftArm, this.rabbitRightArm, this.rabbitTail).forEach((p_228291_8_) -> {
            p_228291_8_.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
         });
         matrixStackIn.pop();
      } else {
         matrixStackIn.push();
         matrixStackIn.scale(0.6F, 0.6F, 0.6F);
         matrixStackIn.translate(0.0D, 1.0D, 0.0D);
         ImmutableList.of(this.rabbitLeftFoot, this.rabbitRightFoot, this.rabbitLeftThigh, this.rabbitRightThigh, this.rabbitBody, this.rabbitLeftArm, this.rabbitRightArm, this.rabbitHead, this.rabbitRightEar, this.rabbitLeftEar, this.rabbitTail, this.rabbitNose).forEach((p_228290_8_) -> {
            p_228290_8_.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
         });
         matrixStackIn.pop();
      }

   }

   /**
    * Sets this entity's model rotation angles
    */
   public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
      float f = ageInTicks - (float)entityIn.ticksExisted;
      this.rabbitNose.rotateAngleX = headPitch * ((float)Math.PI / 180F);
      this.rabbitHead.rotateAngleX = headPitch * ((float)Math.PI / 180F);
      this.rabbitRightEar.rotateAngleX = headPitch * ((float)Math.PI / 180F);
      this.rabbitLeftEar.rotateAngleX = headPitch * ((float)Math.PI / 180F);
      this.rabbitNose.rotateAngleY = netHeadYaw * ((float)Math.PI / 180F);
      this.rabbitHead.rotateAngleY = netHeadYaw * ((float)Math.PI / 180F);
      this.rabbitRightEar.rotateAngleY = this.rabbitNose.rotateAngleY - 0.2617994F;
      this.rabbitLeftEar.rotateAngleY = this.rabbitNose.rotateAngleY + 0.2617994F;
      this.jumpRotation = MathHelper.sin(entityIn.getJumpCompletion(f) * (float)Math.PI);
      this.rabbitLeftThigh.rotateAngleX = (this.jumpRotation * 50.0F - 21.0F) * ((float)Math.PI / 180F);
      this.rabbitRightThigh.rotateAngleX = (this.jumpRotation * 50.0F - 21.0F) * ((float)Math.PI / 180F);
      this.rabbitLeftFoot.rotateAngleX = this.jumpRotation * 50.0F * ((float)Math.PI / 180F);
      this.rabbitRightFoot.rotateAngleX = this.jumpRotation * 50.0F * ((float)Math.PI / 180F);
      this.rabbitLeftArm.rotateAngleX = (this.jumpRotation * -40.0F - 11.0F) * ((float)Math.PI / 180F);
      this.rabbitRightArm.rotateAngleX = (this.jumpRotation * -40.0F - 11.0F) * ((float)Math.PI / 180F);
   }

   public void setLivingAnimations(T entityIn, float limbSwing, float limbSwingAmount, float partialTick) {
      super.setLivingAnimations(entityIn, limbSwing, limbSwingAmount, partialTick);
      this.jumpRotation = MathHelper.sin(entityIn.getJumpCompletion(partialTick) * (float)Math.PI);
   }
}
package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WolfModel<T extends WolfEntity> extends TintedAgeableModel<T> {
   private final ModelRenderer head;
   /** Added as a result/workaround for the loss of renderWithRotation */
   private final ModelRenderer headChild;
   private final ModelRenderer body;
   private final ModelRenderer legBackRight;
   private final ModelRenderer legBackLeft;
   private final ModelRenderer legFrontRight;
   private final ModelRenderer legFrontLeft;
   private final ModelRenderer tail;
   /** Added as a result/workaround for the loss of renderWithRotation */
   private final ModelRenderer tailChild;
   private final ModelRenderer mane;

   public WolfModel() {
      float f = 0.0F;
      float f1 = 13.5F;
      this.head = new ModelRenderer(this, 0, 0);
      this.head.setRotationPoint(-1.0F, 13.5F, -7.0F);
      this.headChild = new ModelRenderer(this, 0, 0);
      this.headChild.addBox(-2.0F, -3.0F, -2.0F, 6.0F, 6.0F, 4.0F, 0.0F);
      this.head.addChild(this.headChild);
      this.body = new ModelRenderer(this, 18, 14);
      this.body.addBox(-3.0F, -2.0F, -3.0F, 6.0F, 9.0F, 6.0F, 0.0F);
      this.body.setRotationPoint(0.0F, 14.0F, 2.0F);
      this.mane = new ModelRenderer(this, 21, 0);
      this.mane.addBox(-3.0F, -3.0F, -3.0F, 8.0F, 6.0F, 7.0F, 0.0F);
      this.mane.setRotationPoint(-1.0F, 14.0F, 2.0F);
      this.legBackRight = new ModelRenderer(this, 0, 18);
      this.legBackRight.addBox(0.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, 0.0F);
      this.legBackRight.setRotationPoint(-2.5F, 16.0F, 7.0F);
      this.legBackLeft = new ModelRenderer(this, 0, 18);
      this.legBackLeft.addBox(0.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, 0.0F);
      this.legBackLeft.setRotationPoint(0.5F, 16.0F, 7.0F);
      this.legFrontRight = new ModelRenderer(this, 0, 18);
      this.legFrontRight.addBox(0.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, 0.0F);
      this.legFrontRight.setRotationPoint(-2.5F, 16.0F, -4.0F);
      this.legFrontLeft = new ModelRenderer(this, 0, 18);
      this.legFrontLeft.addBox(0.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, 0.0F);
      this.legFrontLeft.setRotationPoint(0.5F, 16.0F, -4.0F);
      this.tail = new ModelRenderer(this, 9, 18);
      this.tail.setRotationPoint(-1.0F, 12.0F, 8.0F);
      this.tailChild = new ModelRenderer(this, 9, 18);
      this.tailChild.addBox(0.0F, 0.0F, -1.0F, 2.0F, 8.0F, 2.0F, 0.0F);
      this.tail.addChild(this.tailChild);
      this.headChild.setTextureOffset(16, 14).addBox(-2.0F, -5.0F, 0.0F, 2.0F, 2.0F, 1.0F, 0.0F);
      this.headChild.setTextureOffset(16, 14).addBox(2.0F, -5.0F, 0.0F, 2.0F, 2.0F, 1.0F, 0.0F);
      this.headChild.setTextureOffset(0, 10).addBox(-0.5F, 0.0F, -5.0F, 3.0F, 3.0F, 4.0F, 0.0F);
   }

   protected Iterable<ModelRenderer> getHeadParts() {
      return ImmutableList.of(this.head);
   }

   protected Iterable<ModelRenderer> getBodyParts() {
      return ImmutableList.of(this.body, this.legBackRight, this.legBackLeft, this.legFrontRight, this.legFrontLeft, this.tail, this.mane);
   }

   public void setLivingAnimations(T entityIn, float limbSwing, float limbSwingAmount, float partialTick) {
      if (entityIn.isAngry()) {
         this.tail.rotateAngleY = 0.0F;
      } else {
         this.tail.rotateAngleY = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
      }

      if (entityIn.isSitting()) {
         this.mane.setRotationPoint(-1.0F, 16.0F, -3.0F);
         this.mane.rotateAngleX = 1.2566371F;
         this.mane.rotateAngleY = 0.0F;
         this.body.setRotationPoint(0.0F, 18.0F, 0.0F);
         this.body.rotateAngleX = ((float)Math.PI / 4F);
         this.tail.setRotationPoint(-1.0F, 21.0F, 6.0F);
         this.legBackRight.setRotationPoint(-2.5F, 22.7F, 2.0F);
         this.legBackRight.rotateAngleX = ((float)Math.PI * 1.5F);
         this.legBackLeft.setRotationPoint(0.5F, 22.7F, 2.0F);
         this.legBackLeft.rotateAngleX = ((float)Math.PI * 1.5F);
         this.legFrontRight.rotateAngleX = 5.811947F;
         this.legFrontRight.setRotationPoint(-2.49F, 17.0F, -4.0F);
         this.legFrontLeft.rotateAngleX = 5.811947F;
         this.legFrontLeft.setRotationPoint(0.51F, 17.0F, -4.0F);
      } else {
         this.body.setRotationPoint(0.0F, 14.0F, 2.0F);
         this.body.rotateAngleX = ((float)Math.PI / 2F);
         this.mane.setRotationPoint(-1.0F, 14.0F, -3.0F);
         this.mane.rotateAngleX = this.body.rotateAngleX;
         this.tail.setRotationPoint(-1.0F, 12.0F, 8.0F);
         this.legBackRight.setRotationPoint(-2.5F, 16.0F, 7.0F);
         this.legBackLeft.setRotationPoint(0.5F, 16.0F, 7.0F);
         this.legFrontRight.setRotationPoint(-2.5F, 16.0F, -4.0F);
         this.legFrontLeft.setRotationPoint(0.5F, 16.0F, -4.0F);
         this.legBackRight.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
         this.legBackLeft.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;
         this.legFrontRight.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;
         this.legFrontLeft.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
      }

      this.headChild.rotateAngleZ = entityIn.getInterestedAngle(partialTick) + entityIn.getShakeAngle(partialTick, 0.0F);
      this.mane.rotateAngleZ = entityIn.getShakeAngle(partialTick, -0.08F);
      this.body.rotateAngleZ = entityIn.getShakeAngle(partialTick, -0.16F);
      this.tailChild.rotateAngleZ = entityIn.getShakeAngle(partialTick, -0.2F);
   }

   /**
    * Sets this entity's model rotation angles
    */
   public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
      this.head.rotateAngleX = headPitch * ((float)Math.PI / 180F);
      this.head.rotateAngleY = netHeadYaw * ((float)Math.PI / 180F);
      this.tail.rotateAngleX = ageInTicks;
   }
}
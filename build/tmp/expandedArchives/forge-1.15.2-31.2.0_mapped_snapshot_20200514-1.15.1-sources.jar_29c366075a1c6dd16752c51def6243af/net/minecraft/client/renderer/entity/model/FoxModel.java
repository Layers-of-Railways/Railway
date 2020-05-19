package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.passive.FoxEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FoxModel<T extends FoxEntity> extends AgeableModel<T> {
   public final ModelRenderer head;
   private final ModelRenderer rightEar;
   private final ModelRenderer leftEar;
   private final ModelRenderer snout;
   private final ModelRenderer body;
   private final ModelRenderer legBackRight;
   private final ModelRenderer legBackLeft;
   private final ModelRenderer legFrontRight;
   private final ModelRenderer legFrontLeft;
   private final ModelRenderer tail;
   private float field_217125_n;

   public FoxModel() {
      super(true, 8.0F, 3.35F);
      this.textureWidth = 48;
      this.textureHeight = 32;
      this.head = new ModelRenderer(this, 1, 5);
      this.head.addBox(-3.0F, -2.0F, -5.0F, 8.0F, 6.0F, 6.0F);
      this.head.setRotationPoint(-1.0F, 16.5F, -3.0F);
      this.rightEar = new ModelRenderer(this, 8, 1);
      this.rightEar.addBox(-3.0F, -4.0F, -4.0F, 2.0F, 2.0F, 1.0F);
      this.leftEar = new ModelRenderer(this, 15, 1);
      this.leftEar.addBox(3.0F, -4.0F, -4.0F, 2.0F, 2.0F, 1.0F);
      this.snout = new ModelRenderer(this, 6, 18);
      this.snout.addBox(-1.0F, 2.01F, -8.0F, 4.0F, 2.0F, 3.0F);
      this.head.addChild(this.rightEar);
      this.head.addChild(this.leftEar);
      this.head.addChild(this.snout);
      this.body = new ModelRenderer(this, 24, 15);
      this.body.addBox(-3.0F, 3.999F, -3.5F, 6.0F, 11.0F, 6.0F);
      this.body.setRotationPoint(0.0F, 16.0F, -6.0F);
      float f = 0.001F;
      this.legBackRight = new ModelRenderer(this, 13, 24);
      this.legBackRight.addBox(2.0F, 0.5F, -1.0F, 2.0F, 6.0F, 2.0F, 0.001F);
      this.legBackRight.setRotationPoint(-5.0F, 17.5F, 7.0F);
      this.legBackLeft = new ModelRenderer(this, 4, 24);
      this.legBackLeft.addBox(2.0F, 0.5F, -1.0F, 2.0F, 6.0F, 2.0F, 0.001F);
      this.legBackLeft.setRotationPoint(-1.0F, 17.5F, 7.0F);
      this.legFrontRight = new ModelRenderer(this, 13, 24);
      this.legFrontRight.addBox(2.0F, 0.5F, -1.0F, 2.0F, 6.0F, 2.0F, 0.001F);
      this.legFrontRight.setRotationPoint(-5.0F, 17.5F, 0.0F);
      this.legFrontLeft = new ModelRenderer(this, 4, 24);
      this.legFrontLeft.addBox(2.0F, 0.5F, -1.0F, 2.0F, 6.0F, 2.0F, 0.001F);
      this.legFrontLeft.setRotationPoint(-1.0F, 17.5F, 0.0F);
      this.tail = new ModelRenderer(this, 30, 0);
      this.tail.addBox(2.0F, 0.0F, -1.0F, 4.0F, 9.0F, 5.0F);
      this.tail.setRotationPoint(-4.0F, 15.0F, -1.0F);
      this.body.addChild(this.tail);
   }

   public void setLivingAnimations(T entityIn, float limbSwing, float limbSwingAmount, float partialTick) {
      this.body.rotateAngleX = ((float)Math.PI / 2F);
      this.tail.rotateAngleX = -0.05235988F;
      this.legBackRight.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
      this.legBackLeft.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;
      this.legFrontRight.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;
      this.legFrontLeft.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
      this.head.setRotationPoint(-1.0F, 16.5F, -3.0F);
      this.head.rotateAngleY = 0.0F;
      this.head.rotateAngleZ = entityIn.func_213475_v(partialTick);
      this.legBackRight.showModel = true;
      this.legBackLeft.showModel = true;
      this.legFrontRight.showModel = true;
      this.legFrontLeft.showModel = true;
      this.body.setRotationPoint(0.0F, 16.0F, -6.0F);
      this.body.rotateAngleZ = 0.0F;
      this.legBackRight.setRotationPoint(-5.0F, 17.5F, 7.0F);
      this.legBackLeft.setRotationPoint(-1.0F, 17.5F, 7.0F);
      if (entityIn.isCrouching()) {
         this.body.rotateAngleX = 1.6755161F;
         float f = entityIn.func_213503_w(partialTick);
         this.body.setRotationPoint(0.0F, 16.0F + entityIn.func_213503_w(partialTick), -6.0F);
         this.head.setRotationPoint(-1.0F, 16.5F + f, -3.0F);
         this.head.rotateAngleY = 0.0F;
      } else if (entityIn.isSleeping()) {
         this.body.rotateAngleZ = (-(float)Math.PI / 2F);
         this.body.setRotationPoint(0.0F, 21.0F, -6.0F);
         this.tail.rotateAngleX = -2.6179938F;
         if (this.isChild) {
            this.tail.rotateAngleX = -2.1816616F;
            this.body.setRotationPoint(0.0F, 21.0F, -2.0F);
         }

         this.head.setRotationPoint(1.0F, 19.49F, -3.0F);
         this.head.rotateAngleX = 0.0F;
         this.head.rotateAngleY = -2.0943952F;
         this.head.rotateAngleZ = 0.0F;
         this.legBackRight.showModel = false;
         this.legBackLeft.showModel = false;
         this.legFrontRight.showModel = false;
         this.legFrontLeft.showModel = false;
      } else if (entityIn.isSitting()) {
         this.body.rotateAngleX = ((float)Math.PI / 6F);
         this.body.setRotationPoint(0.0F, 9.0F, -3.0F);
         this.tail.rotateAngleX = ((float)Math.PI / 4F);
         this.tail.setRotationPoint(-4.0F, 15.0F, -2.0F);
         this.head.setRotationPoint(-1.0F, 10.0F, -0.25F);
         this.head.rotateAngleX = 0.0F;
         this.head.rotateAngleY = 0.0F;
         if (this.isChild) {
            this.head.setRotationPoint(-1.0F, 13.0F, -3.75F);
         }

         this.legBackRight.rotateAngleX = -1.3089969F;
         this.legBackRight.setRotationPoint(-5.0F, 21.5F, 6.75F);
         this.legBackLeft.rotateAngleX = -1.3089969F;
         this.legBackLeft.setRotationPoint(-1.0F, 21.5F, 6.75F);
         this.legFrontRight.rotateAngleX = -0.2617994F;
         this.legFrontLeft.rotateAngleX = -0.2617994F;
      }

   }

   protected Iterable<ModelRenderer> getHeadParts() {
      return ImmutableList.of(this.head);
   }

   protected Iterable<ModelRenderer> getBodyParts() {
      return ImmutableList.of(this.body, this.legBackRight, this.legBackLeft, this.legFrontRight, this.legFrontLeft);
   }

   /**
    * Sets this entity's model rotation angles
    */
   public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
      if (!entityIn.isSleeping() && !entityIn.isStuck() && !entityIn.isCrouching()) {
         this.head.rotateAngleX = headPitch * ((float)Math.PI / 180F);
         this.head.rotateAngleY = netHeadYaw * ((float)Math.PI / 180F);
      }

      if (entityIn.isSleeping()) {
         this.head.rotateAngleX = 0.0F;
         this.head.rotateAngleY = -2.0943952F;
         this.head.rotateAngleZ = MathHelper.cos(ageInTicks * 0.027F) / 22.0F;
      }

      if (entityIn.isCrouching()) {
         float f = MathHelper.cos(ageInTicks) * 0.01F;
         this.body.rotateAngleY = f;
         this.legBackRight.rotateAngleZ = f;
         this.legBackLeft.rotateAngleZ = f;
         this.legFrontRight.rotateAngleZ = f / 2.0F;
         this.legFrontLeft.rotateAngleZ = f / 2.0F;
      }

      if (entityIn.isStuck()) {
         float f1 = 0.1F;
         this.field_217125_n += 0.67F;
         this.legBackRight.rotateAngleX = MathHelper.cos(this.field_217125_n * 0.4662F) * 0.1F;
         this.legBackLeft.rotateAngleX = MathHelper.cos(this.field_217125_n * 0.4662F + (float)Math.PI) * 0.1F;
         this.legFrontRight.rotateAngleX = MathHelper.cos(this.field_217125_n * 0.4662F + (float)Math.PI) * 0.1F;
         this.legFrontLeft.rotateAngleX = MathHelper.cos(this.field_217125_n * 0.4662F) * 0.1F;
      }

   }
}
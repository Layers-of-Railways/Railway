package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.monster.RavagerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RavagerModel extends SegmentedModel<RavagerEntity> {
   private final ModelRenderer head;
   private final ModelRenderer jaw;
   private final ModelRenderer body;
   private final ModelRenderer legBackRight;
   private final ModelRenderer legBackLeft;
   private final ModelRenderer legFrontRight;
   private final ModelRenderer legFrontLeft;
   private final ModelRenderer neck;

   public RavagerModel() {
      this.textureWidth = 128;
      this.textureHeight = 128;
      int i = 16;
      float f = 0.0F;
      this.neck = new ModelRenderer(this);
      this.neck.setRotationPoint(0.0F, -7.0F, -1.5F);
      this.neck.setTextureOffset(68, 73).addBox(-5.0F, -1.0F, -18.0F, 10.0F, 10.0F, 18.0F, 0.0F);
      this.head = new ModelRenderer(this);
      this.head.setRotationPoint(0.0F, 16.0F, -17.0F);
      this.head.setTextureOffset(0, 0).addBox(-8.0F, -20.0F, -14.0F, 16.0F, 20.0F, 16.0F, 0.0F);
      this.head.setTextureOffset(0, 0).addBox(-2.0F, -6.0F, -18.0F, 4.0F, 8.0F, 4.0F, 0.0F);
      ModelRenderer modelrenderer = new ModelRenderer(this);
      modelrenderer.setRotationPoint(-10.0F, -14.0F, -8.0F);
      modelrenderer.setTextureOffset(74, 55).addBox(0.0F, -14.0F, -2.0F, 2.0F, 14.0F, 4.0F, 0.0F);
      modelrenderer.rotateAngleX = 1.0995574F;
      this.head.addChild(modelrenderer);
      ModelRenderer modelrenderer1 = new ModelRenderer(this);
      modelrenderer1.mirror = true;
      modelrenderer1.setRotationPoint(8.0F, -14.0F, -8.0F);
      modelrenderer1.setTextureOffset(74, 55).addBox(0.0F, -14.0F, -2.0F, 2.0F, 14.0F, 4.0F, 0.0F);
      modelrenderer1.rotateAngleX = 1.0995574F;
      this.head.addChild(modelrenderer1);
      this.jaw = new ModelRenderer(this);
      this.jaw.setRotationPoint(0.0F, -2.0F, 2.0F);
      this.jaw.setTextureOffset(0, 36).addBox(-8.0F, 0.0F, -16.0F, 16.0F, 3.0F, 16.0F, 0.0F);
      this.head.addChild(this.jaw);
      this.neck.addChild(this.head);
      this.body = new ModelRenderer(this);
      this.body.setTextureOffset(0, 55).addBox(-7.0F, -10.0F, -7.0F, 14.0F, 16.0F, 20.0F, 0.0F);
      this.body.setTextureOffset(0, 91).addBox(-6.0F, 6.0F, -7.0F, 12.0F, 13.0F, 18.0F, 0.0F);
      this.body.setRotationPoint(0.0F, 1.0F, 2.0F);
      this.legBackRight = new ModelRenderer(this, 96, 0);
      this.legBackRight.addBox(-4.0F, 0.0F, -4.0F, 8.0F, 37.0F, 8.0F, 0.0F);
      this.legBackRight.setRotationPoint(-8.0F, -13.0F, 18.0F);
      this.legBackLeft = new ModelRenderer(this, 96, 0);
      this.legBackLeft.mirror = true;
      this.legBackLeft.addBox(-4.0F, 0.0F, -4.0F, 8.0F, 37.0F, 8.0F, 0.0F);
      this.legBackLeft.setRotationPoint(8.0F, -13.0F, 18.0F);
      this.legFrontRight = new ModelRenderer(this, 64, 0);
      this.legFrontRight.addBox(-4.0F, 0.0F, -4.0F, 8.0F, 37.0F, 8.0F, 0.0F);
      this.legFrontRight.setRotationPoint(-8.0F, -13.0F, -5.0F);
      this.legFrontLeft = new ModelRenderer(this, 64, 0);
      this.legFrontLeft.mirror = true;
      this.legFrontLeft.addBox(-4.0F, 0.0F, -4.0F, 8.0F, 37.0F, 8.0F, 0.0F);
      this.legFrontLeft.setRotationPoint(8.0F, -13.0F, -5.0F);
   }

   public Iterable<ModelRenderer> getParts() {
      return ImmutableList.of(this.neck, this.body, this.legBackRight, this.legBackLeft, this.legFrontRight, this.legFrontLeft);
   }

   /**
    * Sets this entity's model rotation angles
    */
   public void setRotationAngles(RavagerEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
      this.head.rotateAngleX = headPitch * ((float)Math.PI / 180F);
      this.head.rotateAngleY = netHeadYaw * ((float)Math.PI / 180F);
      this.body.rotateAngleX = ((float)Math.PI / 2F);
      float f = 0.4F * limbSwingAmount;
      this.legBackRight.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * f;
      this.legBackLeft.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * f;
      this.legFrontRight.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * f;
      this.legFrontLeft.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * f;
   }

   public void setLivingAnimations(RavagerEntity entityIn, float limbSwing, float limbSwingAmount, float partialTick) {
      super.setLivingAnimations(entityIn, limbSwing, limbSwingAmount, partialTick);
      int i = entityIn.func_213684_dX();
      int j = entityIn.func_213687_eg();
      int k = 20;
      int l = entityIn.func_213683_l();
      int i1 = 10;
      if (l > 0) {
         float f = this.func_217167_a((float)l - partialTick, 10.0F);
         float f1 = (1.0F + f) * 0.5F;
         float f2 = f1 * f1 * f1 * 12.0F;
         float f3 = f2 * MathHelper.sin(this.neck.rotateAngleX);
         this.neck.rotationPointZ = -6.5F + f2;
         this.neck.rotationPointY = -7.0F - f3;
         float f4 = MathHelper.sin(((float)l - partialTick) / 10.0F * (float)Math.PI * 0.25F);
         this.jaw.rotateAngleX = ((float)Math.PI / 2F) * f4;
         if (l > 5) {
            this.jaw.rotateAngleX = MathHelper.sin(((float)(-4 + l) - partialTick) / 4.0F) * (float)Math.PI * 0.4F;
         } else {
            this.jaw.rotateAngleX = 0.15707964F * MathHelper.sin((float)Math.PI * ((float)l - partialTick) / 10.0F);
         }
      } else {
         float f5 = -1.0F;
         float f6 = -1.0F * MathHelper.sin(this.neck.rotateAngleX);
         this.neck.rotationPointX = 0.0F;
         this.neck.rotationPointY = -7.0F - f6;
         this.neck.rotationPointZ = 5.5F;
         boolean flag = i > 0;
         this.neck.rotateAngleX = flag ? 0.21991149F : 0.0F;
         this.jaw.rotateAngleX = (float)Math.PI * (flag ? 0.05F : 0.01F);
         if (flag) {
            double d0 = (double)i / 40.0D;
            this.neck.rotationPointX = (float)Math.sin(d0 * 10.0D) * 3.0F;
         } else if (j > 0) {
            float f7 = MathHelper.sin(((float)(20 - j) - partialTick) / 20.0F * (float)Math.PI * 0.25F);
            this.jaw.rotateAngleX = ((float)Math.PI / 2F) * f7;
         }
      }

   }

   private float func_217167_a(float p_217167_1_, float p_217167_2_) {
      return (Math.abs(p_217167_1_ % p_217167_2_ - p_217167_2_ * 0.5F) - p_217167_2_ * 0.25F) / (p_217167_2_ * 0.25F);
   }
}
package net.minecraft.client.renderer.entity.model;

import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.passive.PandaEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PandaModel<T extends PandaEntity> extends QuadrupedModel<T> {
   private float field_217164_l;
   private float field_217165_m;
   private float field_217166_n;

   public PandaModel(int p_i51063_1_, float p_i51063_2_) {
      super(p_i51063_1_, p_i51063_2_, true, 23.0F, 4.8F, 2.7F, 3.0F, 49);
      this.textureWidth = 64;
      this.textureHeight = 64;
      this.headModel = new ModelRenderer(this, 0, 6);
      this.headModel.addBox(-6.5F, -5.0F, -4.0F, 13.0F, 10.0F, 9.0F);
      this.headModel.setRotationPoint(0.0F, 11.5F, -17.0F);
      this.headModel.setTextureOffset(45, 16).addBox(-3.5F, 0.0F, -6.0F, 7.0F, 5.0F, 2.0F);
      this.headModel.setTextureOffset(52, 25).addBox(-8.5F, -8.0F, -1.0F, 5.0F, 4.0F, 1.0F);
      this.headModel.setTextureOffset(52, 25).addBox(3.5F, -8.0F, -1.0F, 5.0F, 4.0F, 1.0F);
      this.body = new ModelRenderer(this, 0, 25);
      this.body.addBox(-9.5F, -13.0F, -6.5F, 19.0F, 26.0F, 13.0F);
      this.body.setRotationPoint(0.0F, 10.0F, 0.0F);
      int i = 9;
      int j = 6;
      this.legBackRight = new ModelRenderer(this, 40, 0);
      this.legBackRight.addBox(-3.0F, 0.0F, -3.0F, 6.0F, 9.0F, 6.0F);
      this.legBackRight.setRotationPoint(-5.5F, 15.0F, 9.0F);
      this.legBackLeft = new ModelRenderer(this, 40, 0);
      this.legBackLeft.addBox(-3.0F, 0.0F, -3.0F, 6.0F, 9.0F, 6.0F);
      this.legBackLeft.setRotationPoint(5.5F, 15.0F, 9.0F);
      this.legFrontRight = new ModelRenderer(this, 40, 0);
      this.legFrontRight.addBox(-3.0F, 0.0F, -3.0F, 6.0F, 9.0F, 6.0F);
      this.legFrontRight.setRotationPoint(-5.5F, 15.0F, -9.0F);
      this.legFrontLeft = new ModelRenderer(this, 40, 0);
      this.legFrontLeft.addBox(-3.0F, 0.0F, -3.0F, 6.0F, 9.0F, 6.0F);
      this.legFrontLeft.setRotationPoint(5.5F, 15.0F, -9.0F);
   }

   public void setLivingAnimations(T entityIn, float limbSwing, float limbSwingAmount, float partialTick) {
      super.setLivingAnimations(entityIn, limbSwing, limbSwingAmount, partialTick);
      this.field_217164_l = entityIn.func_213561_v(partialTick);
      this.field_217165_m = entityIn.func_213583_w(partialTick);
      this.field_217166_n = entityIn.isChild() ? 0.0F : entityIn.func_213591_x(partialTick);
   }

   /**
    * Sets this entity's model rotation angles
    */
   public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
      super.setRotationAngles(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
      boolean flag = entityIn.getUnhappyCounter() > 0;
      boolean flag1 = entityIn.func_213539_dW();
      int i = entityIn.getSneezeCounter();
      boolean flag2 = entityIn.func_213578_dZ();
      boolean flag3 = entityIn.func_213566_eo();
      if (flag) {
         this.headModel.rotateAngleY = 0.35F * MathHelper.sin(0.6F * ageInTicks);
         this.headModel.rotateAngleZ = 0.35F * MathHelper.sin(0.6F * ageInTicks);
         this.legFrontRight.rotateAngleX = -0.75F * MathHelper.sin(0.3F * ageInTicks);
         this.legFrontLeft.rotateAngleX = 0.75F * MathHelper.sin(0.3F * ageInTicks);
      } else {
         this.headModel.rotateAngleZ = 0.0F;
      }

      if (flag1) {
         if (i < 15) {
            this.headModel.rotateAngleX = (-(float)Math.PI / 4F) * (float)i / 14.0F;
         } else if (i < 20) {
            float f = (float)((i - 15) / 5);
            this.headModel.rotateAngleX = (-(float)Math.PI / 4F) + ((float)Math.PI / 4F) * f;
         }
      }

      if (this.field_217164_l > 0.0F) {
         this.body.rotateAngleX = ModelUtils.func_228283_a_(this.body.rotateAngleX, 1.7407963F, this.field_217164_l);
         this.headModel.rotateAngleX = ModelUtils.func_228283_a_(this.headModel.rotateAngleX, ((float)Math.PI / 2F), this.field_217164_l);
         this.legFrontRight.rotateAngleZ = -0.27079642F;
         this.legFrontLeft.rotateAngleZ = 0.27079642F;
         this.legBackRight.rotateAngleZ = 0.5707964F;
         this.legBackLeft.rotateAngleZ = -0.5707964F;
         if (flag2) {
            this.headModel.rotateAngleX = ((float)Math.PI / 2F) + 0.2F * MathHelper.sin(ageInTicks * 0.6F);
            this.legFrontRight.rotateAngleX = -0.4F - 0.2F * MathHelper.sin(ageInTicks * 0.6F);
            this.legFrontLeft.rotateAngleX = -0.4F - 0.2F * MathHelper.sin(ageInTicks * 0.6F);
         }

         if (flag3) {
            this.headModel.rotateAngleX = 2.1707964F;
            this.legFrontRight.rotateAngleX = -0.9F;
            this.legFrontLeft.rotateAngleX = -0.9F;
         }
      } else {
         this.legBackRight.rotateAngleZ = 0.0F;
         this.legBackLeft.rotateAngleZ = 0.0F;
         this.legFrontRight.rotateAngleZ = 0.0F;
         this.legFrontLeft.rotateAngleZ = 0.0F;
      }

      if (this.field_217165_m > 0.0F) {
         this.legBackRight.rotateAngleX = -0.6F * MathHelper.sin(ageInTicks * 0.15F);
         this.legBackLeft.rotateAngleX = 0.6F * MathHelper.sin(ageInTicks * 0.15F);
         this.legFrontRight.rotateAngleX = 0.3F * MathHelper.sin(ageInTicks * 0.25F);
         this.legFrontLeft.rotateAngleX = -0.3F * MathHelper.sin(ageInTicks * 0.25F);
         this.headModel.rotateAngleX = ModelUtils.func_228283_a_(this.headModel.rotateAngleX, ((float)Math.PI / 2F), this.field_217165_m);
      }

      if (this.field_217166_n > 0.0F) {
         this.headModel.rotateAngleX = ModelUtils.func_228283_a_(this.headModel.rotateAngleX, 2.0561945F, this.field_217166_n);
         this.legBackRight.rotateAngleX = -0.5F * MathHelper.sin(ageInTicks * 0.5F);
         this.legBackLeft.rotateAngleX = 0.5F * MathHelper.sin(ageInTicks * 0.5F);
         this.legFrontRight.rotateAngleX = 0.5F * MathHelper.sin(ageInTicks * 0.5F);
         this.legFrontLeft.rotateAngleX = -0.5F * MathHelper.sin(ageInTicks * 0.5F);
      }

   }
}
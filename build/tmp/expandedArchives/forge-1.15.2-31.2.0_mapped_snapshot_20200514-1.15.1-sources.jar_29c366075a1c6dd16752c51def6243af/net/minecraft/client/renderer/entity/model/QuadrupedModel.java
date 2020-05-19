package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class QuadrupedModel<T extends Entity> extends AgeableModel<T> {
   protected ModelRenderer headModel = new ModelRenderer(this, 0, 0);
   protected ModelRenderer body;
   protected ModelRenderer legBackRight;
   protected ModelRenderer legBackLeft;
   protected ModelRenderer legFrontRight;
   protected ModelRenderer legFrontLeft;

   public QuadrupedModel(int p_i225948_1_, float p_i225948_2_, boolean p_i225948_3_, float p_i225948_4_, float p_i225948_5_, float p_i225948_6_, float p_i225948_7_, int p_i225948_8_) {
      super(p_i225948_3_, p_i225948_4_, p_i225948_5_, p_i225948_6_, p_i225948_7_, (float)p_i225948_8_);
      this.headModel.addBox(-4.0F, -4.0F, -8.0F, 8.0F, 8.0F, 8.0F, p_i225948_2_);
      this.headModel.setRotationPoint(0.0F, (float)(18 - p_i225948_1_), -6.0F);
      this.body = new ModelRenderer(this, 28, 8);
      this.body.addBox(-5.0F, -10.0F, -7.0F, 10.0F, 16.0F, 8.0F, p_i225948_2_);
      this.body.setRotationPoint(0.0F, (float)(17 - p_i225948_1_), 2.0F);
      this.legBackRight = new ModelRenderer(this, 0, 16);
      this.legBackRight.addBox(-2.0F, 0.0F, -2.0F, 4.0F, (float)p_i225948_1_, 4.0F, p_i225948_2_);
      this.legBackRight.setRotationPoint(-3.0F, (float)(24 - p_i225948_1_), 7.0F);
      this.legBackLeft = new ModelRenderer(this, 0, 16);
      this.legBackLeft.addBox(-2.0F, 0.0F, -2.0F, 4.0F, (float)p_i225948_1_, 4.0F, p_i225948_2_);
      this.legBackLeft.setRotationPoint(3.0F, (float)(24 - p_i225948_1_), 7.0F);
      this.legFrontRight = new ModelRenderer(this, 0, 16);
      this.legFrontRight.addBox(-2.0F, 0.0F, -2.0F, 4.0F, (float)p_i225948_1_, 4.0F, p_i225948_2_);
      this.legFrontRight.setRotationPoint(-3.0F, (float)(24 - p_i225948_1_), -5.0F);
      this.legFrontLeft = new ModelRenderer(this, 0, 16);
      this.legFrontLeft.addBox(-2.0F, 0.0F, -2.0F, 4.0F, (float)p_i225948_1_, 4.0F, p_i225948_2_);
      this.legFrontLeft.setRotationPoint(3.0F, (float)(24 - p_i225948_1_), -5.0F);
   }

   protected Iterable<ModelRenderer> getHeadParts() {
      return ImmutableList.of(this.headModel);
   }

   protected Iterable<ModelRenderer> getBodyParts() {
      return ImmutableList.of(this.body, this.legBackRight, this.legBackLeft, this.legFrontRight, this.legFrontLeft);
   }

   /**
    * Sets this entity's model rotation angles
    */
   public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
      this.headModel.rotateAngleX = headPitch * ((float)Math.PI / 180F);
      this.headModel.rotateAngleY = netHeadYaw * ((float)Math.PI / 180F);
      this.body.rotateAngleX = ((float)Math.PI / 2F);
      this.legBackRight.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
      this.legBackLeft.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;
      this.legFrontRight.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;
      this.legFrontLeft.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
   }
}
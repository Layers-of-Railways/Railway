package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SnowManModel<T extends Entity> extends SegmentedModel<T> {
   private final ModelRenderer body;
   private final ModelRenderer bottomBody;
   private final ModelRenderer head;
   private final ModelRenderer rightHand;
   private final ModelRenderer leftHand;

   public SnowManModel() {
      float f = 4.0F;
      float f1 = 0.0F;
      this.head = (new ModelRenderer(this, 0, 0)).setTextureSize(64, 64);
      this.head.addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, -0.5F);
      this.head.setRotationPoint(0.0F, 4.0F, 0.0F);
      this.rightHand = (new ModelRenderer(this, 32, 0)).setTextureSize(64, 64);
      this.rightHand.addBox(-1.0F, 0.0F, -1.0F, 12.0F, 2.0F, 2.0F, -0.5F);
      this.rightHand.setRotationPoint(0.0F, 6.0F, 0.0F);
      this.leftHand = (new ModelRenderer(this, 32, 0)).setTextureSize(64, 64);
      this.leftHand.addBox(-1.0F, 0.0F, -1.0F, 12.0F, 2.0F, 2.0F, -0.5F);
      this.leftHand.setRotationPoint(0.0F, 6.0F, 0.0F);
      this.body = (new ModelRenderer(this, 0, 16)).setTextureSize(64, 64);
      this.body.addBox(-5.0F, -10.0F, -5.0F, 10.0F, 10.0F, 10.0F, -0.5F);
      this.body.setRotationPoint(0.0F, 13.0F, 0.0F);
      this.bottomBody = (new ModelRenderer(this, 0, 36)).setTextureSize(64, 64);
      this.bottomBody.addBox(-6.0F, -12.0F, -6.0F, 12.0F, 12.0F, 12.0F, -0.5F);
      this.bottomBody.setRotationPoint(0.0F, 24.0F, 0.0F);
   }

   /**
    * Sets this entity's model rotation angles
    */
   public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
      this.head.rotateAngleY = netHeadYaw * ((float)Math.PI / 180F);
      this.head.rotateAngleX = headPitch * ((float)Math.PI / 180F);
      this.body.rotateAngleY = netHeadYaw * ((float)Math.PI / 180F) * 0.25F;
      float f = MathHelper.sin(this.body.rotateAngleY);
      float f1 = MathHelper.cos(this.body.rotateAngleY);
      this.rightHand.rotateAngleZ = 1.0F;
      this.leftHand.rotateAngleZ = -1.0F;
      this.rightHand.rotateAngleY = 0.0F + this.body.rotateAngleY;
      this.leftHand.rotateAngleY = (float)Math.PI + this.body.rotateAngleY;
      this.rightHand.rotationPointX = f1 * 5.0F;
      this.rightHand.rotationPointZ = -f * 5.0F;
      this.leftHand.rotationPointX = -f1 * 5.0F;
      this.leftHand.rotationPointZ = f * 5.0F;
   }

   public Iterable<ModelRenderer> getParts() {
      return ImmutableList.of(this.body, this.bottomBody, this.head, this.rightHand, this.leftHand);
   }

   public ModelRenderer func_205070_a() {
      return this.head;
   }
}
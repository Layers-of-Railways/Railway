package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PhantomModel<T extends Entity> extends SegmentedModel<T> {
   private final ModelRenderer body;
   private final ModelRenderer leftWingBody;
   private final ModelRenderer leftWing;
   private final ModelRenderer rightWingBody;
   private final ModelRenderer rightWing;
   private final ModelRenderer tail1;
   private final ModelRenderer tail2;

   public PhantomModel() {
      this.textureWidth = 64;
      this.textureHeight = 64;
      this.body = new ModelRenderer(this, 0, 8);
      this.body.addBox(-3.0F, -2.0F, -8.0F, 5.0F, 3.0F, 9.0F);
      this.tail1 = new ModelRenderer(this, 3, 20);
      this.tail1.addBox(-2.0F, 0.0F, 0.0F, 3.0F, 2.0F, 6.0F);
      this.tail1.setRotationPoint(0.0F, -2.0F, 1.0F);
      this.body.addChild(this.tail1);
      this.tail2 = new ModelRenderer(this, 4, 29);
      this.tail2.addBox(-1.0F, 0.0F, 0.0F, 1.0F, 1.0F, 6.0F);
      this.tail2.setRotationPoint(0.0F, 0.5F, 6.0F);
      this.tail1.addChild(this.tail2);
      this.leftWingBody = new ModelRenderer(this, 23, 12);
      this.leftWingBody.addBox(0.0F, 0.0F, 0.0F, 6.0F, 2.0F, 9.0F);
      this.leftWingBody.setRotationPoint(2.0F, -2.0F, -8.0F);
      this.leftWing = new ModelRenderer(this, 16, 24);
      this.leftWing.addBox(0.0F, 0.0F, 0.0F, 13.0F, 1.0F, 9.0F);
      this.leftWing.setRotationPoint(6.0F, 0.0F, 0.0F);
      this.leftWingBody.addChild(this.leftWing);
      this.rightWingBody = new ModelRenderer(this, 23, 12);
      this.rightWingBody.mirror = true;
      this.rightWingBody.addBox(-6.0F, 0.0F, 0.0F, 6.0F, 2.0F, 9.0F);
      this.rightWingBody.setRotationPoint(-3.0F, -2.0F, -8.0F);
      this.rightWing = new ModelRenderer(this, 16, 24);
      this.rightWing.mirror = true;
      this.rightWing.addBox(-13.0F, 0.0F, 0.0F, 13.0F, 1.0F, 9.0F);
      this.rightWing.setRotationPoint(-6.0F, 0.0F, 0.0F);
      this.rightWingBody.addChild(this.rightWing);
      this.leftWingBody.rotateAngleZ = 0.1F;
      this.leftWing.rotateAngleZ = 0.1F;
      this.rightWingBody.rotateAngleZ = -0.1F;
      this.rightWing.rotateAngleZ = -0.1F;
      this.body.rotateAngleX = -0.1F;
      ModelRenderer modelrenderer = new ModelRenderer(this, 0, 0);
      modelrenderer.addBox(-4.0F, -2.0F, -5.0F, 7.0F, 3.0F, 5.0F);
      modelrenderer.setRotationPoint(0.0F, 1.0F, -7.0F);
      modelrenderer.rotateAngleX = 0.2F;
      this.body.addChild(modelrenderer);
      this.body.addChild(this.leftWingBody);
      this.body.addChild(this.rightWingBody);
   }

   public Iterable<ModelRenderer> getParts() {
      return ImmutableList.of(this.body);
   }

   /**
    * Sets this entity's model rotation angles
    */
   public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
      float f = ((float)(entityIn.getEntityId() * 3) + ageInTicks) * 0.13F;
      float f1 = 16.0F;
      this.leftWingBody.rotateAngleZ = MathHelper.cos(f) * 16.0F * ((float)Math.PI / 180F);
      this.leftWing.rotateAngleZ = MathHelper.cos(f) * 16.0F * ((float)Math.PI / 180F);
      this.rightWingBody.rotateAngleZ = -this.leftWingBody.rotateAngleZ;
      this.rightWing.rotateAngleZ = -this.leftWing.rotateAngleZ;
      this.tail1.rotateAngleX = -(5.0F + MathHelper.cos(f * 2.0F) * 5.0F) * ((float)Math.PI / 180F);
      this.tail2.rotateAngleX = -(5.0F + MathHelper.cos(f * 2.0F) * 5.0F) * ((float)Math.PI / 180F);
   }
}
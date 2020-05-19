package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SalmonModel<T extends Entity> extends SegmentedModel<T> {
   private final ModelRenderer bodyFront;
   private final ModelRenderer bodyRear;
   private final ModelRenderer head;
   private final ModelRenderer finRight;
   private final ModelRenderer finLeft;

   public SalmonModel() {
      this.textureWidth = 32;
      this.textureHeight = 32;
      int i = 20;
      this.bodyFront = new ModelRenderer(this, 0, 0);
      this.bodyFront.addBox(-1.5F, -2.5F, 0.0F, 3.0F, 5.0F, 8.0F);
      this.bodyFront.setRotationPoint(0.0F, 20.0F, 0.0F);
      this.bodyRear = new ModelRenderer(this, 0, 13);
      this.bodyRear.addBox(-1.5F, -2.5F, 0.0F, 3.0F, 5.0F, 8.0F);
      this.bodyRear.setRotationPoint(0.0F, 20.0F, 8.0F);
      this.head = new ModelRenderer(this, 22, 0);
      this.head.addBox(-1.0F, -2.0F, -3.0F, 2.0F, 4.0F, 3.0F);
      this.head.setRotationPoint(0.0F, 20.0F, 0.0F);
      ModelRenderer modelrenderer = new ModelRenderer(this, 20, 10);
      modelrenderer.addBox(0.0F, -2.5F, 0.0F, 0.0F, 5.0F, 6.0F);
      modelrenderer.setRotationPoint(0.0F, 0.0F, 8.0F);
      this.bodyRear.addChild(modelrenderer);
      ModelRenderer modelrenderer1 = new ModelRenderer(this, 2, 1);
      modelrenderer1.addBox(0.0F, 0.0F, 0.0F, 0.0F, 2.0F, 3.0F);
      modelrenderer1.setRotationPoint(0.0F, -4.5F, 5.0F);
      this.bodyFront.addChild(modelrenderer1);
      ModelRenderer modelrenderer2 = new ModelRenderer(this, 0, 2);
      modelrenderer2.addBox(0.0F, 0.0F, 0.0F, 0.0F, 2.0F, 4.0F);
      modelrenderer2.setRotationPoint(0.0F, -4.5F, -1.0F);
      this.bodyRear.addChild(modelrenderer2);
      this.finRight = new ModelRenderer(this, -4, 0);
      this.finRight.addBox(-2.0F, 0.0F, 0.0F, 2.0F, 0.0F, 2.0F);
      this.finRight.setRotationPoint(-1.5F, 21.5F, 0.0F);
      this.finRight.rotateAngleZ = (-(float)Math.PI / 4F);
      this.finLeft = new ModelRenderer(this, 0, 0);
      this.finLeft.addBox(0.0F, 0.0F, 0.0F, 2.0F, 0.0F, 2.0F);
      this.finLeft.setRotationPoint(1.5F, 21.5F, 0.0F);
      this.finLeft.rotateAngleZ = ((float)Math.PI / 4F);
   }

   public Iterable<ModelRenderer> getParts() {
      return ImmutableList.of(this.bodyFront, this.bodyRear, this.head, this.finRight, this.finLeft);
   }

   /**
    * Sets this entity's model rotation angles
    */
   public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
      float f = 1.0F;
      float f1 = 1.0F;
      if (!entityIn.isInWater()) {
         f = 1.3F;
         f1 = 1.7F;
      }

      this.bodyRear.rotateAngleY = -f * 0.25F * MathHelper.sin(f1 * 0.6F * ageInTicks);
   }
}
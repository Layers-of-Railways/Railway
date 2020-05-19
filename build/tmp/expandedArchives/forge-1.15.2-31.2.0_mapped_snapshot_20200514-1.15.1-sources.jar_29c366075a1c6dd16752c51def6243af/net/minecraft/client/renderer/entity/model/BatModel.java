package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.passive.BatEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BatModel extends SegmentedModel<BatEntity> {
   private final ModelRenderer batHead;
   private final ModelRenderer batBody;
   private final ModelRenderer batRightWing;
   private final ModelRenderer batLeftWing;
   private final ModelRenderer batOuterRightWing;
   private final ModelRenderer batOuterLeftWing;

   public BatModel() {
      this.textureWidth = 64;
      this.textureHeight = 64;
      this.batHead = new ModelRenderer(this, 0, 0);
      this.batHead.addBox(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 6.0F);
      ModelRenderer modelrenderer = new ModelRenderer(this, 24, 0);
      modelrenderer.addBox(-4.0F, -6.0F, -2.0F, 3.0F, 4.0F, 1.0F);
      this.batHead.addChild(modelrenderer);
      ModelRenderer modelrenderer1 = new ModelRenderer(this, 24, 0);
      modelrenderer1.mirror = true;
      modelrenderer1.addBox(1.0F, -6.0F, -2.0F, 3.0F, 4.0F, 1.0F);
      this.batHead.addChild(modelrenderer1);
      this.batBody = new ModelRenderer(this, 0, 16);
      this.batBody.addBox(-3.0F, 4.0F, -3.0F, 6.0F, 12.0F, 6.0F);
      this.batBody.setTextureOffset(0, 34).addBox(-5.0F, 16.0F, 0.0F, 10.0F, 6.0F, 1.0F);
      this.batRightWing = new ModelRenderer(this, 42, 0);
      this.batRightWing.addBox(-12.0F, 1.0F, 1.5F, 10.0F, 16.0F, 1.0F);
      this.batOuterRightWing = new ModelRenderer(this, 24, 16);
      this.batOuterRightWing.setRotationPoint(-12.0F, 1.0F, 1.5F);
      this.batOuterRightWing.addBox(-8.0F, 1.0F, 0.0F, 8.0F, 12.0F, 1.0F);
      this.batLeftWing = new ModelRenderer(this, 42, 0);
      this.batLeftWing.mirror = true;
      this.batLeftWing.addBox(2.0F, 1.0F, 1.5F, 10.0F, 16.0F, 1.0F);
      this.batOuterLeftWing = new ModelRenderer(this, 24, 16);
      this.batOuterLeftWing.mirror = true;
      this.batOuterLeftWing.setRotationPoint(12.0F, 1.0F, 1.5F);
      this.batOuterLeftWing.addBox(0.0F, 1.0F, 0.0F, 8.0F, 12.0F, 1.0F);
      this.batBody.addChild(this.batRightWing);
      this.batBody.addChild(this.batLeftWing);
      this.batRightWing.addChild(this.batOuterRightWing);
      this.batLeftWing.addChild(this.batOuterLeftWing);
   }

   public Iterable<ModelRenderer> getParts() {
      return ImmutableList.of(this.batHead, this.batBody);
   }

   /**
    * Sets this entity's model rotation angles
    */
   public void setRotationAngles(BatEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
      if (entityIn.getIsBatHanging()) {
         this.batHead.rotateAngleX = headPitch * ((float)Math.PI / 180F);
         this.batHead.rotateAngleY = (float)Math.PI - netHeadYaw * ((float)Math.PI / 180F);
         this.batHead.rotateAngleZ = (float)Math.PI;
         this.batHead.setRotationPoint(0.0F, -2.0F, 0.0F);
         this.batRightWing.setRotationPoint(-3.0F, 0.0F, 3.0F);
         this.batLeftWing.setRotationPoint(3.0F, 0.0F, 3.0F);
         this.batBody.rotateAngleX = (float)Math.PI;
         this.batRightWing.rotateAngleX = -0.15707964F;
         this.batRightWing.rotateAngleY = -1.2566371F;
         this.batOuterRightWing.rotateAngleY = -1.7278761F;
         this.batLeftWing.rotateAngleX = this.batRightWing.rotateAngleX;
         this.batLeftWing.rotateAngleY = -this.batRightWing.rotateAngleY;
         this.batOuterLeftWing.rotateAngleY = -this.batOuterRightWing.rotateAngleY;
      } else {
         this.batHead.rotateAngleX = headPitch * ((float)Math.PI / 180F);
         this.batHead.rotateAngleY = netHeadYaw * ((float)Math.PI / 180F);
         this.batHead.rotateAngleZ = 0.0F;
         this.batHead.setRotationPoint(0.0F, 0.0F, 0.0F);
         this.batRightWing.setRotationPoint(0.0F, 0.0F, 0.0F);
         this.batLeftWing.setRotationPoint(0.0F, 0.0F, 0.0F);
         this.batBody.rotateAngleX = ((float)Math.PI / 4F) + MathHelper.cos(ageInTicks * 0.1F) * 0.15F;
         this.batBody.rotateAngleY = 0.0F;
         this.batRightWing.rotateAngleY = MathHelper.cos(ageInTicks * 1.3F) * (float)Math.PI * 0.25F;
         this.batLeftWing.rotateAngleY = -this.batRightWing.rotateAngleY;
         this.batOuterRightWing.rotateAngleY = this.batRightWing.rotateAngleY * 0.5F;
         this.batOuterLeftWing.rotateAngleY = -this.batRightWing.rotateAngleY * 0.5F;
      }

   }
}
package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.passive.ParrotEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ParrotModel extends SegmentedModel<ParrotEntity> {
   private final ModelRenderer body;
   private final ModelRenderer tail;
   private final ModelRenderer wingLeft;
   private final ModelRenderer wingRight;
   private final ModelRenderer head;
   private final ModelRenderer head2;
   private final ModelRenderer beak1;
   private final ModelRenderer beak2;
   private final ModelRenderer feather;
   private final ModelRenderer legLeft;
   private final ModelRenderer legRight;

   public ParrotModel() {
      this.textureWidth = 32;
      this.textureHeight = 32;
      this.body = new ModelRenderer(this, 2, 8);
      this.body.addBox(-1.5F, 0.0F, -1.5F, 3.0F, 6.0F, 3.0F);
      this.body.setRotationPoint(0.0F, 16.5F, -3.0F);
      this.tail = new ModelRenderer(this, 22, 1);
      this.tail.addBox(-1.5F, -1.0F, -1.0F, 3.0F, 4.0F, 1.0F);
      this.tail.setRotationPoint(0.0F, 21.07F, 1.16F);
      this.wingLeft = new ModelRenderer(this, 19, 8);
      this.wingLeft.addBox(-0.5F, 0.0F, -1.5F, 1.0F, 5.0F, 3.0F);
      this.wingLeft.setRotationPoint(1.5F, 16.94F, -2.76F);
      this.wingRight = new ModelRenderer(this, 19, 8);
      this.wingRight.addBox(-0.5F, 0.0F, -1.5F, 1.0F, 5.0F, 3.0F);
      this.wingRight.setRotationPoint(-1.5F, 16.94F, -2.76F);
      this.head = new ModelRenderer(this, 2, 2);
      this.head.addBox(-1.0F, -1.5F, -1.0F, 2.0F, 3.0F, 2.0F);
      this.head.setRotationPoint(0.0F, 15.69F, -2.76F);
      this.head2 = new ModelRenderer(this, 10, 0);
      this.head2.addBox(-1.0F, -0.5F, -2.0F, 2.0F, 1.0F, 4.0F);
      this.head2.setRotationPoint(0.0F, -2.0F, -1.0F);
      this.head.addChild(this.head2);
      this.beak1 = new ModelRenderer(this, 11, 7);
      this.beak1.addBox(-0.5F, -1.0F, -0.5F, 1.0F, 2.0F, 1.0F);
      this.beak1.setRotationPoint(0.0F, -0.5F, -1.5F);
      this.head.addChild(this.beak1);
      this.beak2 = new ModelRenderer(this, 16, 7);
      this.beak2.addBox(-0.5F, 0.0F, -0.5F, 1.0F, 2.0F, 1.0F);
      this.beak2.setRotationPoint(0.0F, -1.75F, -2.45F);
      this.head.addChild(this.beak2);
      this.feather = new ModelRenderer(this, 2, 18);
      this.feather.addBox(0.0F, -4.0F, -2.0F, 0.0F, 5.0F, 4.0F);
      this.feather.setRotationPoint(0.0F, -2.15F, 0.15F);
      this.head.addChild(this.feather);
      this.legLeft = new ModelRenderer(this, 14, 18);
      this.legLeft.addBox(-0.5F, 0.0F, -0.5F, 1.0F, 2.0F, 1.0F);
      this.legLeft.setRotationPoint(1.0F, 22.0F, -1.05F);
      this.legRight = new ModelRenderer(this, 14, 18);
      this.legRight.addBox(-0.5F, 0.0F, -0.5F, 1.0F, 2.0F, 1.0F);
      this.legRight.setRotationPoint(-1.0F, 22.0F, -1.05F);
   }

   public Iterable<ModelRenderer> getParts() {
      return ImmutableList.of(this.body, this.wingLeft, this.wingRight, this.tail, this.head, this.legLeft, this.legRight);
   }

   /**
    * Sets this entity's model rotation angles
    */
   public void setRotationAngles(ParrotEntity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
      this.setRotationAngles(getParrotState(entityIn), entityIn.ticksExisted, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
   }

   public void setLivingAnimations(ParrotEntity entityIn, float limbSwing, float limbSwingAmount, float partialTick) {
      this.setLivingAnimations(getParrotState(entityIn));
   }

   public void renderOnShoulder(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float p_228284_5_, float p_228284_6_, float p_228284_7_, float p_228284_8_, int p_228284_9_) {
      this.setLivingAnimations(ParrotModel.State.ON_SHOULDER);
      this.setRotationAngles(ParrotModel.State.ON_SHOULDER, p_228284_9_, p_228284_5_, p_228284_6_, 0.0F, p_228284_7_, p_228284_8_);
      this.getParts().forEach((p_228285_4_) -> {
         p_228285_4_.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn);
      });
   }

   private void setRotationAngles(ParrotModel.State p_217162_1_, int p_217162_2_, float p_217162_3_, float p_217162_4_, float p_217162_5_, float p_217162_6_, float p_217162_7_) {
      this.head.rotateAngleX = p_217162_7_ * ((float)Math.PI / 180F);
      this.head.rotateAngleY = p_217162_6_ * ((float)Math.PI / 180F);
      this.head.rotateAngleZ = 0.0F;
      this.head.rotationPointX = 0.0F;
      this.body.rotationPointX = 0.0F;
      this.tail.rotationPointX = 0.0F;
      this.wingRight.rotationPointX = -1.5F;
      this.wingLeft.rotationPointX = 1.5F;
      switch(p_217162_1_) {
      case SITTING:
         break;
      case PARTY:
         float f = MathHelper.cos((float)p_217162_2_);
         float f1 = MathHelper.sin((float)p_217162_2_);
         this.head.rotationPointX = f;
         this.head.rotationPointY = 15.69F + f1;
         this.head.rotateAngleX = 0.0F;
         this.head.rotateAngleY = 0.0F;
         this.head.rotateAngleZ = MathHelper.sin((float)p_217162_2_) * 0.4F;
         this.body.rotationPointX = f;
         this.body.rotationPointY = 16.5F + f1;
         this.wingLeft.rotateAngleZ = -0.0873F - p_217162_5_;
         this.wingLeft.rotationPointX = 1.5F + f;
         this.wingLeft.rotationPointY = 16.94F + f1;
         this.wingRight.rotateAngleZ = 0.0873F + p_217162_5_;
         this.wingRight.rotationPointX = -1.5F + f;
         this.wingRight.rotationPointY = 16.94F + f1;
         this.tail.rotationPointX = f;
         this.tail.rotationPointY = 21.07F + f1;
         break;
      case STANDING:
         this.legLeft.rotateAngleX += MathHelper.cos(p_217162_3_ * 0.6662F) * 1.4F * p_217162_4_;
         this.legRight.rotateAngleX += MathHelper.cos(p_217162_3_ * 0.6662F + (float)Math.PI) * 1.4F * p_217162_4_;
      case FLYING:
      case ON_SHOULDER:
      default:
         float f2 = p_217162_5_ * 0.3F;
         this.head.rotationPointY = 15.69F + f2;
         this.tail.rotateAngleX = 1.015F + MathHelper.cos(p_217162_3_ * 0.6662F) * 0.3F * p_217162_4_;
         this.tail.rotationPointY = 21.07F + f2;
         this.body.rotationPointY = 16.5F + f2;
         this.wingLeft.rotateAngleZ = -0.0873F - p_217162_5_;
         this.wingLeft.rotationPointY = 16.94F + f2;
         this.wingRight.rotateAngleZ = 0.0873F + p_217162_5_;
         this.wingRight.rotationPointY = 16.94F + f2;
         this.legLeft.rotationPointY = 22.0F + f2;
         this.legRight.rotationPointY = 22.0F + f2;
      }

   }

   private void setLivingAnimations(ParrotModel.State p_217160_1_) {
      this.feather.rotateAngleX = -0.2214F;
      this.body.rotateAngleX = 0.4937F;
      this.wingLeft.rotateAngleX = -0.6981F;
      this.wingLeft.rotateAngleY = -(float)Math.PI;
      this.wingRight.rotateAngleX = -0.6981F;
      this.wingRight.rotateAngleY = -(float)Math.PI;
      this.legLeft.rotateAngleX = -0.0299F;
      this.legRight.rotateAngleX = -0.0299F;
      this.legLeft.rotationPointY = 22.0F;
      this.legRight.rotationPointY = 22.0F;
      this.legLeft.rotateAngleZ = 0.0F;
      this.legRight.rotateAngleZ = 0.0F;
      switch(p_217160_1_) {
      case SITTING:
         float f = 1.9F;
         this.head.rotationPointY = 17.59F;
         this.tail.rotateAngleX = 1.5388988F;
         this.tail.rotationPointY = 22.97F;
         this.body.rotationPointY = 18.4F;
         this.wingLeft.rotateAngleZ = -0.0873F;
         this.wingLeft.rotationPointY = 18.84F;
         this.wingRight.rotateAngleZ = 0.0873F;
         this.wingRight.rotationPointY = 18.84F;
         ++this.legLeft.rotationPointY;
         ++this.legRight.rotationPointY;
         ++this.legLeft.rotateAngleX;
         ++this.legRight.rotateAngleX;
         break;
      case PARTY:
         this.legLeft.rotateAngleZ = -0.34906584F;
         this.legRight.rotateAngleZ = 0.34906584F;
      case STANDING:
      case ON_SHOULDER:
      default:
         break;
      case FLYING:
         this.legLeft.rotateAngleX += 0.6981317F;
         this.legRight.rotateAngleX += 0.6981317F;
      }

   }

   private static ParrotModel.State getParrotState(ParrotEntity p_217158_0_) {
      if (p_217158_0_.isPartying()) {
         return ParrotModel.State.PARTY;
      } else if (p_217158_0_.isSitting()) {
         return ParrotModel.State.SITTING;
      } else {
         return p_217158_0_.isFlying() ? ParrotModel.State.FLYING : ParrotModel.State.STANDING;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static enum State {
      FLYING,
      STANDING,
      SITTING,
      PARTY,
      ON_SHOULDER;
   }
}
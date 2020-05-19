package net.minecraft.client.renderer.entity.model;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.passive.horse.AbstractChestedHorseEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class LlamaModel<T extends AbstractChestedHorseEntity> extends EntityModel<T> {
   private final ModelRenderer head;
   private final ModelRenderer body;
   private final ModelRenderer legBackRight;
   private final ModelRenderer legBackLeft;
   private final ModelRenderer legFrontRight;
   private final ModelRenderer legFrontLeft;
   private final ModelRenderer chest1;
   private final ModelRenderer chest2;

   public LlamaModel(float p_i47226_1_) {
      this.textureWidth = 128;
      this.textureHeight = 64;
      this.head = new ModelRenderer(this, 0, 0);
      this.head.addBox(-2.0F, -14.0F, -10.0F, 4.0F, 4.0F, 9.0F, p_i47226_1_);
      this.head.setRotationPoint(0.0F, 7.0F, -6.0F);
      this.head.setTextureOffset(0, 14).addBox(-4.0F, -16.0F, -6.0F, 8.0F, 18.0F, 6.0F, p_i47226_1_);
      this.head.setTextureOffset(17, 0).addBox(-4.0F, -19.0F, -4.0F, 3.0F, 3.0F, 2.0F, p_i47226_1_);
      this.head.setTextureOffset(17, 0).addBox(1.0F, -19.0F, -4.0F, 3.0F, 3.0F, 2.0F, p_i47226_1_);
      this.body = new ModelRenderer(this, 29, 0);
      this.body.addBox(-6.0F, -10.0F, -7.0F, 12.0F, 18.0F, 10.0F, p_i47226_1_);
      this.body.setRotationPoint(0.0F, 5.0F, 2.0F);
      this.chest1 = new ModelRenderer(this, 45, 28);
      this.chest1.addBox(-3.0F, 0.0F, 0.0F, 8.0F, 8.0F, 3.0F, p_i47226_1_);
      this.chest1.setRotationPoint(-8.5F, 3.0F, 3.0F);
      this.chest1.rotateAngleY = ((float)Math.PI / 2F);
      this.chest2 = new ModelRenderer(this, 45, 41);
      this.chest2.addBox(-3.0F, 0.0F, 0.0F, 8.0F, 8.0F, 3.0F, p_i47226_1_);
      this.chest2.setRotationPoint(5.5F, 3.0F, 3.0F);
      this.chest2.rotateAngleY = ((float)Math.PI / 2F);
      int i = 4;
      int j = 14;
      this.legBackRight = new ModelRenderer(this, 29, 29);
      this.legBackRight.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 14.0F, 4.0F, p_i47226_1_);
      this.legBackRight.setRotationPoint(-2.5F, 10.0F, 6.0F);
      this.legBackLeft = new ModelRenderer(this, 29, 29);
      this.legBackLeft.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 14.0F, 4.0F, p_i47226_1_);
      this.legBackLeft.setRotationPoint(2.5F, 10.0F, 6.0F);
      this.legFrontRight = new ModelRenderer(this, 29, 29);
      this.legFrontRight.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 14.0F, 4.0F, p_i47226_1_);
      this.legFrontRight.setRotationPoint(-2.5F, 10.0F, -4.0F);
      this.legFrontLeft = new ModelRenderer(this, 29, 29);
      this.legFrontLeft.addBox(-2.0F, 0.0F, -2.0F, 4.0F, 14.0F, 4.0F, p_i47226_1_);
      this.legFrontLeft.setRotationPoint(2.5F, 10.0F, -4.0F);
      --this.legBackRight.rotationPointX;
      ++this.legBackLeft.rotationPointX;
      this.legBackRight.rotationPointZ += 0.0F;
      this.legBackLeft.rotationPointZ += 0.0F;
      --this.legFrontRight.rotationPointX;
      ++this.legFrontLeft.rotationPointX;
      --this.legFrontRight.rotationPointZ;
      --this.legFrontLeft.rotationPointZ;
   }

   /**
    * Sets this entity's model rotation angles
    */
   public void setRotationAngles(T entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
      this.head.rotateAngleX = headPitch * ((float)Math.PI / 180F);
      this.head.rotateAngleY = netHeadYaw * ((float)Math.PI / 180F);
      this.body.rotateAngleX = ((float)Math.PI / 2F);
      this.legBackRight.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
      this.legBackLeft.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;
      this.legFrontRight.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F + (float)Math.PI) * 1.4F * limbSwingAmount;
      this.legFrontLeft.rotateAngleX = MathHelper.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
      boolean flag = !entityIn.isChild() && entityIn.hasChest();
      this.chest1.showModel = flag;
      this.chest2.showModel = flag;
   }

   public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
      if (this.isChild) {
         float f = 2.0F;
         matrixStackIn.push();
         float f1 = 0.7F;
         matrixStackIn.scale(0.71428573F, 0.64935064F, 0.7936508F);
         matrixStackIn.translate(0.0D, 1.3125D, (double)0.22F);
         this.head.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
         matrixStackIn.pop();
         matrixStackIn.push();
         float f2 = 1.1F;
         matrixStackIn.scale(0.625F, 0.45454544F, 0.45454544F);
         matrixStackIn.translate(0.0D, 2.0625D, 0.0D);
         this.body.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
         matrixStackIn.pop();
         matrixStackIn.push();
         matrixStackIn.scale(0.45454544F, 0.41322312F, 0.45454544F);
         matrixStackIn.translate(0.0D, 2.0625D, 0.0D);
         ImmutableList.of(this.legBackRight, this.legBackLeft, this.legFrontRight, this.legFrontLeft, this.chest1, this.chest2).forEach((p_228280_8_) -> {
            p_228280_8_.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
         });
         matrixStackIn.pop();
      } else {
         ImmutableList.of(this.head, this.body, this.legBackRight, this.legBackLeft, this.legFrontRight, this.legFrontLeft, this.chest1, this.chest2).forEach((p_228279_8_) -> {
            p_228279_8_.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
         });
      }

   }
}
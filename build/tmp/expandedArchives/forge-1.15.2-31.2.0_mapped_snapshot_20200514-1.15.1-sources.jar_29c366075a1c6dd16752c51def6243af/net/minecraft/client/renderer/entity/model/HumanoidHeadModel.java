package net.minecraft.client.renderer.entity.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class HumanoidHeadModel extends GenericHeadModel {
   private final ModelRenderer head = new ModelRenderer(this, 32, 0);

   public HumanoidHeadModel() {
      super(0, 0, 64, 64);
      this.head.addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, 0.25F);
      this.head.setRotationPoint(0.0F, 0.0F, 0.0F);
   }

   public void func_225603_a_(float p_225603_1_, float p_225603_2_, float p_225603_3_) {
      super.func_225603_a_(p_225603_1_, p_225603_2_, p_225603_3_);
      this.head.rotateAngleY = this.field_217105_a.rotateAngleY;
      this.head.rotateAngleX = this.field_217105_a.rotateAngleX;
   }

   public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
      super.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
      this.head.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
   }
}